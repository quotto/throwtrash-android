package net.mythrowaway.app.usecase

import net.mythrowaway.app.adapter.repository.UpdateResult
import net.mythrowaway.app.domain.*
import kotlin.collections.ArrayList

interface DataRepositoryInterface {
    fun saveTrashData(trashData: TrashData)
    fun updateTrashData(trashData: TrashData)
    fun deleteTrashData(id: String)
    fun getAllTrashSchedule(): ArrayList<TrashData>
    fun getTrashData(id: String): TrashData?
    fun importScheduleList(scheduleList: ArrayList<TrashData>)
}

interface ConfigRepositoryInterface {
    fun getAlarmConfig(): AlarmConfig
    fun saveAlarmConfig(alarmConfig: AlarmConfig)
    fun getUserId(): String?
    fun getSyncState(): Int
    fun getTimeStamp(): Long
    fun setUserId(id: String)
    fun setTimestamp(timestamp: Long)
    fun setSyncState(state: Int)
    fun getConfigVersion(): Int
    fun updateConfigVersion(version: Int)
    fun saveAccountLinkToken(token: String)
    fun getAccountLinkToken(): String
    fun saveAccountLinkUrl(url: String)
    fun getAccountLinkUrl(): String
    fun updateLastUsedTime()
    fun getLastUsedTime(): Long
    fun updateContinuousDate(continuousData: Int)
    fun getContinuousDate(): Int
    fun getReviewed(): Boolean
    fun writeReviewed()
}

interface MobileApiInterface {
    fun sync(id: String): Pair<ArrayList<TrashData>,Long>?
    fun update(id:String, scheduleList: ArrayList<TrashData>, currentTimestamp: Long): UpdateResult
    fun register(scheduleList: ArrayList<TrashData>): RegisteredData?
    fun publishActivationCode(id: String): String?
    fun activate(code: String, userId: String): LatestTrashData?
    fun accountLink(id: String): AccountLinkInfo?
    fun accountLinkAsWeb(id: String): AccountLinkInfo?
}