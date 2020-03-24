package com.example.mythrowtrash.adapter

import com.example.mythrowtrash.usecase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CalendarController(private val view: ICalendarView,private val calendarManager: ICalendarManager): ICalendarController {
    private val usecase = CalendarUseCase(
        calendarManager = calendarManager,
        presenter = CalendarPresenter(view,calendarManager),
        trashManager = DIContainer.resolve(TrashManager::class.java)!!,
        config = DIContainer.resolve(IConfigRepository::class.java)!!,
        apiAdapter = DIContainer.resolve(IAPIAdapter::class.java)!!,
        persist = DIContainer.resolve(IPersistentRepository::class.java)!!)

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