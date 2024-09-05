package net.mythrowaway.app.domain.trash.service

import net.mythrowaway.app.domain.trash.usecase.SyncRepositoryInterface
import net.mythrowaway.app.domain.trash.usecase.TrashRepositoryInterface
import net.mythrowaway.app.domain.trash.dto.TrashDTO
import net.mythrowaway.app.domain.trash.dto.mapper.TrashMapper
import java.time.LocalDate
import javax.inject.Inject

class TrashService @Inject constructor(
  private val trashRepository: TrashRepositoryInterface,
  private val syncRepository: SyncRepositoryInterface
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
}