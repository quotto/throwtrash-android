package net.mythrowaway.app.domain

import java.time.LocalDate

class ExcludeDayOfMonth(private val _month: Int, private val _dayOfMonth: Int) {

    val month: Int get() = _month
    val dayOfMonth: Int get() = _dayOfMonth
    companion object {
        private const val MIN_MONTH = 1
        private const val MAX_MONTH = 12
        private const val MIN_DAY_OF_MONTH = 1
        private val MAX_DAY_OF_MONTH_LIST =  listOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    }

    init {
        if (_month < MIN_MONTH || _month > MAX_MONTH) throw IllegalArgumentException("月の指定に誤りがあります")
        if (_dayOfMonth < MIN_DAY_OF_MONTH || _dayOfMonth > MAX_DAY_OF_MONTH_LIST[_month - 1]) throw IllegalArgumentException("日の指定に誤りがあります")
    }
    fun isExcluded(date: LocalDate): Boolean {
        return date.monthValue == _month && date.dayOfMonth == _dayOfMonth
    }
}