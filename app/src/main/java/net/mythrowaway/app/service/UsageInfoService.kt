package net.mythrowaway.app.service

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.play.core.review.ReviewManagerFactory
import net.mythrowaway.app.usecase.ConfigRepositoryInterface
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsageInfoService @Inject constructor(
  private val configRepository: ConfigRepositoryInterface,
  private val applicationContext: Context
) {
    private var continuousDate: Int = -1
    private var reviewed: Boolean = false
    private var lastUsedTime: Long = -1

    /**
     * メンバー変数の初期化
     */
    fun initialize() {
        continuousDate = configRepository.getContinuousDate()
        reviewed = configRepository.getReviewed()
        lastUsedTime = configRepository.getLastUsedTime()
    }

    fun isContinuousUsed(): Boolean {
        return continuousDate >= 3
    }

    fun isReviewed(): Boolean {
        return reviewed
    }

    fun showReviewDialog(activity: Activity) {
        val manager = ReviewManagerFactory.create(applicationContext)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener {
                    Log.d(this.javaClass.simpleName, "review complete")
                }
                review()
            } else {
                Log.e(this.javaClass.simpleName, "Review flow failed")
            }
        }
    }

    private fun review() {
        configRepository.writeReviewed()
        reviewed = true
    }

    fun recordLastUsedTime(today: Calendar) {
        val durationDate = TimeUnit.DAYS.convert(today.timeInMillis - lastUsedTime, TimeUnit.MILLISECONDS)

        if(durationDate == 1L) {
            // 前日も利用していれば継続日数を加算する
            continuousDate += 1
            configRepository.updateContinuousDate(continuousDate)
        } else if(durationDate > 1L) {
            // 2日以上間隔が空いていれば継続日数をリセットする
            continuousDate = 1
            configRepository.updateContinuousDate(continuousDate)
        }

        configRepository.updateLastUsedTime()
    }
}