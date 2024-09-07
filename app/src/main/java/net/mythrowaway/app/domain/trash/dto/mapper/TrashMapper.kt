package net.mythrowaway.app.domain.trash.dto.mapper

import net.mythrowaway.app.domain.trash.entity.trash.ExcludeDayOfMonth
import net.mythrowaway.app.domain.trash.entity.trash.ExcludeDayOfMonthList
import net.mythrowaway.app.domain.trash.entity.trash.Trash
import net.mythrowaway.app.domain.trash.dto.ExcludeDayOfMonthDTO
import net.mythrowaway.app.domain.trash.dto.TrashDTO

class TrashMapper {
  companion object {
    fun toTrashDTO(trash: Trash): TrashDTO {
      return TrashDTO(
        trash.id,
        trash.type,
        trash.displayName,
        trash.schedules.map { ScheduleMapper.toDTO(it) },
        trash.excludeDayOfMonth.members.map { ExcludeDayOfMonthDTO(it.month, it.dayOfMonth) }
      )
    }

    fun toTrash(trashDTO: TrashDTO): Trash {
      return Trash(
        trashDTO.id,
        trashDTO.type,
        trashDTO.displayName,
        trashDTO.scheduleDTOList.map { ScheduleMapper.toSchedule(it) },
        ExcludeDayOfMonthList(
          trashDTO.excludeDayOfMonthDTOList.map { ExcludeDayOfMonth(it.month, it.dayOfMonth) }.toMutableList()
        )
      )
    }
  }
}