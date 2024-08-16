package net.mythrowaway.app.usecase.dto

class WeeklyScheduleDTO(private val _dayOfWeek: Int): ScheduleDTO() {

  val dayOfWeek: Int
        get() = _dayOfWeek
}