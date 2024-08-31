package net.mythrowaway.app.domain.trash.infra.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder("reviewed", "reviewedAt", "continuousUseDateCount", "lastLaunchedAt")
class ReviewJsonData(
  @JsonProperty("reviewed") val reviewed: Boolean,
  @JsonProperty("reviewedAt") val reviewedAt: Long,
  @JsonProperty("continuousUseDateCount") val continuousUseDateCount: Int,
  @JsonProperty("lastLaunchedAt") val lastLaunchedAt: Long
)