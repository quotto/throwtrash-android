package net.mythrowaway.app.module.trash.dto.mapper

import net.mythrowaway.app.module.trash.entity.trash.IntervalWeeklySchedule
import net.mythrowaway.app.module.trash.entity.trash.MonthlySchedule
import net.mythrowaway.app.module.trash.entity.trash.OrdinalWeeklySchedule
import net.mythrowaway.app.module.trash.entity.trash.Schedule
import net.mythrowaway.app.module.trash.entity.trash.WeeklySchedule
import net.mythrowaway.app.module.trash.dto.DTOUtil
import net.mythrowaway.app.module.trash.dto.IntervalWeeklyScheduleDTO
import net.mythrowaway.app.module.trash.dto.MonthlyScheduleDTO
import net.mythrowaway.app.module.trash.dto.OrdinalWeeklyScheduleDTO
import net.mythrowaway.app.module.trash.dto.ScheduleDTO
import net.mythrowaway.app.module.trash.dto.WeeklyScheduleDTO

class ScheduleMapper {
  companion object {
    fun toDTO(schedule: Schedule): ScheduleDTO {
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