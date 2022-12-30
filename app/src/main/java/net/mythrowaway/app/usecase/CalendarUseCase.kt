package net.mythrowaway.app.usecase

import android.util.Log
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.service.TrashManager
import java.util.Calendar
import javax.inject.Inject
import kotlin.collections.ArrayList

class CalendarUseCase @Inject constructor(
    private val presenter: CalendarPresenterInterface,
    private val trashManager: TrashManager,
    private val persist: DataRepositoryInterface,
    private val config: ConfigRepositoryInterface,
    private val apiAdapter: MobileApiInterface
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
        Log.d(this.javaClass.simpleName, "Target Calendar -> year=$year,month=$month")
        val dateList:ArrayList<Int>  = generateMonthCalendar(year, month)
        presenter.setCalendar(year,month,trashManager.getEnableTrashList(year,month,dateList), dateList)
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
        Log.i(this.javaClass.simpleName, "Current Sync status -> ${config.getSyncState()}")
        if(config.getSyncState() == SYNC_WAITING) {
            val userId:String? = config.getUserId()
            val localSchedule: ArrayList<TrashData> = persist.getAllTrashSchedule()
            if(userId == null || userId.isEmpty()) {
                Log.i(this.javaClass.simpleName,"ID not exists,Register.")
                apiAdapter.register(localSchedule)?.let { info ->
                    config.setUserId(info.id)
                    config.setTimestamp(info.timestamp)
                    config.setSyncState(SYNC_COMPLETE)
                    Log.i(this.javaClass.simpleName,"Registered new id -> ${info.id}")
                }
            } else if(localSchedule.size > 0){
                apiAdapter.sync(userId)?.let { data ->
                    val localTimestamp = config.getTimeStamp()
                    Log.i(this.javaClass.simpleName,"Local Timestamp=$localTimestamp")
                    apiAdapter.update(userId, localSchedule, localTimestamp).let {
                        when(it.statusCode) {
                            200 -> {
                                Log.i(this.javaClass.simpleName,"Update to remote from local")
                                config.setTimestamp(it.timestamp)
                                config.setSyncState(SYNC_COMPLETE)
                            }
                            400 -> {
                                // リモートからの同期処理
                                Log.i(this.javaClass.simpleName,"Local timestamp $localTimestamp is not match remote timestamp ${it.timestamp},try sync to local from remote")
                                config.setTimestamp(data.second)
                                persist.importScheduleList(data.first)
                                trashManager.refresh()
                                config.setSyncState(SYNC_COMPLETE)
                            }
                            else -> {
                                // それ以外のエラーはリモート同期待ちを維持
                                Log.e(this.javaClass.simpleName, "Failed update to remote from local, please try later.")
                            }
                        }
                    }
                }
            } else {
                Log.w(this.javaClass.simpleName, "Not update local to remote because local schedule is nothing.")
            }
        }
    }
}