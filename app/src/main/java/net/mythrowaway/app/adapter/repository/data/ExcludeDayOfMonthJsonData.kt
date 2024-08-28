package net.mythrowaway.app.adapter.repository.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder("month", "date")
class ExcludeDayOfMonthJsonData(
  @JsonProperty("month")
  private val _month: Int,
  @JsonProperty("date")
  private val _date: Int
) {
  val month: Int
    get() = _month

  val date: Int
    get() = _date
}