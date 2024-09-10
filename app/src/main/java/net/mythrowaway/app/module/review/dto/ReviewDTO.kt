package net.mythrowaway.app.module.review.dto

class ReviewDTO(
  val reviewedAt: Long,
  val lastLaunchedAt: Long,
  val continuousUseDateCount: Int,
  val reviewed: Boolean
)