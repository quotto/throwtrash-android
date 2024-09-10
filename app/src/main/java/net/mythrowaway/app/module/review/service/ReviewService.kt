package net.mythrowaway.app.module.review.service

import net.mythrowaway.app.module.review.dto.ReviewDTO
import net.mythrowaway.app.module.review.entity.Review
import net.mythrowaway.app.module.review.usecase.ReviewRepositoryInterface
import javax.inject.Inject

class ReviewService @Inject constructor(private val reviewRepository: ReviewRepositoryInterface) {
    fun updateReview(reviewDTO: ReviewDTO) {
      reviewRepository.save(
        Review(
          _reviewedAt = reviewDTO.reviewedAt,
          _lastLaunchedAt = reviewDTO.lastLaunchedAt,
          _continuousUseDateCount = reviewDTO.continuousUseDateCount,
          _reviewed = reviewDTO.reviewed
        )
      )
    }
}