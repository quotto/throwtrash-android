package net.mythrowaway.app.domain.trash.presentation.view_model.edit.data

class MonthlyScheduleViewData(private val _day: Int): ScheduleViewData() {
    override val scheduleType: String
        get() = ScheduleType.MONTHLY.value
    val dayOfMonth: Int get() = this._day
}