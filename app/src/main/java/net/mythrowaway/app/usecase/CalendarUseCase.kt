package net.mythrowaway.app.usecase

import java.util.Calendar
import kotlin.collections.ArrayList

class CalendarUseCase(
    private val presenter: ICalendarPresenter,
    private val trashManager: TrashManager,
    private val calendarManager: ICalendarManager,
    private val persist: IPersistentRepository,
    private val config: IConfigRepository,
    private val apiAdapter: IAPIAdapter
) {

    private fun generateMonthCalendar(year: Int, month: Int): ArrayList<Int> {
        // 出力値算出用のインスタンス
        val computeCalendar = Calendar.getInstance()
        computeCalendar.set(Calendar.YEAR,year)
        computeCalendar.set(Calendar.MONTH,month-1)
        computeCalendar.set(Calendar.DAY_OF_MONTH, 1)

        val dateArray: ArrayList<Int> = ArrayList()
        // 日曜日の場合は戻す必要がないため1日目の曜日から-1する
        computeCalendar.add(Calendar.DAY_OF_MONTH, -1 * (computeCalendar.get(Calendar.DAY_OF_WEEK)-1))
        for(i in 1..35) {
            dateArray.add(computeCalendar.get(Calendar.DAY_OF_MONTH))
            computeCalendar.add(Calendar.DAY_OF_MONTH,1)
        }
        return dateArray
    }

    fun generateMonthSchedule(year:Int, month: Int) {
        val dateList:ArrayList<Int>  = generateMonthCalendar(year, month)
        presenter.setCalendar(year,month,trashManager.getEnableTrashList(month,year,dateList), dateList)
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
    fun syncData() {
        if(config.getSyncState() == SYNC_WAITING) {
            val userId:String? = config.getUserId()
            if(userId == null || userId.isEmpty()) {
                apiAdapter.register(persist.getAllTrashSchedule())?.let { info ->
                    config.setUserId(info.first)
                    config.setTimestamp(info.second)
                    config.setSyncState(SYNC_COMPLETE)
                    println("[MyApp] registered: id=${info.first}")
                }
            } else {
                apiAdapter.sync(userId)?.let { data ->
                    val localTimestamp = config.getTimeStamp()
                    if(data.second > localTimestamp) {
                        println("[MyApp] updated from DB: timestamp=${data.second}")
                        config.setTimestamp(data.second)
                        persist.importScheduleList(data.first)
                        trashManager.refresh()
                    } else if(data.second < localTimestamp) {
                        apiAdapter.update(userId, persist.getAllTrashSchedule())
                            ?.let { timestamp ->
                                println("[MyApp] updated to DB: timestamp=${data.second}")
                                config.setTimestamp(timestamp)
                            }
                    }
                    config.setSyncState(SYNC_COMPLETE)
                }
            }
        }
    }
}