package net.mythrowaway.app.adapter.repository.dto

import com.fasterxml.jackson.annotation.JsonProperty

class ExcludeDayOfMonthDTO {
  @JsonProperty("month")
  var month: Int = 1
  @JsonProperty("date")
  var date: Int = 1
}