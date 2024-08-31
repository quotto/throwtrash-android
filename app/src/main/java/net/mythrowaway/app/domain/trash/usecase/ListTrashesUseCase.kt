package net.mythrowaway.app.domain.trash.usecase

import net.mythrowaway.app.usecase.TrashRepositoryInterface
import net.mythrowaway.app.domain.trash.usecase.dto.TrashDTO
import net.mythrowaway.app.domain.trash.usecase.dto.mapper.TrashMapper
import javax.inject.Inject

class ListTrashesUseCase @Inject constructor(
    private val persistence: TrashRepositoryInterface
){
  fun getTrashList(): List<TrashDTO> {
    return persistence.getAllTrash().trashList.map { TrashMapper.toTrashDTO(it) }
  }
}