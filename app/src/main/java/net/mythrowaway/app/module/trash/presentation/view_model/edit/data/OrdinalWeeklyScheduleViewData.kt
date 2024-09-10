package net.mythrowaway.app.module.trash.presentation.view_model.edit.data

class OrdinalWeeklyScheduleViewData(private val _ordinal: Int, private val _dayOfWeek: Int): ScheduleViewData(){
  override val scheduleType: String
    get() = ScheduleType.ORDINAL_WEEKLY.value
  val ordinal: Int
        get() = _ordinal
  val dayOfWeek: Int
        get() = _dayOfWeek
}