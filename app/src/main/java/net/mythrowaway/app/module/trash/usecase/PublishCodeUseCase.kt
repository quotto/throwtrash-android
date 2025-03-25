package net.mythrowaway.app.module.trash.usecase

import android.util.Log
import net.mythrowaway.app.module.account.service.AuthService
import net.mythrowaway.app.module.account.service.UserIdService
import javax.inject.Inject

class PublishCodeUseCase @Inject constructor(
  private val apiAdapter: MobileApiInterface,
  private val userIdService: UserIdService,
  private val authService: AuthService,
 ) {
    suspend fun publishActivationCode(): String {
      return authService.getIdToken().onSuccess { idToken ->
        val userId = userIdService.getUserId()
        if (userId.isNullOrEmpty()) {
          Log.e(this.javaClass.simpleName, "userId is empty")
          throw IllegalStateException("userId is empty")
        }
        return apiAdapter.publishActivationCode(userId, idToken)
      }.onFailure { error ->
        throw error
      }.getOrThrow()
    }
}