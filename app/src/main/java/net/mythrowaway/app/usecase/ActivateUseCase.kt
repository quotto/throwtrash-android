package net.mythrowaway.app.usecase

import android.util.Log
import net.mythrowaway.app.service.TrashManager
import javax.inject.Inject

class ActivateUseCase @Inject constructor(
    private val adapter: IAPIAdapter,
    private val config: IConfigRepository,
    private val trashManager: TrashManager,
    private val persist: IPersistentRepository,
    private val presenter: IActivatePresenter
) {
    fun activate(code: String) {
        config.getUserId()?.let { userId->
            adapter.activate(code, userId)?.let {registeredData ->
                Log.d(this.javaClass.simpleName,"Success Activation -> code=$code")
                Log.i(this.javaClass.simpleName, "Import Data -> $registeredData")
                config.setTimestamp(registeredData.timestamp)
                config.setSyncState(CalendarUseCase.SYNC_COMPLETE)
                persist.importScheduleList(registeredData.scheduleList)
                trashManager.refresh()
                presenter.notify(ActivationResult.ACTIVATE_SUCCESS)
                return
            }
        }
        Log.w(this.javaClass.simpleName,"Failed Activation -> code=$code")
        presenter.notify(ActivationResult.ACTIVATE_ERROR)
    }

    fun checkCode(code: String) {
        Log.i(this.javaClass.simpleName, "checkCode -> code=$code")
        if(code.length != 10) {
            Log.d(this.javaClass.simpleName, "InvalidCode")
            presenter.notify(ActivationResult.INVALID_CODE)
        } else {
            Log.d(this.javaClass.simpleName, "ValidCode")
            presenter.notify(ActivationResult.VALID_CODE)
        }
    }

    enum class ActivationResult {
        ACTIVATE_SUCCESS,
        ACTIVATE_ERROR,
        INVALID_CODE,
        VALID_CODE
    }
}