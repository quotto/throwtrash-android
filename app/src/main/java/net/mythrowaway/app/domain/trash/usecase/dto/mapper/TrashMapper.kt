package net.mythrowaway.app.domain.trash.usecase.dto.mapper

import net.mythrowaway.app.domain.trash.entity.trash.ExcludeDayOfMonth
import net.mythrowaway.app.domain.trash.entity.trash.ExcludeDayOfMonthList
import net.mythrowaway.app.domain.trash.entity.trash.Trash
import net.mythrowaway.app.domain.trash.usecase.dto.ExcludeDayOfMonthDTO
import net.mythrowaway.app.domain.trash.usecase.dto.TrashDTO

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
        trashDTO.scheduleViewData.map { ScheduleMapper.toSchedule(it) },
        ExcludeDayOfMonthList(
          trashDTO.excludeDayOfMonthDTOs.map { ExcludeDayOfMonth(it.month, it.dayOfMonth) }.toMutableList()
        )
      )
    }
  }
}