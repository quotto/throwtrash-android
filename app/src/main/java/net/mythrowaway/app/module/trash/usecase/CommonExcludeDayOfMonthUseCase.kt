package net.mythrowaway.app.module.trash.usecase

import net.mythrowaway.app.module.trash.dto.ExcludeDayOfMonthDTO
import net.mythrowaway.app.module.trash.dto.mapper.ExcludeDayOfMonthMapper
import net.mythrowaway.app.module.trash.entity.trash.ExcludeDayOfMonthList
import net.mythrowaway.app.module.trash.entity.trash.TrashList
import javax.inject.Inject

class CommonExcludeDayOfMonthUseCase @Inject constructor(
  private val trashRepository: TrashRepositoryInterface,
  private val syncRepository: SyncRepositoryInterface
) {
  fun getCommonExcludeDays(): List<ExcludeDayOfMonthDTO> {
    val trashList = trashRepository.getAllTrash()
    return trashList.globalExcludeDayOfMonthList.members.map { ExcludeDayOfMonthMapper.toDTO(it) }
  }

  fun saveCommonExcludeDays(excludes: List<ExcludeDayOfMonthDTO>) {
    val trashList = trashRepository.getAllTrash()
    val trimmed = excludes.take(ExcludeDayOfMonthList.MAX_SIZE)
    val excludeList = ExcludeDayOfMonthList(
      trimmed.map { ExcludeDayOfMonthMapper.toExcludeDayOfMonth(it) }.toMutableList()
    )
    val updated = TrashList(trashList.trashList, excludeList)
    trashRepository.replaceTrashList(updated)
    syncRepository.setSyncWait()
  }
}
