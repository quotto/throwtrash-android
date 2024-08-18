package net.mythrowaway.app.usecase

import android.util.Log
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.usecase.dto.CalendarDayDTO
import net.mythrowaway.app.domain.Trash
import net.mythrowaway.app.usecase.dto.MonthCalendarDTO
import net.mythrowaway.app.usecase.dto.mapper.TrashMapper
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import kotlin.collections.ArrayList

class CalendarUseCase @Inject constructor(
    private val persist: DataRepositoryInterface,
    private val config: ConfigRepositoryInterface,
    private val apiAdapter: MobileApiInterface
) {

    fun getTrashCalendarOfMonth(year: Int, month: Int): MonthCalendarDTO {
        val trashList = persist.getAllTrashSchedule()
        // TODO: 最終的にはリポジトリを変更して対応したい
        val trashes = trashList.map {
            it.toTrash()
        }

        val calendarDays = generateCalendarDays(year, month, trashes)

        return MonthCalendarDTO(year, month, calendarDays)
    }

    private fun generateCalendarDays(year: Int, month: Int, trashes: List<Trash>): List<CalendarDayDTO> {
        val calendarDayDTOMutableList = mutableListOf<CalendarDayDTO>()
        var currentDate = LocalDate.of(year, month, 1)
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))

        for (i in 0..34) {
            val targetTrashes = trashes.filter { it.isTrashDay(currentDate) }.map{ TrashMapper.toTrashDTO(it) }
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
        Log.i(this.javaClass.simpleName, "Current Sync status -> ${config.getSyncState()}")
        if(config.getSyncState() == SYNC_WAITING) {
            val userId:String? = config.getUserId()
            val localSchedule: ArrayList<TrashData> = persist.getAllTrashSchedule()
            if(userId.isNullOrEmpty()) {
                // TODO: ユーザーの登録処理は別のユースケースに切り出す?
                Log.i(this.javaClass.simpleName,"ID not exists,try register user.")
                apiAdapter.register(localSchedule)?.let { info ->
                    config.setUserId(info.id)
                    config.setTimestamp(info.timestamp)
                    config.setSyncComplete()
                    Log.i(this.javaClass.simpleName,"Registered new id -> ${info.id}")
                }
                return CalendarSyncResult.PUSH_SUCCESS
            } else if(localSchedule.size > 0){
                return apiAdapter.sync(userId)?.let { data ->
                    val localTimestamp = config.getTimeStamp()
                    Log.i(this.javaClass.simpleName,"Local Timestamp=$localTimestamp")
                    apiAdapter.update(userId, localSchedule, localTimestamp).let {
                        return when(it.statusCode) {
                            200 -> {
                                Log.i(this.javaClass.simpleName,"Update to remote from local")
                                config.setTimestamp(it.timestamp)
                                config.setSyncComplete()
                                CalendarSyncResult.PUSH_SUCCESS
                            }
                            400 -> {
                                // リモートからの同期処理
                                Log.i(this.javaClass.simpleName,"Local timestamp $localTimestamp is not match remote timestamp ${it.timestamp},try sync local from remote")
                                config.setTimestamp(data.second)
                                persist.importScheduleList(data.first)
                                config.setSyncComplete()
                                CalendarSyncResult.PULL_SUCCESS
                            }
                            else -> {
                                // それ以外のエラーはリモート同期待ちを維持
                                Log.e(this.javaClass.simpleName, "Failed update to remote from local, please try later.")
                                CalendarSyncResult.PENDING
                            }
                        }
                    }
                } ?: CalendarSyncResult.FAILED
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