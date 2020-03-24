package com.example.mythrowtrash.util

import com.example.mythrowtrash.domain.AlarmConfig
import com.example.mythrowtrash.usecase.IConfigRepository

class TestConfigRepositoryImpl: IConfigRepository {
    private var userId:String? = null
    private var syncState: Int = 0
    private var timeStamp: Long = 0

    override fun getTimeStamp(): Long {
        return timeStamp
    }

    override fun setUserId(id: String) {
        userId = id
    }

    override fun setTimestamp(timestamp: Long) {
        timeStamp = timestamp
    }

    override fun getAlarmConfig(): AlarmConfig {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveAlarmConfig(alarmConfig: AlarmConfig) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUserId(): String? {
        return userId;
    }

    override fun getSyncState(): Int {
        return syncState
    }

    override fun setSyncState(state: Int) {
        syncState = state
    }

    override fun updateLocalTimestamp() {
        syncState = 999999999
    }
}