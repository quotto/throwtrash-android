package net.mythrowaway.app.domain

import java.time.LocalDate

class ExcludeDayOfMonth(private val month: Int, private val dayOfMonth: Int) {

    companion object {
        private const val MIN_MONTH = 1
        private const val MAX_MONTH = 12
        private const val MIN_DAY_OF_MONTH = 1
        private val MAX_DAY_OF_MONTH_LIST =  listOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    }

    init {
        if (month < MIN_MONTH || month > MAX_MONTH) throw IllegalArgumentException("月の指定に誤りがあります")
        if (dayOfMonth < MIN_DAY_OF_MONTH || dayOfMonth > MAX_DAY_OF_MONTH_LIST[month - 1]) throw IllegalArgumentException("日の指定に誤りがあります")
    }
    fun isExcluded(date: LocalDate): Boolean {
        return date.monthValue == month && date.dayOfMonth == dayOfMonth
    }
}