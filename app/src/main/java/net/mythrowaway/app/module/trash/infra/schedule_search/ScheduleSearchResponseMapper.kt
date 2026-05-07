package net.mythrowaway.app.module.trash.infra.schedule_search

import net.mythrowaway.app.module.trash.entity.trash.ExcludeDayOfMonthList
import net.mythrowaway.app.module.trash.entity.trash.IntervalWeeklySchedule
import net.mythrowaway.app.module.trash.entity.trash.MonthlySchedule
import net.mythrowaway.app.module.trash.entity.trash.OrdinalWeeklySchedule
import net.mythrowaway.app.module.trash.entity.trash.Schedule
import net.mythrowaway.app.module.trash.entity.trash.Trash
import net.mythrowaway.app.module.trash.entity.trash.TrashList
import net.mythrowaway.app.module.trash.entity.trash.TrashType
import net.mythrowaway.app.module.trash.entity.trash.WeeklySchedule
import java.time.DayOfWeek
import java.time.LocalDate

object ScheduleSearchResponseMapper {
  fun toTrashList(response: ScheduleSearchResponse): ScheduleSearchMappingResult {
    val messages = mutableListOf<String>()
    val trashes = response.trashes.mapIndexedNotNull { index, item ->
      val trashType = toTrashType(item.type) ?: return@mapIndexedNotNull null
      val trashName = toTrashName(trashType, item.trashName)
      val schedules = item.schedule.mapNotNull { scheduleItem ->
        toSchedule(scheduleItem, itemLabel(trashType, trashName))?.let { result ->
          result.message?.let { messages.add(it) }
          result.schedule
        }
      }.take(3)

      if (item.schedule.size > 3) {
        messages.add("${itemLabel(trashType, trashName)}: 4件目以降の日程")
      }
      if (schedules.isEmpty()) {
        return@mapIndexedNotNull null
      }
      Trash(
        _id = "schedule-search-${System.currentTimeMillis()}-$index",
        _type = trashType,
        _displayName = trashName,
        schedules = schedules,
        _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
      )
    }
    return ScheduleSearchMappingResult(TrashList(trashes), messages)
  }

  private fun toTrashType(type: String): TrashType? {
    return when (type) {
      "burn" -> TrashType.BURN
      "unburn" -> TrashType.UNBURN
      "resource" -> TrashType.RESOURCE
      "plastic" -> TrashType.PLASTIC
      "bin" -> TrashType.BOTTLE
      "can" -> TrashType.CAN
      "bottle" -> TrashType.PETBOTTLE
      "paper" -> TrashType.PAPER
      "other" -> TrashType.OTHER
      else -> null
    }
  }

  private fun toTrashName(type: TrashType, trashName: String?): String {
    if (type != TrashType.OTHER) {
      return ""
    }
    val normalized = trashName?.trim().orEmpty().ifEmpty { "その他" }
    return normalized.take(10)
  }

  private fun itemLabel(type: TrashType, trashName: String): String {
    return if (type == TrashType.OTHER) trashName else type.getTrashText()
  }

  private fun toSchedule(item: ScheduleSearchScheduleItem, label: String): ScheduleMappingResult? {
    return when (item.type) {
      "weekday" -> ScheduleMappingResult(WeeklySchedule(toDayOfWeek(item.value.toIntValue())))
      "biweek" -> {
        val parts = item.value.toString().split("-")
        if (parts.size != 2) return ScheduleMappingResult(null, "$label: ${item.value}")
        ScheduleMappingResult(
          OrdinalWeeklySchedule(
            _dayOfWeek = toDayOfWeek(parts[0].toInt()),
            _ordinalOfWeek = parts[1].toInt()
          )
        )
      }
      "month" -> ScheduleMappingResult(MonthlySchedule(item.value.toIntValue()))
      "evweek" -> toIntervalSchedule(item.value, label)
      "unmatched" -> ScheduleMappingResult(null, "$label: ${item.value}")
      else -> ScheduleMappingResult(null, "$label: ${item.value}")
    }
  }

  private fun toIntervalSchedule(value: Any?, label: String): ScheduleMappingResult {
    val weekday: Int
    val interval: Int
    val startDate: String
    when (value) {
      is EvweekScheduleValue -> {
        weekday = value.weekday
        interval = value.interval
        startDate = value.startDate
      }
      is Map<*, *> -> {
        weekday = value["weekday"].toIntValue()
        interval = value["interval"].toIntValue()
        startDate = (value["start_date"] ?: value["start"]).toString()
      }
      is org.json.JSONObject -> {
        weekday = value.get("weekday").toIntValue()
        interval = value.get("interval").toIntValue()
        startDate = value.optString("start_date").ifBlank { value.getString("start") }
      }
      else -> return ScheduleMappingResult(null, "$label: $value")
    }
    if (interval !in 2..4) {
      return ScheduleMappingResult(null, "$label: ${interval}週間ごとの${toDayOfWeek(weekday).toJapaneseText()}")
    }
    return ScheduleMappingResult(
      IntervalWeeklySchedule(
        _start = LocalDate.parse(startDate),
        _dayOfWeek = toDayOfWeek(weekday),
        _interval = interval
      )
    )
  }

  private fun toDayOfWeek(value: Int): DayOfWeek {
    return if (value == 0) DayOfWeek.SUNDAY else DayOfWeek.of(value)
  }

  private fun Any?.toIntValue(): Int {
    return when (this) {
      is Int -> this
      is Long -> this.toInt()
      is Double -> this.toInt()
      is String -> this.toInt()
      else -> this.toString().toInt()
    }
  }

  private fun DayOfWeek.toJapaneseText(): String {
    return when (this) {
      DayOfWeek.SUNDAY -> "日曜日"
      DayOfWeek.MONDAY -> "月曜日"
      DayOfWeek.TUESDAY -> "火曜日"
      DayOfWeek.WEDNESDAY -> "水曜日"
      DayOfWeek.THURSDAY -> "木曜日"
      DayOfWeek.FRIDAY -> "金曜日"
      DayOfWeek.SATURDAY -> "土曜日"
    }
  }
}

private data class ScheduleMappingResult(
  val schedule: Schedule?,
  val message: String? = null
)
