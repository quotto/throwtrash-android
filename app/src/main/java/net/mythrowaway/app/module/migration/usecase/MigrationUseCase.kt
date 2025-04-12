package net.mythrowaway.app.module.migration.usecase

import android.util.Log
import net.mythrowaway.app.module.account.service.AuthService
import net.mythrowaway.app.module.account.service.UserIdService
import net.mythrowaway.app.module.migration.infra.MigrationApiInterface
import javax.inject.Inject

class MigrationUseCase @Inject constructor(
  private val repository: VersionRepositoryInterface,
  private val userIdService: UserIdService,
  private val api: MigrationApiInterface,
  private val authService: AuthService
) {


  suspend fun migration(configVersion: Int) {
    val currentVersion = repository.getConfigVersion()
    if(configVersion == currentVersion) {
      Log.i(javaClass.simpleName, "version $configVersion is latest version")
      return
    }

    val versionList = (currentVersion+1..configVersion).toList()
    val iterator = versionList.iterator()
    while(iterator.hasNext()) {
      when(val targetVersion=iterator.next()) {
        4 -> {
          // 匿名アカウントを利用するためのマイグレーション
          userIdService.getUserId()?.let {
            authService.getIdToken(false).onSuccess { idToken ->
              api.signUp(it, idToken)
            }.onFailure {
              Log.e(javaClass.simpleName, "Failed to get idToken: ${it.message}")
            }
          }
          repository.updateConfigVersion(targetVersion)
        }
        else -> {
          Log.i(javaClass.simpleName, "version $targetVersion is not migration target")
        }
      }
    }
  }
}