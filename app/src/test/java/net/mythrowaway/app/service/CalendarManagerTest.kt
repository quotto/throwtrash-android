package net.mythrowaway.app.service

import org.junit.Assert
import org.junit.Test

class CalendarManagerTest {
    private val calendarManager =
        CalendarManagerImpl()
    @Test
    fun addYM() {
        val result:Pair<Int,Int> = calendarManager.addYM(2020,2,1)
        Assert.assertEquals(2020,result.first)
        Assert.assertEquals(3,result.second)
    }

    @Test
    fun addYM_OverYear() {
        val result:Pair<Int,Int> = calendarManager.addYM(2020,12,2)
        Assert.assertEquals(2021,result.first)
        Assert.assertEquals(2,result.second)
    }

    @Test
    fun addYM_OverTwoYear() {
        val result:Pair<Int,Int> = calendarManager.addYM(2020,12,13)
        Assert.assertEquals(2022,result.first)
        Assert.assertEquals(1,result.second)
    }

    @Test
    fun subYM() {
        val result:Pair<Int,Int> = calendarManager.subYM(2020,2,1)
        Assert.assertEquals(2020,result.first)
        Assert.assertEquals(1,result.second)
    }

    @Test
    fun subYM_OverYear() {
        val result:Pair<Int,Int> = calendarManager.subYM(2020,2,2)
        Assert.assertEquals(2019,result.first)
        Assert.assertEquals(12,result.second)
    }

    @Test
    fun subYM_TwoYear() {
        val result:Pair<Int,Int> = calendarManager.subYM(2020,2,24)
        Assert.assertEquals(2018,result.first)
        Assert.assertEquals(2,result.second)
    }

    @Test
    fun compareYM(){
        //2つの年月が等しければ0
        Assert.assertEquals(0,calendarManager.compareYM(Pair(2020,1),Pair(2020,1)))

        //第1引数が大きければ1
        Assert.assertEquals(1,calendarManager.compareYM(Pair(2020,2),Pair(2020,1)))
        Assert.assertEquals(1,calendarManager.compareYM(Pair(2021,1),Pair(2020,1)))

        //第2引数が大きければ2
        Assert.assertEquals(2,calendarManager.compareYM(Pair(2020,2),Pair(2020,3)))
        Assert.assertEquals(2,calendarManager.compareYM(Pair(2020,3),Pair(2021,3)))
    }
}