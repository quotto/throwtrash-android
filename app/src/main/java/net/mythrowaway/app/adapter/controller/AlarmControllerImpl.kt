package net.mythrowaway.app.adapter.controller

import net.mythrowaway.app.adapter.DIContainer
import net.mythrowaway.app.adapter.IAlarmView
import net.mythrowaway.app.adapter.presenter.AlarmPresenterImpl
import net.mythrowaway.app.domain.AlarmConfig
import net.mythrowaway.app.usecase.AlarmUseCase
import net.mythrowaway.app.usecase.IConfigRepository
import net.mythrowaway.app.usecase.TrashManager
import net.mythrowaway.app.viewmodel.AlarmViewModel

class AlarmControllerImpl(view: IAlarmView):
    IAlarmController {
    private val usecase: AlarmUseCase = AlarmUseCase(
            DIContainer.resolve(
                TrashManager::class.java
            )!!,
            DIContainer.resolve(
                IConfigRepository::class.java
            )!!,
            AlarmPresenterImpl(
                view,
                DIContainer.resolve(
                    TrashManager::class.java
                )!!
            )
        )
    override fun loadAlarmConfig() {
        usecase.loadAlarmSetting()
    }

    override fun saveAlarmConfig(viewModel: AlarmViewModel) {
        val config = AlarmConfig()
        config.enabled = viewModel.enabled
        config.hourOfDay = viewModel.hourOfDay
        config.minute = viewModel.minute
        config.notifyEveryday = viewModel.notifyEveryday
        usecase.saveAlarmConfig(config)
    }

    override fun alarmToday(year: Int, month: Int, date: Int) {
        usecase.alarmToday(year,month,date)
    }
}