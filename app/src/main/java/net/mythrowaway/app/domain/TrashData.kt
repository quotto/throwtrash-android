package net.mythrowaway.app.domain

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.DayOfWeek
import java.time.LocalDate

class ExcludeDate {
    @JsonProperty("month")
    var month: Int = 1
    @JsonProperty("date")
    var date: Int = 1
}
class TrashSchedule {
    @JsonProperty("type")
    var type: String = ""

    @JsonProperty("value")
    var value: Any = Any()
}

class TrashData {
    @JsonProperty("id")
    var id: String = ""
    @JsonProperty("type")
    var type: TrashType = TrashType.BURN
    @JsonProperty("trash_val")
    var trash_val: String? = null
    @JsonProperty("schedules")
    var schedules: ArrayList<TrashSchedule> = ArrayList()
    @JsonProperty("excludes")
    var excludes: List<ExcludeDate> = listOf()

    fun equals(comparedTrashData: TrashData): Boolean {
        return if(this.type != TrashType.OTHER) {
            this.type == comparedTrashData.type
        } else {
            this.type == comparedTrashData.type  && this.trash_val == comparedTrashData.trash_val
        }
    }

    fun toTrash(): Trash {

        val displayName = trash_val  ?: ""
        val schedules = schedules.map { schedule ->
            when(schedule.type) {
                "weekday" -> {
                    var orgValue = (schedule.value as String).toInt()
                    if(orgValue == 0) orgValue = 7
                    WeeklySchedule(DayOfWeek.of(orgValue))
                }
                "month" -> {
                    val orgValue = (schedule.value as  String).toInt()
                    MonthlySchedule(orgValue)
                }
                "biweek" -> {
                    val orgValue = (schedule.value as String).split('-').map { bval->bval.toInt() }
                    var orgDayOfWeek = orgValue[1]
                    if(orgDayOfWeek == 0) orgDayOfWeek = 7
                    OrdinalWeeklySchedule(orgValue[1], DayOfWeek.of(orgDayOfWeek))
                }
                "evweek" -> {
                    val orgValue = (schedule.value as HashMap<String, Any>)
                    val start = orgValue["start"] as String
                    var weekday = (orgValue["weekday"] as String).toInt()
                    if(weekday == 0) weekday = 7
                    val interval = (orgValue["interval"] as String).toInt()
                    IntervalWeeklySchedule(LocalDate.parse(start), DayOfWeek.of(weekday), interval)
                } else -> {
                throw IllegalArgumentException("スケジュールタイプが不正です")
            }
            }
        }
        val excludes = excludes.map { exclude ->
            ExcludeDayOfMonth(exclude.month, exclude.date)
        }
        return Trash(
            id,
            type,
            displayName,
            schedules,
            ExcludeDayOfMonthList(excludes.toMutableList())
        )
    }

    companion object {
        fun fromTrash(trash: Trash): TrashData {
            val trashData = TrashData()
            trashData.id = trash.id
            trashData.type = trash.type
            trashData.trash_val = trash.displayName
            trashData.schedules = ArrayList(trash.schedules.map { schedule ->
                when(schedule) {
                   is WeeklySchedule -> {
                          val trashSchedule = TrashSchedule()
                          trashSchedule.type = "weekday"
                          trashSchedule.value = if (schedule.dayOfWeek == DayOfWeek.SUNDAY) "0" else schedule.dayOfWeek.value.toString()
                          trashSchedule
                      }
                      is MonthlySchedule -> {
                          val trashSchedule = TrashSchedule()
                          trashSchedule.type = "month"
                          trashSchedule.value = schedule.day
                          trashSchedule
                      }
                      is OrdinalWeeklySchedule -> {
                          val trashSchedule = TrashSchedule()
                          trashSchedule.type = "biweek"
                          trashSchedule.value = "${if (schedule.dayOfWeek == DayOfWeek.SUNDAY) "0" else schedule.dayOfWeek.value.toString()}-${schedule.ordinalOfWeek}"
                          trashSchedule
                      }
                      is IntervalWeeklySchedule -> {
                          val trashSchedule = TrashSchedule()
                          trashSchedule.type = "evweek"
                          val evweekValue = hashMapOf(
                              "weekday" to if (schedule.dayOfWeek == DayOfWeek.SUNDAY) "0" else schedule.dayOfWeek.value.toString(),
                              "start" to schedule.start.toString(),
                              "interval" to schedule.interval
                          )
                          trashSchedule.value = evweekValue
                          trashSchedule
                      }
                      else -> {
                          throw IllegalArgumentException("スケジュールタイプが不正です")
                      }
                }
            })
            trashData.excludes = trash.excludeDayOfMonth.members.map { exclude ->
                val excludeDate = ExcludeDate()
                excludeDate.month = exclude.month
                excludeDate.date = exclude.dayOfMonth
                excludeDate
            }
            return trashData
        }
    }
}

class RegisteredData {
    var id: String = ""
    var scheduleList: ArrayList<TrashData> = arrayListOf()
    var timestamp: Long = 0
}

class LatestTrashData {
    var scheduleList: ArrayList<TrashData> = arrayListOf()
    var timestamp: Long = 0
}
