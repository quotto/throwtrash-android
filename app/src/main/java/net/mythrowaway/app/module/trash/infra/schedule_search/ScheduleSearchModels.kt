package net.mythrowaway.app.module.trash.infra.schedule_search

data class ScheduleSearchResponse(
  val trashes: List<ScheduleSearchTrashItem>,
  val message: String? = null,
  val errorType: ScheduleSearchErrorType = ScheduleSearchErrorType.NONE
)

enum class ScheduleSearchErrorType(val value: String) {
  NONE(""),
  INVALID_ADDRESS("invalid_address"),
  INVALID_POSTAL_CODE("invalid_postalcode"),
  UNSUPPORTED_SCHEDULE("unsupported_schedule"),
  UNKNOWN("unknown");

  companion object {
    fun from(value: String?): ScheduleSearchErrorType {
      return entries.firstOrNull { it.value == value.orEmpty() } ?: UNKNOWN
    }
  }
}

data class ScheduleSearchTrashItem(
  val type: String,
  val trashName: String? = null,
  val schedule: List<ScheduleSearchScheduleItem>
)

data class ScheduleSearchScheduleItem(
  val type: String,
  val value: Any?
)

data class EvweekScheduleValue(
  val weekday: Int,
  val interval: Int,
  val startDate: String
)

data class ScheduleSearchMappingResult(
  val trashList: net.mythrowaway.app.module.trash.entity.trash.TrashList,
  val messages: List<String>
)
