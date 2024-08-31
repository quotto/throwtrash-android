package net.mythrowaway.app.domain.trash.infra.data.mapper

import net.mythrowaway.app.domain.trash.infra.data.ExcludeDayOfMonthJsonData
import net.mythrowaway.app.domain.trash.infra.data.ScheduleJsonData
import net.mythrowaway.app.domain.trash.infra.data.TrashJsonData
import net.mythrowaway.app.domain.trash.entity.ExcludeDayOfMonth
import net.mythrowaway.app.domain.trash.entity.ExcludeDayOfMonthList
import net.mythrowaway.app.domain.trash.entity.IntervalWeeklySchedule
import net.mythrowaway.app.domain.trash.entity.MonthlySchedule
import net.mythrowaway.app.domain.trash.entity.OrdinalWeeklySchedule
import net.mythrowaway.app.domain.trash.entity.Trash
import net.mythrowaway.app.domain.trash.entity.WeeklySchedule
import java.time.DayOfWeek
import java.time.LocalDate

class TrashJsonDataMapper {
  companion object {
    fun toData(trash: Trash): TrashJsonData {
      return TrashJsonData(
        _id = trash.id,
        _type = trash.type,
        _trashVal = trash.displayName,
        _schedules = trash.schedules.map { schedule ->
          when(schedule) {
            is WeeklySchedule -> {
              ScheduleJsonData(
                _type = "weekday",
                _value = if (schedule.dayOfWeek == DayOfWeek.SUNDAY) "0" else schedule.dayOfWeek.value.toString()
              )
            }
            is MonthlySchedule -> {
              ScheduleJsonData(
                _type = "month",
                _value = schedule.day.toString()
              )
            }
            is OrdinalWeeklySchedule -> {
              ScheduleJsonData(
                _type = "biweek",
                _value = "${if (schedule.dayOfWeek == DayOfWeek.SUNDAY) "0" else schedule.dayOfWeek.value.toString()}-${schedule.ordinalOfWeek}"
              )
            }
            is IntervalWeeklySchedule -> {
              ScheduleJsonData(
                _type = "evweek",
                _value = hashMapOf(
                  "weekday" to if (schedule.dayOfWeek == DayOfWeek.SUNDAY) "0" else schedule.dayOfWeek.value.toString(),
                  "start" to schedule.start.toString(),
                  "interval" to schedule.interval
                )
              )
            }
            else -> {
              throw IllegalArgumentException("スケジュールタイプが不正です")
            }
          }
        },
        _excludes = trash.excludeDayOfMonth.members.map { exclude ->
          ExcludeDayOfMonthJsonData(
            _month = exclude.month,
            _date = exclude.dayOfMonth
          )
        }
      )
    }

    fun toTrash(trashJsonData: TrashJsonData): Trash {

      val displayName = trashJsonData.trashVal  ?: ""
      val schedules = trashJsonData.schedules.map { schedule ->
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
            var orgDayOfWeek = orgValue[0]
            if(orgDayOfWeek == 0) orgDayOfWeek = 7
            OrdinalWeeklySchedule(_ordinalOfWeek = orgValue[1], _dayOfWeek = DayOfWeek.of(orgDayOfWeek))
          }
          "evweek" -> {
            val orgValue = (schedule.value as HashMap<*, *>)
            var start = orgValue["start"] as String
            var weekday = (orgValue["weekday"] as String).toInt()
            if(weekday == 0) weekday = 7
            val interval = orgValue["interval"] as Int
            // startが0埋めされていない場合は0埋めする
            if(start.length != 10) {
              val startArray = start.split("-")
              start = "${startArray[0]}-${startArray[1].padStart(2, '0')}-${startArray[2].padStart(2, '0')}"
            }

            IntervalWeeklySchedule(
              _start = LocalDate.parse(start),
              _dayOfWeek = DayOfWeek.of(weekday),
              _interval = interval
            )
          } else -> {
          throw IllegalArgumentException("スケジュールタイプが不正です")
        }
        }
      }
      val excludes = trashJsonData.excludes.map { exclude ->
        ExcludeDayOfMonth(exclude.month, exclude.date)
      }
      return Trash(
        trashJsonData.id,
        trashJsonData.type,
        displayName,
        schedules,
        ExcludeDayOfMonthList(excludes.toMutableList())
      )
    }
  }
}