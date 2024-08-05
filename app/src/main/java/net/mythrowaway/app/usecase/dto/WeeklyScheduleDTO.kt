package net.mythrowaway.app.usecase.dto

import net.mythrowaway.app.domain.WeeklySchedule
import java.time.DayOfWeek

class WeeklyScheduleDTO(private val _dayOfWeek: Int): ScheduleDTO() {

  val dayOfWeek: Int
        get() = _dayOfWeek
}