package com.example.mythrowtrash

import com.example.mythrowtrash.adapter.CalendarController
import com.example.mythrowtrash.adapter.DIContainer
import com.example.mythrowtrash.domain.TrashData
import com.example.mythrowtrash.domain.TrashSchedule
import com.example.mythrowtrash.usecase.*
import com.example.mythrowtrash.util.TestPersistImpl
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import java.util.*
import kotlin.collections.ArrayList

class CalendarControllerTest {
    private class TestCalendarManager:ICalendarManager {
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

        override fun getTodayStringDate(cal: Calendar): String {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    init {
        val testPersist = TestPersistImpl()
        testPersist.injectTestData(arrayListOf(
            TrashData().apply {
                type = "burn"
                schedules = arrayListOf(TrashSchedule().apply {
                    type = "weekday"
                    value = "1"
                }, TrashSchedule().apply {
                    type = "weekday"
                    value = "2"
                })
            },
            TrashData().apply {
                type = "bin"
                schedules = arrayListOf(TrashSchedule().apply {
                    type = "weekday"
                    value = "1"
                })
            }
        ))
        DIContainer.register(TrashManager::class.java, TrashManager(testPersist))
        DIContainer.register(TestCalendarManager::class.java, TestCalendarManager())
    }

    private val view = CalendarPresenterTest.TestView()
    private val controller = CalendarController(view,DIContainer.resolve(TestCalendarManager::class.java)!!)

    @Test
    fun generateCalendarFromPositionAsync() {
        // インデックスをもとに現在年月に加算して表示する年月を算出する
        runBlocking {
            launch {
                controller.generateCalendarFromPositionAsync(1)
            }
        }

        // 2020年1月の1ヶ月後
        Assert.assertEquals(2020,view.calendarViewModel.year)
        Assert.assertEquals(2,view.calendarViewModel.month)

        runBlocking {
            launch {
                controller.generateCalendarFromPositionAsync(13)
            }
        }
        // 2020年1月の13ヶ月後
        Assert.assertEquals(2021,view.calendarViewModel.year)
        Assert.assertEquals(2,view.calendarViewModel.month)
    }
}