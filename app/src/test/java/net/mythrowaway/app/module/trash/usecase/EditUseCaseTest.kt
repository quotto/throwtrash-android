package net.mythrowaway.app.module.trash.usecase

import net.mythrowaway.app.module.trash.dto.IntervalWeeklyScheduleDTO
import net.mythrowaway.app.module.trash.dto.MonthlyScheduleDTO
import net.mythrowaway.app.module.trash.dto.OrdinalWeeklyScheduleDTO
import net.mythrowaway.app.module.trash.dto.WeeklyScheduleDTO
import net.mythrowaway.app.module.trash.entity.sync.SyncState
import net.mythrowaway.app.module.trash.entity.trash.ExcludeDayOfMonth
import net.mythrowaway.app.module.trash.entity.trash.ExcludeDayOfMonthList
import net.mythrowaway.app.module.trash.entity.trash.IntervalWeeklySchedule
import net.mythrowaway.app.module.trash.entity.trash.MonthlySchedule
import net.mythrowaway.app.module.trash.entity.trash.OrdinalWeeklySchedule
import net.mythrowaway.app.module.trash.entity.trash.Trash
import net.mythrowaway.app.module.trash.entity.trash.TrashList
import net.mythrowaway.app.module.trash.entity.trash.TrashType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate

class EditUseCaseTest {
  @Nested
  inner class CopyTrash {
    @Test
    fun copied_trash_has_new_id_and_same_values_without_saving() {
      val originalTrash = Trash(
        _id = "original-id",
        _type = TrashType.OTHER,
        _displayName = "家電",
        schedules = listOf(
          MonthlySchedule(_day = 3),
          OrdinalWeeklySchedule(_dayOfWeek = DayOfWeek.THURSDAY, _ordinalOfWeek = 3),
          IntervalWeeklySchedule(
            _dayOfWeek = DayOfWeek.FRIDAY,
            _interval = 3,
            _start = LocalDate.of(2026, 5, 1)
          )
        ),
        _excludeDayOfMonth = ExcludeDayOfMonthList(
          mutableListOf(
            ExcludeDayOfMonth(_month = 1, _dayOfMonth = 1),
            ExcludeDayOfMonth(_month = 12, _dayOfMonth = 31)
          )
        )
      )
      val trashRepository = FakeTrashRepository(TrashList(listOf(originalTrash)))
      val syncRepository = FakeSyncRepository()
      val useCase = EditUseCase(syncRepository, trashRepository)

      val copiedTrash = useCase.copyTrashById("original-id")

      assertNotNull(copiedTrash)
      copiedTrash!!
      assertNotEquals("original-id", copiedTrash.id)
      assertEquals(TrashType.OTHER, copiedTrash.type)
      assertEquals("家電", copiedTrash.displayName)
      assertEquals(3, copiedTrash.scheduleDTOList.size)
      assertEquals(3, (copiedTrash.scheduleDTOList[0] as MonthlyScheduleDTO).dayOfMonth)
      assertEquals(3, (copiedTrash.scheduleDTOList[1] as OrdinalWeeklyScheduleDTO).ordinal)
      assertEquals(4, (copiedTrash.scheduleDTOList[1] as OrdinalWeeklyScheduleDTO).dayOfWeek)
      assertEquals(3, (copiedTrash.scheduleDTOList[2] as IntervalWeeklyScheduleDTO).interval)
      assertEquals(5, (copiedTrash.scheduleDTOList[2] as IntervalWeeklyScheduleDTO).dayOfWeek)
      assertEquals(LocalDate.of(2026, 5, 1), (copiedTrash.scheduleDTOList[2] as IntervalWeeklyScheduleDTO).startDate)
      assertEquals(2, copiedTrash.excludeDayOfMonthDTOList.size)
      assertEquals(1, copiedTrash.excludeDayOfMonthDTOList[0].month)
      assertEquals(1, copiedTrash.excludeDayOfMonthDTOList[0].dayOfMonth)
      assertEquals(12, copiedTrash.excludeDayOfMonthDTOList[1].month)
      assertEquals(31, copiedTrash.excludeDayOfMonthDTOList[1].dayOfMonth)
      assertEquals(1, trashRepository.getAllTrash().trashList.size)
      assertEquals("original-id", trashRepository.getAllTrash().trashList[0].id)
      assertEquals(SyncState.Synced, syncRepository.getSyncState())
    }

    @Test
    fun return_null_when_original_trash_is_not_found() {
      val trashRepository = FakeTrashRepository(TrashList(listOf()))
      val useCase = EditUseCase(FakeSyncRepository(), trashRepository)

      val copiedTrash = useCase.copyTrashById("missing-id")

      assertNull(copiedTrash)
    }
  }
}

private class FakeTrashRepository(
  private var trashList: TrashList
) : TrashRepositoryInterface {
  override fun saveTrash(trash: Trash) {
    trashList.addTrash(trash)
  }

  override fun findTrashById(id: String): Trash? {
    return trashList.trashList.firstOrNull { it.id == id }
  }

  override fun deleteTrash(trash: Trash) {
    trashList.removeTrash(trash)
  }

  override fun getAllTrash(): TrashList {
    return trashList
  }

  override fun replaceTrashList(trashList: TrashList) {
    this.trashList = trashList
  }
}

private class FakeSyncRepository : SyncRepositoryInterface {
  private var syncState: SyncState = SyncState.Synced
  override fun getSyncState(): SyncState = syncState
  override fun getTimeStamp(): Long = 0
  override fun setTimestamp(timestamp: Long) = Unit
  override fun setSyncWait() {
    syncState = SyncState.Wait
  }

  override fun setSyncComplete() {
    syncState = SyncState.Synced
  }
}
