package net.mythrowaway.app.module.trash.usecase

import android.util.Log
import net.mythrowaway.app.module.account.service.AuthService
import net.mythrowaway.app.module.account.service.UserIdService
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
  private val authService: AuthService,
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
    val currentSyncState = syncRepository.getSyncState()
    Log.i(this.javaClass.simpleName, "Current Sync status -> $currentSyncState")
    var userId:String? = userIdService.getUserId()
    val localSchedule: TrashList = persist.getAllTrash()

    authService.getIdToken().fold(
      onSuccess = { idToken ->
      if (userId.isNullOrEmpty()) {
        Log.i(this.javaClass.simpleName, "ID not exists,try register user.")
        try {
          apiAdapter.register(idToken).let { registeredTrash ->
            userIdService.registerUserId(registeredTrash.userId)
            userId = registeredTrash.userId

            // 初回登録できない状態のままローカル側のデータを保存した場合を想定して、
            // 取得したデータをそのまま保存する。
            // ローカルデータがない場合は空配列を返すため、正常に初期化されれば空データで登録される。
            persist.replaceTrashList(localSchedule)

            syncRepository.setTimestamp(registeredTrash.latestTrashListRegisteredTimestamp)
            Log.i(this.javaClass.simpleName, "Registered new id -> ${registeredTrash.userId}")
          }
        } catch (e: Exception) {
          Log.e(this.javaClass.simpleName, "Failed to register user.")
          Log.e(this.javaClass.simpleName, e.stackTraceToString())
          return CalendarSyncResult.FAILED
        }
      }
      val localTimestamp = syncRepository.getTimeStamp()
      try {
        val remoteTrash = apiAdapter.getRemoteTrash(userId!!, idToken)
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
          syncRemoteToLocal(remoteTrash.trashList, remoteTrash.timestamp)
          return if (currentSyncState == SyncState.Wait) {
            Log.d(this.javaClass.simpleName, "Updated local data discard.")
            CalendarSyncResult.PULL_AND_DISCARD
          } else {
            CalendarSyncResult.PULL_SUCCESS
          }
        } else {
          return if (currentSyncState == SyncState.Wait) {
            Log.i(this.javaClass.simpleName, "Update from local to remote")
            syncLocalToRemote(userId!!, localSchedule, localTimestamp)
            CalendarSyncResult.PUSH_SUCCESS
          } else {
            CalendarSyncResult.NONE
          }
        }
      } catch (e: Exception) {
        Log.e(this.javaClass.simpleName, "Failed to sync data.")
        Log.e(this.javaClass.simpleName, e.stackTraceToString())
        return CalendarSyncResult.FAILED
      }
    },
    onFailure = {
      Log.e(this.javaClass.simpleName, "Failed to get ID token.")
      Log.e(this.javaClass.simpleName, it.stackTraceToString())
      return CalendarSyncResult.FAILED
    })
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

  private fun syncRemoteToLocal(remoteTrash: TrashList, remoteTimestamp: Long) {
    syncRepository.setTimestamp(remoteTimestamp)
    persist.replaceTrashList(remoteTrash)
    syncRepository.setSyncComplete()
  }
  private suspend fun syncLocalToRemote(userId: String, localSchedule: TrashList, localTimestamp: Long) {
    authService.getIdToken().fold(
      onSuccess = { idToken ->
        apiAdapter.update(userId, localSchedule, localTimestamp, idToken).let { updateResult ->
          return when (updateResult.statusCode) {
            200 -> {
              syncRepository.setTimestamp(updateResult.timestamp)
              syncRepository.setSyncComplete()
            }
            else -> {
              throw Exception("Failed to sync local to remote. statusCode=${updateResult.statusCode}")
            }
          }
        }
      },
      onFailure = {
        throw Exception("Failed to get ID token.")
      }
    )
  }

}

enum class CalendarSyncResult {
  PUSH_SUCCESS,
  PULL_SUCCESS,
  PULL_AND_DISCARD,
  FAILED,
  NONE
}