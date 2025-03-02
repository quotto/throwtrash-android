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
  private val apiAdapter: MobileApiInterface,
) {

  fun getTrashCalendarOfMonth(year: Int, month: Int): MonthCalendarDTO {
    val trashList = persist.getAllTrash()

    val calendarDays = generateCalendarDays(year, month, trashList)

    return MonthCalendarDTO(year, month, calendarDays)
  }

  /**
   * クラウド上のDBに登録されたデータを同期する
   * ID未発行→DBにUserIDとFirebaseのIDを登録
   * DBのタイムスタンプ>ローカルのタイムスタンプ→ローカルのデータを更新
   * DBのタイムスタンプ<ローカルのタイムスタンプ→DBへ書き込み
   */
  suspend fun syncData(): CalendarSyncResult {
    val syncState = syncRepository.getSyncState()
    Log.i(this.javaClass.simpleName, "Current Sync status -> $syncState")
    val userId:String? = userIdService.getUserId()
    val localSchedule: TrashList = persist.getAllTrash()
    if(userId.isNullOrEmpty()) {
      Log.i(this.javaClass.simpleName, "ID not exists,try register user.")
      try {
        apiAdapter.register().let { registeredTrash ->
          userIdService.registerUserId(registeredTrash.userId)
          persist.replaceTrashList(TrashList(listOf()))
          syncRepository.setTimestamp(registeredTrash.latestTrashListRegisteredTimestamp)
          syncRepository.setSyncComplete()
          Log.i(this.javaClass.simpleName, "Registered new id -> ${registeredTrash.userId}")
          // 初回登録時はリモート側のタイムスタンプがローカルに保存されるため、ユーザーIDが登録された時点で空データでカレンダーを更新する
          return CalendarSyncResult.PULL_SUCCESS
        }
      } catch (e: Exception) {
        Log.e(this.javaClass.simpleName, "Failed to register user.")
        Log.e(this.javaClass.simpleName, e.stackTraceToString())
        return CalendarSyncResult.FAILED
      }
    }
    if(syncState == SyncState.Wait) {
      val localTimestamp = syncRepository.getTimeStamp()
      try {
        val remoteTrash = apiAdapter.getRemoteTrash(userId)
        Log.i(
          this.javaClass.simpleName,
          "Local Timestamp=$localTimestamp, Remote Timestamp=${remoteTrash.timestamp}"
        )
        if (localTimestamp != remoteTrash.timestamp) {
          // リモートからの同期処理
          Log.i(
            this.javaClass.simpleName,
            "Local timestamp $localTimestamp is not match remote timestamp ${remoteTrash.timestamp},try sync from remote to local"
          )
          return syncRemoteToLocal(remoteTrash.trashList, remoteTrash.timestamp)
        } else {
          if (localSchedule.trashList.isNotEmpty()) {
            Log.i(this.javaClass.simpleName, "Update from local to remote")
            return syncLocalToRemote(userId, localSchedule, localTimestamp)
          }
          else {
            Log.w(
              this.javaClass.simpleName,
              "Not update local to remote because local schedule is nothing."
            )
            return CalendarSyncResult.NONE
          }
        }
      } catch (e: Exception) {
        Log.e(this.javaClass.simpleName, "Failed to sync data.")
        Log.e(this.javaClass.simpleName, e.stackTraceToString())
        return CalendarSyncResult.FAILED
      }
    }
    return CalendarSyncResult.NONE
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

  private fun syncRemoteToLocal(remoteTrash: TrashList, remoteTimestamp: Long): CalendarSyncResult {
    syncRepository.setTimestamp(remoteTimestamp)
    persist.replaceTrashList(remoteTrash)
    syncRepository.setSyncComplete()
    return CalendarSyncResult.PULL_SUCCESS
  }
  private suspend fun syncLocalToRemote(userId: String, localSchedule: TrashList, localTimestamp: Long): CalendarSyncResult{
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

}

enum class CalendarSyncResult {
  PENDING,
  PUSH_SUCCESS,
  PULL_SUCCESS,
  FAILED,
  NONE
}