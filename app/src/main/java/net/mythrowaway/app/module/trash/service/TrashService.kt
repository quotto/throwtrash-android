package net.mythrowaway.app.module.trash.service

import net.mythrowaway.app.module.trash.usecase.SyncRepositoryInterface
import net.mythrowaway.app.module.trash.usecase.TrashRepositoryInterface
import net.mythrowaway.app.module.trash.dto.TrashDTO
import net.mythrowaway.app.module.trash.dto.mapper.TrashMapper
import net.mythrowaway.app.module.trash.entity.trash.TrashList
import net.mythrowaway.app.module.trash.usecase.MobileApiInterface
import net.mythrowaway.app.module.trash.usecase.ResetTrashUseCase
import java.time.LocalDate
import javax.inject.Inject

class TrashService @Inject constructor(
  private val trashRepository: TrashRepositoryInterface,
  private val syncRepository: SyncRepositoryInterface,
  private val resetTrashUseCase: ResetTrashUseCase,
) {
  fun findTrashInDay(year: Int, month: Int, date: Int): List<TrashDTO> {
    val targetDate = LocalDate.of(year, month, date)
    return trashRepository.getAllTrash().trashList.filter {
      it.isTrashDay(targetDate)
    }.map {
      TrashMapper.toTrashDTO(it)
    }
  }

  fun updateSyncTime(timestamp: Long) {
    syncRepository.setTimestamp(timestamp)
  }

  fun reset() {
    resetTrashUseCase.resetTrashData()
  }
}