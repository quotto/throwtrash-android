package com.example.mythrowtrash.adapter

import com.example.mythrowtrash.usecase.CalendarUseCase
import com.example.mythrowtrash.usecase.ICalendarManager
import com.example.mythrowtrash.usecase.TrashManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CalendarController(private val view: ICalendarView,private val calendarManager: ICalendarManager): ICalendarController {
    private val usecase = CalendarUseCase(
        calendarManager = calendarManager,
        presenter = CalendarPresenter(view,calendarManager),
        trashManager = DIContainer.resolve(TrashManager::class.java)!!)

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