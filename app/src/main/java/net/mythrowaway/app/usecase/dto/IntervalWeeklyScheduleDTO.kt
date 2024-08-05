package net.mythrowaway.app.usecase.dto

import net.mythrowaway.app.domain.IntervalWeeklySchedule
import net.mythrowaway.app.domain.Schedule
import java.time.LocalDate

class IntervalWeeklyScheduleDTO(private val _start: String, private val _dayOfWeek: Int, private val _interval: Int ): ScheduleDTO(){
    val start: String
        get() = _start
    val dayOfWeek: Int
        get() = _dayOfWeek
  val interval: Int
    get() = _interval
}