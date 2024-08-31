package net.mythrowaway.app.domain.trash.usecase

import android.util.Log
import net.mythrowaway.app.domain.trash.usecase.dto.CalendarDayDTO
import net.mythrowaway.app.domain.trash.entity.TrashList
import net.mythrowaway.app.usecase.MobileApiInterface
import net.mythrowaway.app.usecase.SyncRepositoryInterface
import net.mythrowaway.app.usecase.TrashRepositoryInterface
import net.mythrowaway.app.usecase.UserRepositoryInterface
import net.mythrowaway.app.domain.trash.usecase.dto.MonthCalendarDTO
import net.mythrowaway.app.domain.trash.usecase.dto.mapper.TrashMapper
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

class CalendarUseCase @Inject constructor(
    private val persist: TrashRepositoryInterface,
    private val userRepository: UserRepositoryInterface,
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


    companion object {
        // まだ一度もDBと同期していない状態、アプリインストール時の初期状態
        const val SYNC_NO = 0
        // ローカルでデータ更新済みの状態、DB同期済みであればアプリ起動時の初期状態
        const val SYNC_WAITING = 1
        // ローカル更新後にDBに保存した状態
        const val SYNC_COMPLETE = 2
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
        if(syncState == SYNC_WAITING) {
            val userId:String? = userRepository.getUserId()
            val localSchedule: TrashList = persist.getAllTrash()
            if(userId.isNullOrEmpty()) {
                // TODO: ユーザーの登録処理は別のユースケースに切り出す?
                Log.i(this.javaClass.simpleName,"ID not exists,try register user.")
                apiAdapter.register(localSchedule).let { registeredTrash ->
                    userRepository.setUserId(registeredTrash.userId)
                    syncRepository.setTimestamp(registeredTrash.latestTrashListRegisteredTimestamp)
                    syncRepository.setSyncComplete()
                    Log.i(this.javaClass.simpleName,"Registered new id -> ${registeredTrash.userId}")
                }
                return CalendarSyncResult.PUSH_SUCCESS
            } else if(localSchedule.trashList.isNotEmpty()){
                    val localTimestamp = syncRepository.getTimeStamp()
                    Log.i(this.javaClass.simpleName,"Local Timestamp=$localTimestamp")
                    apiAdapter.update(userId, localSchedule, localTimestamp).let {updateResult ->
                        return when(updateResult.statusCode) {
                            200 -> {
                                Log.i(this.javaClass.simpleName,"Update to remote from local")
                                syncRepository.setTimestamp(updateResult.timestamp)
                                syncRepository.setSyncComplete()
                                CalendarSyncResult.PUSH_SUCCESS
                            }
                            400 -> {
                                val remoteTrash = apiAdapter.getRemoteTrash(userId)
                                // リモートからの同期処理
                                Log.i(
                                    this.javaClass.simpleName,
                                    "Local timestamp $localTimestamp is not match remote timestamp ${remoteTrash.timestamp},try sync local from remote"
                                )
                                syncRepository.setTimestamp(remoteTrash.timestamp)
                                persist.importScheduleList(remoteTrash.trashList)
                                syncRepository.setSyncComplete()
                                CalendarSyncResult.PULL_SUCCESS
                            }
                            else -> {
                                // それ以外のエラーはリモート同期待ちを維持
                                Log.e(this.javaClass.simpleName, "Failed update to remote from local, please try later.")
                                CalendarSyncResult.PENDING
                            }
                    }
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