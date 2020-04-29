package com.example.mythrowtrash.adapter

import com.example.mythrowtrash.domain.TrashData

interface IEditView {
    fun showOtherTextError(resultCode: Int)
    fun addTrashSchedule(nextAdd: Boolean, deleteEnabled: Boolean)
    fun deleteTrashSchedule(delete_index:Int, nextAdd: Boolean)
    fun complete()
    fun showTrashData(viewModel: EditViewModel)
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
}

interface IConnectView {
    fun setEnabledStatus(viewModel: ConnectViewModel)
}