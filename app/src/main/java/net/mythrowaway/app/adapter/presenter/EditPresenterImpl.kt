package net.mythrowaway.app.adapter.presenter

import android.util.Log
import net.mythrowaway.app.adapter.IEditView
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.usecase.EditUseCase
import net.mythrowaway.app.usecase.ICalendarManager
import net.mythrowaway.app.usecase.IEditPresenter
import net.mythrowaway.app.usecase.TrashManager
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class EditViewModelSchedule: Serializable {
    var type:String = ""
    var weekdayValue:String =""
    var monthValue: String = ""
    var numOfWeekWeekdayValue:String = ""
    var numOfWeekNumberValue:String = ""
    var evweekWeekdayValue:String = ""
    var evweekStartValue: String = "" // "this" or "next"

    companion object {
        const val EVWEEK_START_THIS_WEEK = "this"
        const val EVWEEK_START_NEXT_WEEK = "next"
    }
}
class EditViewModel {
    var id:String? = null
    var type:String = ""
    var trashVal: String = ""
    var schedule:ArrayList<EditViewModelSchedule> = ArrayList()
}

class EditPresenterImpl(
    private val calendarManager: ICalendarManager,
    private val trashManager: TrashManager,
    private val view: IEditView): IEditPresenter {
    override fun complete(resultCode: EditUseCase.ResultCode) {
        when(resultCode) {
            EditUseCase.ResultCode.SUCCESS ->
                view.complete()
            EditUseCase.ResultCode.MAX_SCHEDULE ->
                view.showErrorMaxSchedule()
            else -> {
                Log.e(this.javaClass.simpleName,"Unknown result code -> $resultCode")
            }
        }
    }

    override fun showError(resultCode: EditUseCase.ResultCode) {
        when(resultCode) {
            EditUseCase.ResultCode.INVALID_OTHER_TEXT_EMPTY,
            EditUseCase.ResultCode.INVALID_OTHER_TEXT_OVER ->
                view.showOtherTextError(1)
            EditUseCase.ResultCode.INVALID_OTHER_TEXT_CHARACTER ->
                view.showOtherTextError(2)
            else ->
                view.showOtherTextError(0)
        }
    }

    override fun addTrashSchedule(scheduleCount:Int)
    {
        // 追加可能なスケジュールは3つまで,1つ以上のスケジュールを残しておく
        view.addTrashSchedule(scheduleCount < 3,scheduleCount > 1)
    }

    override fun deleteTrashSchedule(delete_index: Int, scheduleCount: Int) {
        // スケジュールが3→2となった場合は追加ボタンを復活
        view.deleteTrashSchedule(delete_index, scheduleCount == 2)
    }

    override fun loadTrashData(trashData: TrashData) {
        val viewModel = EditViewModel()
        viewModel.id = trashData.id
        viewModel.type = trashData.type
        viewModel.trashVal = trashData.trash_val ?: ""
        trashData.schedules.forEach {trashSchedule ->
            val scheduleViewModel =
                EditViewModelSchedule()
            scheduleViewModel.type = trashSchedule.type
            when(trashSchedule.type) {
                "weekday" -> scheduleViewModel.weekdayValue = trashSchedule.value as String
                "month" -> scheduleViewModel.monthValue = trashSchedule.value as String
                "biweek" -> {
                    val v = (trashSchedule.value as String).split("-")
                    scheduleViewModel.numOfWeekNumberValue = v[1]
                    scheduleViewModel.numOfWeekWeekdayValue = v[0]
                }
                "evweek" -> {
                    val v:HashMap<String,String> = trashSchedule.value as HashMap<String,String>
                    v["start"]?.let{start ->
                        scheduleViewModel.evweekWeekdayValue = v["weekday"] ?: ""
                        scheduleViewModel.evweekStartValue =
                            if(trashManager.isThisWeek(start,calendarManager.getTodayStringDate(Calendar.getInstance()))) {
                                EditViewModelSchedule.EVWEEK_START_THIS_WEEK
                            } else {
                                EditViewModelSchedule.EVWEEK_START_NEXT_WEEK
                            }
                    }
                }
            }
            viewModel.schedule.add(scheduleViewModel)
        }

        view.showTrashData(viewModel)
    }
}