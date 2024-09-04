package net.mythrowaway.app.domain.trash.usecase

import net.mythrowaway.app.domain.trash.infra.UpdateResult
import net.mythrowaway.app.domain.account_link.entity.FinishAccountLinkRequestInfo
import net.mythrowaway.app.domain.account_link.usecase.dto.StartAccountLinkResponse
import net.mythrowaway.app.domain.trash.entity.trash.Trash
import net.mythrowaway.app.domain.trash.entity.trash.TrashList
import net.mythrowaway.app.domain.trash.entity.sync.RegisteredInfo
import net.mythrowaway.app.domain.trash.entity.sync.RemoteTrash

interface TrashRepositoryInterface {
  fun saveTrash(trash: Trash)
  fun findTrashById(id: String): Trash?
  fun deleteTrash(trash: Trash)
  fun getAllTrash(): TrashList
  fun replaceTrashList(trashList: TrashList)
}

interface SyncRepositoryInterface {
  fun getSyncState(): Int
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
