package net.mythrowaway.app.domain.trash.presentation.view_model.edit.data

class ExcludeDayOfMonthViewData(private val _month: Int, private val _day: Int) {
    val day: Int
        get() = _day
    val month: Int
        get() = _month
}