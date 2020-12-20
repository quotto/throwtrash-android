package net.mythrowaway.app.usecase

import net.mythrowaway.app.domain.AccountLinkInfo
import net.mythrowaway.app.domain.AlarmConfig
import net.mythrowaway.app.domain.RegisteredData
import net.mythrowaway.app.domain.TrashData
import kotlin.collections.ArrayList

interface IPersistentRepository {
    fun saveTrashData(trashData: TrashData)
    fun updateTrashData(trashData: TrashData)
    fun deleteTrashData(id: String)
    fun getAllTrashSchedule(): ArrayList<TrashData>
    fun getTrashData(id: String): TrashData?
    fun importScheduleList(scheduleList: ArrayList<TrashData>)
}

interface IConfigRepository {
    fun getAlarmConfig(): AlarmConfig
    fun saveAlarmConfig(alarmConfig: AlarmConfig)
    fun getUserId(): String?
    fun getSyncState(): Int
    fun getTimeStamp(): Long
    fun setUserId(id: String)
    fun setTimestamp(timestamp: Long)
    fun setSyncState(state: Int)
    fun updateLocalTimestamp()
    fun getConfigVersion(): Int
    fun updateConfigVersion()
    fun saveAccountLinkSession(sessionId: String,sessionValue: String)
    fun getAccountLinkSession(): String
}

interface IAPIAdapter {
    fun sync(id: String): Pair<ArrayList<TrashData>,Long>?
    fun update(id:String, scheduleList: ArrayList<TrashData>): Long?
    fun register(scheduleList: ArrayList<TrashData>): Pair<String, Long>?
    fun publishActivationCode(id: String): String?
    fun activate(code: String): RegisteredData?
    fun accountLink(id: String): AccountLinkInfo?
}