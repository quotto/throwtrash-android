package net.mythrowaway.app.usecase.dto

import net.mythrowaway.app.domain.OrdinalWeeklySchedule
import net.mythrowaway.app.domain.Schedule
import java.time.DayOfWeek

class OrdinalWeeklyScheduleDTO(private val _ordinal: Int, private val _dayOfWeek: Int): ScheduleDTO(){
  val ordinal: Int
        get() = _ordinal
  val dayOfWeek: Int
        get() = _dayOfWeek
}