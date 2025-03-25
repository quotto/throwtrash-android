package net.mythrowaway.app.usecase

import androidx.preference.PreferenceManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.times
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.mythrowaway.app.module.account.infra.PreferenceUserRepositoryImpl
import net.mythrowaway.app.module.account.service.AuthService
import net.mythrowaway.app.module.account.service.UserIdService
import net.mythrowaway.app.module.account.usecase.AccountUseCase
import net.mythrowaway.app.module.account.usecase.AuthManagerInterface
import net.mythrowaway.app.module.account.usecase.UserApiInterface
import net.mythrowaway.app.module.trash.infra.UpdateResult
import net.mythrowaway.app.module.trash.entity.trash.ExcludeDayOfMonthList
import net.mythrowaway.app.module.trash.entity.trash.Trash
import net.mythrowaway.app.module.trash.entity.trash.TrashList
import net.mythrowaway.app.module.trash.entity.trash.TrashType
import net.mythrowaway.app.module.trash.entity.trash.WeeklySchedule
import net.mythrowaway.app.module.trash.entity.sync.RegisteredInfo
import net.mythrowaway.app.module.trash.entity.sync.RemoteTrash
import net.mythrowaway.app.module.trash.entity.sync.SyncState
import net.mythrowaway.app.module.trash.infra.PreferenceSyncRepositoryImpl
import net.mythrowaway.app.module.trash.infra.PreferenceTrashRepositoryImpl
import net.mythrowaway.app.module.trash.service.TrashService
import net.mythrowaway.app.module.trash.usecase.CalendarSyncResult
import net.mythrowaway.app.module.trash.usecase.CalendarUseCase
import net.mythrowaway.app.module.trash.usecase.MobileApiInterface
import net.mythrowaway.app.module.trash.usecase.ResetTrashUseCase
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
  @Mock private lateinit var mockUserApi: UserApiInterface
  @Mock private lateinit var mockAuthManager: AuthManagerInterface

  private lateinit var usecase: CalendarUseCase

  private val trashRepository = PreferenceTrashRepositoryImpl(
    InstrumentationRegistry.getInstrumentation().context
  )
  private val userRepository = PreferenceUserRepositoryImpl(
    InstrumentationRegistry.getInstrumentation().context
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
    Mockito.reset(mockAuthManager)

    runBlocking {
      Mockito.`when`(mockAuthManager.getIdToken(any())).thenReturn(Result.success("dummy-token"))
    }

    preferences.edit().clear().commit()

    usecase = CalendarUseCase(
      persist = trashRepository,
      userIdService = UserIdService(
        useCase = AccountUseCase(
          userRepository = userRepository,
          userApi = mockUserApi,
          authManager = mockAuthManager,
          trashService = TrashService(
            trashRepository = trashRepository,
            syncRepository = syncRepository,
            resetTrashUseCase = ResetTrashUseCase(
              syncRepository = syncRepository,
              trashRepository = trashRepository
            ),
          ),
        )
      ),
      syncRepository = syncRepository,
      apiAdapter = mockAPIAdapterImpl,
      authService = AuthService(
        usecase = mockAuthManager
      )
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

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun register_and_update_remote_db_when_userId_is_null_and_sync_is_wait() = runTest {
    syncRepository.setSyncWait()
    val localTrashData1 = Trash(
        _id = "id-00001",
        _type = TrashType.BURN,
        _displayName = "",
        schedules = listOf(
          WeeklySchedule(_dayOfWeek = DayOfWeek.MONDAY),
          WeeklySchedule(_dayOfWeek = DayOfWeek.TUESDAY)
        ),
        _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
      )
    trashRepository.saveTrash(localTrashData1)
    Mockito.`when`(mockAPIAdapterImpl.register(
      eq("dummy-token")
    )).thenReturn(
      RegisteredInfo(
      _userId = "id-00001",
      _latestTrashListUpdateTimestamp = 12345678
    ))
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
    Mockito.`when`(mockAPIAdapterImpl.getRemoteTrash("id-00001", "dummy-token")).thenReturn(
      RemoteTrash(
        _trashList = TrashList(listOf()),
        _timestamp=12345678
      )
    )
    Mockito.`when`(mockAPIAdapterImpl.update(eq("id-00001"),
      any()
      ,eq(12345678),
      eq("dummy-token"))
    ).thenReturn(UpdateResult(200,12345679))

    lateinit var result: CalendarSyncResult
    launch {
      result = usecase.syncData()
    }

    advanceUntilIdle()

    // configにuserIdが未登録の場合は新規にIDが発行される
    assertEquals(CalendarSyncResult.PUSH_SUCCESS, result)
    assertEquals("id-00001",userRepository.getUserId())
    Mockito.verify(mockAPIAdapterImpl, times(1)).update(eq("id-00001"),
      any()
      ,eq(12345678),
      eq("dummy-token"))
    assertEquals(12345679,  syncRepository.getTimeStamp())
    assertEquals(SyncState.Synced, syncRepository.getSyncState())
    val localTrashList = trashRepository.getAllTrash()
    assertEquals(1, localTrashList.trashList.size)
    assertEquals(remoteTrash.id, localTrashList.trashList[0].id)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun only_register_and_set_local_timestamp_when_user_id_is_null_and_sync_state_is_not_init() = runTest {
    Mockito.`when`(mockAPIAdapterImpl.register(
      eq("dummy-token")
    )).thenReturn(
      RegisteredInfo(
        _userId = "id-00001",
        _latestTrashListUpdateTimestamp = 12345678
      ))
    lateinit var result: CalendarSyncResult
    launch {
      result = usecase.syncData()
    }

    advanceUntilIdle()

    assertEquals(CalendarSyncResult.NONE, result)
    assertEquals("id-00001", userRepository.getUserId())
    assertEquals(12345678, syncRepository.getTimeStamp())
    assertEquals(SyncState.NotInit, syncRepository.getSyncState())
    Mockito.verify(mockAPIAdapterImpl, times(0)).update(any(), any(),any(), eq("dummy-token"))
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun update_local_trash_and_timestamp_when_remote_timestamp_is_greater_than_local() = runTest {
    // ローカルタイムスタンプとDBタイムスタンプが一致しない場合にローカル側を最新化する
    syncRepository.setSyncWait()
    userRepository.saveUserId("id-00001")
    syncRepository.setTimestamp(123)
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
    Mockito.`when`(mockAPIAdapterImpl.getRemoteTrash("id-00001", "dummy-token")).thenReturn(
      RemoteTrash(
        _trashList = TrashList(listOf(remoteTrash)),
        _timestamp=12345678
      )
    )

    lateinit var result: CalendarSyncResult
    launch {
      result = usecase.syncData()
    }
    advanceUntilIdle()

    Mockito.verify(mockAPIAdapterImpl, times(0)).update(any(), any(),any(), eq("dummy-token"))
    Mockito.verify(mockAPIAdapterImpl, times(1)).getRemoteTrash(eq("id-00001"), eq("dummy-token"))
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

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun update_local_trash_and_timestamp_when_remote_timestamp_is_less_than_local() = runTest {
    // ローカルタイムスタンプとDBタイムスタンプが一致しない場合にローカル側を最新化する
    syncRepository.setSyncWait()
    userRepository.saveUserId("id-00001")
    syncRepository.setTimestamp(12345678)
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
    Mockito.`when`(mockAPIAdapterImpl.getRemoteTrash("id-00001", "dummy-token")).thenReturn(
      RemoteTrash(
        _trashList = TrashList(listOf(remoteTrash)),
        _timestamp = 12345677
      )
    )

    lateinit var result: CalendarSyncResult
    launch {
      result = usecase.syncData()
    }
    advanceUntilIdle()

    Mockito.verify(mockAPIAdapterImpl, times(0)).update(any(), any(),any(), eq("dummy-token"))
    Mockito.verify(mockAPIAdapterImpl, times(1)).getRemoteTrash(eq("id-00001"), eq("dummy-token"))

    assertEquals(CalendarSyncResult.PULL_SUCCESS, result)
    assertEquals(12345677, syncRepository.getTimeStamp())
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
  fun update_remote_trash_and_renew_local_timestamp_when_remote_timestamp_equal_to_local_timestamp() = runTest {
    // リモートとローカルのタイムスタンプが一致する場合はリモートのデータを更新してローカルのタイムスタンプを更新する
    syncRepository.setTimestamp(12345678)
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
    Mockito.`when`(mockAPIAdapterImpl.getRemoteTrash(
      eq("id-00001"), eq("dummy-token"))).thenReturn(
      RemoteTrash(
        _trashList = TrashList(listOf(remoteTrash)),
        _timestamp = 12345678
      )
    )
    Mockito.`when`(mockAPIAdapterImpl.update(eq("id-00001"),
      any()
      ,eq(12345678),
      eq("dummy-token"))).thenReturn(UpdateResult(200,12345679))

    val result = usecase.syncData()

    Mockito.verify(mockAPIAdapterImpl, times(1)).update(eq("id-00001"),
      any()
      ,eq(12345678),
      eq("dummy-token"))
    assertEquals(CalendarSyncResult.PUSH_SUCCESS, result)
    assertEquals(12345679, syncRepository.getTimeStamp())
    assertEquals(SyncState.Synced, syncRepository.getSyncState())
    val localTrashList = trashRepository.getAllTrash()
    assertEquals(2, localTrashList.trashList.size)
    assertEquals(trash1.id, localTrashList.trashList[0].id)
    assertEquals(trash2.id, localTrashList.trashList[1].id)
  }
  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun update_trash_and_timestamp_and_sync_state_when_if_remote_timestamp_equal_to_local_and_local_trash_list_is_empty() = runTest {
    // ローカルタイムスタンプ=DBタイムスタンプの場合でもローカルのデータが0件の場合でもDBのデータを更新する
    syncRepository.setTimestamp(12345678)
    syncRepository.setSyncWait()
    userRepository.saveUserId("id-00001")
    Mockito.`when`(mockAPIAdapterImpl.getRemoteTrash(eq("id-00001"), eq("dummy-token"))).thenReturn(
      RemoteTrash(
        _trashList = TrashList(listOf()),
        _timestamp = 12345678
      )
    )
    Mockito.`when`(mockAPIAdapterImpl.update(eq("id-00001"),
      any()
      ,eq(12345678),
      eq("dummy-token"))).thenReturn(UpdateResult(200,12345679))

    lateinit var result: CalendarSyncResult
    launch {
      result = usecase.syncData()
    }
    advanceUntilIdle()

    Mockito.verify(mockAPIAdapterImpl, times(1)).update(eq("id-00001"),
      any()
      ,eq(12345678),
      eq("dummy-token"))
    assertEquals(CalendarSyncResult.PUSH_SUCCESS, result)
    assertEquals(12345679, syncRepository.getTimeStamp())
    assertEquals(SyncState.Synced, syncRepository.getSyncState())
    val localTrashList = trashRepository.getAllTrash()
    assertEquals(0, localTrashList.trashList.size)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun not_update_sync_state_when_update_failed() = runTest {
    syncRepository.setTimestamp(12345678)
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
    Mockito.`when`(mockAPIAdapterImpl.getRemoteTrash(eq("id-00001"), eq("dummy-token"))).thenReturn(
      RemoteTrash(
        _trashList = TrashList(listOf(remoteTrash)),
        _timestamp = 12345678
      )
    )
    Mockito.`when`(mockAPIAdapterImpl.update(eq("id-00001"),
      any()
      ,eq(12345678),
      eq("dummy-token"))).thenReturn(UpdateResult(500,-1))


    lateinit var result: CalendarSyncResult
    launch {
      result = usecase.syncData()
    }
    advanceUntilIdle()

    assertEquals(CalendarSyncResult.PENDING, result)
    assertEquals(12345678, syncRepository.getTimeStamp())
    assertEquals(SyncState.Wait, syncRepository.getSyncState())
  }
}