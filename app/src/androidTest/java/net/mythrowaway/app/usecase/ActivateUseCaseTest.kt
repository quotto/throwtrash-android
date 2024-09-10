package net.mythrowaway.app.usecase

import androidx.preference.PreferenceManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.nhaarman.mockito_kotlin.any
import net.mythrowaway.app.module.info.infra.PreferenceUserRepositoryImpl
import net.mythrowaway.app.module.trash.entity.sync.RegisteredInfo
import net.mythrowaway.app.module.trash.entity.trash.ExcludeDayOfMonthList
import net.mythrowaway.app.module.trash.entity.trash.Trash
import net.mythrowaway.app.module.trash.entity.trash.TrashList
import net.mythrowaway.app.module.trash.entity.trash.TrashType
import net.mythrowaway.app.module.trash.entity.trash.WeeklySchedule
import net.mythrowaway.app.module.trash.entity.sync.RemoteTrash
import net.mythrowaway.app.module.trash.entity.sync.SyncState
import net.mythrowaway.app.module.trash.infra.PreferenceSyncRepositoryImpl
import net.mythrowaway.app.module.trash.infra.PreferenceTrashRepositoryImpl
import net.mythrowaway.app.module.trash.usecase.ActivateUseCase
import net.mythrowaway.app.module.trash.usecase.MobileApiInterface
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import java.time.DayOfWeek

@RunWith(AndroidJUnit4::class)
class ActivateUseCaseTest {
  @Mock private lateinit var mockAPIAdapterImpl: MobileApiInterface

  private val trashRepository = PreferenceTrashRepositoryImpl(
    InstrumentationRegistry.getInstrumentation().context
  )

  private val userRepository = PreferenceUserRepositoryImpl(
    InstrumentationRegistry.getInstrumentation().context
  )

  private val syncRepository = PreferenceSyncRepositoryImpl(
    InstrumentationRegistry.getInstrumentation().context
  )

  private lateinit var instance: ActivateUseCase

  private val preferences = PreferenceManager.getDefaultSharedPreferences(
    InstrumentationRegistry.getInstrumentation().context
  )
  @Before
  fun before() {
    MockitoAnnotations.openMocks(this)
    Mockito.reset(mockAPIAdapterImpl)

    preferences.edit().clear().commit()

    instance = ActivateUseCase(
      mockAPIAdapterImpl,
      trashRepository,
      userRepository,
      syncRepository
    )
  }

  @Test
  fun replace_trash_list_and_timestamp_and_set_sync_state_to_wait_if_user_id_exists_and_activate_success() {
    userRepository.saveUserId("id001")

    trashRepository.saveTrash(
      Trash(
        _id = "id999",
        _type = TrashType.BURN,
        _displayName = "",
        schedules = listOf(
          WeeklySchedule(
            _dayOfWeek = DayOfWeek.MONDAY
          )
        ),
        _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
      )

    )

    Mockito.`when`(mockAPIAdapterImpl.activate("12345678910", "id001")).thenReturn(
      RemoteTrash(
        _timestamp = 1234567890L,
        _trashList = TrashList(listOf(
          Trash(
            _id = "id001",
            _type = TrashType.BURN,
            _displayName = "",
            schedules = listOf(
              WeeklySchedule(
                _dayOfWeek = DayOfWeek.MONDAY
              )
            ),
            _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
          )
        ))
      )
    )

    val result = instance.activate("12345678910")

    assertEquals(ActivateUseCase.ActivationResult.ACTIVATE_SUCCESS, result)

    assertEquals(1234567890, syncRepository.getTimeStamp())

    assertEquals(SyncState.Wait, syncRepository.getSyncState())

    val trashList = trashRepository.getAllTrash()
    assertEquals(1,trashList.trashList.size)
    assertEquals("id001",trashList.trashList[0].id)
  }

  @Test
  fun register_user_id_and_set_sync_state_to_wait_and_update_timestamp_and_replace_trash_list_if_user_id_is_empty_and_activate_success() {
    Mockito.`when`(mockAPIAdapterImpl.register(any())).thenReturn(
      RegisteredInfo(
        _userId = "id001",
        _latestTrashListUpdateTimestamp = 11111111L
      )
    )

    Mockito.`when`(mockAPIAdapterImpl.activate("12345678910", "id001")).thenReturn(
      RemoteTrash(
        _timestamp = 1234567890L,
        _trashList = TrashList(listOf(
          Trash(
            _id = "id001",
            _type = TrashType.BURN,
            _displayName = "",
            schedules = listOf(
              WeeklySchedule(
                _dayOfWeek = DayOfWeek.MONDAY
              )
            ),
            _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
          )
        ))
      )
    )

    val result = instance.activate("12345678910")

    assertEquals(ActivateUseCase.ActivationResult.ACTIVATE_SUCCESS, result)

    assertEquals("id001", userRepository.getUserId())

    assertEquals(1234567890, syncRepository.getTimeStamp())

    assertEquals(SyncState.Wait, syncRepository.getSyncState())

    val trashList = trashRepository.getAllTrash()
    assertEquals(1,trashList.trashList.size)
    assertEquals("id001",trashList.trashList[0].id)
  }

  @Test
  fun return_activate_error_if_activate_failed() {
    userRepository.saveUserId("id001")
    syncRepository.setSyncComplete()
    syncRepository.setTimestamp(0L)
    trashRepository.saveTrash(
      Trash(
        _id = "id999",
        _type = TrashType.BURN,
        _displayName = "",
        schedules = listOf(
          WeeklySchedule(
            _dayOfWeek = DayOfWeek.MONDAY
          )
        ),
        _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
      )

    )

    Mockito.`when`(mockAPIAdapterImpl.activate("12345678910", "id001")).thenThrow(
      RuntimeException("Failed to activate")
    )

    val result = instance.activate("12345678910")

    assertEquals(ActivateUseCase.ActivationResult.ACTIVATE_ERROR, result)

    assertEquals(0L, syncRepository.getTimeStamp())

    assertEquals(SyncState.Synced, syncRepository.getSyncState())

    val trashList = trashRepository.getAllTrash()
    assertEquals(1,trashList.trashList.size)
    assertEquals("id999",trashList.trashList[0].id)
  }

  @Test
  fun return_activate_error_if_user_id_is_empty_and_register_failed() {
    Mockito.`when`(mockAPIAdapterImpl.register(any())).thenThrow(
      RuntimeException("Failed to register")
    )

    val result = instance.activate("12345678910")

    assertEquals(ActivateUseCase.ActivationResult.ACTIVATE_ERROR, result)

    assertEquals(null, userRepository.getUserId())

    assertEquals(SyncState.NotInit, syncRepository.getSyncState())

    val trashList = trashRepository.getAllTrash()
    assertEquals(0,trashList.trashList.size)
  }
}