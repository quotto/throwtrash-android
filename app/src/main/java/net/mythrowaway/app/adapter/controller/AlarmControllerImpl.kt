package net.mythrowaway.app.adapter.controller

import net.mythrowaway.app.domain.AlarmConfig
import net.mythrowaway.app.usecase.AlarmUseCase
import net.mythrowaway.app.viewmodel.AlarmViewModel
import javax.inject.Inject

class AlarmControllerImpl @Inject constructor(private val useCase: AlarmUseCase):
    AlarmControllerInterface {

    override fun loadAlarmConfig() {
        useCase.loadAlarmSetting()
    }

    override fun saveAlarmConfig(viewModel: AlarmViewModel) {
        val config = AlarmConfig()
        config.enabled = viewModel.enabled
        config.hourOfDay = viewModel.hourOfDay
        config.minute = viewModel.minute
        config.notifyEveryday = viewModel.notifyEveryday
        useCase.saveAlarmConfig(config)
    }

    override fun alarmToday(year: Int, month: Int, date: Int) {
        useCase.alarmToday(year,month,date)
    }
}