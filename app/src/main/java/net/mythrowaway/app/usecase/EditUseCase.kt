package net.mythrowaway.app.usecase

import android.util.Log
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
        Log.i(this.javaClass.simpleName, "Save new trash -> $trashData")

        if(trashManager.getScheduleCount() >= 10) {
            // スケジュール数上限のためエラー
            presenter.complete(ResultCode.MAX_SCHEDULE)
            return
        }
        persistence.saveTrashData(trashData)
        trashManager.refresh()
        presenter.complete(ResultCode.SUCCESS)
    }

    /**
     * 入力スケジュールが追加された
     */
    fun addTrashSchedule() {
        if(scheduleCount < 3) {
            scheduleCount++
            Log.d(this.javaClass.simpleName, "add schedule, now schedule count -> $scheduleCount")
            presenter.addTrashSchedule(scheduleCount)
        }
    }

    /**
     * 入力スケジュールが削除された
     */
    fun deleteTrashSchedule(delete_index:Int) {
        if(scheduleCount > 1) {
            scheduleCount--
            Log.d(this.javaClass.simpleName, "delete schedule, now schedule count -> $scheduleCount")
            presenter.deleteTrashSchedule(delete_index, scheduleCount)
        }
    }

    /**
     * 登録済みスケジュールの更新
     */
    fun updateTrashData(updateData: TrashData) {
        persistence.updateTrashData(updateData)
        Log.i(this.javaClass.simpleName, "update trash data -> $updateData")
        trashManager.refresh()
        presenter.complete(ResultCode.SUCCESS)
    }

    /**
     * 登録済みスケジュールを表示する
     */
    fun loadTrashData(id:String) {
        persistence.getTrashData(id)?.let {
            Log.i(this.javaClass.simpleName, "load trash data -> $it")
            scheduleCount = it.schedules.size
            presenter.loadTrashData(it)
        }
    }

    /**
     * 現在のスケジュールカウントを設定する
     */
    fun setScheduleCount(count:Int) {
        scheduleCount = count
    }

    /**
     * その他のゴミの入力チェック
     */
    fun validateOtherTrashText(text:String) {
        when {
            text.isEmpty() -> {
                Log.d(this.javaClass.simpleName, "other trash text is empty")
                presenter.showError(ResultCode.INVALID_OTHER_TEXT_EMPTY)
            }
            text.length > 10 -> {
                Log.d(this.javaClass.simpleName, "other trash text is over length")
                presenter.showError(ResultCode.INVALID_OTHER_TEXT_OVER)
            }
            Regex("^[A-z0-9Ａ-ｚ０-９ぁ-んァ-ヶー一-龠\\s]+$").find(text)?.value == null -> {
                Log.d(this.javaClass.simpleName, "other trash text has invalid character")
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