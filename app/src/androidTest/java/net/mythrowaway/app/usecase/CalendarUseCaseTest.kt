package net.mythrowaway.app.usecase

import androidx.preference.PreferenceManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import net.mythrowaway.app.domain.info.infra.PreferenceUserRepositoryImpl
import net.mythrowaway.app.domain.info.service.UserIdService
import net.mythrowaway.app.domain.info.usecase.InformationUseCase
import net.mythrowaway.app.domain.trash.infra.UpdateResult
import net.mythrowaway.app.domain.trash.entity.trash.ExcludeDayOfMonthList
import net.mythrowaway.app.domain.trash.entity.trash.Trash
import net.mythrowaway.app.domain.trash.entity.trash.TrashList
import net.mythrowaway.app.domain.trash.entity.trash.TrashType
import net.mythrowaway.app.domain.trash.entity.trash.WeeklySchedule
import net.mythrowaway.app.domain.trash.entity.sync.RegisteredInfo
import net.mythrowaway.app.domain.trash.entity.sync.RemoteTrash
import net.mythrowaway.app.domain.trash.entity.sync.SyncState
import net.mythrowaway.app.domain.trash.infra.PreferenceSyncRepositoryImpl
import net.mythrowaway.app.domain.trash.infra.PreferenceTrashRepositoryImpl
import net.mythrowaway.app.domain.trash.usecase.CalendarSyncResult
import net.mythrowaway.app.domain.trash.usecase.CalendarUseCase
import net.mythrowaway.app.domain.trash.usecase.MobileApiInterface
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import java.time.DayOfWeek
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class CalendarUseCaseTest {
  @Mock private lateinit var mockAPIAdapterImpl: MobileApiInterface

  private lateinit var usecase: CalendarUseCase

  private val trashRepository = PreferenceTrashRepositoryImpl(
    InstrumentationRegistry.getInstrumentation().context
  )
  private val userRepository = PreferenceUserRepositoryImpl(
    InstrumentationRegistry.getInstrumentation().context
  )
  private val userIdService = UserIdService(
    useCase = InformationUseCase(
      userRepository = userRepository
    )
  )
  private val syncRepository = PreferenceSyncRepositoryImpl(
    InstrumentationRegistry.getInstrumentation().context
  )

  private val preferences by lazy {
    PreferenceManager.getDefaultSharedPreferences(
      InstrumentationRegistry.getInstrumentation().context
    )
  }

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
  }

  @Before
  fun before() {
    MockitoAnnotations.openMocks(this)
    Mockito.reset(mockAPIAdapterImpl)

    preferences.edit().clear().commit()

    usecase = CalendarUseCase(
      persist = trashRepository,
      userIdService = userIdService,
      syncRepository = syncRepository,
      apiAdapter = mockAPIAdapterImpl
    )
  }

  @Test
  fun calendar_start_at_2019_12_29_end_on_2020_02_01() {
    trashRepository.saveTrash(
      Trash(
        _id = "id-00001",
        _type = TrashType.BURN,
        _displayName = "",
        schedules = listOf(
          WeeklySchedule(_dayOfWeek = DayOfWeek.MONDAY),
          WeeklySchedule(_dayOfWeek = DayOfWeek.TUESDAY)
        ),
        _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
      )
    )
    trashRepository.saveTrash(
      Trash(
        _id = "id-00002",
        _type = TrashType.BOTTLE,
        schedules = listOf(
          WeeklySchedule(_dayOfWeek = DayOfWeek.MONDAY)
        ),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
      )
    )
    val start = LocalDate.parse("2019-12-29")
    val expect: MutableList<LocalDate> = mutableListOf()
    repeat(35) {
      expect.add(start.plusDays(it.toLong()))
    }

    // 1ヶ月（35日分）のゴミ出し予定リストを取得する
    val monthCalendarDTO = usecase.getTrashCalendarOfMonth(2020,1)

    assertEquals(2020, monthCalendarDTO.baseYear)
    assertEquals(1, monthCalendarDTO.baseMonth)
    repeat(expect.size) {
      assertEquals(
        expect[it].year,
        monthCalendarDTO.calendarDayDTOS[it].getYear()
      )
      assertEquals(
        expect[it].month.value,
        monthCalendarDTO.calendarDayDTOS[it].getMonth()
      )
      assertEquals(
        expect[it].dayOfMonth,
        monthCalendarDTO.calendarDayDTOS[it].getDayOfMonth()
      )
      assertEquals(
        expect[it].dayOfWeek,
        monthCalendarDTO.calendarDayDTOS[it].getDayOfWeek()
      )
      when (it) {
        1, 8, 15, 22, 29 -> {
          assertEquals(2, monthCalendarDTO.calendarDayDTOS[it].getTrashes().size)
          assertEquals(trash1.id, monthCalendarDTO.calendarDayDTOS[it].getTrashes()[0].id)
          assertEquals(trash2.id, monthCalendarDTO.calendarDayDTOS[it].getTrashes()[1].id)
        }
        2, 9, 16, 23, 30 -> {
          assertEquals(1, monthCalendarDTO.calendarDayDTOS[it].getTrashes().size)
          assertEquals(trash1.id, monthCalendarDTO.calendarDayDTOS[it].getTrashes()[0].id)
        }
        else -> {
          assertEquals(0, monthCalendarDTO.calendarDayDTOS[it].getTrashes().size)
        }
      }
    }
  }

  @Test
  fun register_when_userId_is_null() {
    syncRepository.setSyncWait()
    Mockito.`when`(mockAPIAdapterImpl.register(
      any()
    )).thenReturn(
      RegisteredInfo(
      _userId = "id-00001",
      _latestTrashListUpdateTimestamp = 12345678
    )
    )
    val result = usecase.syncData()

    // configにuserIdが未登録の場合は新規にIDが発行される
    assertEquals("id-00001",userRepository.getUserId())
    assertEquals(12345678,  syncRepository.getTimeStamp())
    assertEquals(SyncState.Synced, syncRepository.getSyncState())
    assertEquals(CalendarSyncResult.PUSH_SUCCESS, result)
  }

  @Test
  fun nothing_to_sync_when_sync_state_is_not_init() {
    val result = usecase.syncData()

    // 初回起動時は何もしない
    assertEquals(null, userRepository.getUserId())
    assertEquals(0, syncRepository.getTimeStamp())
    assertEquals(SyncState.NotInit, syncRepository.getSyncState())
    assertEquals(CalendarSyncResult.NONE, result)
  }

  @Test
  fun update_local_trash_and_timestamp_when_remote_timestamp_is_greater_than_local() {
    // ローカルタイムスタンプとDBタイムスタンプが一致しない場合にローカル側を最新化する
    syncRepository.setSyncWait()
    userRepository.saveUserId("id-00001")
    syncRepository.setTimestamp(123)
    trashRepository.saveTrash(trash1)
    trashRepository.saveTrash(trash2)
    Mockito.`when`(mockAPIAdapterImpl.update(eq("id-00001"),
      any()
      ,eq(123))).thenReturn(UpdateResult(400,-1))
    val remoteTrash = Trash(
      _id = "id-00001",
      _type = TrashType.BURN,
      _displayName = "",
      schedules = listOf(
        WeeklySchedule(_dayOfWeek = DayOfWeek.MONDAY),
        WeeklySchedule(_dayOfWeek = DayOfWeek.TUESDAY)
      ),
      _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())

    )
    Mockito.`when`(mockAPIAdapterImpl.getRemoteTrash("id-00001")).thenReturn(
      RemoteTrash(
        _trashList = TrashList(listOf(remoteTrash)),
        _timestamp=12345678
      )
    )
    val result = usecase.syncData()
    assertEquals(CalendarSyncResult.PULL_SUCCESS, result)
    assertEquals(12345678, syncRepository.getTimeStamp())
    assertEquals(SyncState.Synced, syncRepository.getSyncState())
    val localTrashList = trashRepository.getAllTrash()
    assertEquals(1, localTrashList.trashList.size)
    assertEquals(remoteTrash.id, localTrashList.trashList[0].id)
    assertEquals(remoteTrash.type, localTrashList.trashList[0].type)
    assertEquals(remoteTrash.displayName, localTrashList.trashList[0].displayName)
    assertEquals(remoteTrash.schedules.size, localTrashList.trashList[0].schedules.size)
    assertEquals(remoteTrash.excludeDayOfMonth.members.size, localTrashList.trashList[0].excludeDayOfMonth.members.size)
  }

  @Test
  fun renew_local_timestamp_when_remote_timestamp_is_less_than_local() {
    // ローカル側のデータをDBに更新する
    val quiteLargeTimestamp = 9999999999999
    syncRepository.setTimestamp(quiteLargeTimestamp)
    syncRepository.setSyncWait()
    userRepository.saveUserId("id-00001")
    trashRepository.saveTrash(trash1)
    trashRepository.saveTrash(trash2)
    val remoteTrash = Trash(
      _id = "id-00001",
      _type = TrashType.BURN,
      _displayName = "",
      schedules = listOf(
        WeeklySchedule(_dayOfWeek = DayOfWeek.MONDAY),
        WeeklySchedule(_dayOfWeek = DayOfWeek.TUESDAY)
      ),
      _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
    )
    Mockito.`when`(mockAPIAdapterImpl.getRemoteTrash("id-00001")).thenReturn(
      RemoteTrash(
        _trashList = TrashList(listOf(remoteTrash)),
        _timestamp = 12345678
      )
    )
    Mockito.`when`(mockAPIAdapterImpl.update(eq("id-00001"),
      any()
      ,eq(quiteLargeTimestamp))).thenReturn(UpdateResult(200,quiteLargeTimestamp+1))

    val result = usecase.syncData()
    assertEquals(CalendarSyncResult.PUSH_SUCCESS, result)
    assertEquals(quiteLargeTimestamp+1, syncRepository.getTimeStamp())
    assertEquals(SyncState.Synced, syncRepository.getSyncState())
    val localTrashList = trashRepository.getAllTrash()
    assertEquals(2, localTrashList.trashList.size)
    assertEquals(trash1.id, localTrashList.trashList[0].id)
    assertEquals(trash2.id, localTrashList.trashList[1].id)

  }
  @Test
  fun not_update_trash_and_timestamp_and_sync_state_when_if_remote_timestamp_less_than_local_but_local_trash_list_is_empty() {
    // ローカルタイムスタンプ>DBタイムスタンプの場合でもローカルのデータが0件の場合はDBを更新しない
    val quiteLargeTimestamp = 9999999999999
    syncRepository.setTimestamp(quiteLargeTimestamp)
    syncRepository.setSyncWait()
    userRepository.saveUserId("id-00001")

    val result = usecase.syncData()
    assertEquals(CalendarSyncResult.NONE, result)
    assertEquals(quiteLargeTimestamp, syncRepository.getTimeStamp())
    assertEquals(SyncState.Wait, syncRepository.getSyncState())
  }

  @Test
  fun not_update_sync_state_when_update_failed() {
    val quiteLargeTimestamp = 9999999999999
    syncRepository.setTimestamp(quiteLargeTimestamp)
    syncRepository.setSyncWait()
    userRepository.saveUserId("id-00001")
    trashRepository.saveTrash(trash1)
    trashRepository.saveTrash(trash2)
    Mockito.`when`(mockAPIAdapterImpl.update(eq("id-00001"),
      any()
      ,eq(quiteLargeTimestamp))).thenReturn(UpdateResult(500,-1))

    val result = usecase.syncData()
    assertEquals(CalendarSyncResult.PENDING, result)
    assertEquals(quiteLargeTimestamp, syncRepository.getTimeStamp())
    assertEquals(SyncState.Wait, syncRepository.getSyncState())
  }
}