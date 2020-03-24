package com.example.mythrowtrash.util

import com.example.mythrowtrash.domain.TrashData
import com.example.mythrowtrash.usecase.IPersistentRepository

class TestPersistImpl(): IPersistentRepository {
    private var testDataSet: ArrayList<TrashData> = arrayListOf()
    fun injectTestData(data: ArrayList<TrashData>) {
        testDataSet = data
    }

    override fun saveTrashData(trashData: TrashData) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun importScheduleList(scheduleList: ArrayList<TrashData>) {
        testDataSet = scheduleList
    }

    override fun updateTrashData(trashData: TrashData) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteTrashData(id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllTrashSchedule(): ArrayList<TrashData> {
        return testDataSet
    }

    override fun getTrashData(id: String): TrashData? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}