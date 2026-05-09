package net.mythrowaway.app.module.trash.usecase

import net.mythrowaway.app.module.trash.infra.UpdateResult
import net.mythrowaway.app.module.trash.entity.trash.Trash
import net.mythrowaway.app.module.trash.entity.trash.TrashList
import net.mythrowaway.app.module.trash.entity.sync.RegisteredInfo
import net.mythrowaway.app.module.trash.entity.sync.RemoteTrash
import net.mythrowaway.app.module.trash.entity.sync.SyncState
import net.mythrowaway.app.module.trash.infra.schedule_search.ScheduleSearchResponse

interface TrashRepositoryInterface {
  fun saveTrash(trash: Trash)
  fun findTrashById(id: String): Trash?
  fun deleteTrash(trash: Trash)
  fun getAllTrash(): TrashList
  fun replaceTrashList(trashList: TrashList)
}

interface SyncRepositoryInterface {
  fun getSyncState(): SyncState
  fun getTimeStamp(): Long
  fun setTimestamp(timestamp: Long)
  fun setSyncWait()
  fun setSyncComplete()
}

interface MobileApiInterface {
  fun getRemoteTrash(userId: String): RemoteTrash
  fun update(userId:String, trashList: TrashList, currentTimestamp: Long): UpdateResult
  fun register(trashList: TrashList): RegisteredInfo
  fun publishActivationCode(id: String): String
  fun activate(code: String, userId: String): RemoteTrash
}

data class ScheduleSearchRequest(
  val address: String?,
  val postalCode: String?
)

interface ScheduleSearchApiInterface {
  fun search(request: ScheduleSearchRequest): ScheduleSearchResponse
}

interface ScheduleSearchStateRepositoryInterface {
  fun shouldShowStartupDialog(): Boolean
  fun suppressStartupDialog()
  fun saveImportResult(result: ScheduleSearchImportResult)
  fun consumeImportResult(): ScheduleSearchImportResult?
}
