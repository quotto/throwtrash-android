package com.example.mythrowtrash.usecase

import com.example.mythrowtrash.domain.TrashData
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TrashManager(private val persist: IPersistentRepository) {
    var schedule: ArrayList<TrashData> = arrayListOf()

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
        schedule = persist.getAllTrashSchedule()
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
        return schedule.count()
    }

    /**
     * 新しいゴミ出し予定を追加する
     */
    fun addTrashData(trashData: TrashData) {
        schedule.add(trashData)
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

        schedule.forEach { trash->
            val trashName = getTrashName(trash.type,trash.trash_val)
            (trash.schedules).forEach { schedule->
                when(schedule.type) {
                    "weekday"->{
                        weekdayOfPosition[(schedule.value as String).toInt()].forEach { pos ->
                                resultArray[pos].add(trashName)
                        }
                    }
                    "month"->{
                        var i = 0
                        targetDateList.forEach { date ->
                            if((schedule.value as String).toInt() == date) {
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
        startCal.time = sdf.parse(start)
        targetCal.time = sdf.parse(target)
        targetCal.add(Calendar.DATE, -1 * (targetCal.get(Calendar.DAY_OF_WEEK) - 1))
        val diffDate = ((targetCal.timeInMillis - startCal.timeInMillis) / 1000 / 60 / 60 / 24).toInt()
        return diffDate % 2 == 0
    }
}