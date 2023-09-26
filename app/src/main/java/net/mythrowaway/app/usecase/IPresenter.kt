package net.mythrowaway.app.usecase

import net.mythrowaway.app.adapter.*
import net.mythrowaway.app.domain.AccountLinkInfo
import net.mythrowaway.app.domain.AlarmConfig
import net.mythrowaway.app.domain.TrashData

interface EditPresenterInterface {
    fun complete(resultCode: EditUseCase.ResultCode)
    fun showError(resultCode: EditUseCase.ResultCode)
    fun addTrashSchedule(scheduleCount:Int)
    fun deleteTrashSchedule(delete_index: Int, scheduleCount:Int)
    fun loadTrashData(trashData: TrashData)
    fun setView(view: EditViewInterface)
}

interface CalendarPresenterInterface {
    fun setCalendar(year:Int, month:Int, trashList:Array<ArrayList<TrashData>>, dateList:ArrayList<Int>)
    fun setView(view: CalendarViewInterface)
}

interface ScheduleListPresenterInterface {
    fun showScheduleList(scheduleList: ArrayList<TrashData>)
    fun setView(view: ScheduleListViewInterface)
}

interface AlarmPresenterInterface {
    fun notifyAlarm(trashArray: ArrayList<TrashData>)
    fun loadAlarmConfig(alarmConfig: AlarmConfig)
    fun setView(view: AlarmViewInterface)
}

interface PublishCodePresenterInterface {
    fun showActivationCode(activationCode: String)
    fun showPublishCodeError()
    fun setView(view: PublishCodeViewInterface)
}

interface ActivatePresenterInterface {
    fun notify(resultCode: ActivateUseCase.ActivationResult)
    fun setView(view: ActivateViewInterface)
}

interface ConnectPresenterInterface {
    fun changeEnabledStatus(status: ConnectUseCase.ConnectStatus)
    fun setView(view: ConnectViewInterface)
}

interface AccountLinkPresenterInterface {
    suspend fun startAccountLink(accountLinkInfo: AccountLinkInfo)
    suspend fun handleError()
    fun setView(view: AccountLinkViewInterface)
}

interface InformationPresenterInterface {
    fun showUserInfo(accountId: String)
    fun setView(view: InformationViewInterface)
}