package net.mythrowaway.app.module.trash.infra.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import net.mythrowaway.app.module.trash.entity.trash.TrashType
@JsonPropertyOrder("id", "type", "trash_val", "schedules", "excludes")
class TrashJsonData (
  @JsonProperty("id")
  private val _id: String,
  @JsonProperty("type")
  private val _type: TrashType,
  @JsonProperty("trash_val")
  private val _trashVal: String? = null,
  @JsonProperty("schedules")
  private val _schedules: List<ScheduleJsonData>,
  @JsonProperty("excludes")
  private val _excludes: List<ExcludeDayOfMonthJsonData>?
) {
  val id: String
    get() = _id

  val type: TrashType
    get() = _type

  val trashVal: String
    @JsonProperty("trash_val")
    get() = _trashVal?: _type.getTrashText()

  val schedules: List<ScheduleJsonData>
    get() = _schedules

  val excludes: List<ExcludeDayOfMonthJsonData>
    get() = _excludes?: listOf()
}