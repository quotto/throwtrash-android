package net.mythrowaway.app.module.trash.usecase

import android.util.Log
import net.mythrowaway.app.module.info.service.UserIdService
import net.mythrowaway.app.module.trash.dto.CalendarDayDTO
import net.mythrowaway.app.module.trash.entity.trash.TrashList
import net.mythrowaway.app.module.trash.dto.MonthCalendarDTO
import net.mythrowaway.app.module.trash.dto.mapper.TrashMapper
import net.mythrowaway.app.module.trash.entity.sync.SyncState
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

class CalendarUseCase @Inject constructor(
  private val persist: TrashRepositoryInterface,
  private val userIdService: UserIdService,
  private val syncRepository: SyncRepositoryInterface,
  private val apiAdapter: MobileApiInterface
) {

  fun getTrashCalendarOfMonth(year: Int, month: Int): MonthCalendarDTO {
    val trashList = persist.getAllTrash()

    val calendarDays = generateCalendarDays(year, month, trashList)

    return MonthCalendarDTO(year, month, calendarDays)
  }

  private fun generateCalendarDays(year: Int, month: Int, trashes: TrashList): List<CalendarDayDTO> {
    val calendarDayDTOMutableList = mutableListOf<CalendarDayDTO>()
    var currentDate = LocalDate.of(year, month, 1)
      .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))

    for (i in 0..34) {
      val targetTrashes = trashes.trashList.filter { it.isTrashDay(currentDate) }.map{ TrashMapper.toTrashDTO(it) }
      calendarDayDTOMutableList.add(
        CalendarDayDTO(
          currentDate.year,
          currentDate.monthValue,
          currentDate.dayOfMonth,
          currentDate.dayOfWeek,
          targetTrashes
        )
      )
      currentDate = currentDate.plusDays(1)
    }
    return calendarDayDTOMutableList.toList()
  }

  private fun syncRemoteToLocal(userId: String, remoteTrash: TrashList, remoteTimestamp: Long): CalendarSyncResult {
    syncRepository.setTimestamp(remoteTimestamp)
    persist.replaceTrashList(remoteTrash)
    syncRepository.setSyncComplete()
    return CalendarSyncResult.PULL_SUCCESS
  }
  private fun syncLocalToRemote(userId: String, localSchedule: TrashList, localTimestamp: Long): CalendarSyncResult{
    apiAdapter.update(userId, localSchedule, localTimestamp).let { updateResult ->
      return when (updateResult.statusCode) {
        200 -> {
          syncRepository.setTimestamp(updateResult.timestamp)
          syncRepository.setSyncComplete()
          CalendarSyncResult.PUSH_SUCCESS
        }
        else -> {
          // それ以外のエラーはリモート同期待ちを維持
          Log.e(
            this.javaClass.simpleName,
            "Failed update to remote from local, please try later."
          )
          CalendarSyncResult.PENDING
        }
      }
    }
  }

  /**
   * クラウド上のDBに登録されたデータを同期する
   * ID未発行→DBに新規に登録
   * DBのタイムスタンプ>ローカルのタイムスタンプ→ローカルのデータを更新
   * DBのタイムスタンプ<ローカルのタイムスタンプ→DBへ書き込み
   */
  fun syncData(): CalendarSyncResult {
    val syncState = syncRepository.getSyncState()
    Log.i(this.javaClass.simpleName, "Current Sync status -> $syncState")
    if(syncState == SyncState.Wait) {
      val userId:String? = userIdService.getUserId()
      val localSchedule: TrashList = persist.getAllTrash()
      if(userId.isNullOrEmpty()) {
        Log.i(this.javaClass.simpleName,"ID not exists,try register user.")
        apiAdapter.register(localSchedule).let { registeredTrash ->
          userIdService.registerUserId(registeredTrash.userId)
          syncRepository.setTimestamp(registeredTrash.latestTrashListRegisteredTimestamp)
          syncRepository.setSyncComplete()
          Log.i(this.javaClass.simpleName,"Registered new id -> ${registeredTrash.userId}")
        }
        return CalendarSyncResult.PUSH_SUCCESS
      } else if(localSchedule.trashList.isNotEmpty()){
        val localTimestamp = syncRepository.getTimeStamp()
        val remoteTrash = apiAdapter.getRemoteTrash(userId)
        Log.i(this.javaClass.simpleName,"Local Timestamp=$localTimestamp, Remote Timestamp=${remoteTrash.timestamp}")
        if(localTimestamp != remoteTrash.timestamp) {
          // リモートからの同期処理
          Log.i(
            this.javaClass.simpleName,
            "Local timestamp $localTimestamp is not match remote timestamp ${remoteTrash.timestamp},try sync from remote to local"
          )
          return syncRemoteToLocal(userId, remoteTrash.trashList, remoteTrash.timestamp)
        } else {
          Log.i(this.javaClass.simpleName, "Update from local to remote")
          return syncLocalToRemote(userId, localSchedule, localTimestamp)
        }
      } else {
        Log.w(this.javaClass.simpleName, "Not update local to remote because local schedule is nothing.")
        return CalendarSyncResult.NONE
      }
    }
    return CalendarSyncResult.NONE
  }
}

enum class CalendarSyncResult {
  PENDING,
  PUSH_SUCCESS,
  PULL_SUCCESS,
  FAILED,
  NONE
}