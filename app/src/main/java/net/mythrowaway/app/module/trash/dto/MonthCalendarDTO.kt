package net.mythrowaway.app.module.trash.dto

class MonthCalendarDTO (
  private val _baseYear: Int,
  private val _baseMonth: Int,
  private val _calendarDayDTOS: List<CalendarDayDTO>
) {
  val baseYear: Int get() = this._baseYear
  val baseMonth: Int get() = this._baseMonth
  val calendarDayDTOS: List<CalendarDayDTO> get() = this._calendarDayDTOS
}