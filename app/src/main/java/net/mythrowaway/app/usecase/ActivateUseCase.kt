package net.mythrowaway.app.usecase

import android.util.Log
import javax.inject.Inject

class ActivateUseCase @Inject constructor(
    private val api: MobileApiInterface,
    private val config: ConfigRepositoryInterface,
    private val persist: DataRepositoryInterface,
) {
    fun activate(code: String): ActivationResult {
        config.getUserId()?.let { userId->
            api.activate(code, userId)?.let { registeredData ->
                Log.d(this.javaClass.simpleName,"Success Activation -> code=$code")
                Log.i(this.javaClass.simpleName, "Import Data -> $registeredData")
                config.setTimestamp(registeredData.timestamp)
                config.setSyncWait()
                persist.importScheduleList(registeredData.scheduleList)
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