package net.mythrowaway.app.usecase

import net.mythrowaway.app.domain.AlarmConfig
import net.mythrowaway.app.domain.TrashData

interface IEditPresenter {
    fun complete(resultCode: EditUseCase.ResultCode)
    fun showError(resultCode: EditUseCase.ResultCode)
    fun addTrashSchedule(scheduleCount:Int)
    fun deleteTrashSchedule(delete_index: Int, scheduleCount:Int)
    fun loadTrashData(trashData: TrashData)
}

interface ICalendarPresenter {
    fun setCalendar(year:Int, month:Int, trashList:Array<ArrayList<String>>, dateList:ArrayList<Int>)
}

interface IScheduleListPresenter {
    fun showScheduleList(scheduleList: ArrayList<TrashData>)
}

interface IAlarmPresenter {
    fun notifyAlarm(trashArray: ArrayList<TrashData>)
    fun loadAlarmConfig(alarmConfig: AlarmConfig)
}

interface IPublishCodePresenter {
    fun showActivationCode(activationCode: String)
    fun showPublishCodeError()
}

interface  IActivatePresenter {
    fun success()
    fun failed()
}

interface IConnectPresenter {
    fun changeEnabledStatus(status: ConnectUseCase.ConnectStatus)
}