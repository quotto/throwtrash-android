package net.mythrowaway.app.domain.trash.usecase.dto

class WeeklyScheduleDTO(private val _dayOfWeek: Int): ScheduleDTO() {

  val dayOfWeek: Int
        get() = _dayOfWeek
}