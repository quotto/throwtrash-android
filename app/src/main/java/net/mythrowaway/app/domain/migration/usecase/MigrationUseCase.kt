package net.mythrowaway.app.domain.migration.usecase

import android.util.Log
import net.mythrowaway.app.domain.info.usecase.UserRepositoryInterface
import net.mythrowaway.app.domain.migration.infra.MigrationApiInterface
import net.mythrowaway.app.domain.trash.usecase.SyncRepositoryInterface
import javax.inject.Inject

class MigrationUseCase @Inject constructor(
  private val repository: VersionRepositoryInterface,
  private val userRepository: UserRepositoryInterface,
  private val syncRepository: SyncRepositoryInterface,
  private val api: MigrationApiInterface
) {

  private val versionList = arrayListOf(1,2)

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
      when(val targetVersion = iterator.next()) {
        2 -> {
          Log.i(javaClass.simpleName, "start migration to version $targetVersion")
          //apiでタイムスタンプ更新
          userRepository.getUserId()?.let {
            val timestamp = api.updateTrashScheduleTimestamp(it)
            if(timestamp > 0) {
              syncRepository.setTimestamp(timestamp)
            }
          }
          repository.updateConfigVersion(targetVersion)
          Log.i(javaClass.simpleName, "complete migration")
        }
        else -> {
          Log.i(javaClass.simpleName, "version $targetVersion is not migration target")
        }
      }
    }
  }
}