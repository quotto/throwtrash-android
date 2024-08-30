package net.mythrowaway.app.usecase

import android.util.Log
import javax.inject.Inject

class ActivateUseCase @Inject constructor(
  private val api: MobileApiInterface,
  private val config: ConfigRepositoryInterface,
  private val persist: TrashRepositoryInterface,
  private val userRepository: UserRepositoryInterface
) {
    fun activate(code: String): ActivationResult {
        userRepository.getUserId()?.let { userId->
            api.activate(code, userId).let { remoteTrash ->
                Log.d(this.javaClass.simpleName,"Success Activation -> code=$code")
                Log.i(this.javaClass.simpleName, "Import Data -> $remoteTrash")
                config.setTimestamp(remoteTrash.timestamp)
                config.setSyncWait()
                persist.importScheduleList(remoteTrash.trashList)
                return ActivationResult.ACTIVATE_SUCCESS
            }
        }
        Log.w(this.javaClass.simpleName,"Failed Activation -> code=$code")
        return ActivationResult.ACTIVATE_ERROR
    }

    enum class ActivationResult {
        ACTIVATE_SUCCESS,
        ACTIVATE_ERROR
    }
}