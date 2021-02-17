package net.mythrowaway.app.adapter.presenter

import net.mythrowaway.app.adapter.IAlarmView
import net.mythrowaway.app.domain.AlarmConfig
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.usecase.IAlarmPresenter
import net.mythrowaway.app.usecase.TrashManager
import net.mythrowaway.app.viewmodel.AlarmViewModel
import javax.inject.Inject

class AlarmPresenterImpl @Inject constructor(
    private val trashManager: TrashManager): IAlarmPresenter {

    private lateinit var view: IAlarmView
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
        val alarmViewModel =
            AlarmViewModel()
        alarmViewModel.enabled = alarmConfig.enabled
        alarmViewModel.hourOfDay = alarmConfig.hourOfDay
        alarmViewModel.minute = alarmConfig.minute
        alarmViewModel.notifyEveryday = alarmConfig.notifyEveryday
        view.update(alarmViewModel)
    }

    override fun setView(view: IAlarmView) {
        this.view = view
    }
}

