package com.example.mythrowtrash

import com.example.mythrowtrash.domain.TrashData
import com.example.mythrowtrash.domain.TrashSchedule
import com.example.mythrowtrash.usecase.*
import kotlin.collections.ArrayList
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class CalendarUseCaseTest {
    class TestPresenter: ICalendarPresenter {
        var trashList: Array<ArrayList<String>> = arrayOf()
        var dateList: ArrayList<Int> = arrayListOf()
        val calenderList: ArrayList<Pair<Array<ArrayList<String>>,ArrayList<Int>>> = arrayListOf()
        var backCalendarFlg = false
        override fun setCalendar(
            year:Int,month:Int,
            trashList: Array<ArrayList<String>>,
            dateList: ArrayList<Int>
        ) {
            this.trashList = trashList
            this.dateList = dateList
            this.calenderList.add(Pair(trashList,dateList))
        }
    }

    class TestCalManager: ICalendarManager {
        override fun getYear(): Int {
            return 2020
        }

        override fun getMonth(): Int {
            return 1
        }

        override fun addYM(year: Int, month: Int, addMonth: Int): Pair<Int, Int> {
            return CalendarManager().addYM(year,month,addMonth)
        }

        override fun subYM(year: Int, month: Int, subMonth: Int): Pair<Int, Int> {
            return CalendarManager().subYM(year,month,subMonth)
        }

        override fun compareYM(param1: Pair<Int, Int>, param2: Pair<Int, Int>): Int {
            return CalendarManager().compareYM(param1,param2)
        }
    }

    private val testPresenter = TestPresenter()
    private val testPersist: TestPersist = TestPersist()
    private val trashManager: TrashManager = TrashManager(testPersist)
    private val usecase: CalendarUseCase = CalendarUseCase(testPresenter, trashManager, TestCalManager())

    private val trash1 = TrashData().apply {
        type = "burn"
        schedules = arrayListOf(TrashSchedule().apply{
            type = "weekday"
            value = "1"
        }, TrashSchedule().apply{
            type = "weekday"
            value = "2"
        })
    }
    private val trash2 = TrashData().apply {
        type = "bin"
        schedules = arrayListOf(TrashSchedule().apply{
            type = "weekday"
            value = "1"
        })
    }

    @Before
    fun cleanTestData() {
        testPersist.injectTestData(arrayListOf(trash1, trash2))
        trashManager.refresh()
        testPresenter.calenderList.clear()
        testPresenter.backCalendarFlg = false
    }

    @Test
    fun generateMonthSchedule() {
        // 1ヶ月（35日分）のゴミ出し予定リストを取得する
        usecase.generateMonthSchedule(2020,1)

        Assert.assertEquals(2,testPresenter.trashList[8].size)
        Assert.assertEquals("もえるゴミ",testPresenter.trashList[8][0])
        Assert.assertEquals("ビン",testPresenter.trashList[8][1])
        Assert.assertEquals(1,testPresenter.trashList[9].size)
        Assert.assertEquals(0,testPresenter.trashList[10].size)

        val expect: List<Int> = listOf(29,30,31,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,1)
        for (i in testPresenter.dateList.indices) {
            assert(testPresenter.dateList[i] == expect[i])
        }
    }
}