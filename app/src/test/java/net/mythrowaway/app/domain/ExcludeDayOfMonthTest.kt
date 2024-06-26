package net.mythrowaway.app.domain

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ExcludeDayOfMonthTest {
  @Test
  fun date_of_2020_01_01_and_01_01_is_excluded() {
    val excludeDayOfMonth = ExcludeDayOfMonth(1, 1)
    val date = LocalDate.of(2020, 1, 1)

    val actual = excludeDayOfMonth.isExcluded(date)

    Assertions.assertTrue(actual)
  }

  @Test
  fun date_of_2020_01_01_and_01_02_is_not_excluded() {
    val excludeDayOfMonth = ExcludeDayOfMonth(1, 1)
    val date = LocalDate.of(2020, 1, 2)

    val actual = excludeDayOfMonth.isExcluded(date)

    Assertions.assertTrue(!actual)
  }

  @Test
  fun parameter_month_0_is_invalid() {
    Assertions.assertThrows(IllegalArgumentException::class.java) {
      ExcludeDayOfMonth(0, 1)
    }
  }

  @Test
  fun parameter_month_13_is_invalid() {
    Assertions.assertThrows(IllegalArgumentException::class.java) {
      ExcludeDayOfMonth(13, 1)
    }
  }

  @Test
  fun parameter_dayOfMonth_0_is_invalid() {
    Assertions.assertThrows(IllegalArgumentException::class.java) {
      ExcludeDayOfMonth(1, 0)
    }
  }

  @Test
  fun parameter_month_1_and_dayOfMonth_31_is_valid() {
    Assertions.assertDoesNotThrow { ExcludeDayOfMonth(1, 31) }
  }

  @Test
  fun parameter_month_2_and_dayOfMonth_30_is_invalid() {
    Assertions.assertThrows(IllegalArgumentException::class.java) {
      ExcludeDayOfMonth(2, 30)
    }
  }

  @Test
  fun parameter_month_2_and_dayOfMonth_29_is_valid() {
    Assertions.assertDoesNotThrow { ExcludeDayOfMonth(2, 29) }

  }

  @Test
  fun parameter_month_3_and_dayOfMonth_31_is_valid() {
    Assertions.assertDoesNotThrow { ExcludeDayOfMonth(3, 31) }
  }

  @Test
  fun parameter_month_3_and_dayOfMonth_32_is_invalid() {
    Assertions.assertThrows(IllegalArgumentException::class.java) {
      ExcludeDayOfMonth(3, 32)
    }
  }

  @Test
  fun parameter_month_4_and_dayOfMonth_30_is_valid() {
    Assertions.assertDoesNotThrow { ExcludeDayOfMonth(4, 30) }
  }

  @Test
  fun parameter_month_4_and_dayOfMonth_31_is_invalid() {
    Assertions.assertThrows(IllegalArgumentException::class.java) {
      ExcludeDayOfMonth(4, 31)
    }
  }

  @Test
  fun parameter_month_5_and_dayOfMonth_31_is_valid() {
    Assertions.assertDoesNotThrow { ExcludeDayOfMonth(5, 31) }
  }

  @Test
  fun parameter_month_5_and_dayOfMonth_32_is_invalid() {
    Assertions.assertThrows(IllegalArgumentException::class.java) {
      ExcludeDayOfMonth(5, 32)
    }
  }

  @Test
  fun parameter_month_6_and_dayOfMonth_30_is_valid() {
    Assertions.assertDoesNotThrow { ExcludeDayOfMonth(6, 30) }
  }

  @Test
  fun parameter_month_6_and_dayOfMonth_31_is_invalid() {
    Assertions.assertThrows(IllegalArgumentException::class.java) {
      ExcludeDayOfMonth(6, 31)
    }
  }

  @Test
  fun parameter_month_7_and_dayOfMonth_31_is_valid() {
    Assertions.assertDoesNotThrow { ExcludeDayOfMonth(7, 31) }
  }

  @Test
  fun parameter_month_7_and_dayOfMonth_32_is_invalid() {
    Assertions.assertThrows(IllegalArgumentException::class.java) {
      ExcludeDayOfMonth(7, 32)
    }
  }

  @Test
  fun parameter_month_8_and_dayOfMonth_31_is_valid() {
    Assertions.assertDoesNotThrow { ExcludeDayOfMonth(8, 31) }
  }

  @Test
  fun parameter_month_8_and_dayOfMonth_32_is_invalid() {
    Assertions.assertThrows(IllegalArgumentException::class.java) {
      ExcludeDayOfMonth(8, 32)
    }
  }

  @Test
  fun parameter_month_9_and_dayOfMonth_30_is_valid() {
    Assertions.assertDoesNotThrow { ExcludeDayOfMonth(9, 30) }
  }

  @Test
  fun parameter_month_9_and_dayOfMonth_31_is_invalid() {
    Assertions.assertThrows(IllegalArgumentException::class.java) {
      ExcludeDayOfMonth(9, 31)
    }
  }

  @Test
  fun parameter_month_10_and_dayOfMonth_31_is_valid() {
    Assertions.assertDoesNotThrow { ExcludeDayOfMonth(10, 31) }
  }

  @Test
  fun parameter_month_10_and_dayOfMonth_32_is_invalid() {
    Assertions.assertThrows(IllegalArgumentException::class.java) {
      ExcludeDayOfMonth(10, 32)
    }
  }

  @Test
  fun parameter_month_11_and_dayOfMonth_30_is_valid() {
    Assertions.assertDoesNotThrow { ExcludeDayOfMonth(11, 30) }
  }

  @Test
  fun parameter_month_11_and_dayOfMonth_31_is_invalid() {
    Assertions.assertThrows(IllegalArgumentException::class.java) {
      ExcludeDayOfMonth(11, 31)
    }
  }

  @Test
  fun parameter_month_12_and_dayOfMonth_31_is_valid() {
    Assertions.assertDoesNotThrow { ExcludeDayOfMonth(12, 31) }
  }

  @Test
  fun parameter_month_12_and_dayOfMonth_32_is_invalid() {
    Assertions.assertThrows(IllegalArgumentException::class.java) {
      ExcludeDayOfMonth(12, 32)
    }
  }
}