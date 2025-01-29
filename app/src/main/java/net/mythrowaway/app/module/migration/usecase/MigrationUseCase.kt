package net.mythrowaway.app.module.migration.usecase

import android.util.Log
import net.mythrowaway.app.module.info.service.UserIdService
import net.mythrowaway.app.module.migration.infra.MigrationApiInterface
import net.mythrowaway.app.module.review.dto.ReviewDTO
import net.mythrowaway.app.module.review.service.ReviewService
import net.mythrowaway.app.module.trash.service.TrashService
import javax.inject.Inject

class MigrationUseCase @Inject constructor(
  private val repository: VersionRepositoryInterface,
  private val migrationRepository: MigrationRepositoryInterface,
  private val userIdService: UserIdService,
  private val trashService: TrashService,
  private val reviewService: ReviewService,
  private val api: MigrationApiInterface
) {

  private val versionList = arrayListOf(1,2,3)

  fun migration(thisVersion: Int) {
    val currentVersion = repository.getConfigVersion()
    if(currentVersion == 0) {
      Log.i(javaClass.simpleName, "set new configuration version $thisVersion")
      repository.updateConfigVersion(thisVersion)
      return
    }
    if(thisVersion == currentVersion) {
      Log.i(javaClass.simpleName, "version $thisVersion is latest version")
      return
    }

    val iterator = versionList.iterator()
    while(iterator.hasNext()) {
      if(iterator.next() == currentVersion) {
        break
      }
    }
    while(iterator.hasNext()) {
      val targetVersion = iterator.next()
      when(targetVersion) {
        3 -> {
          // Reviewデータ保存形式のマイグレーション
          val oldLastUsedTime = migrationRepository.getLongValue("KEY_LAST_USED_TIME", 0)
          val oldContinuousDate = migrationRepository.getIntValue("KEY_CONTINUOUS_DATE", 0)
          val oldReviewed = migrationRepository.getBooleanValue("KEY_REVIEWED", false)

          val newReview = ReviewDTO(
            reviewed = oldReviewed,
            reviewedAt = 0L,
            continuousUseDateCount = oldContinuousDate,
            lastLaunchedAt = oldLastUsedTime
          )

          reviewService.updateReview(newReview)
          repository.updateConfigVersion(targetVersion)
        }
        2 -> {
          Log.i(javaClass.simpleName, "start migration to version $targetVersion")
          //apiでタイムスタンプ更新
          userIdService.getUserId()?.let {
            val timestamp = api.updateTrashScheduleTimestamp(it)
            if(timestamp > 0) {
              trashService.updateSyncTime(timestamp)
            }
          }
          repository.updateConfigVersion(targetVersion)
          Log.i(javaClass.simpleName, "complete migration")
        }
        else -> {
          Log.i(javaClass.simpleName, "version $targetVersion is not migration target")
        }
      }
      if(targetVersion == thisVersion) {
        break
      }
    }
  }
}