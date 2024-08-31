package net.mythrowaway.app.domain.trash.usecase.dto

import java.time.LocalDate

class IntervalWeeklyScheduleDTO(private val _startData: LocalDate, private val _dayOfWeek: Int, private val _interval: Int ): ScheduleDTO(){
    val startDate: LocalDate
        get() = _startData
    val dayOfWeek: Int
        get() = _dayOfWeek
  val interval: Int
    get() = _interval
}