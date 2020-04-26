package com.example.mythrowtrash.util

import com.example.mythrowtrash.domain.RegisteredData
import com.example.mythrowtrash.domain.TrashData
import com.example.mythrowtrash.domain.TrashSchedule
import com.example.mythrowtrash.usecase.IAPIAdapter

class TestApiAdapterImpl: IAPIAdapter {
    var data001:ArrayList<TrashData> = arrayListOf()
    var timestamp001: Long = 0

    override fun sync(id: String): Pair<ArrayList<TrashData>, Long>? {
        if(id === "id001") {
            return Pair(data001, timestamp001)
        }
        return null
    }

    override fun update(id: String, scheduleList: ArrayList<TrashData>): Long? {
        if(id == "id001") {
            data001 = scheduleList
            return timestamp001
        }
        return null
    }

    override fun register(scheduleList: ArrayList<TrashData>): Pair<String, Long>? {
        if(scheduleList.size == 0) {
            return null
        }
        return Pair("id999", 12345678)
    }

    override fun publishActivationCode(id: String): String? {
        if(id == "id001") {
            return "55555"
        }
        return null
    }

    override fun activate(code: String): RegisteredData? {
        if(code == "55555") {
            return RegisteredData().apply {
                this.id = "id001"
                this.scheduleList = data001
                this.timestamp = timestamp001
            }
        }
        return null
    }

    override fun getAccountLinkUrl(id: String): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}