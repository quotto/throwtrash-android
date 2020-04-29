package net.mythrowaway.app.usecase

import android.util.Log

class ScheduleListUseCase(
    private val trashManager: TrashManager,
    private val persistent: IPersistentRepository,
    private val config: IConfigRepository,
    private val presenter: IScheduleListPresenter
) {
    /**
     * スケジュールの初期表示
     * 永続ストアからデータを取得する
     */
    fun showScheduleList() {
        presenter.showScheduleList(
            persistent.getAllTrashSchedule()
        )
    }

    /**
     * 指定されたIDのデータを永続ストアから削除する
     */
    fun deleteList(id: String) {
        Log.i(this.javaClass.simpleName, "Delete Trash Data -> id:$id")
        persistent.deleteTrashData(id)
        trashManager.refresh()
        config.updateLocalTimestamp()
        config.setSyncState(CalendarUseCase.SYNC_WAITING)
        showScheduleList()
    }
}