package com.example.mythrowtrash.usecase

import java.util.Calendar
import kotlin.collections.ArrayList

class CalendarUseCase(private val presenter: ICalendarPresenter, private val trashManager: TrashManager, private val calendarManager: ICalendarManager) {
    private fun generateMonthCalendar(year: Int, month: Int): ArrayList<Int> {
        // 出力値算出用のインスタンス
        val computeCalendar = Calendar.getInstance()
        computeCalendar.set(Calendar.YEAR,year)
        computeCalendar.set(Calendar.MONTH,month-1)
        computeCalendar.set(Calendar.DAY_OF_MONTH, 1)

        val dateArray: ArrayList<Int> = ArrayList()
        // 日曜日の場合は戻す必要がないため1日目の曜日から-1する
        computeCalendar.add(Calendar.DAY_OF_MONTH, -1 * (computeCalendar.get(Calendar.DAY_OF_WEEK)-1))
        for(i in 1..35) {
            dateArray.add(computeCalendar.get(Calendar.DAY_OF_MONTH))
            computeCalendar.add(Calendar.DAY_OF_MONTH,1)
        }
        return dateArray
    }

    fun generateMonthSchedule(year:Int, month: Int) {
        val dateList:ArrayList<Int>  = generateMonthCalendar(year, month)
        presenter.setCalendar(year,month,trashManager.getEnableTrashList(month,year,dateList), dateList)
    }
}