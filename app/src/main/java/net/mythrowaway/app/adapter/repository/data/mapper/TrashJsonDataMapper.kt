package net.mythrowaway.app.adapter.repository.data.mapper

import net.mythrowaway.app.adapter.repository.data.ExcludeDayOfMonthJsonData
import net.mythrowaway.app.adapter.repository.data.ScheduleJsonData
import net.mythrowaway.app.adapter.repository.data.TrashJsonData
import net.mythrowaway.app.domain.ExcludeDayOfMonth
import net.mythrowaway.app.domain.ExcludeDayOfMonthList
import net.mythrowaway.app.domain.IntervalWeeklySchedule
import net.mythrowaway.app.domain.MonthlySchedule
import net.mythrowaway.app.domain.OrdinalWeeklySchedule
import net.mythrowaway.app.domain.Trash
import net.mythrowaway.app.domain.WeeklySchedule
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
                  "interval" to schedule.interval.toString()
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