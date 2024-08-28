package net.mythrowaway.app.usecase

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.capture
import com.nhaarman.mockito_kotlin.eq
import net.mythrowaway.app.adapter.repository.UpdateResult
import net.mythrowaway.app.domain.ExcludeDayOfMonthList
import net.mythrowaway.app.domain.Trash
import net.mythrowaway.app.domain.TrashList
import net.mythrowaway.app.domain.TrashType
import net.mythrowaway.app.domain.WeeklySchedule
import net.mythrowaway.app.domain.sync.RegisteredInfo
import net.mythrowaway.app.domain.sync.RemoteTrash
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.*
import java.time.DayOfWeek
import java.time.LocalDate

class CalendarUseCaseTest {
  @Mock private lateinit var mockPersistImpl: DataRepositoryInterface
  @Mock private lateinit var mockConfigImpl: ConfigRepositoryInterface
  @Mock private lateinit var mockAPIAdapterImpl: MobileApiInterface

  @InjectMocks private lateinit var targetUseCase: CalendarUseCase

  @Captor private lateinit var captorId: ArgumentCaptor<String>
  @Captor private lateinit var captorTimeStamp: ArgumentCaptor<Long>

  private val trash1 = Trash(
    _id = "id-00001",
    _type = TrashType.BURN,
    _displayName = "",
    schedules = listOf(
      WeeklySchedule(_dayOfWeek = DayOfWeek.MONDAY),
      WeeklySchedule(_dayOfWeek = DayOfWeek.TUESDAY)
    ),
    _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
  )
  private val trash2 = Trash(
    _id = "id-00002",
    _type = TrashType.BOTTLE,
    schedules = listOf(
      WeeklySchedule(_dayOfWeek = DayOfWeek.MONDAY)
    ),
    _displayName = "",
    _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
  )

  private val mapper = ObjectMapper()
  init {
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    Mockito.`when`(mockPersistImpl.getAllTrash()).thenReturn(TrashList(listOf(trash1,trash2)))
  }

  @BeforeEach
  fun cleanTestData() {
    MockitoAnnotations.openMocks(this)
    Mockito.reset(mockConfigImpl)
    Mockito.reset(mockPersistImpl)
  }

  @Test
  fun generateMonthSchedule() {
//        val expectDay: List<Int> =
//            listOf(29,30,31,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,1)
    val start = LocalDate.parse("2019-12-29")
    val expect: MutableList<LocalDate> = mutableListOf()
    repeat(35) {
      expect.add(start.plusDays(it.toLong()))
    }

    // 1ヶ月（35日分）のゴミ出し予定リストを取得する
    val monthCalendarDTO = targetUseCase.getTrashCalendarOfMonth(2020,1)

    assertEquals(2020, monthCalendarDTO.baseYear)
    assertEquals(1, monthCalendarDTO.baseMonth)
    repeat(expect.size) {
      assertEquals(expect[it], monthCalendarDTO.calendarDayDTOS[it])
    }
  }

  @Test
  fun syncData_Register() {
    Mockito.`when`(mockConfigImpl.getSyncState()).thenReturn(CalendarUseCase.SYNC_WAITING)
    Mockito.`when`(mockConfigImpl.getUserId()).thenReturn(null)
    Mockito.`when`(mockAPIAdapterImpl.register(
      org.mockito.kotlin.any()
    )).thenReturn(RegisteredInfo(
      _userId = "id-00001",
      _latestTrashListUpdateTimestamp = 12345678
    )
    )
    targetUseCase.syncData()

    Mockito.verify(mockConfigImpl,Mockito.times(1)).setUserId(capture(captorId))
    Mockito.verify(mockConfigImpl,Mockito.times(1)).setTimestamp(capture(captorTimeStamp))
    Mockito.verify(mockConfigImpl,Mockito.times(1)).setSyncComplete()

    // configにuserIdが未登録の場合は新規にIDが発行される
    assertEquals("id-00001",captorId.value)
    assertEquals(12345678,captorTimeStamp.value)
  }

  @Test
  fun syncData_Init() {
    Mockito.`when`(mockConfigImpl.getSyncState()).thenReturn(CalendarUseCase.SYNC_NO)
    targetUseCase.syncData()

    // 初回起動時は何もしない
    Mockito.verify(mockConfigImpl,Mockito.times(0)).setUserId(capture(captorId))
    Mockito.verify(mockConfigImpl,Mockito.times(0)).setTimestamp(capture(captorTimeStamp))
    Mockito.verify(mockConfigImpl,Mockito.times(0)).setSyncComplete()
  }

  @Test
  fun syncData_SyncFromDB() {
    // ローカルタイムスタンプとDBタイムスタンプが一致しない場合にローカル側を最新化する
    Mockito.`when`(mockConfigImpl.getSyncState()).thenReturn(CalendarUseCase.SYNC_WAITING)
    Mockito.`when`(mockConfigImpl.getUserId()).thenReturn("id-00001")
    Mockito.`when`(mockConfigImpl.getTimeStamp()).thenReturn(123)
    Mockito.`when`(mockPersistImpl.getAllTrash()).thenReturn(TrashList(listOf(trash1,trash2)))
    Mockito.`when`(mockAPIAdapterImpl.getRemoteTrash("id-00001")).thenReturn(
      RemoteTrash(
        _trashList = TrashList(listOf(trash1,trash2)),
        _timestamp=12345678
      )
    )

    Mockito.`when`(mockAPIAdapterImpl.update(eq("id-00001"),
      org.mockito.kotlin.any()
      ,eq(123))).thenReturn(UpdateResult(400,-1))

    targetUseCase.syncData()

    Mockito.verify(mockPersistImpl, Mockito.times(1)).importScheduleList(any())
    Mockito.verify(mockConfigImpl,Mockito.times(1)).setTimestamp(capture(captorTimeStamp))
    Mockito.verify(mockConfigImpl,Mockito.times(1)).setSyncComplete()

    // DBのタイムスタンプでConfigが上書きされること
    assertEquals(12345678,captorTimeStamp.value)
  }

  @Test
  fun syncData_UpdateToDB() {
    // ローカル側のデータをDBに更新する
    val quiteLargeTimestamp = 9999999999999
    Mockito.`when`(mockConfigImpl.getSyncState()).thenReturn(CalendarUseCase.SYNC_WAITING)
    Mockito.`when`(mockConfigImpl.getUserId()).thenReturn("id-00001")
    Mockito.`when`(mockConfigImpl.getTimeStamp()).thenReturn(quiteLargeTimestamp)
    Mockito.`when`(mockPersistImpl.getAllTrash()).thenReturn(TrashList(listOf(trash1,trash2)))
    Mockito.`when`(mockAPIAdapterImpl.getRemoteTrash("id-00001")).thenReturn(
      RemoteTrash(
        _trashList = TrashList(listOf(trash1,trash2)),
        _timestamp = 12345678
      )
    )
    Mockito.`when`(mockAPIAdapterImpl.update(eq("id-00001"),
      org.mockito.kotlin.any()
      ,eq(quiteLargeTimestamp))).thenReturn(UpdateResult(200,quiteLargeTimestamp+1))

    targetUseCase.syncData()

    Mockito.verify(mockConfigImpl,Mockito.times(1)).setTimestamp(capture(captorTimeStamp))
    Mockito.verify(mockConfigImpl,Mockito.times(1)).setSyncComplete()

    // DBに更新したタイムスタンプでConfigのタイムスタンプが上書きされること
    assertEquals(quiteLargeTimestamp+1,captorTimeStamp.value)
  }
  @Test
  fun syncData_Update_LocalSchedule_is_0() {
    // ローカルタイムスタンプ>DBタイムスタンプの場合でもローカルのデータが0件の場合はDBを更新しない
    val quiteLargeTimestamp = 9999999999999
    Mockito.`when`(mockConfigImpl.getSyncState()).thenReturn(CalendarUseCase.SYNC_WAITING)
    Mockito.`when`(mockConfigImpl.getUserId()).thenReturn("id-00001")
    Mockito.`when`(mockConfigImpl.getTimeStamp()).thenReturn(quiteLargeTimestamp+1)
    Mockito.`when`(mockPersistImpl.getAllTrash()).thenReturn(TrashList(listOf()))
    Mockito.`when`(mockAPIAdapterImpl.getRemoteTrash(eq("id-00001"))).thenReturn(
      RemoteTrash(
        _trashList = TrashList(listOf(trash1,trash2)),
        _timestamp = 12345678
      )
    )

    targetUseCase.syncData()
    // タイムスタンプは更新されない
    // Configの同期状態はSYNC_WATINGを維持する
    Mockito.verify(mockConfigImpl,Mockito.times(0)).setTimestamp(capture(captorTimeStamp))
    Mockito.verify(mockConfigImpl,Mockito.times(0)).setSyncComplete()
  }
}