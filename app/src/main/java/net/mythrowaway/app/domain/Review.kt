package net.mythrowaway.app.domain

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

data class Review(
  private val _reviewed: Boolean,
  private val _reviewedAt: Long,
  private val _continuousUseDateCount: Int,
  private val _lastLaunchedAt: Long
  ) {
  val reviewed: Boolean get() = _reviewed
  val reviewedAt: Long get() = _reviewedAt
  val continuousUseDateCount: Int get() = _continuousUseDateCount
  val lastLaunchedAt: Long get() = _lastLaunchedAt

  fun review(
    reviewedAt: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
  ): Review {
    return this.copy(
      _reviewed = true,
      _reviewedAt = reviewedAt
    )
  }

  fun launched(
    lastLaunchedAt: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
  ): Review {
    val updatedLastLaunchedDate = LocalDateTime.ofEpochSecond(lastLaunchedAt, 0, ZoneOffset.UTC).toLocalDate()
    val lastLaunchedDate = LocalDateTime.ofEpochSecond(_lastLaunchedAt, 0, ZoneOffset.UTC).toLocalDate()
    // 最終起動日から更新日の経過日数を取得
    val diffDays = ChronoUnit.DAYS.between(lastLaunchedDate, updatedLastLaunchedDate)

    // 最終起動日が今日の1日前であれば連続利用日数をインクリメントする
    // それ以外は連続利用日数をリセットする
    val continuousUseDateCount = if (diffDays == 1L) {
      _continuousUseDateCount + 1
    } else {
      1
    }
    return this.copy(
      _lastLaunchedAt = lastLaunchedAt,
      _continuousUseDateCount = continuousUseDateCount
    )
  }
}