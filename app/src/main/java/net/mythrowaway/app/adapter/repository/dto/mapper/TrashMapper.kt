package net.mythrowaway.app.adapter.repository.dto.mapper

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import net.mythrowaway.app.adapter.repository.dto.ExcludeDayOfMonthDTO
import net.mythrowaway.app.adapter.repository.dto.ScheduleDTO
import net.mythrowaway.app.adapter.repository.dto.TrashDTO
import net.mythrowaway.app.domain.ExcludeDayOfMonth
import net.mythrowaway.app.domain.ExcludeDayOfMonthList
import net.mythrowaway.app.domain.IntervalWeeklySchedule
import net.mythrowaway.app.domain.MonthlySchedule
import net.mythrowaway.app.domain.OrdinalWeeklySchedule
import net.mythrowaway.app.domain.Trash
import net.mythrowaway.app.domain.WeeklySchedule
import java.time.DayOfWeek
import java.time.LocalDate

class TrashDTOListTypeReference : TypeReference<ArrayList<TrashDTO>>(){}
class TrashMapper {
  companion object {
    fun toTrashDTO(trash: Trash): TrashDTO {
      val trashData = TrashDTO()
      trashData.id = trash.id
      trashData.type = trash.type
      trashData.trash_val = trash.displayName
      trashData.schedules = ArrayList(trash.schedules.map { schedule ->
        when(schedule) {
          is WeeklySchedule -> {
            val trashSchedule = ScheduleDTO()
            trashSchedule.type = "weekday"
            trashSchedule.value = if (schedule.dayOfWeek == DayOfWeek.SUNDAY) "0" else schedule.dayOfWeek.value.toString()
            trashSchedule
          }
          is MonthlySchedule -> {
            val trashSchedule = ScheduleDTO()
            trashSchedule.type = "month"
            trashSchedule.value = schedule.day.toString()
            trashSchedule
          }
          is OrdinalWeeklySchedule -> {
            val trashSchedule = ScheduleDTO()
            trashSchedule.type = "biweek"
            trashSchedule.value = "${if (schedule.dayOfWeek == DayOfWeek.SUNDAY) "0" else schedule.dayOfWeek.value.toString()}-${schedule.ordinalOfWeek}"
            trashSchedule
          }
          is IntervalWeeklySchedule -> {
            val trashSchedule = ScheduleDTO()
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
        val excludeDate = ExcludeDayOfMonthDTO()
        excludeDate.month = exclude.month
        excludeDate.date = exclude.dayOfMonth
        excludeDate
      }
      return trashData
    }

    fun toTrash(trashDTO: TrashDTO): Trash {

      val displayName = trashDTO.trash_val  ?: ""
      val schedules = trashDTO.schedules.map { schedule ->
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
      val excludes = trashDTO.excludes.map { exclude ->
        ExcludeDayOfMonth(exclude.month, exclude.date)
      }
      return Trash(
        trashDTO.id,
        trashDTO.type,
        displayName,
        schedules,
        ExcludeDayOfMonthList(excludes.toMutableList())
      )
    }

    fun toTrashDTOList(stringData: String): ArrayList<TrashDTO> {
      print(stringData)
      val mapper = ObjectMapper()
      mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
      return mapper.readValue(stringData, TrashDTOListTypeReference())
    }
    fun toJson(trashDTOList:List<TrashDTO>): String {
      val mapper = ObjectMapper()
      mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
      mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
      return mapper.writeValueAsString(trashDTOList)
    }
  }
}