package com.example.mythrowtrash.usecase

import com.example.mythrowtrash.domain.TrashData
import java.util.Calendar
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
                        val targetDate: List<String>? = evweekValue["start"]?.split("-")
                        targetDate?.let {
                            val targetCalendar = Calendar.getInstance()
                            targetCalendar.set(Calendar.YEAR, targetDate[0].toInt())
                            targetCalendar.set(Calendar.MONTH,targetDate[1].toInt()-1) //Calendarの月は0スタート
                            targetCalendar.set(Calendar.DATE,targetDate[2].toInt())
                            evweekValue["weekday"]?.let{weekday->
                                weekdayOfPosition[weekday.toInt()].forEach{ pos ->
                                    val computeCalendar = getComputeCalendar(year, month, targetDateList[pos], pos)
                                    // 奇数週/偶数週が一致すればゴミ出し日
                                    if(targetCalendar.get(Calendar.WEEK_OF_YEAR) % 2 ==
                                            computeCalendar.get(Calendar.WEEK_OF_YEAR) % 2) {
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
}