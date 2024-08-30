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

class ScheduleMapper {
  companion object {
    fun toDTO(schedule: net.mythrowaway.app.domain.Schedule): ScheduleDTO {
      return when (schedule) {
        is WeeklySchedule -> WeeklyScheduleDTO(DTOUtil.dayOfWeekToInt(schedule.dayOfWeek))
        is MonthlySchedule -> MonthlyScheduleDTO(schedule.day)
        is OrdinalWeeklySchedule -> OrdinalWeeklyScheduleDTO(
          schedule.ordinalOfWeek,
          DTOUtil.dayOfWeekToInt(schedule.dayOfWeek)
        )

        is IntervalWeeklySchedule -> IntervalWeeklyScheduleDTO(
          schedule.start,
          DTOUtil.dayOfWeekToInt(schedule.dayOfWeek),
          schedule.interval
        )

        else -> throw IllegalArgumentException("Invalid schedule type")
      }
    }

    fun toSchedule(scheduleDTO: ScheduleDTO): Schedule {
      return when (scheduleDTO) {
        is WeeklyScheduleDTO -> WeeklySchedule(DTOUtil.intToDayOfWeek(scheduleDTO.dayOfWeek))
        is MonthlyScheduleDTO -> MonthlySchedule(scheduleDTO.dayOfMonth)
        is OrdinalWeeklyScheduleDTO -> OrdinalWeeklySchedule(
          scheduleDTO.ordinal,
          DTOUtil.intToDayOfWeek(scheduleDTO.dayOfWeek)
        )

        is IntervalWeeklyScheduleDTO -> IntervalWeeklySchedule(
          scheduleDTO.startDate,
          DTOUtil.intToDayOfWeek(scheduleDTO.dayOfWeek),
          scheduleDTO.interval
        )

        else -> throw IllegalArgumentException("Invalid schedule type")
      }
    }
  }
}