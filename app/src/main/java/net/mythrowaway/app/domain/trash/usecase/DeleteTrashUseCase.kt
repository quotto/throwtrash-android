package net.mythrowaway.app.domain.trash.usecase

import javax.inject.Inject

class DeleteTrashUseCase @Inject constructor(
  private val syncRepository: SyncRepositoryInterface,
  private val persistence: TrashRepositoryInterface
){
  fun deleteTrash(trashId: String) {
    val targetTrash = persistence.findTrashById(trashId)
    if (targetTrash != null) {
      persistence.deleteTrash(targetTrash)
      syncRepository.setSyncWait()
    } else {
      throw IllegalArgumentException("Trash not found")
    }
  }
}