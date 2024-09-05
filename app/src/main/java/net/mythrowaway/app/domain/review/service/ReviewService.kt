package net.mythrowaway.app.domain.review.service

import net.mythrowaway.app.domain.review.dto.ReviewDTO
import net.mythrowaway.app.domain.review.entity.Review
import net.mythrowaway.app.domain.review.usecase.ReviewRepositoryInterface
import net.mythrowaway.app.domain.review.usecase.ReviewUseCase
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