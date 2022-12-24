package net.mythrowaway.app.usecase

import android.util.Log
import net.mythrowaway.app.service.TrashManager
import javax.inject.Inject

class ScheduleListUseCase @Inject constructor(
  private val trashManager: TrashManager,
  private val persistent: DataRepositoryInterface,
  private val config: ConfigRepositoryInterface,
  private val presenter: ScheduleListPresenterInterface
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
//        config.updateLocalTimestamp()
        config.setSyncState(CalendarUseCase.SYNC_WAITING)
        showScheduleList()
    }
}