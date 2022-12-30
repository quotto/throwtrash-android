package net.mythrowaway.app.adapter.controller

import net.mythrowaway.app.adapter.EditViewInterface
import net.mythrowaway.app.viewmodel.AlarmViewModel
import net.mythrowaway.app.viewmodel.EditItemViewModel
import net.mythrowaway.app.viewmodel.EditViewModel


interface EditControllerInterface {
    fun checkOtherText(text: String, view: EditViewInterface)
    fun addTrashSchedule()
    fun saveTrashData(item: EditItemViewModel)
    fun deleteSchedule(removed_index:Int)
    fun loadTrashData(id: String?)
    fun loadTrashData(view:EditViewInterface, editViewModel: EditViewModel)
}

interface CalendarControllerInterface {
    suspend fun syncData()
    suspend fun generateCalendarFromPositionAsync(position: Int)
}

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
