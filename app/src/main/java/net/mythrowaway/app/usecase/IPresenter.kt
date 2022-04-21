package net.mythrowaway.app.usecase

import net.mythrowaway.app.adapter.*
import net.mythrowaway.app.domain.AccountLinkInfo
import net.mythrowaway.app.domain.AlarmConfig
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.viewmodel.InformationViewModel

interface IEditPresenter {
    fun complete(resultCode: EditUseCase.ResultCode)
    fun showError(resultCode: EditUseCase.ResultCode)
    fun addTrashSchedule(scheduleCount:Int)
    fun deleteTrashSchedule(delete_index: Int, scheduleCount:Int)
    fun loadTrashData(trashData: TrashData)
    fun setView(view: IEditView)
}

interface ICalendarPresenter {
    fun setCalendar(year:Int, month:Int, trashList:Array<ArrayList<String>>, dateList:ArrayList<Int>)
    fun setView(view: ICalendarView)
}

interface IScheduleListPresenter {
    fun showScheduleList(scheduleList: ArrayList<TrashData>)
    fun setView(view: IScheduleListView)
}

interface IAlarmPresenter {
    fun notifyAlarm(trashArray: ArrayList<TrashData>)
    fun loadAlarmConfig(alarmConfig: AlarmConfig)
    fun setView(view: IAlarmView)
}

interface IPublishCodePresenter {
    fun showActivationCode(activationCode: String)
    fun showPublishCodeError()
    fun setView(view: IPublishCodeView)
}

interface  IActivatePresenter {
    fun notify(resultCode: ActivateUseCase.ActivationResult)
    fun setView(view: IActivateView)
}

interface IConnectPresenter {
    fun changeEnabledStatus(status: ConnectUseCase.ConnectStatus)
    fun setView(view: IConnectView)
}

interface IAccountLinkPresenter {
    suspend fun startAccountLink(accountLinkInfo: AccountLinkInfo)
    suspend fun handleError()
    fun setView(view: IAccountLinkView)
}

interface IInformationPresenter {
    fun showUserInfo(accountId: String)
    fun setView(view: IInformationView)
}