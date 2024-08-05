package net.mythrowaway.app.usecase.dto

class MonthlyScheduleDTO(private val _dayOfMonth: Int): ScheduleDTO(){
    val dayOfMonth: Int
        get() = _dayOfMonth
}