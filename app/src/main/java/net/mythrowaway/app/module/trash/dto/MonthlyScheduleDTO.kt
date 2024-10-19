package net.mythrowaway.app.module.trash.dto

class MonthlyScheduleDTO(private val _dayOfMonth: Int): ScheduleDTO(){
    val dayOfMonth: Int
        get() = _dayOfMonth
}