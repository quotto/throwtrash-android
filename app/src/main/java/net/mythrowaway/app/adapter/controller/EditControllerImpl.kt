package net.mythrowaway.app.adapter.controller

import net.mythrowaway.app.adapter.*
import net.mythrowaway.app.adapter.presenter.EditPresenterImpl
import net.mythrowaway.app.adapter.presenter.EditItem
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.domain.TrashSchedule
import net.mythrowaway.app.usecase.EditUseCase
import net.mythrowaway.app.usecase.IConfigRepository
import net.mythrowaway.app.usecase.IPersistentRepository
import net.mythrowaway.app.usecase.TrashManager
import java.text.SimpleDateFormat
import java.util.Calendar

class EditControllerImpl(private val presenterImpl: EditPresenterImpl):
    IEditController {
    private val editUseCase = EditUseCase(
        presenter = presenterImpl,
        persistence = DIContainer.resolve(
            IPersistentRepository::class.java
        )!!,
        config = DIContainer.resolve(
            IConfigRepository::class.java
        )!!,
        trashManager = DIContainer.resolve(
            TrashManager::class.java
        )!!
    )
    override fun saveTrashData(item: EditItem) {
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
                    val sdfSource = SimpleDateFormat("yyyy/MM/dd")
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

    override fun checkOtherText(text: String,view: IEditView) {
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
     * viewより渡されたEditItemの内容でUseCaseの状態を変更、Viewにそのまま設定指示を出す
     */
    override fun loadTrashData(view: IEditView,itemEditItem: EditItem) {
        editUseCase.setScheduleCount(itemEditItem.scheduleItem.size)
    }
}