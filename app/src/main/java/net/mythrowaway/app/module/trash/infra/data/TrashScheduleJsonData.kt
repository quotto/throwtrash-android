package net.mythrowaway.app.module.trash.infra.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder("trashData", "globalExcludes")
data class TrashScheduleJsonData(
  @JsonProperty("trashData")
  private val _trashData: List<TrashJsonData>,
  @JsonProperty("globalExcludes")
  private val _globalExcludes: List<ExcludeDayOfMonthJsonData>?
) {
  val trashData: List<TrashJsonData>
    get() = _trashData

  val globalExcludes: List<ExcludeDayOfMonthJsonData>
    get() = _globalExcludes?: listOf()
}
