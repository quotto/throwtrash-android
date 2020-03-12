package com.example.mythrowtrash.usecase

import com.example.mythrowtrash.domain.TrashData

class EditUseCase(private val presenter:IEditPresenter,private val persistence:IPersistentRepository,private val trashManager: TrashManager) {
    private var scheduleCount:Int = 0

    /**
     * 入力されたゴミ出し予定に対してIDを採番して永続データに保存する
     */
    fun saveTrashData(trashData: TrashData) {
        println("[MyApp] trash manager add: $trashData")
        persistence.saveTrashData(trashData)
        trashManager.refresh()
        presenter.complete(trashData)
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
        presenter.complete(updateData)
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
}