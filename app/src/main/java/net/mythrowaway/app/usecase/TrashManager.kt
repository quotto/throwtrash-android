package net.mythrowaway.app.usecase

import android.util.Log
import net.mythrowaway.app.domain.TrashData
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TrashManager(private val persist: IPersistentRepository) {
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
     * 登録スケジュールの件数を返す
     */
    fun getScheduleCount(): Int {
        return mSchedule.count()
    }

    /**
     * 新しいゴミ出し予定を追加する
     */
    fun addTrashData(trashData: TrashData) {
        mSchedule.add(trashData)
    }

    /**
     * 5週間分全てのゴミを返す
     * @param
     * month 計算対象（現在CalendarViewに設定されている月。1月スタート）
     * dataSet カレンダーに表示する日付のリスト
     *
     * @return カレンダーのポジションごとのゴミ捨てリスト
     */
    fun getEnableTrashList(year:Int, month: Int, targetDateList: ArrayList<Int>) : Array<ArrayList<String>> {
        val resultArray: Array<ArrayList<String>> = Array(35){arrayListOf<String>()}

        mSchedule.forEach { trash->
            val trashName = getTrashName(trash.type,trash.trash_val)
            (trash.schedules).forEach { schedule->
                when(schedule.type) {
                    "weekday"->{
                        weekdayOfPosition[(schedule.value as String).toInt()].forEach { pos ->
                                Log.d(this.javaClass.simpleName, "pos $pos is $trashName")
                                resultArray[pos].add(trashName)
                        }
                    }
                    "month"->{
                        var i = 0
                        targetDateList.forEach { date ->
                            if((schedule.value as String).toInt() == date) {
                                Log.d(this.javaClass.simpleName, "$date is $trashName")
                                resultArray[i].add(trashName)
                            }
                            i++
                        }
                    }
                    "biweek" -> {
                        val dayOfWeek: List<String>? = (schedule.value as String).split("-")
                        dayOfWeek?.let {
                            weekdayOfPosition[dayOfWeek[0].toInt()].forEach { pos ->
                                val computeCalendar = getComputeCalendar(year, month, targetDateList[pos], pos)
                                if(dayOfWeek[1].toInt() == computeCalendar.get(Calendar.DAY_OF_WEEK_IN_MONTH)) {
                                    Log.d(this.javaClass.simpleName, "$pos is $trashName")
                                    resultArray[pos].add(trashName)
                                }
                            }
                        }
                    }
                    "evweek" -> {
                        val evweekValue: HashMap<String,String> = schedule.value as HashMap<String, String>
                        evweekValue["start"]?.let {start ->
                            evweekValue["weekday"]?.let { weekday ->
                                weekdayOfPosition[weekday.toInt()].forEach { pos ->
                                    if(isThisWeek(start,"$year-$month-${targetDateList[pos]}")) {
                                        Log.d(this.javaClass.simpleName, "$pos is $trashName")
                                        resultArray[pos].add(trashName)
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
    fun isThisWeek(start: String, target: String): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val startCal: Calendar = Calendar.getInstance()
        val targetCal: Calendar = Calendar.getInstance()
        startCal.time = sdf.parse(start)!!
        targetCal.time = sdf.parse(target)!!
        targetCal.add(Calendar.DATE, -1 * (targetCal.get(Calendar.DAY_OF_WEEK) - 1))
        val diffDate = ((targetCal.timeInMillis - startCal.timeInMillis) / 1000 / 60 / 60 / 24).toInt()
        return diffDate % 2 == 0
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
            trashData.schedules.forEach {schedule ->
                var judge = false
                when(schedule.type) {
                    "weekday" -> {
                        judge = schedule.value.toString().toInt() == weekday
                    }
                    "month" -> {
                        judge = schedule.value.toString().toInt() == date
                    }
                    "biweek" -> {
                        var numOfDay:Int = 1 //対象日の曜日が第何かを示す
                        while((date - (numOfDay * 7) > 0)) {
                            numOfDay++
                        }
                        judge = schedule.value.toString() == "$weekday-$numOfDay"
                    }
                    "evweek" -> {
                        val vMap:HashMap<String,String> = schedule.value as HashMap<String,String>
                        judge = vMap["weekday"]!! == weekday.toString() && isThisWeek(vMap["start"]!!, "$year-$month-$date")
                    }
                }
                if(judge) {
                    Log.d(this.javaClass.simpleName, "$year-$month-$date is $trashData")
                    result.add(trashData)
                    return@forEach
                }
            }
        }
        return result
    }
}