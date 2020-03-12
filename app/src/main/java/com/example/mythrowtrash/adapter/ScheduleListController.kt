package com.example.mythrowtrash.adapter

import com.example.mythrowtrash.usecase.IPersistentRepository
import com.example.mythrowtrash.usecase.ScheduleListUseCase
import com.example.mythrowtrash.usecase.TrashManager

class ScheduleListController(private val view: IScheduleListView) {
    private val usecase = ScheduleListUseCase(
        DIContainer.resolve(TrashManager::class.java)!!,
        DIContainer.resolve(IPersistentRepository::class.java)!!,
        ScheduleListPresenter(DIContainer.resolve(TrashManager::class.java)!!,view)
        )
    fun showScheduleList() {
        usecase.showScheduleList()
    }

    fun deleteSchedule(id: String) {
        usecase.deleteList(id)
    }
}