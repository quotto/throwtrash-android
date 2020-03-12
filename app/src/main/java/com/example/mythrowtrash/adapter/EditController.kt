package com.example.mythrowtrash.adapter

import com.example.mythrowtrash.usecase.TrashManager
import com.example.mythrowtrash.domain.TrashData
import com.example.mythrowtrash.domain.TrashSchedule
import com.example.mythrowtrash.usecase.*
import java.util.Calendar

class EditController(private val presenter: EditPresenter):
    IEditController {
    private val editUseCase = EditUseCase(presenter,DIContainer.resolve(IPersistentRepository::class.java)!!,
        DIContainer.resolve(TrashManager::class.java)!!)
    override fun saveTrashData(viewModel: EditViewModel) {
        val trashData = TrashData()
        trashData.type = viewModel.type
        if(viewModel.type == "other")  trashData.trash_val = viewModel.trashVal
        viewModel.schedule.forEach {
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
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                    when(it.evweekStartValue) {
                        "next" -> calendar.add(Calendar.DAY_OF_MONTH,7)
                    }
                    hashMapOf(
                        "weekday" to it.evweekWeekdayValue,
                        "start" to "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)+1}-${calendar.get(
                            Calendar.DAY_OF_MONTH)}"
                    )
                }
                else -> ""
            }
            trashData.schedules.add(schedule)
        }

        if(viewModel.id != null) {
            trashData.id = viewModel.id!!
            editUseCase.updateTrashData(trashData)
        } else {
            editUseCase.saveTrashData(trashData)
        }
    }

    override fun deleteSchedule(removed_index: Int) {
        editUseCase.deleteTrashSchedule(removed_index)
    }

    override fun checkOtherText(text: String,view: IEditView) {
        presenter.showOtherTextError(Validator.validateOtherText(text))
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
}