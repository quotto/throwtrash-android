package net.mythrowaway.app.adapter.controller

import net.mythrowaway.app.adapter.IEditView
import net.mythrowaway.app.viewmodel.AlarmViewModel
import net.mythrowaway.app.viewmodel.EditItemViewModel
import net.mythrowaway.app.viewmodel.EditViewModel


interface IEditController {
    fun checkOtherText(text: String, view: IEditView)
    fun addTrashSchedule()
    fun saveTrashData(item: EditItemViewModel)
    fun deleteSchedule(removed_index:Int)
    fun loadTrashData(id: String?)
    fun loadTrashData(view:IEditView,editViewModel: EditViewModel)
}

interface ICalendarController {
    suspend fun syncData()
    suspend fun generateCalendarFromPositionAsync(position: Int)
}

interface IAlarmController {
    fun loadAlarmConfig()
    fun saveAlarmConfig(viewModel: AlarmViewModel)
    fun alarmToday(year: Int, month: Int, date: Int)
}

interface IPublishCodeController {
    suspend fun publishActivationCode()
}

interface IActivateController {
    suspend fun activate(code: String)
    fun checkCode(code: String)
}

interface IConnectController {
    fun changeEnabledStatus()
}

interface IAccountLinkController {
    suspend fun accountLinkWithApp()
    suspend fun accountLinkWithLWA()
}

interface IInformationController {
    fun loadInformation()
}
