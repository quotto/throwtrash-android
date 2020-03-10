package com.example.mythrowtrash.adapter

import com.example.mythrowtrash.domain.AlarmConfig
import com.example.mythrowtrash.usecase.AlarmUseCase
import com.example.mythrowtrash.usecase.IConfigRepository
import com.example.mythrowtrash.usecase.TrashManager

class AlarmController(view: IAlarmView): IAlarmController {
    private val usecase: AlarmUseCase = AlarmUseCase(
        DIContainer.resolve(TrashManager::class.java)!!,
        DIContainer.resolve(IConfigRepository::class.java)!!,
        AlarmPresenter(view, DIContainer.resolve(TrashManager::class.java)!!)
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