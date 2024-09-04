package net.mythrowaway.app.domain

import net.mythrowaway.app.domain.trash.entity.trash.OrdinalWeeklySchedule
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate

class OrdinalWeeklyScheduleTestDTO {
  @Test
  fun date_of_2020_01_19_is_third_sunday_and_is_trashDay() {
    val specificWeeklySchedule = OrdinalWeeklySchedule(3, DayOfWeek.SUNDAY)
    val date = LocalDate.of(2020, 1, 19)

    val actual = specificWeeklySchedule.isTrashDay(date)

    Assertions.assertTrue(actual)
  }
  @Test
  fun date_of_2020_01_20_is_third_monday_and_is_not_trashDay() {
    val specificWeeklySchedule = OrdinalWeeklySchedule(3, DayOfWeek.SUNDAY)
    val date = LocalDate.of(2020, 1, 20)

    val actual = specificWeeklySchedule.isTrashDay(date)

    Assertions.assertTrue(!actual)
  }

  @Test
  fun date_of_2020_01_12_is_second_sunday_and_is_not_trashDay() {
    val specificWeeklySchedule = OrdinalWeeklySchedule(3, DayOfWeek.SUNDAY)
    val date = LocalDate.of(2020, 1, 12)

    val actual = specificWeeklySchedule.isTrashDay(date)

    Assertions.assertFalse(actual)
  }

  @Test
  fun date_of_2020_01_26_is_fourth_sunday_and_is_trashDay() {
    val specificWeeklySchedule = OrdinalWeeklySchedule(4, DayOfWeek.SUNDAY)
    val date = LocalDate.of(2020, 1, 26)

    val actual = specificWeeklySchedule.isTrashDay(date)

    Assertions.assertTrue(actual)
  }

  @Test
  fun date_of_2020_01_04_is_first_saturday_and_is_trashDay() {
    val specificWeeklySchedule = OrdinalWeeklySchedule(1, DayOfWeek.SATURDAY)
    val date = LocalDate.of(2020, 1, 4)

    val actual = specificWeeklySchedule.isTrashDay(date)

    Assertions.assertTrue(actual)
  }

  @Test
  fun date_of_2020_01_11_is_second_saturday_and_is_trashDay() {
    val specificWeeklySchedule = OrdinalWeeklySchedule(2, DayOfWeek.SATURDAY)
    val date = LocalDate.of(2020, 1, 11)

    val actual = specificWeeklySchedule.isTrashDay(date)

    Assertions.assertTrue(actual)
  }

  @Test
  fun date_of_2020_01_01_is_first_wednesday_and_is_trashDay() {
    val specificWeeklySchedule = OrdinalWeeklySchedule(1, DayOfWeek.WEDNESDAY)
    val date = LocalDate.of(2020, 1, 1)

    val actual = specificWeeklySchedule.isTrashDay(date)

    Assertions.assertTrue(actual)
  }

  @Test
  fun date_of_2020_01_29_is_fifth_wednesday_and_is_trashDay() {
    val specificWeeklySchedule = OrdinalWeeklySchedule(5, DayOfWeek.WEDNESDAY)
    val date = LocalDate.of(2020, 1, 29)

    val actual = specificWeeklySchedule.isTrashDay(date)

    Assertions.assertTrue(actual)
  }

  @Test
  fun ordinal_week_of_0_is_illegal() {
    Assertions.assertThrows(IllegalArgumentException::class.java) {
      OrdinalWeeklySchedule(0, DayOfWeek.SUNDAY)
    }
  }

  @Test
  fun ordinal_week_of_6_is_illegal() {
    Assertions.assertThrows(IllegalArgumentException::class.java) {
      OrdinalWeeklySchedule(6, DayOfWeek.SUNDAY)
    }
  }
}