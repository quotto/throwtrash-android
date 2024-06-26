package net.mythrowaway.app.adapter

import net.mythrowaway.app.viewmodel.*

interface EditViewInterface {
    fun showOtherTextError(resultCode: Int)
    fun addTrashSchedule(nextAdd: Boolean, deleteEnabled: Boolean)
    fun deleteTrashSchedule(delete_index:Int, nextAdd: Boolean)
    fun complete()
    fun setTrashData(item: EditItemViewModel)
    fun showErrorMaxSchedule()
}

interface ScheduleListViewInterface {
    fun update(viewModel: ArrayList<ScheduleViewModel>)
}

interface AlarmViewInterface {
    fun notify(trashList: List<String>)
    fun update(viewModel: AlarmViewModel)
}

interface PublishCodeViewInterface {
    fun showActivationCode(code: String)
    fun showError()
}

interface ActivateViewInterface {
    fun success()
    fun failed()
    fun invalidCodeError()
    fun validCode()
}

interface ConnectViewInterface {
    fun setEnabledStatus(viewModel: ConnectViewModel)
}

interface AccountLinkViewInterface {
    suspend fun startAccountLinkWithAlexaApp()
    fun startAccountLinkWithLWA()
    suspend fun showError()
}
interface InformationViewInterface {
    fun showUserInfo(viewModel: InformationViewModel)
}