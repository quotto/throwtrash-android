package net.mythrowaway.app.domain.trash.presentation.view_model.edit.mapper

import net.mythrowaway.app.domain.trash.dto.IntervalWeeklyScheduleDTO
import net.mythrowaway.app.domain.trash.dto.MonthlyScheduleDTO
import net.mythrowaway.app.domain.trash.dto.OrdinalWeeklyScheduleDTO
import net.mythrowaway.app.domain.trash.dto.ScheduleDTO
import net.mythrowaway.app.domain.trash.dto.WeeklyScheduleDTO
import net.mythrowaway.app.domain.trash.presentation.view_model.edit.data.IntervalWeeklyScheduleViewData
import net.mythrowaway.app.domain.trash.presentation.view_model.edit.data.MonthlyScheduleViewData
import net.mythrowaway.app.domain.trash.presentation.view_model.edit.data.OrdinalWeeklyScheduleViewData
import net.mythrowaway.app.domain.trash.presentation.view_model.edit.data.ScheduleViewData
import net.mythrowaway.app.domain.trash.presentation.view_model.edit.data.WeeklyScheduleViewData
import java.time.LocalDate

class ScheduleMapper {
  companion object {
    fun toViewData(scheduleDTO: ScheduleDTO): ScheduleViewData {
      return when (scheduleDTO) {
        is WeeklyScheduleDTO -> WeeklyScheduleViewData(scheduleDTO.dayOfWeek)
        is MonthlyScheduleDTO -> MonthlyScheduleViewData(scheduleDTO.dayOfMonth - 1)
        is OrdinalWeeklyScheduleDTO -> OrdinalWeeklyScheduleViewData(
          scheduleDTO.ordinal - 1,
          scheduleDTO.dayOfWeek
        )
        is IntervalWeeklyScheduleDTO -> IntervalWeeklyScheduleViewData(
          scheduleDTO.startDate.toString(),
          scheduleDTO.dayOfWeek,
          scheduleDTO.interval - 2
        )
        else -> throw IllegalArgumentException("Invalid schedule type")
      }
    }

    fun toDTO(scheduleViewData: ScheduleViewData): ScheduleDTO {
      return when (scheduleViewData) {
        is WeeklyScheduleViewData -> WeeklyScheduleDTO(scheduleViewData.dayOfWeek)
        is MonthlyScheduleViewData -> MonthlyScheduleDTO(scheduleViewData.dayOfMonth + 1)
        is OrdinalWeeklyScheduleViewData -> OrdinalWeeklyScheduleDTO(
          scheduleViewData.ordinal + 1,
          scheduleViewData.dayOfWeek
        )
        is IntervalWeeklyScheduleViewData -> IntervalWeeklyScheduleDTO(
          LocalDate.parse(scheduleViewData.startDate),
          scheduleViewData.dayOfWeek,
          scheduleViewData.interval + 2
        )
        else -> throw IllegalArgumentException("Invalid schedule type")
      }
    }
  }
}