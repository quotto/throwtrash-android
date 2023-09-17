package net.mythrowaway.app.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CalendarManagerTest {
    private val calendarManager =
        CalendarManagerImpl()
    @Test
    fun addYM() {
        val result:Pair<Int,Int> = calendarManager.addYM(2020,2,1)
        assertEquals(2020,result.first)
        assertEquals(3,result.second)
    }

    @Test
    fun addYM_OverYear() {
        val result:Pair<Int,Int> = calendarManager.addYM(2020,12,2)
        assertEquals(2021,result.first)
        assertEquals(2,result.second)
    }

    @Test
    fun addYM_OverTwoYear() {
        val result:Pair<Int,Int> = calendarManager.addYM(2020,12,13)
        assertEquals(2022,result.first)
        assertEquals(1,result.second)
    }

    @Test
    fun subYM() {
        val result:Pair<Int,Int> = calendarManager.subYM(2020,2,1)
        assertEquals(2020,result.first)
        assertEquals(1,result.second)
    }

    @Test
    fun subYM_OverYear() {
        val result:Pair<Int,Int> = calendarManager.subYM(2020,2,2)
        assertEquals(2019,result.first)
        assertEquals(12,result.second)
    }

    @Test
    fun subYM_TwoYear() {
        val result:Pair<Int,Int> = calendarManager.subYM(2020,2,24)
        assertEquals(2018,result.first)
        assertEquals(2,result.second)
    }

    @Test
    fun compareYM(){
        //2つの年月が等しければ0
        assertEquals(0,calendarManager.compareYM(Pair(2020,1),Pair(2020,1)))

        //第1引数が大きければ1
        assertEquals(1,calendarManager.compareYM(Pair(2020,2),Pair(2020,1)))
        assertEquals(1,calendarManager.compareYM(Pair(2021,1),Pair(2020,1)))

        //第2引数が大きければ2
        assertEquals(2,calendarManager.compareYM(Pair(2020,2),Pair(2020,3)))
        assertEquals(2,calendarManager.compareYM(Pair(2020,3),Pair(2021,3)))
    }
}