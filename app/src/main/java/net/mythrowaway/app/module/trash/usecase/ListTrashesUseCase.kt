package net.mythrowaway.app.module.trash.usecase

import net.mythrowaway.app.module.trash.dto.TrashDTO
import net.mythrowaway.app.module.trash.dto.mapper.TrashMapper
import javax.inject.Inject

class ListTrashesUseCase @Inject constructor(
    private val persistence: TrashRepositoryInterface
){
  fun getTrashList(): List<TrashDTO> {
    return persistence.getAllTrash().trashList.map { TrashMapper.toTrashDTO(it) }
  }
}