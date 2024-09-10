package net.mythrowaway.app.module.trash.presentation.view_model.edit.data

enum class ScheduleType(val value: String) {
  MONTHLY("monthly"),
  WEEKLY("weekly"),
  ORDINAL_WEEKLY("ordinalWeekly"),
  INTERVAL_WEEKLY("intervalWeekly"),
}
abstract class ScheduleViewData {
  abstract val scheduleType: String;
}