package net.mythrowaway.app.domain.trash.dto

import java.time.DayOfWeek

class DTOUtil {
  companion object {
    fun dayOfWeekToInt(dayOfWeek: DayOfWeek): Int {
      return if (dayOfWeek == DayOfWeek.SUNDAY) 0 else dayOfWeek.value
    }

    fun intToDayOfWeek(dayOfWeekValue: Int): DayOfWeek {
      return if (dayOfWeekValue == 0) DayOfWeek.SUNDAY else DayOfWeek.of(dayOfWeekValue)
    }
  }
}