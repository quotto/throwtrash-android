package net.mythrowaway.app.usecase

import android.util.Log
import net.mythrowaway.app.adapter.repository.IMigrationApi
import javax.inject.Inject

class MigrationUseCase @Inject constructor(val repository: IConfigRepository, val api: IMigrationApi) {

  private val versionList = arrayListOf<Int>(1,2)

  fun migration(thisVersion: Int) {
    val currentVersion = repository.getConfigVersion()
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