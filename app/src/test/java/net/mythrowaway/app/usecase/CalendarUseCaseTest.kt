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
import net.mythrowaway.app.domain.TrashType
import net.mythrowaway.app.service.TrashManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.*

class CalendarUseCaseTest {
    @Mock private lateinit var mockPersistImpl: DataRepositoryInterface
    @Mock private lateinit var mockConfigImpl: ConfigRepositoryInterface
    @Mock private lateinit var mockAPIAdapterImpl: MobileApiInterface
    @Mock private lateinit var mockTrashManager: TrashManager

    @InjectMocks private lateinit var targetUseCase: CalendarUseCase

    @Captor private lateinit var captorYear: ArgumentCaptor<Int>
    @Captor private lateinit var captorMonth: ArgumentCaptor<Int>
    @Captor private lateinit var captorDateList: ArgumentCaptor<ArrayList<Int>>
    @Captor private lateinit var captorTrashList: ArgumentCaptor<Array<ArrayList<TrashData>>>
    @Captor private lateinit var captorId: ArgumentCaptor<String>
    @Captor private lateinit var captorTimeStamp: ArgumentCaptor<Long>
    @Captor private lateinit var captorSyncState: ArgumentCaptor<Int>

    private val trash1 = TrashData().apply {
        type = TrashType.BURN
        schedules = arrayListOf(TrashSchedule().apply{
            type = "weekday"
            value = "1"
        }, TrashSchedule().apply{
            type = "weekday"
            value = "2"
        })
    }
    private val trash2 = TrashData().apply {
        type = TrashType.BOTTLE
        schedules = arrayListOf(TrashSchedule().apply{
            type = "weekday"
            value = "1"
        })
    }

    private val mapper = ObjectMapper()
    init {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }

    @BeforeEach
    fun cleanTestData() {
        MockitoAnnotations.openMocks(this)
        Mockito.reset(mockConfigImpl)
        Mockito.reset(mockPersistImpl)
    }

    @Test
    fun generateMonthSchedule() {
        // 1ヶ月（35日分）のゴミ出し予定リストを取得する
        targetUseCase.getTrashCalendarOfMonth(2020,1)

        Mockito.verify(mockTrashManager,Mockito.times(1)).getEnableTrashList(
            capture(captorYear),capture(captorMonth), capture(captorDateList)
        )

        assertEquals(2020,captorYear.value)
        assertEquals(1,captorMonth.value)

        val expect: List<Int> = listOf(29,30,31,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,1)
        for (i in captorDateList.value.indices) {
            assert(captorDateList.value[i] == expect[i])
        }
    }

    @Test
    fun syncData_Register() {
        Mockito.`when`(mockConfigImpl.getSyncState()).thenReturn(CalendarUseCase.SYNC_WAITING)
        Mockito.`when`(mockConfigImpl.getUserId()).thenReturn(null)
        Mockito.`when`(mockAPIAdapterImpl.register(
            org.mockito.kotlin.any()
            )).thenReturn(RegisteredData().apply{id="id-00001";timestamp=12345678})
        targetUseCase.syncData()

        Mockito.verify(mockConfigImpl,Mockito.times(1)).setUserId(capture(captorId))
        Mockito.verify(mockConfigImpl,Mockito.times(1)).setTimestamp(capture(captorTimeStamp))
        Mockito.verify(mockConfigImpl,Mockito.times(1)).setSyncState(capture(captorSyncState))

        // configにuserIdが未登録の場合は新規にIDが発行される
        assertEquals("id-00001",captorId.value)
        assertEquals(12345678,captorTimeStamp.value)
        assertEquals(CalendarUseCase.SYNC_COMPLETE, captorSyncState.value)
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
            org.mockito.kotlin.any()
            ,eq(123))).thenReturn(UpdateResult(400,-1))

        targetUseCase.syncData()

        Mockito.verify(mockPersistImpl, Mockito.times(1)).importScheduleList(any())
        Mockito.verify(mockConfigImpl,Mockito.times(1)).setTimestamp(capture(captorTimeStamp))
        Mockito.verify(mockConfigImpl,Mockito.times(1)).setSyncState(capture(captorSyncState))

        // DBのタイムスタンプでConfigが上書きされること
        assertEquals(12345678,captorTimeStamp.value)
        // Configの同期状態がSYNC_COMPLETEになること
        assertEquals(CalendarUseCase.SYNC_COMPLETE, captorSyncState.value)
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
            org.mockito.kotlin.any()
        ,eq(quiteLargeTimestamp))).thenReturn(UpdateResult(200,quiteLargeTimestamp+1))

        targetUseCase.syncData()

        Mockito.verify(mockConfigImpl,Mockito.times(1)).setTimestamp(capture(captorTimeStamp))
        Mockito.verify(mockConfigImpl,Mockito.times(1)).setSyncState(capture(captorSyncState))

        // DBに更新したタイムスタンプでConfigのタイムスタンプが上書きされること
        assertEquals(quiteLargeTimestamp+1,captorTimeStamp.value)
        // Configの同期状態がSYNC_COMPLETEになること
        assertEquals(CalendarUseCase.SYNC_COMPLETE, captorSyncState.value)
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