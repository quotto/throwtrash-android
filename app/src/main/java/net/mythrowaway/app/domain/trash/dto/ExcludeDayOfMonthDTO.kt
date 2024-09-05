package net.mythrowaway.app.domain.trash.dto

class ExcludeDayOfMonthDTO(private val _month: Int, private val _dayOfMonth: Int) {
    val month: Int get() = this._month
    val dayOfMonth: Int get() = this._dayOfMonth
}