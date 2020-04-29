package net.mythrowaway.app.usecase

import net.mythrowaway.app.domain.TrashData

class EditUseCase(
    private val presenter: IEditPresenter,
    private val persistence: IPersistentRepository,
    private val config: IConfigRepository,
    private val trashManager: TrashManager
) {
    private var scheduleCount:Int = 0

    /**
     * 入力されたゴミ出し予定に対してIDを採番して永続データに保存する
     */
    fun saveTrashData(trashData: TrashData) {
        println("[MyApp] trash manager add: $trashData")

        if(trashManager.getScheduleCount() >= 10) {
            // スケジュール数上限のためエラー
            presenter.complete(ResultCode.MAX_SCHEDULE)
            return
        }
        persistence.saveTrashData(trashData)
        trashManager.refresh()
        config.updateLocalTimestamp()
        config.setSyncState(CalendarUseCase.SYNC_WAITING)
        presenter.complete(ResultCode.SUCCESS)
    }

    /**
     * 入力スケジュールが追加された
     */
    fun addTrashSchedule() {
        if(scheduleCount < 3) {
            scheduleCount++
            println("[MyApp] schedule count: $scheduleCount")
            presenter.addTrashSchedule(scheduleCount)
        }
    }

    /**
     * 入力スケジュールが削除された
     */
    fun deleteTrashSchedule(delete_index:Int) {
        if(scheduleCount > 1) {
            scheduleCount--
            println("[MyApp] schedule count: $scheduleCount")
            presenter.deleteTrashSchedule(delete_index, scheduleCount)
        }
    }

    /**
     * 登録済みスケジュールの更新
     */
    fun updateTrashData(updateData: TrashData) {
        persistence.updateTrashData(updateData)
        trashManager.refresh()
        presenter.complete(ResultCode.SUCCESS)
    }

    /**
     * 登録済みスケジュールを表示する
     */
    fun loadTrashData(id:String) {
        persistence.getTrashData(id)?.let {
            scheduleCount = it.schedules.size
            presenter.loadTrashData(it)
        }
    }

    /**
     * その他のゴミの入力チェック
     */
    fun validateOtherTrashText(text:String) {
        when {
            text.isEmpty() -> {
                presenter.showError(ResultCode.INVALID_OTHER_TEXT_EMPTY)
            }
            text.length > 10 -> {
                presenter.showError(ResultCode.INVALID_OTHER_TEXT_OVER)
            }
            Regex("^[A-z0-9Ａ-ｚ０-９ぁ-んァ-ヶー一-龠\\s]+$").find(text)?.value == null -> {
                presenter.showError(ResultCode.INVALID_OTHER_TEXT_CHARACTER)
            }
            else ->
                presenter.showError(ResultCode.SUCCESS)
        }
    }

    enum class ResultCode {
        SUCCESS,
        MAX_SCHEDULE,
        INVALID_OTHER_TEXT_EMPTY,
        INVALID_OTHER_TEXT_OVER,
        INVALID_OTHER_TEXT_CHARACTER
    }
}