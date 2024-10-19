package net.mythrowaway.app.module.trash.presentation.view_model.edit.data

class IntervalWeeklyScheduleViewData(private val _start: String, private val _dayOfWeek: Int, private val _interval: Int ): ScheduleViewData(){
    override val scheduleType: String
        get() = ScheduleType.INTERVAL_WEEKLY.value
    val startDate: String
        get() = _start
    val dayOfWeek: Int
        get() = _dayOfWeek
  val interval: Int
    get() = _interval
}