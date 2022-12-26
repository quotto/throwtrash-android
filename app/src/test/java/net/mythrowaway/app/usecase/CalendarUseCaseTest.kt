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
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@Suppress("UNCHECKED_CAST")
@RunWith(PowerMockRunner::class)
@PrepareForTest(
    TrashManager::class
)
class CalendarUseCaseTest {
    @Mock private lateinit var mockPresenter: CalendarPresenterInterface
    @Mock private lateinit var mockPersistImpl: DataRepositoryInterface
    @Mock private lateinit var mockConfigImpl: ConfigRepositoryInterface
    @Mock private lateinit var mockAPIAdapterImpl: MobileApiInterface
    @Mock private lateinit var mockTrashManager: TrashManager

    @InjectMocks private lateinit var targetUseCase: CalendarUseCase

    @Captor private lateinit var captorYear: ArgumentCaptor<Int>
    @Captor private lateinit var captorMonth: ArgumentCaptor<Int>
    @Captor private lateinit var captorDateList: ArgumentCaptor<ArrayList<Int>>
    @Captor private lateinit var captorTrashList: ArgumentCaptor<Array<ArrayList<String>>>
    @Captor private lateinit var captorId: ArgumentCaptor<String>
    @Captor private lateinit var captorTimeStamp: ArgumentCaptor<Long>
    @Captor private lateinit var captorSyncState: ArgumentCaptor<Int>

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
        Mockito.`when`(mockAPIAdapterImpl.sync("id-00001")).thenReturn(Pair(arrayListOf(),12345678))

        Mockito.`when`(mockAPIAdapterImpl.update(eq("id-00001"),
            any(ArrayList::class.java) as ArrayList<TrashData>
            ,eq(123))).thenReturn(UpdateResult(400,-1))

        targetUseCase.syncData()

        Mockito.verify(mockPersistImpl, Mockito.times(1)).importScheduleList(any())
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
        Mockito.`when`(mockAPIAdapterImpl.sync("id-00001")).thenReturn(Pair(arrayListOf(),quiteLargeTimestamp))
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
        Mockito.`when`(mockAPIAdapterImpl.sync(eq("id-00001"))).thenReturn(Pair(arrayListOf(trash1,trash2),quiteLargeTimestamp))

        targetUseCase.syncData()
        // タイムスタンプは更新されない
        // Configの同期状態はSYNC_WATINGを維持する
        Mockito.verify(mockConfigImpl,Mockito.times(0)).setTimestamp(capture(captorTimeStamp))
        Mockito.verify(mockConfigImpl,Mockito.times(0)).setSyncState(capture(captorSyncState))
    }
}