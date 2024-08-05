package net.mythrowaway.app.usecase.dto

import net.mythrowaway.app.domain.ExcludeDayOfMonth

class ExcludeDayDTO(private val _month: Int, private val _dayOfMonth: Int) {
    val month: Int get() = this._month
    val dayOfMonth: Int get() = this._dayOfMonth
}