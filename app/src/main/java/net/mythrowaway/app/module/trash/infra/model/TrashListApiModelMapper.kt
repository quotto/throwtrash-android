package net.mythrowaway.app.module.trash.infra.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import net.mythrowaway.app.module.trash.entity.trash.ExcludeDayOfMonth
import net.mythrowaway.app.module.trash.entity.trash.ExcludeDayOfMonthList
import net.mythrowaway.app.module.trash.entity.trash.IntervalWeeklySchedule
import net.mythrowaway.app.module.trash.entity.trash.MonthlySchedule
import net.mythrowaway.app.module.trash.entity.trash.OrdinalWeeklySchedule
import net.mythrowaway.app.module.trash.entity.trash.Trash
import net.mythrowaway.app.module.trash.entity.trash.TrashList
import net.mythrowaway.app.module.trash.entity.trash.WeeklySchedule
import java.time.DayOfWeek
import java.time.LocalDate

class TrashListApiModelTypeReference : TypeReference<List<TrashApiModel>>()
class TrashListApiModelMapper {
  companion object {

    fun toTrashList(trashListApiModel: TrashListApiModel): TrashList {
      return TrashList(
          trashListApiModel.description.map { trashApiModel -> toTrash(trashApiModel) }
        )
    }

    fun toTrashApiModelList(trashList: TrashList): List<TrashApiModel> {
      return trashList.trashList.map { trash -> toTrashApiModel(trash) }
    }

    @Suppress("UNCHECKED_CAST")
    fun fromJson(jsonString: String): TrashListApiModel {
      val mapper = ObjectMapper()
      mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
      val trashApiModel = TrashListApiModel(
        mapper.readValue(jsonString, TrashListApiModelTypeReference())
      )
      return trashApiModel.copy(_description = trashApiModel.description.map {orgTrashApiModel ->
        orgTrashApiModel.copy(_schedules = orgTrashApiModel.schedules.map {scheduleApiModel ->
          if(scheduleApiModel.type == "evweek") {
            val orgValue = scheduleApiModel.value as HashMap<String,Any>
            if((orgValue["start"] as String).length != 10) {
              // 0埋めする
              val startArray = (orgValue["start"] as String).split("-")
              orgValue["start"] = "${startArray[0]}-${startArray[1].padStart(2, '0')}-${startArray[2].padStart(2, '0')}"
            }
            if(!orgValue.contains("interval")){
              orgValue["interval"] = 2
            }
            @Suppress("UnusedDataClassCopyResult")
            scheduleApiModel.copy(_value = orgValue)
          }
          scheduleApiModel
        })
      })
    }
    fun toJson(trashApiModelList: List<TrashApiModel>): String {
      val mapper = ObjectMapper()
      mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
      return mapper.writeValueAsString(trashApiModelList)
    }
    private fun toTrashApiModel(trash: Trash): TrashApiModel {
      return TrashApiModel(
        _id= trash.id,
        _type = trash.type,
        _trashVal = trash.displayName,
        _schedules = (trash.schedules.map { schedule ->
          when(schedule) {
            is WeeklySchedule -> {
              ScheduleApiModel(
                _type = "weekday",
                _value = if (schedule.dayOfWeek == DayOfWeek.SUNDAY) "0" else schedule.dayOfWeek.value.toString()
              )
            }
            is MonthlySchedule -> {
              ScheduleApiModel(
                _type = "month",
                _value = schedule.day.toString()
              )
            }
            is OrdinalWeeklySchedule -> {
              ScheduleApiModel(
                _type = "biweek",
                _value = "${if (schedule.dayOfWeek == DayOfWeek.SUNDAY) "0" else schedule.dayOfWeek.value.toString()}-${schedule.ordinalOfWeek}"
              )
            }
            is IntervalWeeklySchedule -> {
              ScheduleApiModel(
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
        }),
        _excludes = trash.excludeDayOfMonth.members.map { exclude ->
          ExcludeDayOfMonthApiModel(
            _month = exclude.month,
            _date = exclude.dayOfMonth
          )
        }
      )
    }

    private fun toTrash(trashApiModel: TrashApiModel): Trash {

      val displayName = trashApiModel.trashVal  ?: ""
      val schedules = trashApiModel.schedules.map { schedule ->
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
            OrdinalWeeklySchedule(orgValue[1], DayOfWeek.of(orgDayOfWeek))
          }
          "evweek" -> {
            val orgValue = (schedule.value as HashMap<*, *>)
            val start = orgValue["start"] as String
            var weekday = (orgValue["weekday"] as String).toInt()
            if(weekday == 0) weekday = 7
            val interval = orgValue["interval"] as Int
            IntervalWeeklySchedule(LocalDate.parse(start), DayOfWeek.of(weekday), interval)
          } else -> {
          throw IllegalArgumentException("スケジュールタイプが不正です")
        }
        }
      }
      val excludes = trashApiModel.excludes.map { exclude ->
        ExcludeDayOfMonth(exclude.month, exclude.date)
      }
      return Trash(
        trashApiModel.id,
        trashApiModel.type,
        displayName,
        schedules,
        ExcludeDayOfMonthList(excludes.toMutableList())
      )
    }
  }
}