package net.mythrowaway.app.module.trash.dto

class WeeklyScheduleDTO(private val _dayOfWeek: Int): ScheduleDTO() {

  val dayOfWeek: Int
        get() = _dayOfWeek
}