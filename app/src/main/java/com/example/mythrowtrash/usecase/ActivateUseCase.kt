package com.example.mythrowtrash.usecase

class ActivateUseCase(
        private val adapter:IAPIAdapter,
        private val config: IConfigRepository,
        private val trashManager: TrashManager,
        private val persist: IPersistentRepository,
        private val presenter: IActivatePresenter) {
    fun activate(code: String) {
        adapter.activate(code)?.let {registeredData ->
            config.setUserId(registeredData.id)
            config.setTimestamp(registeredData.timestamp)
            config.setSyncState(CalendarUseCase.SYNC_COMPLETE)
            persist.importScheduleList(registeredData.scheduleList)
            trashManager.refresh()
            presenter.success()
            return
        }
        presenter.failed()
    }
}