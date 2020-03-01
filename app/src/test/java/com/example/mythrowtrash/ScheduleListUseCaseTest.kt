package com.example.mythrowtrash

import com.example.mythrowtrash.domain.TrashData
import com.example.mythrowtrash.domain.TrashSchedule
import com.example.mythrowtrash.usecase.IPersistentRepository
import com.example.mythrowtrash.usecase.IScheduleListPresenter
import com.example.mythrowtrash.usecase.ScheduleListUseCase
import com.example.mythrowtrash.usecase.TrashManager
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ScheduleListUseCaseTest {
    inner class TestPresenter: IScheduleListPresenter {
        var scheduleList: ArrayList<TrashData> = arrayListOf()
        override fun showScheduleList(scheduleList: ArrayList<TrashData>) {
            this.scheduleList = scheduleList
        }
    }
    inner class TestPersistent: IPersistentRepository {
        var scheduleList:ArrayList<TrashData> = arrayListOf()
        override fun saveTrashData(trashData: TrashData) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun updateTrashData(trashData: TrashData) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun deleteTrashData(id: Int) {
            // テスト用なのでidをインデックスとみなしてデータを削除
            scheduleList.removeAt(id-1)
        }

        override fun getAllTrashSchedule(): ArrayList<TrashData> {
            return scheduleList
        }

        override fun incrementCount(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getTrashData(id: Int): TrashData? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    private val testPresenter = TestPresenter()
    private val testPersistent = TestPersistent()
    private val usecase = ScheduleListUseCase(TrashManager(testPersistent),testPersistent,testPresenter)

    @Before
    fun before() {
        testPersistent.scheduleList.clear()
        testPresenter.scheduleList.clear()
    }

    @Test
    fun showScheduleList() {
        val trash1 = TrashData().apply {
            id = 1
            type = "burn"
            schedules = arrayListOf(TrashSchedule().apply{
                type = "biweek"
                value = "0-3"
            }, TrashSchedule().apply{
                type = "biweek"
                value = "6-1"
            })
        }
        val trash2 = TrashData().apply {
            id = 2
            type = "other"
            trash_val = "家電"
            schedules = arrayListOf(TrashSchedule().apply{
                type = "biweek"
                value = "0-3"
            })
        }

        testPersistent.scheduleList = arrayListOf(trash1,trash2)
        usecase.showScheduleList()
        Assert.assertEquals(2,testPresenter.scheduleList.size)
        testPresenter.scheduleList.forEachIndexed {index, trashData ->
            Assert.assertEquals(testPresenter.scheduleList[index], trashData)
        }
    }

    @Test
    fun deleteSchedule() {
        val trash1 = TrashData().apply {
            id = 1
            type = "burn"
            schedules = arrayListOf(TrashSchedule().apply{
                type = "biweek"
                value = "0-3"
            }, TrashSchedule().apply{
                type = "biweek"
                value = "6-1"
            })
        }
        val trash2 = TrashData().apply {
            id = 2
            type = "other"
            trash_val = "家電"
            schedules = arrayListOf(TrashSchedule().apply{
                type = "biweek"
                value = "0-3"
            })
        }

        testPersistent.scheduleList = arrayListOf(trash1,trash2)
        usecase.deleteList(1)
        Assert.assertEquals(1,testPresenter.scheduleList.size)
        Assert.assertEquals(trash2,testPresenter.scheduleList[0])
    }
}