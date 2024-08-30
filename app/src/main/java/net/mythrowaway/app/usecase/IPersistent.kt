package net.mythrowaway.app.usecase

import net.mythrowaway.app.adapter.repository.UpdateResult
import net.mythrowaway.app.domain.*
import net.mythrowaway.app.domain.account_link.FinishAccountLinkRequestInfo
import net.mythrowaway.app.domain.sync.RegisteredInfo
import net.mythrowaway.app.domain.sync.RemoteTrash
import net.mythrowaway.app.usecase.dto.StartAccountLinkResponse

interface TrashRepositoryInterface {
    fun saveTrash(trash: Trash)
    fun findTrashById(id: String): Trash?
    fun deleteTrash(trash: Trash)
    fun getAllTrash(): TrashList
    fun importScheduleList(trashList: TrashList)
}

interface AlarmRepositoryInterface {
    fun getAlarmConfig(): AlarmConfig?
    fun saveAlarmConfig(alarmConfig: AlarmConfig)
}

interface ConfigRepositoryInterface {
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
    fun getRemoteTrash(userId: String): RemoteTrash
    fun update(userId:String, trashList: TrashList, currentTimestamp: Long): UpdateResult
    fun register(trashList: TrashList): RegisteredInfo
    fun publishActivationCode(id: String): String
    fun activate(code: String, userId: String): RemoteTrash
    fun accountLink(id: String): StartAccountLinkResponse
    fun accountLinkAsWeb(id: String): StartAccountLinkResponse
}