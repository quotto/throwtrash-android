package net.mythrowaway.app.viewmodel.edit

enum class ScheduleType(val value: String) {
  MONTHLY("monthly"),
  WEEKLY("weekly"),
  ORDINAL_WEEKLY("ordinalWeekly"),
  INTERVAL_WEEKLY("intervalWeekly"),
}
abstract class ScheduleViewData {
  abstract val scheduleType: String;
}