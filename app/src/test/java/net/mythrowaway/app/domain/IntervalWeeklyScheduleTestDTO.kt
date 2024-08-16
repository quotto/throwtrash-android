package net.mythrowaway.app.domain

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate

class IntervalWeeklyScheduleTestDTO {

  @Nested
  inner class TwoIntervalsTest {
    @Test
    fun from_2020_01_05_to_2020_01_19_is_two_intervals_and_2020_01_22_is_trashDay() {
      val intervalWeeklySchedule =
        IntervalWeeklySchedule(LocalDate.parse("2020-01-05"), DayOfWeek.WEDNESDAY, 2)
      val targetDate = LocalDate.of(2020, 1, 22)

      val actual = intervalWeeklySchedule.isTrashDay(targetDate)

      Assertions.assertTrue(actual)
    }

    @Test
    fun from_2020_01_05_to_2020_02_02_is_two_intervals_and_2020_02_5_is_trashDay() {
      val intervalWeeklySchedule =
        IntervalWeeklySchedule(LocalDate.parse("2020-01-08"), DayOfWeek.WEDNESDAY, 2)
      val targetDate = LocalDate.of(2020, 2, 5)

      val actual = intervalWeeklySchedule.isTrashDay(targetDate)

      Assertions.assertTrue(actual)
    }

    @Test
    fun from_2020_01_05_to_2020_01_26_is_three_intervals_and_2020_01_26_is_not_trashDay() {
      val intervalWeeklySchedule =
        IntervalWeeklySchedule(LocalDate.parse("2020-01-08"), DayOfWeek.WEDNESDAY, 2)
      val targetDate = LocalDate.of(2020, 1, 26)

      val actual = intervalWeeklySchedule.isTrashDay(targetDate)

      Assertions.assertFalse(actual)
    }

    @Test
    fun from_2020_01_05_to_2020_01_19_is_two_intervals_and_2020_01_21_is_not_trashDay() {
      val intervalWeeklySchedule =
        IntervalWeeklySchedule(LocalDate.parse("2020-01-08"), DayOfWeek.WEDNESDAY, 2)
      val targetDate = LocalDate.of(2020, 1, 21)

      val actual = intervalWeeklySchedule.isTrashDay(targetDate)

      Assertions.assertFalse(actual)
    }

    @Test
    fun from_2019_12_29_to_2020_01_12_is_two_intervals_and_2020_01_12_is_trashDay() {
      val intervalWeeklySchedule =
        IntervalWeeklySchedule(LocalDate.parse("2019-12-29"), DayOfWeek.SUNDAY, 2)
      val targetDate = LocalDate.of(2020, 1, 12)

      val actual = intervalWeeklySchedule.isTrashDay(targetDate)

      Assertions.assertTrue(actual)
    }

    @Test
    fun from_2019_12_29_to_2020_01_26_is_two_intervals_and_2020_01_26_is_trashDay() {
      val intervalWeeklySchedule =
        IntervalWeeklySchedule(LocalDate.parse("2019-12-29"), DayOfWeek.SUNDAY, 2)
      val targetDate = LocalDate.of(2020, 1, 26)

      val actual = intervalWeeklySchedule.isTrashDay(targetDate)

      Assertions.assertTrue(actual)
    }

    @Test
    fun from_2019_12_29_to_2020_01_19_is_three_intervals_and_2020_01_19_is_not_trashDay() {
      val intervalWeeklySchedule =
        IntervalWeeklySchedule(LocalDate.parse("2019-12-29"), DayOfWeek.SUNDAY, 2)
      val targetDate = LocalDate.of(2020, 1, 19)

      val actual = intervalWeeklySchedule.isTrashDay(targetDate)

      Assertions.assertFalse(actual)
    }

    @Test
    fun from_2020_03_01_to_2020_02_16_is_two_intervals_and_2020_02_19_is_trashDay() {
      val intervalWeeklySchedule =
        IntervalWeeklySchedule(LocalDate.parse("2020-03-04"), DayOfWeek.WEDNESDAY, 2)
      val targetDate = LocalDate.of(2020, 2, 19)

      val actual = intervalWeeklySchedule.isTrashDay(targetDate)

      Assertions.assertTrue(actual)
    }

    @Test
    fun from_2020_03_01_to_2020_02_23_is_one_intervals_and_2020_02_26_is_not_trashDay() {
      val intervalWeeklySchedule =
        IntervalWeeklySchedule(LocalDate.parse("2020-03-04"), DayOfWeek.WEDNESDAY, 2)
      val targetDate = LocalDate.of(2020, 2, 26)

      val actual = intervalWeeklySchedule.isTrashDay(targetDate)

      Assertions.assertFalse(actual)
    }
  }

  @Nested
  inner class ThreeIntervalsTest {

    @Test
    fun from_2020_01_05_to_2020_01_26_is_three_intervals_and_2020_01_29_is_trashDay() {
      val intervalWeeklySchedule =
        IntervalWeeklySchedule(LocalDate.parse("2020-01-08"), DayOfWeek.WEDNESDAY, 3)
      val targetDate = LocalDate.of(2020, 1, 29)

      val actual = intervalWeeklySchedule.isTrashDay(targetDate)

      Assertions.assertTrue(actual)
    }

    @Test
    fun from_2020_01_05_to_2020_01_26_is_three_intervals_and_2020_01_08_is_trashDay() {
      val intervalWeeklySchedule =
        IntervalWeeklySchedule(LocalDate.parse("2020-01-08"), DayOfWeek.WEDNESDAY, 3)
      val targetDate = LocalDate.of(2020, 1, 8)

      val actual = intervalWeeklySchedule.isTrashDay(targetDate)

      Assertions.assertTrue(actual)
    }

    @Test
    fun from_2020_01_05_to_2020_01_12_is_one_intervals_and_2020_01_15_is_not_trashDay() {
      val intervalWeeklySchedule =
        IntervalWeeklySchedule(LocalDate.parse("2020-01-08"), DayOfWeek.WEDNESDAY, 3)
      val targetDate = LocalDate.of(2020, 1, 15)

      val actual = intervalWeeklySchedule.isTrashDay(targetDate)

      Assertions.assertFalse(actual)
    }

    @Test
    fun from_2019_12_22_to_2020_01_12_is_three_intervals_and_2020_01_12_is_trashDay() {
      val intervalWeeklySchedule =
        IntervalWeeklySchedule(LocalDate.parse("2019-12-22"), DayOfWeek.SUNDAY, 3)
      val targetDate = LocalDate.of(2020, 1, 12)

      val actual = intervalWeeklySchedule.isTrashDay(targetDate)

      Assertions.assertTrue(actual)
    }

    @Test
    fun from_2020_03_01_to_2020_02_09_is_three_intervals_and_2020_02_12_is_trashDay() {
      val intervalWeeklySchedule =
        IntervalWeeklySchedule(LocalDate.parse("2020-03-04"), DayOfWeek.WEDNESDAY, 3)
      val targetDate = LocalDate.of(2020, 2, 12)

      val actual = intervalWeeklySchedule.isTrashDay(targetDate)

      Assertions.assertTrue(actual)
    }

    @Test
    fun from_2020_03_01_to_2020_02_23_is_one_intervals_and_2020_02_26_is_not_trashDay() {
      val intervalWeeklySchedule =
        IntervalWeeklySchedule(LocalDate.parse("2020-03-04"), DayOfWeek.WEDNESDAY, 3)
      val targetDate = LocalDate.of(2020, 2, 26)

      val actual = intervalWeeklySchedule.isTrashDay(targetDate)

      Assertions.assertFalse(actual)
    }

    @Test
    fun from_2020_03_01_to_2020_02_16_is_two_intervals_and_2020_02_19_is_not_trashDay() {
      val intervalWeeklySchedule =
        IntervalWeeklySchedule(LocalDate.parse("2020-03-04"), DayOfWeek.WEDNESDAY, 3)
      val targetDate = LocalDate.of(2020, 2, 19)

      val actual = intervalWeeklySchedule.isTrashDay(targetDate)

      Assertions.assertFalse(actual)
    }
  }

  @Nested
  inner class FourIntervalsTest {
    @Test
    fun from_2019_12_29_to_2020_01_26_is_four_intervals_and_2020_01_29_is_trashDay() {
      val intervalWeeklySchedule =
        IntervalWeeklySchedule(LocalDate.parse("2020-01-01"), DayOfWeek.WEDNESDAY, 4)
      val targetDate = LocalDate.of(2020, 1, 29)

      val actual = intervalWeeklySchedule.isTrashDay(targetDate)

      Assertions.assertTrue(actual)
    }

    @Test
    fun from_2019_12_29_to_2020_02_23_is_four_intervals_and_2020_02_26_is_trashDay() {
      val intervalWeeklySchedule =
        IntervalWeeklySchedule(LocalDate.parse("2020-01-01"), DayOfWeek.WEDNESDAY, 4)
      val targetDate = LocalDate.of(2020, 2, 26)

      val actual = intervalWeeklySchedule.isTrashDay(targetDate)

      Assertions.assertTrue(actual)
    }

    @Test
    fun from_2019_12_29_to_2020_01_12_is_two_intervals_and_2020_01_15_is_not_trashDay() {
      val intervalWeeklySchedule =
        IntervalWeeklySchedule(LocalDate.parse("2020-01-01"), DayOfWeek.WEDNESDAY, 4)
      val targetDate = LocalDate.of(2020, 1, 15)

      val actual = intervalWeeklySchedule.isTrashDay(targetDate)

      Assertions.assertFalse(actual)
    }

    @Test
    fun from_2019_12_01_to_2019_12_29_is_four_intervals_and_2019_12_29_is_trashDay() {
      val intervalWeeklySchedule =
        IntervalWeeklySchedule(LocalDate.parse("2019-12-01"), DayOfWeek.SUNDAY, 4)
      val targetDate = LocalDate.of(2019, 12, 29)

      val actual = intervalWeeklySchedule.isTrashDay(targetDate)

      Assertions.assertTrue(actual)
    }

    @Test
    fun from_2020_03_01_to_2020_02_02_is_four_intervals_and_2020_02_04_is_trashDay() {
      val intervalWeeklySchedule =
        IntervalWeeklySchedule(LocalDate.parse("2020-03-03"), DayOfWeek.TUESDAY, 4)
      val targetDate = LocalDate.of(2020, 2, 4)

      val actual = intervalWeeklySchedule.isTrashDay(targetDate)

      Assertions.assertTrue(actual)
    }

    @Test
    fun from_2020_03_01_to_2020_02_23_is_one_intervals_and_2020_02_29_is_not_trashDay() {
      val intervalWeeklySchedule =
        IntervalWeeklySchedule(LocalDate.parse("2020-03-07"), DayOfWeek.SATURDAY, 4)
      val targetDate = LocalDate.of(2020, 2, 29)

      val actual = intervalWeeklySchedule.isTrashDay(targetDate)

      Assertions.assertFalse(actual)
    }

    @Test
    fun from_2020_03_01_to_2020_02_16_is_two_intervals_and_2020_02_21_is_not_trashDay() {
      val intervalWeeklySchedule =
        IntervalWeeklySchedule(LocalDate.parse("2020-03-07"), DayOfWeek.SATURDAY, 4)
      val targetDate = LocalDate.of(2020, 2, 21)

      val actual = intervalWeeklySchedule.isTrashDay(targetDate)

      Assertions.assertFalse(actual)
    }

    @Test
    fun from_2020_03_01_to_2020_02_09_is_three_intervals_and_2020_02_14_is_not_trashDay() {
      val intervalWeeklySchedule =
        IntervalWeeklySchedule(LocalDate.parse("2020-03-07"), DayOfWeek.SATURDAY, 4)
      val targetDate = LocalDate.of(2020, 2, 14)

      val actual = intervalWeeklySchedule.isTrashDay(targetDate)

      Assertions.assertFalse(actual)
    }
  }

  @Nested
  inner class IllegalIntervalTest {
    @Test
    fun interval_of_1_is_illegal() {
      Assertions.assertThrows(IllegalArgumentException::class.java) {
        IntervalWeeklySchedule(LocalDate.parse("2020-01-05"), DayOfWeek.WEDNESDAY, 1)
      }
    }

    @Test
    fun interval_of_5_is_illegal() {
      Assertions.assertThrows(IllegalArgumentException::class.java) {
        IntervalWeeklySchedule(LocalDate.parse("2020-01-05"), DayOfWeek.WEDNESDAY, 5)
      }
    }
  }
}