package net.mythrowaway.app.adapter.controller

import net.mythrowaway.app.viewmodel.AlarmViewModel

interface AlarmControllerInterface {
    fun loadAlarmConfig()
    fun saveAlarmConfig(viewModel: AlarmViewModel)
    fun alarmToday(year: Int, month: Int, date: Int)
}

interface PublishActivationCodeControllerInterface {
    suspend fun publishActivationCode()
}

interface ActivateControllerInterface {
    suspend fun activate(code: String)
    fun checkCode(code: String)
}

interface ConnectControllerInterface {
    fun changeEnabledStatus()
}

interface AccountLinkControllerInterface {
    suspend fun accountLinkWithApp()
    suspend fun accountLinkWithLWA()
}

interface InformationControllerInterface {
    fun loadInformation()
}
