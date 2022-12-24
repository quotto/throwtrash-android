package net.mythrowaway.app.usecase

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.capture
import com.nhaarman.mockito_kotlin.eq
import net.mythrowaway.app.adapter.repository.UpdateResult
import net.mythrowaway.app.domain.RegisteredData
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.domain.TrashSchedule
import net.mythrowaway.app.service.TrashManager
import kotlin.collections.ArrayList
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(
    ICalendarPresenter::class,
    IPersistentRepository::class,
    IConfigRepository::class,
    IAPIAdapter::class,
    TrashManager::class
)
class CalendarUseCaseTest {
    private val mockPresenter = PowerMockito.mock(ICalendarPresenter::class.java)
    private val mockPersistImpl = PowerMockito.mock(IPersistentRepository::class.java)
    private val mockConfigImpl = PowerMockito.mock(IConfigRepository::class.java)
    private val mockAPIAdapterImpl = PowerMockito.mock(IAPIAdapter::class.java)
    private val mockTrashManager = PowerMockito.mock(TrashManager::class.java)

    private val targetUseCase: CalendarUseCase =
        CalendarUseCase(
            mockPresenter,
            mockTrashManager,
            mockPersistImpl,
            mockConfigImpl,
            mockAPIAdapterImpl
        )

    @Captor
    private lateinit var captorYear: ArgumentCaptor<Int>
    @Captor
    private lateinit var captorMonth: ArgumentCaptor<Int>
    @Captor
    private lateinit var captorDateList: ArgumentCaptor<ArrayList<Int>>
    @Captor
    private lateinit var captorTrashList: ArgumentCaptor<Array<ArrayList<String>>>
    @Captor
    private lateinit var captorId: ArgumentCaptor<String>
    @Captor
    private lateinit var captorTimeStamp: ArgumentCaptor<Long>
    @Captor
    private lateinit var captorSyncState: ArgumentCaptor<Int>
    @Captor
    private lateinit var captorScheduleList: ArgumentCaptor<ArrayList<TrashData>>

    private val trash1 = TrashData().apply {
        type = "burn"
        schedules = arrayListOf(TrashSchedule().apply{
            type = "weekday"
            value = "1"
        }, TrashSchedule().apply{
            type = "weekday"
            value = "2"
        })
    }
    private val trash2 = TrashData().apply {
        type = "bin"
        schedules = arrayListOf(TrashSchedule().apply{
            type = "weekday"
            value = "1"
        })
    }

    private val adapterData1 = TrashData().apply {
        this.id = "123456"
        this.type = "burn"
        this.schedules = arrayListOf(
            TrashSchedule().apply{
            this.type = "biweek"
            this.value = "0-3"
        }, TrashSchedule().apply{
            this.type = "biweek"
            this.value = "6-1"
        })
    }
    private val adapterData2 = TrashData().apply {
        this.id = "5678"
        this.type = "other"
        this.trash_val = "家電"
        this.schedules = arrayListOf(
            TrashSchedule().apply{
            this.type = "biweek"
            this.value = "0-3"
        })
    }

    private val mapper = ObjectMapper()
    init {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }

    @Before
    fun cleanTestData() {
        Mockito.reset(mockConfigImpl)
        Mockito.reset(mockPersistImpl)
        Mockito.reset(mockPresenter)
    }

    @Test
    fun generateMonthSchedule() {
        // 1ヶ月（35日分）のゴミ出し予定リストを取得する
        targetUseCase.generateMonthSchedule(2020,1)

        Mockito.verify(mockTrashManager,Mockito.times(1)).getEnableTrashList(
            capture(captorYear),capture(captorMonth), capture(captorDateList)
        )
        // captorTrashList,captorDateListはNull
        Mockito.verify(mockPresenter,Mockito.times(1)).setCalendar(
            capture(captorYear),capture(captorMonth),capture(captorTrashList),capture(captorDateList)
        )

        Assert.assertEquals(2020,captorYear.value)
        Assert.assertEquals(1,captorMonth.value)

        val expect: List<Int> = listOf(29,30,31,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,1)
        for (i in captorDateList.value.indices) {
            assert(captorDateList.value[i] == expect[i])
        }
    }

    @Test
    fun syncData_Register() {
        Mockito.`when`(mockConfigImpl.getSyncState()).thenReturn(CalendarUseCase.SYNC_WAITING)
        Mockito.`when`(mockConfigImpl.getUserId()).thenReturn(null)
        Mockito.`when`(mockAPIAdapterImpl.register(any(ArrayList::class.java) as ArrayList<TrashData>)).thenReturn(RegisteredData().apply{id="id-00001";timestamp=12345678})
        targetUseCase.syncData()

        Mockito.verify(mockConfigImpl,Mockito.times(1)).setUserId(capture(captorId))
        Mockito.verify(mockConfigImpl,Mockito.times(1)).setTimestamp(capture(captorTimeStamp))
        Mockito.verify(mockConfigImpl,Mockito.times(1)).setSyncState(capture(captorSyncState))

        // configにuserIdが未登録の場合は新規にIDが発行される
        Assert.assertEquals("id-00001",captorId.value)
        Assert.assertEquals(12345678,captorTimeStamp.value)
        Assert.assertEquals(CalendarUseCase.SYNC_COMPLETE, captorSyncState.value)
    }

    @Test
    fun syncData_Init() {
        Mockito.`when`(mockConfigImpl.getSyncState()).thenReturn(CalendarUseCase.SYNC_NO)
        targetUseCase.syncData()

        // 初回起動時は何もしない
        Mockito.verify(mockConfigImpl,Mockito.times(0)).setUserId(capture(captorId))
        Mockito.verify(mockConfigImpl,Mockito.times(0)).setTimestamp(capture(captorTimeStamp))
        Mockito.verify(mockConfigImpl,Mockito.times(0)).setSyncState(capture(captorSyncState))
    }

    @Test
    fun syncData_SyncFromDB() {
        // ローカルタイムスタンプとDBタイムスタンプが一致しない場合にローカル側を最新化する
        Mockito.`when`(mockConfigImpl.getSyncState()).thenReturn(CalendarUseCase.SYNC_WAITING)
        Mockito.`when`(mockConfigImpl.getUserId()).thenReturn("id-00001")
        Mockito.`when`(mockConfigImpl.getTimeStamp()).thenReturn(123)
        Mockito.`when`(mockPersistImpl.getAllTrashSchedule()).thenReturn(arrayListOf(trash1,trash2))
        Mockito.`when`(mockAPIAdapterImpl.sync("id-00001")).thenReturn(Pair(arrayListOf<TrashData>(),12345678))

        Mockito.`when`(mockAPIAdapterImpl.update(eq("id-00001"),
            any(ArrayList::class.java) as ArrayList<TrashData>
            ,eq(123))).thenReturn(UpdateResult(400,-1))

        targetUseCase.syncData()

        Mockito.verify(mockPersistImpl, Mockito.times(1)).importScheduleList(any());
        Mockito.verify(mockConfigImpl,Mockito.times(1)).setTimestamp(capture(captorTimeStamp))
        Mockito.verify(mockConfigImpl,Mockito.times(1)).setSyncState(capture(captorSyncState))

        // DBのタイムスタンプでConfigが上書きされること
        Assert.assertEquals(12345678,captorTimeStamp.value)
        // Configの同期状態がSYNC_COMPLETEになること
        Assert.assertEquals(CalendarUseCase.SYNC_COMPLETE, captorSyncState.value)
    }

    @Test
    fun syncData_UpdateToDB() {
        // ローカル側のデータをDBに更新する
        val quiteLargeTimestamp:Long = 9999999999999
        Mockito.`when`(mockConfigImpl.getSyncState()).thenReturn(CalendarUseCase.SYNC_WAITING)
        Mockito.`when`(mockConfigImpl.getUserId()).thenReturn("id-00001")
        Mockito.`when`(mockConfigImpl.getTimeStamp()).thenReturn(quiteLargeTimestamp)
        Mockito.`when`(mockPersistImpl.getAllTrashSchedule()).thenReturn(arrayListOf(trash1,trash2))
        Mockito.`when`(mockAPIAdapterImpl.sync("id-00001")).thenReturn(Pair(arrayListOf<TrashData>(),quiteLargeTimestamp))
        Mockito.`when`(mockAPIAdapterImpl.update(eq("id-00001"),
            any(ArrayList::class.java) as ArrayList<TrashData>
        ,eq(quiteLargeTimestamp))).thenReturn(UpdateResult(200,quiteLargeTimestamp+1))

        targetUseCase.syncData()

        Mockito.verify(mockConfigImpl,Mockito.times(1)).setTimestamp(capture(captorTimeStamp))
        Mockito.verify(mockConfigImpl,Mockito.times(1)).setSyncState(capture(captorSyncState))

        // DBに更新したタイムスタンプでConfigのタイムスタンプが上書きされること
        Assert.assertEquals(quiteLargeTimestamp+1,captorTimeStamp.value)
        // Configの同期状態がSYNC_COMPLETEになること
        Assert.assertEquals(CalendarUseCase.SYNC_COMPLETE, captorSyncState.value)
    }
    private fun <T> any(clazz: Class<T>): T {
        return Mockito.any(clazz)
    }
    @Test
    fun syncData_Update_LocalSchedule_is_0() {
        // ローカルタイムスタンプ>DBタイムスタンプの場合でもローカルのデータが0件の場合はDBを更新しない
        val quiteLargeTimestamp:Long = 9999999999999
        Mockito.`when`(mockConfigImpl.getSyncState()).thenReturn(CalendarUseCase.SYNC_WAITING)
        Mockito.`when`(mockConfigImpl.getUserId()).thenReturn("id-00001")
        Mockito.`when`(mockConfigImpl.getTimeStamp()).thenReturn(quiteLargeTimestamp+1)
        Mockito.`when`(mockPersistImpl.getAllTrashSchedule()).thenReturn(arrayListOf())
        Mockito.`when`(mockAPIAdapterImpl.sync(eq("id-00001"))).thenReturn(Pair(arrayListOf<TrashData>(trash1,trash2),quiteLargeTimestamp))

        targetUseCase.syncData()
        // タイムスタンプは更新されない
        // Configの同期状態はSYNC_WATINGを維持する
        Mockito.verify(mockConfigImpl,Mockito.times(0)).setTimestamp(capture(captorTimeStamp))
        Mockito.verify(mockConfigImpl,Mockito.times(0)).setSyncState(capture(captorSyncState))
    }
}