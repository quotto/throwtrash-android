package com.example.mythrowtrash.adapter

import com.example.mythrowtrash.domain.TrashData
import com.example.mythrowtrash.usecase.IEditPresenter
import com.example.mythrowtrash.usecase.Validator
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class EditViewModelSchedule {
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
    var id:Int? = null
    var type:String = ""
    var trashVal: String = ""
    var schedule:ArrayList<EditViewModelSchedule> = ArrayList()
}

class EditPresenter(private val view: IEditView): IEditPresenter {
    override fun complete(trashData: TrashData) {
        view.complete(trashData)
    }

    override fun showOtherTextError(resultCode: Int) {
        when(resultCode) {
            Validator.RESULT_EMPTY, Validator.RESULT_OVER_CHAR ->
                view.showOtherTextError(1)
            Validator.RESULT_INVALID_CHAR ->
                view.showOtherTextError(2)
            else -> view.showOtherTextError(0)
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
            val scheduleViewModel = EditViewModelSchedule()
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
                    val cCal = Calendar.getInstance()
                    v["start"]?.let{
                        val ymd = it.split("-")
                        cCal.set(Calendar.YEAR,ymd[0].toInt())
                        cCal.set(Calendar.MONTH,ymd[1].toInt() - 1)
                        cCal.set(Calendar.DATE,ymd[2].toInt())
                        val weekOfYear = cCal.get(Calendar.WEEK_OF_YEAR) % 2
                        val thisWeekOfYear = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) % 2

                        scheduleViewModel.evweekWeekdayValue = v["weekday"] ?: ""
                        scheduleViewModel.evweekStartValue = if(weekOfYear == thisWeekOfYear) {
                            EditViewModelSchedule.EVWEEK_START_THIS_WEEK
                        } else {
                            EditViewModelSchedule.EVWEEK_START_NEXT_WEEK
                        }
                    }
                }
            }
            viewModel.schedule.add(scheduleViewModel)
        }

        view.showTrashDtada(viewModel)
    }
}