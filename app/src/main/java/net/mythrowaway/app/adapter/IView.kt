package net.mythrowaway.app.adapter

import net.mythrowaway.app.adapter.presenter.*

interface IEditView {
    fun showOtherTextError(resultCode: Int)
    fun addTrashSchedule(nextAdd: Boolean, deleteEnabled: Boolean)
    fun deleteTrashSchedule(delete_index:Int, nextAdd: Boolean)
    fun complete()
    fun setTrashData(item: EditItem)
    fun showErrorMaxSchedule()
}

interface ICalendarView {
    fun update(viewModel: CalendarViewModel)
}

interface IScheduleListView {
    fun update(viewModel: ArrayList<ScheduleViewModel>)
}

interface IAlarmView {
    fun notify(trashList: List<String>)
    fun update(viewModel: AlarmViewModel)
}

interface IPublishCodeView {
    fun showActivationCode(code: String)
    fun showError()
}

interface IActivateView {
    fun success()
    fun failed()
    fun invalidCodeError()
    fun validCode()
}

interface IConnectView {
    fun setEnabledStatus(viewModel: ConnectViewModel)
}