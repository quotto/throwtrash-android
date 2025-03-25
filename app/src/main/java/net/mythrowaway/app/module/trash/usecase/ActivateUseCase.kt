package net.mythrowaway.app.module.trash.usecase

import android.util.Log
import net.mythrowaway.app.module.account.service.AuthService
import net.mythrowaway.app.module.account.service.UserIdService
import javax.inject.Inject

class ActivateUseCase @Inject constructor(
  private val api: MobileApiInterface,
  private val trashRepository: TrashRepositoryInterface,
  private val userIdService: UserIdService,
  private val syncRepository: SyncRepositoryInterface,
  private val authService: AuthService,
) {
  suspend fun activate(code: String): ActivationResult {
    authService.getIdToken().fold(
      onSuccess = { idToken ->
        val currentUserId = userIdService.getUserId()
        if (currentUserId == null) {
          try {
            api.register(idToken).let { registeredInfo ->
              Log.d(
                this.javaClass.simpleName,
                "Success Register -> id=${registeredInfo.userId}"
              )
              userIdService.registerUserId(registeredInfo.userId)
              syncRepository.setTimestamp(registeredInfo.latestTrashListRegisteredTimestamp)
            }
          } catch (e: Exception) {
            Log.e(this.javaClass.simpleName, e.stackTraceToString())
            return ActivationResult.ACTIVATE_ERROR
          }
        }
        userIdService.getUserId()?.let { userId ->
          try {
            api.activate(code, userId, idToken).let { remoteTrash ->
              Log.d(this.javaClass.simpleName, "Success Activation -> code=$code")
              syncRepository.setTimestamp(remoteTrash.timestamp)
              syncRepository.setSyncWait()
              trashRepository.replaceTrashList(remoteTrash.trashList)
              return ActivationResult.ACTIVATE_SUCCESS
            }
          } catch (e: Exception) {
            Log.e(this.javaClass.simpleName, e.stackTraceToString())
            return ActivationResult.ACTIVATE_ERROR
          }
        }
        Log.w(this.javaClass.simpleName, "Failed Activation -> code=$code")
        return ActivationResult.ACTIVATE_ERROR
      },
      onFailure = { _ ->
        return ActivationResult.ACTIVATE_ERROR
      }
    )
  }

  enum class ActivationResult {
    ACTIVATE_SUCCESS,
    ACTIVATE_ERROR
  }
}