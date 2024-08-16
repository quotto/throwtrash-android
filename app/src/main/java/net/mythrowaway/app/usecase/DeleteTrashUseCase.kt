package net.mythrowaway.app.usecase

import javax.inject.Inject

class DeleteTrashUseCase @Inject constructor(
    private val persistence: DataRepositoryInterface
){
  fun deleteTrash(trashId: String) {
    val targetTrash = persistence.findTrashById(trashId)
    if (targetTrash != null) {
      persistence.deleteTrash(targetTrash)
    } else {
      throw IllegalArgumentException("Trash not found")
    }
  }
}