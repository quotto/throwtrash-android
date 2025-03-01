package net.mythrowaway.app.module.trash.usecase

import net.mythrowaway.app.module.trash.infra.UpdateResult
import net.mythrowaway.app.module.trash.entity.trash.Trash
import net.mythrowaway.app.module.trash.entity.trash.TrashList
import net.mythrowaway.app.module.trash.entity.sync.RegisteredInfo
import net.mythrowaway.app.module.trash.entity.sync.RemoteTrash
import net.mythrowaway.app.module.trash.entity.sync.SyncState

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
  suspend fun getRemoteTrash(userId: String): RemoteTrash
  suspend fun update(userId:String, trashList: TrashList, currentTimestamp: Long): UpdateResult
  suspend fun register(): RegisteredInfo
  suspend fun publishActivationCode(id: String): String
  suspend fun activate(code: String, userId: String): RemoteTrash
}
