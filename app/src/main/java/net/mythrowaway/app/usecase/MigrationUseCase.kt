package net.mythrowaway.app.usecase

import android.util.Log
import net.mythrowaway.app.adapter.repository.MigrationApiInterface
import javax.inject.Inject

class MigrationUseCase @Inject constructor(val repository: ConfigRepositoryInterface, val api: MigrationApiInterface) {

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
          repository.getUserId()?.let {
            val timestamp = api.updateTrashScheduleTimestamp(it)
            if(timestamp > 0) {
              repository.setTimestamp(timestamp)
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