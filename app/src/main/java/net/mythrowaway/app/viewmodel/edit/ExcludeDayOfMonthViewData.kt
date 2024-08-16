package net.mythrowaway.app.viewmodel.edit

class ExcludeDayOfMonthViewData(private val _month: Int, private val _day: Int) {
    val day: Int
        get() = _day
    val month: Int
        get() = _month
}