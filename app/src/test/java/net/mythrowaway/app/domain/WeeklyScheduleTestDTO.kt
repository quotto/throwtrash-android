package net.mythrowaway.app.domain

import net.mythrowaway.app.domain.WeeklySchedule
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate

class WeeklyScheduleTestDTO {
  @Test
  fun date_of_2021_01_04_is_monday_and_is_trashDay() {
    // Given
    val weeklySchedule = WeeklySchedule(DayOfWeek.MONDAY)
    val date = LocalDate.of(2021, 1, 4)
    // When
    val actual = weeklySchedule.isTrashDay(date)
    // Then
    Assertions.assertTrue(actual)
  }

  @Test
  fun date_of_2021_01_05_is_not_monday_and_is_not_trashDay() {
    // Given
    val weeklySchedule = WeeklySchedule(DayOfWeek.MONDAY)
    val date = LocalDate.of(2021, 1, 5)
    // When
    val actual = weeklySchedule.isTrashDay(date)
    // Then
    Assertions.assertTrue(!actual)
  }
}