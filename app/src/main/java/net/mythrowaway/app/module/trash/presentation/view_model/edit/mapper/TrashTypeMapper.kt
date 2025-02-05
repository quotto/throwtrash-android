package net.mythrowaway.app.module.trash.presentation.view_model.edit.mapper

import net.mythrowaway.app.module.trash.entity.trash.TrashType
import net.mythrowaway.app.module.trash.dto.TrashDTO
import net.mythrowaway.app.module.trash.presentation.view_model.edit.data.ExcludeDayOfMonthViewData
import net.mythrowaway.app.module.trash.presentation.view_model.edit.data.ScheduleViewData
import net.mythrowaway.app.module.trash.presentation.view_model.edit.data.TrashTypeViewData

class TrashTypeMapper {
  companion object {
    fun toViewData(trashDTO: TrashDTO): TrashTypeViewData {
      return TrashTypeViewData(
        trashDTO.type.toString(),
        trashDTO.type.getTrashText(),
        trashDTO.displayName
      )
    }

    fun toDTO(
      trashTypeViewData: TrashTypeViewData,
      id: String,
      scheduleViewDataList: List<ScheduleViewData>,
      excludeDayOfMonthViewDataList: List<ExcludeDayOfMonthViewData>
    ): TrashDTO {
      return TrashDTO(
        id,
        TrashType.valueOf(trashTypeViewData.type),
        trashTypeViewData.displayName,
        scheduleViewDataList.map { ScheduleMapper.toDTO(it) },
        excludeDayOfMonthViewDataList.map { ExcludeDayOfMonthMapper.toDTO(it) }
      )
    }
  }
}