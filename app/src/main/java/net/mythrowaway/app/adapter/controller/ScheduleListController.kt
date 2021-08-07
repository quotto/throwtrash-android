package net.mythrowaway.app.adapter.controller

import net.mythrowaway.app.usecase.ScheduleListUseCase
import javax.inject.Inject

class ScheduleListController @Inject constructor(private val useCase: ScheduleListUseCase) {
    fun showScheduleList() {
        useCase.showScheduleList()
    }

    fun deleteSchedule(id: String) {
        useCase.deleteList(id)
    }
}