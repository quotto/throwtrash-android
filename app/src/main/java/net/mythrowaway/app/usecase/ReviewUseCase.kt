package net.mythrowaway.app.usecase

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.play.core.review.ReviewManagerFactory
import net.mythrowaway.app.domain.Review
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit
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