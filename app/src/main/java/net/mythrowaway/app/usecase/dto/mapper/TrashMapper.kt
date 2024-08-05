package net.mythrowaway.app.usecase.dto.mapper

import net.mythrowaway.app.domain.ExcludeDayOfMonth
import net.mythrowaway.app.domain.ExcludeDayOfMonthList
import net.mythrowaway.app.domain.Trash
import net.mythrowaway.app.usecase.dto.ExcludeDayDTO
import net.mythrowaway.app.usecase.dto.TrashDTO

class TrashMapper {
  companion object {
    fun toTrashDTO(trash: Trash): TrashDTO {
      return TrashDTO(
        trash.id,
        trash.type,
        trash.displayName,
        trash.schedules.map { ScheduleMapper.toDTO(it) },
        trash.excludeDayOfMonth.members.map { ExcludeDayDTO(it.month, it.dayOfMonth) }
      )
    }

    fun toTrash(trashDTO: TrashDTO): Trash {
      return Trash(
        trashDTO.id,
        trashDTO.type,
        trashDTO.displayName,
        trashDTO.scheduleDTOs.map { ScheduleMapper.toSchedule(it) },
        ExcludeDayOfMonthList(
          trashDTO.excludeDayOfMonthDTOs.map { ExcludeDayOfMonth(it.month, it.dayOfMonth) }.toMutableList()
        )
      )
    }
  }
}