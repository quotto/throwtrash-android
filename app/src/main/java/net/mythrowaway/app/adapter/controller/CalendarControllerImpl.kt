package net.mythrowaway.app.adapter.controller

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mythrowaway.app.adapter.presenter.CalendarPresenterImpl
import net.mythrowaway.app.adapter.DIContainer
import net.mythrowaway.app.adapter.ICalendarView
import net.mythrowaway.app.usecase.*

class CalendarControllerImpl(
    private val view: ICalendarView,
    private val calendarManager: ICalendarManager):
    ICalendarController {
    private val usecase = CalendarUseCase(
        calendarManager = calendarManager,
        presenter = CalendarPresenterImpl(
            view,
            calendarManager
        ),
        trashManager = DIContainer.resolve(
            TrashManager::class.java
        )!!,
        config = DIContainer.resolve(
            IConfigRepository::class.java
        )!!,
        apiAdapter = DIContainer.resolve(
            IAPIAdapter::class.java
        )!!,
        persist = DIContainer.resolve(
            IPersistentRepository::class.java
        )!!
    )

    override suspend fun syncData() {
        withContext(Dispatchers.IO) {
            usecase.syncData()
        }
    }

    override suspend fun generateCalendarFromPositionAsync(position: Int) {
        withContext(Dispatchers.Default) {
            val targetYM = calendarManager.addYM(
                calendarManager.getYear(),
                calendarManager.getMonth(),
                position
            )
            usecase.generateMonthSchedule(targetYM.first, targetYM.second)
        }
    }
}