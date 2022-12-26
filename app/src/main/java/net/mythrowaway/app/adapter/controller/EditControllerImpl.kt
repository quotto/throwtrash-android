package net.mythrowaway.app.adapter.controller

import net.mythrowaway.app.adapter.*
import net.mythrowaway.app.viewmodel.EditItemViewModel
import net.mythrowaway.app.domain.ExcludeDate
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.domain.TrashSchedule
import net.mythrowaway.app.usecase.EditUseCase
import net.mythrowaway.app.viewmodel.EditViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class EditControllerImpl @Inject constructor(
    private val editUseCase: EditUseCase): EditControllerInterface {
    override fun saveTrashData(item: EditItemViewModel) {
        val trashData = TrashData()
        trashData.type = item.type
        if(item.type == "other")  trashData.trash_val = item.trashVal
        item.scheduleItem.forEach {
            val schedule = TrashSchedule()
            schedule.type = it.type
            schedule.value = when(it.type) {
                "weekday" -> {
                    it.weekdayValue
                }
                "month" -> {
                    it.monthValue
                }
                "biweek" -> {
                    "${it.numOfWeekWeekdayValue}-${it.numOfWeekNumberValue}"
                }
                "evweek" -> {
                    val sdfSource = SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN)
                    val dt = sdfSource.parse(it.evweekStartValue)
                    val calendar = Calendar.getInstance()
                    calendar.time = dt
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - (calendar.get(Calendar.DAY_OF_WEEK) - 1))
                    hashMapOf(
                        "weekday" to it.evweekWeekdayValue,
                        "start" to "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)+1}-${calendar.get(
                            Calendar.DAY_OF_MONTH)}",
                        "interval" to it.evweekIntervalValue
                    )
                }
                else -> ""
            }
            trashData.schedules.add(schedule)
        }

        trashData.excludes = item.excludes.map {
            ExcludeDate().apply {
                month = it.first
                date = it.second
            }
        }

        if(item.id != null) {
            trashData.id = item.id!!
            editUseCase.updateTrashData(trashData)
        } else {
            editUseCase.saveTrashData(trashData)
        }
    }

    override fun deleteSchedule(removed_index: Int) {
        editUseCase.deleteTrashSchedule(removed_index)
    }

    override fun checkOtherText(text: String,view: EditViewInterface) {
        editUseCase.validateOtherTrashText(text)
    }

    override fun addTrashSchedule() {
        editUseCase.addTrashSchedule()
    }

    override fun loadTrashData(id: String?) {
        if(id == null) {
            editUseCase.addTrashSchedule()
        }
        else {
            editUseCase.loadTrashData(id)
        }
    }

    /**
     * viewより渡されたEditItemの内容でUseCaseの状態を変更
     */
    override fun loadTrashData(view: EditViewInterface, editViewModel: EditViewModel) {
        editUseCase.setScheduleCount(editViewModel.itemCount)
    }
}