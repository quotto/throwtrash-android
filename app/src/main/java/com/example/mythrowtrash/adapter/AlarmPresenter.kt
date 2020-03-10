package com.example.mythrowtrash.adapter

import com.example.mythrowtrash.domain.AlarmConfig
import com.example.mythrowtrash.domain.TrashData
import com.example.mythrowtrash.usecase.IAlarmPresenter
import com.example.mythrowtrash.usecase.TrashManager

class AlarmViewModel {
    var enabled:Boolean = false
    var hourOfDay:Int = 7
    var minute: Int = 0
    var notifyEveryday:Boolean = false
}

class AlarmPresenter(private val view: IAlarmView, private val trashManager: TrashManager): IAlarmPresenter {
    /**
     * TrashDataから単純な文字配列に経関する
     */
    override fun notifyAlarm(trashArray: ArrayList<TrashData>) {
        val trashNameArray: ArrayList<String> = ArrayList()
        trashArray.forEach {
            trashNameArray.add(trashManager.getTrashName(it.type, it.trash_val))
        }
        view.notify(trashNameArray.distinct())
    }

    override fun loadAlarmConfig(alarmConfig: AlarmConfig) {
        val alarmViewModel = AlarmViewModel()
        alarmViewModel.enabled = alarmConfig.enabled
        alarmViewModel.hourOfDay = alarmConfig.hourOfDay
        alarmViewModel.minute = alarmConfig.minute
        alarmViewModel.notifyEveryday = alarmConfig.notifyEveryday
        view.update(alarmViewModel)
    }
}