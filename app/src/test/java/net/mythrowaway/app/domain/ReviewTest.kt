package net.mythrowaway.app.domain

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneOffset

class ReviewTest {

  val timeOf20240829000000 = LocalDateTime.of(2024, 8, 29, 0, 0, 0).toEpochSecond(ZoneOffset.UTC)
  val timeOf20240829235959 = LocalDateTime.of(2024, 8, 29, 23, 59, 59).toEpochSecond(ZoneOffset.UTC)
  val timeOf20240830000000 = LocalDateTime.of(2024, 8, 30, 0, 0, 0).toEpochSecond(ZoneOffset.UTC)
  val timeOf20240830235959 = LocalDateTime.of(2024, 8, 30, 23, 59, 59).toEpochSecond(ZoneOffset.UTC)
  val timeOf20240831000000 = LocalDateTime.of(2024, 8, 31, 0, 0, 0).toEpochSecond(ZoneOffset.UTC)
  val timeOf20240831235959 = LocalDateTime.of(2024, 8, 31, 23, 59, 59).toEpochSecond(ZoneOffset.UTC)
  @Nested
  inner class Review {
    @Test
    fun reviewed_and_recorded_current_time_if_not_reviewed_and_not_specified_time() {
      // Arrange
      val review = Review(
        _reviewed = false,
        _reviewedAt = 0,
        _continuousUseDateCount = 0,
        _lastLaunchedAt = 0
      )

      // Act
      val actual = review.review()

      // Assert
      Assertions.assertTrue(actual.reviewed)
      Assertions.assertTrue(actual.reviewedAt > 0)
    }

    @Test
    fun reviewed_and_recorded_specific_time_if_not_reviewed() {
      // Arrange
      val review = Review(
        _reviewed = false,
        _reviewedAt = 0,
        _continuousUseDateCount = 0,
        _lastLaunchedAt = 0
      )

      // Act
      val actual = review.review(1614556800)

      // Assert
      Assertions.assertTrue(actual.reviewed)
      Assertions.assertEquals(1614556800, actual.reviewedAt)
    }

    @Test
    fun reviewed_and_recorded_specific_time_if_reviewed() {
      // Arrange
      val review = Review(
        _reviewed = true,
        _reviewedAt = 1614556800,
        _continuousUseDateCount = 0,
        _lastLaunchedAt = 0
      )

      // Act
      val actual = review.review(1614643200)

      // Assert
      Assertions.assertTrue(actual.reviewed)
      Assertions.assertEquals(1614643200, actual.reviewedAt)
    }
  }

  @Nested
  inner class Launched {
    @Test
    fun recorded_last_launched_time_and_increment_continuous_use_date_count_if_last_launched_date_is_yesterday() {
      // Arrange
      val review = Review(
        _reviewed = false,
        _reviewedAt = 0,
        _continuousUseDateCount = 0,
        _lastLaunchedAt = timeOf20240830000000
      )

      // Act
      val actual = review.launched(timeOf20240831000000)

      // Assert
      Assertions.assertEquals(1, actual.continuousUseDateCount)
      Assertions.assertEquals(timeOf20240831000000, actual.lastLaunchedAt)
    }

    @Test
    fun reset_continuous_use_date_count_if_last_launched_date_is_two_days_ago() {
      // Arrange
      val review = Review(
        _reviewed = false,
        _reviewedAt = 0,
        _continuousUseDateCount = 0,
        _lastLaunchedAt = timeOf20240829000000
      )

      // Act
      val actual = review.launched(timeOf20240831000000)

      // Assert
      Assertions.assertEquals(1, actual.continuousUseDateCount)
    }

    @Test
    fun not_increment_continuous_use_date_count_if_last_launched_date_is_today() {
      // Arrange
      val review = Review(
        _reviewed = false,
        _reviewedAt = 0,
        _continuousUseDateCount = 1,
        _lastLaunchedAt = timeOf20240831000000
      )

      // Act
      val actual = review.launched(timeOf20240831235959)

      // Assert
      Assertions.assertEquals(1, actual.continuousUseDateCount)
    }

    @Test
    fun increment_continuous_use_date_count_if_last_launched_date_is_one_second_before_and_yesterday() {
      // Arrange
      val review = Review(
        _reviewed = false,
        _reviewedAt = 0,
        _continuousUseDateCount = 1,
        _lastLaunchedAt = timeOf20240830235959
      )

      // Act
      val actual = review.launched(timeOf20240831000000)

      // Assert
      Assertions.assertEquals(2, actual.continuousUseDateCount)
    }

    @Test
    fun reset_continuous_use_date_count_if_last_launched_date_is_before_172799_seconds_and_2days_ago() {
      // Arrange
      val review = Review(
        _reviewed = false,
        _reviewedAt = 0,
        _continuousUseDateCount = 1,
        _lastLaunchedAt = timeOf20240829235959
      )

      // Act
      val actual = review.launched(timeOf20240831000000)

      // Assert
      Assertions.assertEquals(1, actual.continuousUseDateCount)
    }

    @Test
    fun recorded_current_time_as_last_launched_time_if_not_specified_time() {
      // Arrange
      val review = Review(
        _reviewed = false,
        _reviewedAt = 0,
        _continuousUseDateCount = 0,
        // 2024-08-31T00:00:00Z
        _lastLaunchedAt = 1614643200
      )

      // Act
      // 2024-08-31T23:59:59Z
      val actual = review.launched(1614643199)

      // Assert
      Assertions.assertTrue(actual.lastLaunchedAt > 0)
    }
  }

}