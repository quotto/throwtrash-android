package net.mythrowaway.app.module.trash.dto

class OrdinalWeeklyScheduleDTO(private val _ordinal: Int, private val _dayOfWeek: Int): ScheduleDTO(){
  val ordinal: Int
        get() = _ordinal
  val dayOfWeek: Int
        get() = _dayOfWeek
}