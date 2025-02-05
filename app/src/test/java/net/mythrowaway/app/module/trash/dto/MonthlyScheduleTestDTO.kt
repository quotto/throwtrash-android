package net.mythrowaway.app.module.trash.dto

import net.mythrowaway.app.module.trash.entity.trash.MonthlySchedule
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate

class MonthlyScheduleTestDTO {
  @Test
  fun same_dayOfMonth_on_january_first_is_trashDay() {
    // Given
    val monthlySchedule = MonthlySchedule(1)
    val date = LocalDate.of(2021, 1, 1)
    // When
    val actual = monthlySchedule.isTrashDay(date)
    // Then
    Assertions.assertTrue(actual)
  }

  @Test
  fun different_dayOfMonth_on_january_first_is_not_trashDay() {
    // Given
    val monthlySchedule = MonthlySchedule(1)
    val date = LocalDate.of(2021, 1, 2)
    // When
    val actual = monthlySchedule.isTrashDay(date)
    // Then
    Assertions.assertTrue(!actual)
  }

  @Test
  fun same_dayOfMonth_on_december_thirtyOne_is_trashDay() {
    // Given
    val monthlySchedule = MonthlySchedule(31)
    val date = LocalDate.of(2021, 12, 31)
    // When
    val actual = monthlySchedule.isTrashDay(date)
    // Then
    Assertions.assertTrue(actual)
  }

  @Test
  fun december_thirtyOne_is_not_trashDay_in_monthlySchedule_with_day_of_30() {
    // Given
    val monthlySchedule = MonthlySchedule(30)
    val date = LocalDate.of(2021, 12, 31)
    // When
    val actual = monthlySchedule.isTrashDay(date)
    // Then
    Assertions.assertTrue(!actual)
  }

  @Test
  fun december_first_is_not_trashDay_in_monthlySchedule_with_day_of_2() {
    // Given
    val monthlySchedule = MonthlySchedule(2)
    val date = LocalDate.of(2021, 12, 1)
    // When
    val actual = monthlySchedule.isTrashDay(date)
    // Then
    Assertions.assertTrue(!actual)
  }

  @Test
  fun day_of_0_is_illegal() {
    Assertions.assertThrows(IllegalArgumentException::class.java) {
      MonthlySchedule(0)
    }
  }

  @Test
  fun day_of_32_is_illegal() {
    Assertions.assertThrows(IllegalArgumentException::class.java) {
      MonthlySchedule(32)
    }
  }
}