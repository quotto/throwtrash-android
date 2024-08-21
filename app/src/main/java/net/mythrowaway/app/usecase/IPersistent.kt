package net.mythrowaway.app.usecase

import net.mythrowaway.app.adapter.repository.UpdateResult
import net.mythrowaway.app.domain.*
import net.mythrowaway.app.domain.account_link.FinishAccountLinkRequestInfo
import net.mythrowaway.app.usecase.dto.StartAccountLinkResponse
import kotlin.collections.ArrayList

interface DataRepositoryInterface {
    fun saveTrashData(trashData: TrashData)
    fun saveTrash(trash: Trash)
    fun findTrashById(id: String): Trash?
    fun updateTrashData(trashData: TrashData)
    fun deleteTrashData(id: String)

    fun deleteTrash(trash: Trash)
    fun getAllTrashSchedule(): ArrayList<TrashData>
    fun getAllTrash(): TrashList
    fun getTrashData(id: String): TrashData?
    fun importScheduleList(scheduleList: ArrayList<TrashData>)
}

interface ConfigRepositoryInterface {
    fun getAlarmConfig(): AlarmConfig?
    fun saveAlarmConfig(alarmConfig: AlarmConfig)
    fun getUserId(): String?
    fun getSyncState(): Int
    fun getTimeStamp(): Long
    fun setUserId(id: String)
    fun setTimestamp(timestamp: Long)
    fun setSyncWait()
    fun setSyncComplete()
    fun getConfigVersion(): Int
    fun updateConfigVersion(version: Int)
    fun saveAccountLinkToken(token: String)
    fun getAccountLinkToken(): String
    fun saveAccountLinkUrl(url: String)
    fun getAccountLinkUrl(): String
    fun saveAccountLinkRequestInfo(finishAccountLinkRequestInfo: FinishAccountLinkRequestInfo)
    fun getAccountLinkRequestInfo(): FinishAccountLinkRequestInfo

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
    fun publishActivationCode(id: String): String
    fun activate(code: String, userId: String): LatestTrashData?
    fun accountLink(id: String): StartAccountLinkResponse
    fun accountLinkAsWeb(id: String): StartAccountLinkResponse
}