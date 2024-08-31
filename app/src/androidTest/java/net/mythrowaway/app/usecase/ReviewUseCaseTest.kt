package net.mythrowaway.app.usecase

import androidx.preference.PreferenceManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import net.mythrowaway.app.adapter.repository.PreferenceReviewRepositoryImpl
import net.mythrowaway.app.domain.Review
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime
import java.time.ZoneOffset

@RunWith(AndroidJUnit4::class)
class ReviewUseCaseTest {
  private val reviewRepository = PreferenceReviewRepositoryImpl(
    InstrumentationRegistry.getInstrumentation().context
  )
  private val preferences = PreferenceManager.getDefaultSharedPreferences(
    InstrumentationRegistry.getInstrumentation().context
  )
  private val usecase = ReviewUseCase(reviewRepository)

  @Before
  fun before() {
    preferences.edit().clear().commit()
  }

  @Test
  fun get_default_review_object_when_not_stored() {
    val review = usecase.getReview()
    Assert.assertEquals(false, review.reviewed)
    Assert.assertEquals(0, review.reviewedAt)
    Assert.assertEquals(0, review.continuousUseDateCount)
    Assert.assertEquals(0, review.lastLaunchedAt)
  }

  @Test
  fun get_stored_review_object_when_stored() {
    val review = Review(
      _reviewed = true,
      _reviewedAt = 1614556800,
      _continuousUseDateCount = 1,
      _lastLaunchedAt = 1614556800
    )
    reviewRepository.save(review)
    val found = reviewRepository.find()
    Assert.assertEquals(review, found)
  }

  @Test
  fun update_launched_time_and_set_continuous_use_date_count_to_1_when_not_stored() {
    usecase.updateLastLaunchedTime(1614556800)
    val review = reviewRepository.find()
    Assert.assertEquals(false, review?.reviewed)
    Assert.assertEquals(0L, review?.reviewedAt)
    Assert.assertEquals(1, review?.continuousUseDateCount)
    Assert.assertEquals(1614556800L, review?.lastLaunchedAt)
  }

  @Test
  fun update_launched_time_and_increase_continuous_use_date_count_when_last_launched_is_yesterday() {
    val timeOf20240830000000 = LocalDateTime.of(2024, 8, 30, 0, 0, 0).toEpochSecond(ZoneOffset.UTC)
    val timeOf20240831000000 = LocalDateTime.of(2024, 8, 31, 0, 0, 0).toEpochSecond(ZoneOffset.UTC)
    val review = Review(
      _reviewed = true,
      _reviewedAt = 1614556800L,
      _continuousUseDateCount = 1,
      _lastLaunchedAt = timeOf20240830000000
    )
    reviewRepository.save(review)
    usecase.updateLastLaunchedTime(timeOf20240831000000)
    val updated = reviewRepository.find()
    Assert.assertEquals(true, updated?.reviewed)
    Assert.assertEquals(1614556800L, updated?.reviewedAt)
    Assert.assertEquals(2, updated?.continuousUseDateCount)
    Assert.assertEquals(timeOf20240831000000, updated?.lastLaunchedAt)
  }

  @Test
  fun update_launched_time_and_reset_continuous_use_date_count_when_last_launched_is_two_days_ago() {
    val timeOf20240829000000 = LocalDateTime.of(2024, 8, 29, 0, 0, 0).toEpochSecond(ZoneOffset.UTC)
    val timeOf20240831000000 = LocalDateTime.of(2024, 8, 31, 0, 0, 0).toEpochSecond(ZoneOffset.UTC)
    val review = Review(
      _reviewed = true,
      _reviewedAt = 1614556800L,
      _continuousUseDateCount = 2,
      _lastLaunchedAt = timeOf20240829000000
    )
    reviewRepository.save(review)
    usecase.updateLastLaunchedTime(timeOf20240831000000)
    val updated = reviewRepository.find()
    Assert.assertEquals(true, updated?.reviewed)
    Assert.assertEquals(1614556800L, updated?.reviewedAt)
    Assert.assertEquals(1, updated?.continuousUseDateCount)
    Assert.assertEquals(timeOf20240831000000, updated?.lastLaunchedAt)
  }

  @Test
  fun update_launched_time_and_increase_continuous_use_date_count_when_last_launched_is_today() {
    val timeOf20240830000000 = LocalDateTime.of(2024, 8, 30, 0, 0, 0).toEpochSecond(ZoneOffset.UTC)
    val timeOf20240830100000 = LocalDateTime.of(2024, 8, 30, 10, 0, 0).toEpochSecond(ZoneOffset.UTC)
    val review = Review(
      _reviewed = true,
      _reviewedAt = 1614556800L,
      _continuousUseDateCount = 1,
      _lastLaunchedAt = timeOf20240830000000
    )
    reviewRepository.save(review)
    usecase.updateLastLaunchedTime(timeOf20240830100000)
    val updated = reviewRepository.find()
    Assert.assertEquals(true, updated?.reviewed)
    Assert.assertEquals(1614556800L, updated?.reviewedAt)
    Assert.assertEquals(1, updated?.continuousUseDateCount)
    Assert.assertEquals(timeOf20240830100000, updated?.lastLaunchedAt)
  }

  @Test
  fun reviewed_whe_not_stored() {
    usecase.review(1614556800)
    val review = reviewRepository.find()
    Assert.assertEquals(true, review?.reviewed)
    Assert.assertEquals(1614556800L, review?.reviewedAt)
    Assert.assertEquals(0, review?.continuousUseDateCount)
    Assert.assertEquals(0L, review?.lastLaunchedAt)
  }

  @Test
  fun reviewed_when_not_reviewed() {
    val review = Review(
      _reviewed = false,
      _reviewedAt = 0,
      _continuousUseDateCount = 0,
      _lastLaunchedAt = 0
    )
    reviewRepository.save(review)
    usecase.review(1614556800)
    val updated = reviewRepository.find()
    Assert.assertEquals(true, updated?.reviewed)
    Assert.assertEquals(1614556800L, updated?.reviewedAt)
    Assert.assertEquals(0, updated?.continuousUseDateCount)
    Assert.assertEquals(0L, updated?.lastLaunchedAt)
  }

  @Test
  fun reviewed_time_update_when_already_reviewed() {
    val review = Review(
      _reviewed = true,
      _reviewedAt = 1614556800,
      _continuousUseDateCount = 0,
      _lastLaunchedAt = 0
    )
    reviewRepository.save(review)
    usecase.review(1614643200)
    val updated = reviewRepository.find()
    Assert.assertEquals(true, updated?.reviewed)
    Assert.assertEquals(1614643200L, updated?.reviewedAt)
    Assert.assertEquals(0, updated?.continuousUseDateCount)
    Assert.assertEquals(0L, updated?.lastLaunchedAt)
  }
}