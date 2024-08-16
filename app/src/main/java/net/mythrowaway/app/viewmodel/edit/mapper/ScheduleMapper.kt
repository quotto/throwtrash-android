package net.mythrowaway.app.viewmodel.edit.mapper

import net.mythrowaway.app.usecase.dto.IntervalWeeklyScheduleDTO
import net.mythrowaway.app.usecase.dto.MonthlyScheduleDTO
import net.mythrowaway.app.usecase.dto.OrdinalWeeklyScheduleDTO
import net.mythrowaway.app.usecase.dto.WeeklyScheduleDTO
import net.mythrowaway.app.viewmodel.edit.data.IntervalWeeklyScheduleViewData
import net.mythrowaway.app.viewmodel.edit.data.MonthlyScheduleViewData
import net.mythrowaway.app.viewmodel.edit.data.OrdinalWeeklyScheduleViewData
import net.mythrowaway.app.viewmodel.edit.data.ScheduleViewData
import net.mythrowaway.app.viewmodel.edit.data.WeeklyScheduleViewData
import java.time.LocalDate

class ScheduleMapper {
  companion object {
    fun toViewData(scheduleDTO: net.mythrowaway.app.usecase.dto.ScheduleDTO): ScheduleViewData {
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

    fun toDTO(scheduleViewData: ScheduleViewData): net.mythrowaway.app.usecase.dto.ScheduleDTO {
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