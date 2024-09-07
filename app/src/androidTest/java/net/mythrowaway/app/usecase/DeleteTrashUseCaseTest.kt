package net.mythrowaway.app.usecase

import androidx.preference.PreferenceManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import net.mythrowaway.app.domain.trash.entity.sync.SyncState
import net.mythrowaway.app.domain.trash.entity.trash.ExcludeDayOfMonthList
import net.mythrowaway.app.domain.trash.entity.trash.OrdinalWeeklySchedule
import net.mythrowaway.app.domain.trash.entity.trash.Trash
import net.mythrowaway.app.domain.trash.entity.trash.TrashType
import net.mythrowaway.app.domain.trash.infra.PreferenceSyncRepositoryImpl
import net.mythrowaway.app.domain.trash.infra.PreferenceTrashRepositoryImpl
import net.mythrowaway.app.domain.trash.usecase.DeleteTrashUseCase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.DayOfWeek

@RunWith(AndroidJUnit4::class)
class DeleteTrashUseCaseTest {
  private val trashRepository = PreferenceTrashRepositoryImpl(
    InstrumentationRegistry.getInstrumentation().context
  )
  private val syncRepository = PreferenceSyncRepositoryImpl(
    InstrumentationRegistry.getInstrumentation().context
  )
  private val useCase = DeleteTrashUseCase(
    syncRepository = syncRepository,
    persistence = trashRepository
  )

  private val preferences = PreferenceManager.getDefaultSharedPreferences(
    InstrumentationRegistry.getInstrumentation().context
  )

  @Before
  fun before() {
    preferences.edit().clear().commit()
  }

  @Test
  fun trash_list_is_empty_and_sync_state_is_wait_after_delete_when_has_single_trash_list() {
    val trash1 = Trash(
      _id = "0",
      _type = TrashType.BURN,
      schedules = listOf(
        OrdinalWeeklySchedule(_dayOfWeek = DayOfWeek.SUNDAY, _ordinalOfWeek = 3),
        OrdinalWeeklySchedule(_dayOfWeek = DayOfWeek.SATURDAY, _ordinalOfWeek = 1)
      ),
      _displayName = "",
      _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
    )
    trashRepository.saveTrash(trash1)

    useCase.deleteTrash("0")

    val trashList = trashRepository.getAllTrash()
    assertEquals(0,trashList.trashList.size)
    assertEquals(SyncState.Wait, syncRepository.getSyncState())
  }

  @Test
  fun trash_list_has_one_trash_and_sync_state_is_wait_after_delete_when_has_two_trash() {
    val trash1 = Trash(
      _id = "0",
      _type = TrashType.BURN,
      schedules = listOf(
        OrdinalWeeklySchedule(_dayOfWeek = DayOfWeek.SUNDAY, _ordinalOfWeek = 3),
        OrdinalWeeklySchedule(_dayOfWeek = DayOfWeek.SATURDAY, _ordinalOfWeek = 1)
      ),
      _displayName = "",
      _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
    )
    val trash2 = Trash(
      _id = "1",
      _type = TrashType.BURN,
      schedules = listOf(
        OrdinalWeeklySchedule(_dayOfWeek = DayOfWeek.SUNDAY, _ordinalOfWeek = 3),
        OrdinalWeeklySchedule(_dayOfWeek = DayOfWeek.SATURDAY, _ordinalOfWeek = 1)
      ),
      _displayName = "",
      _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
    )
    trashRepository.saveTrash(trash1)
    trashRepository.saveTrash(trash2)

    useCase.deleteTrash("0")

    val trashList = trashRepository.getAllTrash()
    assertEquals(1,trashList.trashList.size)
    assertEquals("1",trashList.trashList[0].id)
    assertEquals(SyncState.Wait, syncRepository.getSyncState())
  }

  @Test
  fun throw_exception_if_trash_id_is_not_found() {
    syncRepository.setSyncComplete()
    val trash1 = Trash(
      _id = "0",
      _type = TrashType.BURN,
      schedules = listOf(
        OrdinalWeeklySchedule(_dayOfWeek = DayOfWeek.SUNDAY, _ordinalOfWeek = 3),
        OrdinalWeeklySchedule(_dayOfWeek = DayOfWeek.SATURDAY, _ordinalOfWeek = 1)
      ),
      _displayName = "",
      _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
    )
    trashRepository.saveTrash(trash1)

    try {
      useCase.deleteTrash("1")
    } catch (e: IllegalArgumentException) {
      assertEquals("Trash not found", e.message)
      assertEquals(SyncState.Synced, syncRepository.getSyncState())
    }
  }
}