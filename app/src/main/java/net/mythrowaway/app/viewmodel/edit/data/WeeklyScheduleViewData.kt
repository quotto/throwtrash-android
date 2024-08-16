package net.mythrowaway.app.viewmodel.edit.data

class WeeklyScheduleViewData(private val _dayOfWeek: Int): ScheduleViewData() {
  override val scheduleType: String
    get() = ScheduleType.WEEKLY.value

  val dayOfWeek: Int
        get() = _dayOfWeek
}