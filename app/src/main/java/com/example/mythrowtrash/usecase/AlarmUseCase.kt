package com.example.mythrowtrash.usecase

import com.example.mythrowtrash.domain.AlarmConfig

class AlarmUseCase(private val trashManager: TrashManager,private val config: IConfigRepository, private val presenter: IAlarmPresenter) {
    /**
     * アラームに関する設定を読み込む
     */
    fun loadAlarmSetting() {
        presenter.loadAlarmConfig(config.getAlarmConfig())
    }

    /**
     * アラーム設定の保存
     */
    fun saveAlarmConfig(alarmConfig: AlarmConfig) {
        config.saveAlarmConfig(alarmConfig)
    }

    /**
     * 指定された年月日のゴミ出し可能なTrashDataを通知する
     */
    fun alarmToday(year:Int, month: Int, date:Int) {
        presenter.notifyAlarm(trashManager.getTodaysTrash(year, month, date))
    }
}