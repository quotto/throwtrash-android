package net.mythrowaway.app.domain.review.usecase

import net.mythrowaway.app.domain.review.entity.Review
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepositoryInterface,
) {
    fun getReview(): Review {
        return reviewRepository.find()?: Review(false, 0, 0, 0)
    }

    fun updateLastLaunchedTime(launchedAt: Long) {
        val review = getReview()
        val updatedReview = review.launched(launchedAt)
        reviewRepository.save(updatedReview)
    }

    fun review(reviewedAt: Long) {
        val review = getReview()
        val updatedReview = review.review(reviewedAt)
        reviewRepository.save(updatedReview)
    }
}