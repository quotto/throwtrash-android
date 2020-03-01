package com.example.mythrowtrash

import com.example.mythrowtrash.adapter.DIContainer
import com.example.mythrowtrash.domain.TrashData
import com.example.mythrowtrash.domain.TrashSchedule
import com.example.mythrowtrash.usecase.IPersistentRepository
import com.example.mythrowtrash.usecase.TrashManager
import org.junit.Assert
import org.junit.Test
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.ArrayList

class TestPersist(): IPersistentRepository {
    private lateinit var testDataSet: ArrayList<TrashData>
    fun injectTestData(data: ArrayList<TrashData>) {
        testDataSet = data
    }

    override fun saveTrashData(trashData: TrashData) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateTrashData(trashData: TrashData) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteTrashData(id: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllTrashSchedule(): ArrayList<TrashData> {
        return testDataSet
    }

    override fun incrementCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTrashData(id: Int): TrashData? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}


class TrashManagerTest {
    // 202001を想定したカレンダー日付
    private val dataSet: ArrayList<Int> = arrayListOf(29,30,31,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,1)

    private val testPersist = TestPersist()

    private lateinit var trashManager:TrashManager
    init {
        DIContainer.register(IPersistentRepository::class.java, testPersist)
        trashManager = TrashManager(DIContainer.resolve(IPersistentRepository::class.java)!!)
        DIContainer.register(TrashManager::class.java,trashManager)
    }

    @Test
    fun getTrashName() {
        val method: Method = trashManager.javaClass.getDeclaredMethod("getTrashName",String::class.java,String::class?.java)
        method.setAccessible(true)
        Assert.assertEquals("もえるゴミ",method.invoke(trashManager,"burn", null))
        Assert.assertEquals("生ゴミ",method.invoke(trashManager,"other","生ゴミ"))
        Assert.assertEquals("",method.invoke(trashManager,"none","trash_val"))
        Assert.assertEquals("",method.invoke(trashManager,"other",null))
    }

    @Test
    fun getComputeCalendar() {
        val method: Method = trashManager.javaClass.getDeclaredMethod("getComputeCalendar",Int::class.java,Int::class.java,Int::class.java,Int::class.java)
        method.setAccessible(true)

        // 当月
        val result1: Calendar = method.invoke(trashManager,2020,1,12,13) as Calendar
        Assert.assertEquals(0,result1.get(Calendar.MONTH))
        Assert.assertEquals(1,result1.get(Calendar.DAY_OF_WEEK))

        // 前月
        val result2: Calendar = method.invoke(trashManager,2020,1,31,2) as Calendar
        Assert.assertEquals(11,result2.get(Calendar.MONTH))
        Assert.assertEquals(3,result2.get(Calendar.DAY_OF_WEEK))

        // 翌月
        val result3: Calendar = method.invoke(trashManager,2020,1,1,34) as Calendar
        Assert.assertEquals(1,result3.get(Calendar.MONTH))
        Assert.assertEquals(7,result3.get(Calendar.DAY_OF_WEEK))
    }

    @Test
    fun getEnableTrashListByWeekday() {
        val trash1 = TrashData().apply {
            type = "burn"
            schedules = arrayListOf(TrashSchedule().apply{
                type = "weekday"
                value = "1"
            },TrashSchedule().apply{
                type = "weekday"
                value = "2"
            })
        }
        val trash2 = TrashData().apply {
            type = "bin"
            schedules = arrayListOf(TrashSchedule().apply{
                type = "weekday"
                value = "1"
            })
        }

        testPersist.injectTestData(arrayListOf(trash1,trash2))
        trashManager.refresh()
        val result: Array<ArrayList<String>> = trashManager.getEnableTrashList(2020,1,dataSet)
        Assert.assertEquals(2,result[8].size)
        Assert.assertEquals("もえるゴミ",result[8][0])
        Assert.assertEquals("ビン",result[8][1])
        Assert.assertEquals(1,result[9].size)
        Assert.assertEquals(0,result[10].size)
    }

    @Test
    fun getEnableTrashListByMonth() {
        val trash1 = TrashData().apply {
            type = "burn"
            schedules = arrayListOf(TrashSchedule().apply{
                type = "month"
                value = "3"
            },TrashSchedule().apply{
                type = "month"
                value = "29"
            })
        }
        val trash2 = TrashData().apply {
            type = "other"
            trash_val = "家電"
            schedules = arrayListOf(TrashSchedule().apply{
                type = "month"
                value = "3"
            })
        }
        testPersist.injectTestData(arrayListOf(trash1, trash2))
        trashManager.refresh()
        val result: Array<ArrayList<String>> = trashManager.getEnableTrashList(2020,1,dataSet)
        Assert.assertEquals(2,result[5].size)
        Assert.assertEquals("もえるゴミ",result[5][0])
        Assert.assertEquals("家電",result[5][1])
        Assert.assertEquals(1,result[0].size)
        Assert.assertEquals(1,result[31].size)
        Assert.assertEquals("もえるゴミ",result[0][0])
    }

    @Test
    fun getEnableTrashListByBiweek() {
        val trash1 = TrashData().apply {
            type = "burn"
            schedules = arrayListOf(TrashSchedule().apply{
                type = "biweek"
                value = "0-3"
            },TrashSchedule().apply{
                type = "biweek"
                value = "6-1"
            })
        }
        val trash2 = TrashData().apply {
            type = "other"
            trash_val = "家電"
            schedules = arrayListOf(TrashSchedule().apply{
                type = "biweek"
                value = "0-3"
            })
        }
        testPersist.injectTestData(arrayListOf(trash1, trash2))
        trashManager.refresh()
        val result: Array<ArrayList<String>> = trashManager.getEnableTrashList(2020,1,dataSet)
        Assert.assertEquals(2,result[21].size)
        Assert.assertEquals("もえるゴミ",result[21][0])
        Assert.assertEquals("家電",result[21][1])
        Assert.assertEquals(1,result[6].size)
        Assert.assertEquals(1,result[34].size)
        Assert.assertEquals("もえるゴミ",result[34][0])
    }

    @Test
    fun getEnableTrashListByEvweek() {
        val trash1 = TrashData().apply {
            type = "burn"
            schedules = arrayListOf(TrashSchedule().apply{
                type = "evweek"
                value = hashMapOf("weekday" to "3", "start" to "2020-01-08")
            },TrashSchedule().apply{
                type = "evweek"
                value = hashMapOf("weekday" to "0", "start" to "2019-12-29")
            })
        }
        val trash2 = TrashData().apply {
            type = "other"
            trash_val = "家電"
            schedules = arrayListOf(TrashSchedule().apply{
                type = "evweek"
                value = hashMapOf("weekday" to "3", "start" to "2020-01-08")
            })
        }
        testPersist.injectTestData(arrayListOf(trash1, trash2))
        trashManager.refresh()
        val result: Array<ArrayList<String>> = trashManager.getEnableTrashList(2020,1,dataSet)
        Assert.assertEquals(2,result[10].size)
        Assert.assertEquals(2,result[24].size)
        Assert.assertEquals("もえるゴミ",result[10][0])
        Assert.assertEquals("家電",result[24][1])
        Assert.assertEquals(1,result[0].size)
        Assert.assertEquals(1,result[14].size)
        Assert.assertEquals(1,result[28].size)
        Assert.assertEquals("もえるゴミ",result[0][0])
    }
}