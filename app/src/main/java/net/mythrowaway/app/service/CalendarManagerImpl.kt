package net.mythrowaway.app.service

import java.util.Calendar
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.ceil

interface CalendarManagerInterface {
    fun getYear():Int
    fun getMonth():Int
    fun addYM(year: Int, month: Int, addMonth: Int): Pair<Int,Int>
    fun subYM(year: Int, month: Int, subMonth: Int): Pair<Int,Int>
    fun compareYM(param1: Pair<Int, Int>, param2: Pair<Int, Int>): Int
    fun getTodayStringDate(cal:Calendar): String
}

class CalendarManagerImpl @Inject constructor() : CalendarManagerInterface {
    private val mCalendar:Calendar = Calendar.getInstance()
    init {
        mCalendar.set(Calendar.DATE,1)
    }

    override fun getTodayStringDate(cal: Calendar): String {
        return "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)+1}-${cal.get(Calendar.DATE)}"
    }

    /**
     * 月数を足し算した年月を返す
     */
    override fun addYM(year:Int, month:Int, addMonth:Int):Pair<Int,Int> {
        var resultMonth = month + addMonth
        var resultYear = year
        if(resultMonth > 12) {
            val factor = ceil((resultMonth.toFloat() / 24.0)).toInt()
            resultMonth -= factor * 12
            resultYear += factor
        }
        return Pair(resultYear,resultMonth)
    }

    /**
     * 月数を引いた年月を返す
     */
    override fun subYM(year:Int, month:Int, subMonth: Int):Pair<Int,Int> {
        var resultMonth = month - subMonth
        var resultYear = year
        if(resultMonth < 1) {
            // resultMonth=0が正しい値にならないため-1した値で計算する
            val factor = ceil((abs(resultMonth-1).toFloat() / 12.0)).toInt()
            resultMonth += factor * 12
            resultYear -= factor
        }
        return Pair(resultYear,resultMonth)
    }

    /**
     * 2つの年月を比較する
     * param1=param2:0
     * param1>param2:1
     * param1<param2:2
     */
    override fun compareYM(param1:Pair<Int,Int>,param2:Pair<Int,Int>):Int {
        when {
            param1.first == param2.first -> {
                return when {
                    param1.second == param2.second -> {
                        0
                    }
                    param1.second > param2.second -> {
                        1
                    }
                    else -> {
                        2
                    }
                }
            }
            param1.first > param2.first -> {
                return 1
            }
            else -> {
                return 2
            }
        }
    }

    override fun getYear(): Int {
        return mCalendar.get(Calendar.YEAR)
    }

    override fun getMonth(): Int {
        return mCalendar.get(Calendar.MONTH)+1
    }
}