package net.mythrowaway.app.adapter.controller

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mythrowaway.app.usecase.*
import javax.inject.Inject

class CalendarControllerImpl @Inject constructor(
    private val calendarManager: CalendarManager,
    private val calendarUseCase: CalendarUseCase):
    ICalendarController
{
    override suspend fun syncData() {
        withContext(Dispatchers.IO) {
            calendarUseCase.syncData()
        }
    }

    override suspend fun generateCalendarFromPositionAsync(position: Int) {
        withContext(Dispatchers.Default) {
            val targetYM = calendarManager.addYM(
                calendarManager.getYear(),
                calendarManager.getMonth(),
                position
            )
            calendarUseCase.generateMonthSchedule(targetYM.first, targetYM.second)
        }
    }
}
