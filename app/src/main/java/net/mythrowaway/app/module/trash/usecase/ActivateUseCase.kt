package net.mythrowaway.app.module.trash.usecase

import android.util.Log
import net.mythrowaway.app.module.info.usecase.UserRepositoryInterface
import net.mythrowaway.app.module.trash.entity.trash.TrashList
import javax.inject.Inject

class ActivateUseCase @Inject constructor(
    private val api: MobileApiInterface,
    private val trashRepository: TrashRepositoryInterface,
    private val userRepository: UserRepositoryInterface,
    private val syncRepository: SyncRepositoryInterface
) {
    fun activate(code: String): ActivationResult {
        val currentUserId = userRepository.getUserId()
        if(currentUserId == null) {
            try {
                api.register(
                    TrashList(
                        trashList = listOf()
                    )
                ).let { registeredInfo ->
                    Log.d(
                        this.javaClass.simpleName,
                        "Success Register -> id=${registeredInfo.userId}"
                    )
                    userRepository.saveUserId(registeredInfo.userId)
                    syncRepository.setTimestamp(registeredInfo.latestTrashListRegisteredTimestamp)
                }
            } catch (e: Exception) {
                Log.e(this.javaClass.simpleName, e.stackTraceToString())
                return ActivationResult.ACTIVATE_ERROR
            }
        }
        userRepository.getUserId()?.let { userId->
            try {
                api.activate(code, userId).let { remoteTrash ->
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
        Log.w(this.javaClass.simpleName,"Failed Activation -> code=$code")
        return ActivationResult.ACTIVATE_ERROR
    }

    enum class ActivationResult {
        ACTIVATE_SUCCESS,
        ACTIVATE_ERROR
    }
}