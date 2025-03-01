package net.mythrowaway.app.module.trash.usecase

import net.mythrowaway.app.module.trash.entity.trash.TrashList
import javax.inject.Inject

class ResetTrashUseCase @Inject constructor (
  private val syncRepository: SyncRepositoryInterface,
  private val trashRepository: TrashRepositoryInterface,
) {
  fun resetTrashData() {
    syncRepository.setSyncWait()
    syncRepository.setTimestamp(0)
    trashRepository.replaceTrashList(TrashList(listOf()))
  }
}