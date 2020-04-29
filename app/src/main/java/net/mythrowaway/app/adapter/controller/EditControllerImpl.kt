package net.mythrowaway.app.adapter.controller

import net.mythrowaway.app.adapter.*
import net.mythrowaway.app.adapter.presenter.EditPresenterImpl
import net.mythrowaway.app.adapter.presenter.EditViewModel
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.domain.TrashSchedule
import net.mythrowaway.app.usecase.EditUseCase
import net.mythrowaway.app.usecase.IConfigRepository
import net.mythrowaway.app.usecase.IPersistentRepository
import net.mythrowaway.app.usecase.TrashManager
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
}