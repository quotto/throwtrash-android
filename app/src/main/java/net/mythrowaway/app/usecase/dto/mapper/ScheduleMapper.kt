package net.mythrowaway.app.usecase.dto.mapper

import net.mythrowaway.app.domain.IntervalWeeklySchedule
import net.mythrowaway.app.domain.MonthlySchedule
import net.mythrowaway.app.domain.OrdinalWeeklySchedule
import net.mythrowaway.app.domain.Schedule
import net.mythrowaway.app.domain.WeeklySchedule
import net.mythrowaway.app.usecase.dto.DTOUtil
import net.mythrowaway.app.usecase.dto.IntervalWeeklyScheduleDTO
import net.mythrowaway.app.usecase.dto.MonthlyScheduleDTO
import net.mythrowaway.app.usecase.dto.OrdinalWeeklyScheduleDTO
import net.mythrowaway.app.usecase.dto.ScheduleDTO
import net.mythrowaway.app.usecase.dto.WeeklyScheduleDTO
import java.time.LocalDate

class ScheduleMapper {
  companion object {
    fun toDTO(schedule: Schedule): ScheduleDTO {
      return when (schedule) {
        is WeeklySchedule -> WeeklyScheduleDTO(DTOUtil.dayOfWeekToInt(schedule.dayOfWeek))
        is MonthlySchedule -> MonthlyScheduleDTO(schedule.day - 1)
        is OrdinalWeeklySchedule -> OrdinalWeeklyScheduleDTO(
          schedule.ordinalOfWeek - 1,
          DTOUtil.dayOfWeekToInt(schedule.dayOfWeek)
        )

        is IntervalWeeklySchedule -> IntervalWeeklyScheduleDTO(
          schedule.start.toString(),
          DTOUtil.dayOfWeekToInt(schedule.dayOfWeek),
          schedule.interval - 2
        )

        else -> throw IllegalArgumentException("Invalid schedule type")
      }
    }

    fun toSchedule(scheduleDTO: ScheduleDTO): Schedule {
      return when (scheduleDTO) {
        is WeeklyScheduleDTO -> WeeklySchedule(DTOUtil.intToDayOfWeek(scheduleDTO.dayOfWeek))
        is MonthlyScheduleDTO -> MonthlySchedule(scheduleDTO.dayOfMonth + 1)
        is OrdinalWeeklyScheduleDTO -> OrdinalWeeklySchedule(
          scheduleDTO.ordinal + 1,
          DTOUtil.intToDayOfWeek(scheduleDTO.dayOfWeek)
        )

        is IntervalWeeklyScheduleDTO -> IntervalWeeklySchedule(
          LocalDate.parse(scheduleDTO.start),
          DTOUtil.intToDayOfWeek(scheduleDTO.dayOfWeek),
          scheduleDTO.interval + 2
        )

        else -> throw IllegalArgumentException("Invalid schedule type")
      }
    }
  }
}