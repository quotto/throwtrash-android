package com.example.mythrowtrash

import com.example.mythrowtrash.adapter.CalendarPresenter
import com.example.mythrowtrash.adapter.CalendarViewModel
import com.example.mythrowtrash.adapter.ICalendarView
import com.example.mythrowtrash.domain.TrashData
import com.example.mythrowtrash.domain.TrashSchedule
import com.example.mythrowtrash.usecase.CalendarManager
import com.example.mythrowtrash.usecase.ICalendarManager
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.collections.ArrayList

class CalendarPresenterTest {
    class TestView : ICalendarView {
        var calendarViewModel = CalendarViewModel()
        override fun update(calendarViewModel: CalendarViewModel) {
            this.calendarViewModel = calendarViewModel
        }
    }

    class TestCalendarManager: ICalendarManager {
        private val calendarManager = CalendarManager()
        override fun getYear(): Int {
            return 2020
        }

        override fun getMonth(): Int {
            return 1
        }

        override fun addYM(year: Int, month: Int, addMonth: Int): Pair<Int, Int> {
            return calendarManager.addYM(year,month,addMonth)
        }

        override fun subYM(year: Int, month: Int, subMonth: Int): Pair<Int, Int> {
            return calendarManager.subYM(year,month,subMonth)
        }

        override fun compareYM(param1: Pair<Int, Int>, param2: Pair<Int, Int>): Int {
            return calendarManager.compareYM(param1,param2)
        }

        override fun getTodayStringDate(cal: Calendar): String {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    private val testView = TestView()
    private val presenter = CalendarPresenter(testView,TestCalendarManager())

    @Test
    fun setCalendarSameMonth() {
        // 202001を想定したカレンダー日付
        val dateList: ArrayList<Int> = arrayListOf(29,30,31,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,1)
        val trashList: Array<ArrayList<String>> = Array(35) { arrayListOf("ゴミ１", "ゴミ2")}

        //年月を受け取ってインデックスに変換する
        presenter.setCalendar(2020,1,trashList,dateList)
        Assert.assertEquals(2020, testView.calendarViewModel.year)
        Assert.assertEquals(1, testView.calendarViewModel.month)
        Assert.assertEquals(0, testView.calendarViewModel.position)
        repeat(trashList.size) {
            Assert.assertEquals(trashList[it], testView.calendarViewModel.trashList[it])
        }
        repeat(dateList.size) {
            Assert.assertEquals(dateList[it], testView.calendarViewModel.dateList[it])
        }
    }

    @Test
    fun setCalendarSameYear() {
        // 202001を想定したカレンダー日付
        val dateList: ArrayList<Int> = arrayListOf(29,30,31,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,1)
        val trashList: Array<ArrayList<String>> = Array(35) { arrayListOf("ゴミ１", "ゴミ2")}

        //年月を受け取ってインデックスに変換する
        presenter.setCalendar(2020,4,trashList,dateList)
        Assert.assertEquals(2020, testView.calendarViewModel.year)
        Assert.assertEquals(4, testView.calendarViewModel.month)
        Assert.assertEquals(3, testView.calendarViewModel.position)
        repeat(trashList.size) {
            Assert.assertEquals(trashList[it], testView.calendarViewModel.trashList[it])
        }
        repeat(dateList.size) {
            Assert.assertEquals(dateList[it], testView.calendarViewModel.dateList[it])
        }
    }

    @Test
    fun setCalendarOverYear() {
        // 202001を想定したカレンダー日付
        val dateList: ArrayList<Int> = arrayListOf(29,30,31,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,1)
        val trashList: Array<ArrayList<String>> = Array(35) { arrayListOf("ゴミ１", "ゴミ2")}

        //年月を受け取ってインデックスに変換する
        presenter.setCalendar(2021,4,trashList,dateList)
        Assert.assertEquals(2021, testView.calendarViewModel.year)
        Assert.assertEquals(4, testView.calendarViewModel.month)
        Assert.assertEquals(15, testView.calendarViewModel.position)
        repeat(trashList.size) {
            Assert.assertEquals(trashList[it], testView.calendarViewModel.trashList[it])
        }
        repeat(dateList.size) {
            Assert.assertEquals(dateList[it], testView.calendarViewModel.dateList[it])
        }
    }

    @Test
    fun removeDuplicate() {
        // 202001を想定したカレンダー日付
        val dateList: ArrayList<Int> = arrayListOf(29,30,31,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,1)
        val trashList: Array<ArrayList<String>> = Array(35) { arrayListOf("ゴミ1", "ゴミ1","ゴミ2")}

        presenter.setCalendar(2020,1,trashList,dateList)
        testView.calendarViewModel.trashList.forEach {
            Assert.assertEquals(2, it.size)
            Assert.assertEquals("ゴミ1", it[0])
            Assert.assertEquals("ゴミ2", it[1])
        }
    }
}