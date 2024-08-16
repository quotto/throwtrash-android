package net.mythrowaway.app.adapter.repository.dto

import com.fasterxml.jackson.annotation.JsonProperty

class ScheduleDTO {
  @JsonProperty("type")
  var type: String = ""

  @JsonProperty("value")
  var value: Any = Any()
}