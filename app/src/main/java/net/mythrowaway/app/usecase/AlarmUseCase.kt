package net.mythrowaway.app.usecase

import net.mythrowaway.app.domain.AlarmConfig
import net.mythrowaway.app.domain.TrashData
import javax.inject.Inject

class AlarmUseCase @Inject constructor(
    private val trashManager: TrashManager,
    private val config: IConfigRepository,
    private val presenter: IAlarmPresenter) {
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
        val trashList: ArrayList<TrashData> = trashManager.getTodaysTrash(year, month, date)
        val alarmConfig: AlarmConfig = config.getAlarmConfig()

        // アラーム通知が有効かつ毎日通知に設定されているか、今日出せるゴミがある場合は通知する
        if(alarmConfig.enabled &&
            (alarmConfig.notifyEveryday || trashList.isNotEmpty())) {
            presenter.notifyAlarm(trashList)
        }
    }
}