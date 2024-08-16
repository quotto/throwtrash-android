package net.mythrowaway.app.adapter.repository.dto

import com.fasterxml.jackson.annotation.JsonProperty
import net.mythrowaway.app.domain.TrashType
class TrashDTO {
  @JsonProperty("id")
  var id: String = ""
  @JsonProperty("type")
  var type: TrashType = TrashType.BURN
  @JsonProperty("trash_val")
  var trash_val: String? = null
  @JsonProperty("schedules")
  var schedules: ArrayList<ScheduleDTO> = ArrayList()
  @JsonProperty("excludes")
  var excludes: List<ExcludeDayOfMonthDTO> = listOf()
}