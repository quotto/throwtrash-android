package net.mythrowaway.app.usecase.dto

import java.time.DayOfWeek

class CalendarDayDTO(
  private val year: Int,
  private val month: Int,
  private val day: Int,
  private val dayOfWeek: DayOfWeek,
  private val trashes: List<TrashDTO>
) {
  fun getYear(): Int {
    return this.year
  }

  fun getMonth(): Int {
    return this.month
  }

  fun getDayOfMonth(): Int {
    return this.day
  }

  fun getDayOfWeek(): DayOfWeek {
    return this.dayOfWeek
  }

  fun getTrashes(): List<TrashDTO> {
    return this.trashes
  }
}