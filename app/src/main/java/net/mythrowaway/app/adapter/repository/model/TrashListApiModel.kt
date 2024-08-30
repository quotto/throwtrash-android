package net.mythrowaway.app.adapter.repository.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import net.mythrowaway.app.domain.TrashType

data class TrashListApiModel(
  @JsonProperty("description") private val _description: List<TrashApiModel>
) {
  val description: List<TrashApiModel>
    get() = _description
}

@JsonPropertyOrder("id", "type", "trash_val", "schedules", "excludes")
data class TrashApiModel(
  @JsonProperty("id") private val _id: String,
  @JsonProperty("type") private val _type: TrashType,
  @JsonProperty("trash_val") private val _trashVal: String?,
  @JsonProperty("schedules") private val  _schedules: List<ScheduleApiModel>,
  @JsonProperty("excludes") private val _excludes: List<ExcludeDayOfMonthApiModel>?
) {
  val id: String
    get() = _id
  val type: TrashType
    get() = _type
  val trashVal: String
    // 変数名とJSONのキー名が異なる場合、コンストラクタ内で@JsonPropertyを指定する形式だと
    // Object -> Jsonへの変換時にアノーテーションの指定が無視されるため、getterに指定している
    @JsonProperty("trash_val")
    get() = _trashVal?: _type.getTrashText()
  val schedules: List<ScheduleApiModel>
    get() = _schedules
  val excludes: List<ExcludeDayOfMonthApiModel>
    get() = _excludes?: listOf()
}


data class ScheduleApiModel(
  @JsonProperty("type")
  private val  _type: String,
  @JsonProperty("value")
  private val  _value: Any
) {
  val type: String
    get() = _type
  val value: Any
    get() = _value

}

data class ExcludeDayOfMonthApiModel(
  @JsonProperty("month")
  private val _month: Int,
  @JsonProperty("date")
  private val  _date: Int
) {
  val month: Int
    get() = _month
  val date: Int
    get() = _date
}

