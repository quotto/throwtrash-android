package net.mythrowaway.app.module.trash.service

import net.mythrowaway.app.module.trash.usecase.SyncRepositoryInterface
import net.mythrowaway.app.module.trash.usecase.TrashRepositoryInterface
import net.mythrowaway.app.module.trash.dto.TrashDTO
import net.mythrowaway.app.module.trash.dto.mapper.TrashMapper
import java.time.LocalDate
import javax.inject.Inject

class TrashService @Inject constructor(
  private val trashRepository: TrashRepositoryInterface,
  private val syncRepository: SyncRepositoryInterface
) {
  fun findTrashInDay(year: Int, month: Int, date: Int): List<TrashDTO> {
    val targetDate = LocalDate.of(year, month, date)
    return trashRepository.getAllTrash().findTrashByDate(targetDate).map {
      TrashMapper.toTrashDTO(it)
    }
  }

  fun updateSyncTime(timestamp: Long) {
    syncRepository.setTimestamp(timestamp)
  }

  fun migrateTrashScheduleFormat() {
    // 旧形式の保存データを読み込み直して新形式で保存する
    val trashList = trashRepository.getAllTrash()
    trashRepository.replaceTrashList(trashList)
  }
}
