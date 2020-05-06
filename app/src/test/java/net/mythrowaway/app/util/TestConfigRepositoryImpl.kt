package net.mythrowaway.app.util

import net.mythrowaway.app.domain.AlarmConfig
import net.mythrowaway.app.usecase.IConfigRepository

class TestConfigRepositoryImpl: IConfigRepository {
    private var userId:String? = null
    private var syncState: Int = 0
    private var timeStamp: Long = 0
    private lateinit var alarmConfig: AlarmConfig

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
        return alarmConfig
    }

    override fun saveAlarmConfig(alarmConfig: AlarmConfig) {
        this.alarmConfig = alarmConfig
    }

    override fun getUserId(): String? {
        return userId
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

    override fun getConfigVersion(): Int {
        TODO("Not yet implemented")
    }

    override fun updateConfigVersion() {
        TODO("Not yet implemented")
    }
}