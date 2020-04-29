package net.mythrowaway.app.adapter.controller

import net.mythrowaway.app.adapter.DIContainer
import net.mythrowaway.app.adapter.IScheduleListView
import net.mythrowaway.app.adapter.presenter.ScheduleListPresenterImpl
import net.mythrowaway.app.usecase.IConfigRepository
import net.mythrowaway.app.usecase.IPersistentRepository
import net.mythrowaway.app.usecase.ScheduleListUseCase
import net.mythrowaway.app.usecase.TrashManager

class ScheduleListController(private val view: IScheduleListView) {
    private val usecase = ScheduleListUseCase(
        trashManager = DIContainer.resolve(
            TrashManager::class.java
        )!!,
        persistent = DIContainer.resolve(
            IPersistentRepository::class.java
        )!!,
        config = DIContainer.resolve(
            IConfigRepository::class.java
        )!!,
        presenter = ScheduleListPresenterImpl(
            DIContainer.resolve(
                TrashManager::class.java
            )!!,
            view
        )
    )
    fun showScheduleList() {
        usecase.showScheduleList()
    }

    fun deleteSchedule(id: String) {
        usecase.deleteList(id)
    }
}