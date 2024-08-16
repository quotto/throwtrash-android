package net.mythrowaway.app.usecase

import net.mythrowaway.app.usecase.dto.TrashDTO
import net.mythrowaway.app.usecase.dto.mapper.TrashMapper
import javax.inject.Inject

class ListTrashesUseCase @Inject constructor(
    private val persistence: DataRepositoryInterface
){
  fun getTrashList(): List<TrashDTO> {
    return persistence.getAllTrash().trashList.map { TrashMapper.toTrashDTO(it) }
  }
}