package net.mythrowaway.app.domain.review.infra

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import net.mythrowaway.app.domain.trash.infra.data.ReviewJsonData
import net.mythrowaway.app.domain.review.entity.Review
import net.mythrowaway.app.usecase.ReviewRepositoryInterface
import java.util.*
import javax.inject.Inject

class PreferenceReviewRepositoryImpl @Inject constructor(private val context: Context): ReviewRepositoryInterface{
    private val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    companion object {
        private const val KEY_LAST_USED_TIME = "KEY_LAST_USED_TIME"
        private const val KEY_CONTINUOUS_DATE = "KEY_CONTINUOUS_DATE"
        private const val KEY_REVIEWED = "KEY_REVIEWED"
        private const val KEY_REVIEW = "KEY_REVIEW"
    }
    override fun find(): Review? {
        return preference.getString(KEY_REVIEW, null)?.let {
            val mapper = ObjectMapper()
            val jsonData = mapper.readValue(it, ReviewJsonData::class.java)
            Review(
                _reviewed = jsonData.reviewed,
                _reviewedAt = jsonData.reviewedAt,
                _continuousUseDateCount = jsonData.continuousUseDateCount,
                _lastLaunchedAt = jsonData.lastLaunchedAt
            )
        }
    }

    override fun save(review: Review) {
        preference.edit {
            val jsonData = ReviewJsonData(
                reviewed = review.reviewed,
                reviewedAt = review.reviewedAt,
                continuousUseDateCount = review.continuousUseDateCount,
                lastLaunchedAt = review.lastLaunchedAt
            )
            val mapper = ObjectMapper()
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
            putString(KEY_REVIEW, mapper.writeValueAsString(jsonData))
        }
    }
}