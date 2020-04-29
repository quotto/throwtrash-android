package net.mythrowaway.app.usecase

import android.util.Log

class ActivateUseCase(
    private val adapter: IAPIAdapter,
    private val config: IConfigRepository,
    private val trashManager: TrashManager,
    private val persist: IPersistentRepository,
    private val presenter: IActivatePresenter
) {
    fun activate(code: String) {
        adapter.activate(code)?.let {registeredData ->
            Log.d(this.javaClass.simpleName,"Success Activation(code:$code)")
            Log.i(this.javaClass.simpleName, "Import Data -> $registeredData")
            config.setUserId(registeredData.id)
            config.setTimestamp(registeredData.timestamp)
            config.setSyncState(CalendarUseCase.SYNC_COMPLETE)
            persist.importScheduleList(registeredData.scheduleList)
            trashManager.refresh()
            presenter.success()
            return
        }
        Log.w(this.javaClass.simpleName,"Failed Activation(code:$code)")
        presenter.failed()
    }
}