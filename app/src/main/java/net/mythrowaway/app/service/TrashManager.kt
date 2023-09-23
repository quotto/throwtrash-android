package net.mythrowaway.app.service

import android.util.Log
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.usecase.DataRepositoryInterface
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Suppress("UNCHECKED_CAST", "LABEL_NAME_CLASH")
@Singleton
class TrashManager @Inject constructor(private val persist: DataRepositoryInterface) {
    private var mSchedule: ArrayList<TrashData> = arrayListOf()

    init {
        refresh()
    }

    private val weekdayOfPosition: List<List<Int>> = listOf(
        listOf(0,7,14,21,28),
        listOf(1,8,15,22,29),
        listOf(2,9,16,23,30),
        listOf(3,10,17,24,31),
        listOf(4,11,18,25,32),
        listOf(5,12,19,26,33),
        listOf(6,13,20,27,34)
    )

    private val trashNameMap: HashMap<String,String> = hashMapOf(
        "burn" to "もえるゴミ",
        "unburn" to "もえないゴミ",
        "plastic" to "プラスチック",
        "bin" to "ビン",
        "can" to "カン",
        "petbottle" to "ペットボトル",
        "paper" to "古紙",
        "resource" to "資源ごみ",
        "coarse" to "粗大ごみ"
    )

    fun refresh() {
        mSchedule = persist.getAllTrashSchedule()
    }

    fun getTrashName(type: String, trash_val: String?): String {
        return when(type) {
            "other" ->  trash_val ?: ""
            else -> trashNameMap[type] ?: ""
        }
    }

    /**
     * @param month 月（Calendarではなく通常の数え月）
     */
    private fun getComputeCalendar(year:Int, month:Int, date:Int, pos:Int): Calendar {
        val computeCalendar = Calendar.getInstance()
        var actualMonth = month - 1
        if(pos < 7 && date > 7) {
            actualMonth = month - 2
        } else if(pos > 27 && date < 7) {
            actualMonth = month
        }
        computeCalendar.set(Calendar.YEAR, year)
        computeCalendar.set(Calendar.MONTH, actualMonth)
        computeCalendar.set(Calendar.DATE, date)
        return computeCalendar
    }

    /**
     * dateとposの情報から当該月の前月/当月/翌月を判定してその月を返す
     * @param month 判定対象の月
     * @param date 判定対象の日
     * @param pos カレンダー上の月日の位置インデックス
     */
    private fun getActualMonth(month: Int,date: Int, pos: Int): Int {
        return if(pos < 7 && date > 7) {
            if(month - 1 == 0) 12 else month - 1
        } else if (pos > 27 && date < 7) {
            if(month + 1 == 13) 1 else month + 1
        } else month
    }

    /**
     * 登録スケジュールの件数を返す
     */
    fun getScheduleCount(): Int {
        return mSchedule.count()
    }

    /**
     * 5週間分全てのゴミを返す
     * @param
     * month 計算対象（現在CalendarViewに設定されている月。1月スタート）
     * dataSet カレンダーに表示する日付のリスト
     *
     * @return カレンダーのポジションごとのゴミ捨てリスト
     */
    fun getEnableTrashList(year:Int, month: Int, targetDateList: ArrayList<Int>) : Array<ArrayList<TrashData>> {
        val resultArray: Array<ArrayList<TrashData>> = Array(35){arrayListOf()}

        mSchedule.forEach { trash->
            val trashName = getTrashName(trash.type,trash.trash_val)
            val excludeList = trash.excludes.map{
                "${it.month}-${it.date}"
            }
            (trash.schedules).forEach { schedule->
                when(schedule.type) {
                    "weekday"->{
                        weekdayOfPosition[(schedule.value as String).toInt()].forEach { pos ->
                                Log.d(this.javaClass.simpleName, "pos $pos is $trashName")
                                if(!excludeList.contains("${getActualMonth(month,targetDateList[pos],pos)}-${targetDateList[pos]}")) resultArray[pos].add(trash)
                        }
                    }
                    "month"->{
                        var pos = 0
                        targetDateList.forEach { date ->
                            if((schedule.value as String).toInt() == date) {
                                Log.d(this.javaClass.simpleName, "$date is $trashName")
                                if(!excludeList.contains("${getActualMonth(month,targetDateList[pos],pos)}-$date")) resultArray[pos].add(trash)
                            }
                            pos++
                        }
                    }
                    "biweek" -> {
                        val dayOfWeek: List<String> = (schedule.value as String).split("-")
                        dayOfWeek.let {
                            weekdayOfPosition[dayOfWeek[0].toInt()].forEach { pos ->
                                val computeCalendar = getComputeCalendar(year, month, targetDateList[pos], pos)
                                if(dayOfWeek[1].toInt() == computeCalendar.get(Calendar.DAY_OF_WEEK_IN_MONTH)) {
                                    Log.d(this.javaClass.simpleName, "$pos is $trashName")
                                    if(!excludeList.contains("${getActualMonth(month,targetDateList[pos],pos)}-${targetDateList[pos]}")) resultArray[pos].add(trash)
                                }
                            }
                        }
                    }
                    "evweek" -> {
                        val evweekValue: HashMap<String,Any> = schedule.value as HashMap<String, Any>
                        (evweekValue["start"] as String).let {start ->
                            (evweekValue["weekday"] as String).let { weekday ->
                                weekdayOfPosition[weekday.toInt()].forEach { pos ->
                                    var interval = 2
                                    evweekValue["interval"]?.apply {
                                        interval = this as Int
                                    }
                                    if(isEvWeek(start,"$year-$month-${targetDateList[pos]}",interval) &&
                                       !excludeList.contains("${getActualMonth(month,targetDateList[pos],pos)}-${targetDateList[pos]}")) {
                                        Log.d(this.javaClass.simpleName, "$pos is $trashName")
                                        resultArray[pos].add(trash)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return resultArray
    }

    /**
     * 隔週のゴミ出し日の週であるかを判定する
     */
    fun isEvWeek(start: String, target: String, interval: Int): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val startCal: Calendar = Calendar.getInstance()
        val targetCal: Calendar = Calendar.getInstance()
        startCal.time = sdf.parse(start)!!
        targetCal.time = sdf.parse(target)!!
        targetCal.add(Calendar.DATE, -1 * (targetCal.get(Calendar.DAY_OF_WEEK) - 1))
        val diffDate = ((targetCal.timeInMillis - startCal.timeInMillis) / 1000 / 60 / 60 / 24).toInt()
        return diffDate % interval == 0
    }

    fun getTodaysTrash(year:Int, month: Int, date: Int): ArrayList<TrashData> {
        val result:ArrayList<TrashData> = ArrayList()

        val today:Calendar = Calendar.getInstance()
        today.set(Calendar.YEAR, year)
        today.set(Calendar.MONTH,month-1)
        today.set(Calendar.DATE, date)
        // 登録値は日曜=0,java.util.Calendarは日曜=1のため-1する
        val weekday:Int = today.get(Calendar.DAY_OF_WEEK) - 1

        mSchedule.forEach { trashData ->
            val excludeList = trashData.excludes.map {
                "${it.month}-${it.date}"
            }

            if(!excludeList.contains("$month-$date")) {
                trashData.schedules.forEach { schedule ->
                    var judge = false
                    when (schedule.type) {
                        "weekday" -> {
                            judge = schedule.value.toString().toInt() == weekday
                        }
                        "month" -> {
                            judge = schedule.value.toString().toInt() == date
                        }
                        "biweek" -> {
                            var numOfDay = 1 //対象日の曜日が第何かを示す
                            while ((date - (numOfDay * 7) > 0)) {
                                numOfDay++
                            }
                            judge = schedule.value.toString() == "$weekday-$numOfDay"
                        }
                        "evweek" -> {
                            val vMap: HashMap<String, Any> = schedule.value as HashMap<String, Any>
                            var interval = 2
                            vMap["interval"]?.apply {
                                interval = this as Int
                            }
                            judge = vMap["weekday"]!! == weekday.toString() && isEvWeek(
                                vMap["start"] as String,
                                "$year-$month-$date",
                                interval
                            )
                        }
                    }
                    if (judge) {
                        Log.d(this.javaClass.simpleName, "$year-$month-$date is $trashData")
                        result.add(trashData)
                        return@forEach
                    }
                }
            }
        }
        return result
    }
}