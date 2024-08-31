package net.mythrowaway.app.domain.trash.infra.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder("type", "value")
class ScheduleJsonData (
  @JsonProperty("type")
  private val _type: String,

  @JsonProperty("value")
  private val _value: Any
){
  val type: String
    get() = _type

  val value: Any
    get() = _value
}