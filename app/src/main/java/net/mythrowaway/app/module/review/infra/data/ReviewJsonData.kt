package net.mythrowaway.app.module.review.infra.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder("reviewed", "reviewedAt", "continuousUseDateCount", "lastLaunchedAt")
class ReviewJsonData(
  @JsonProperty("reviewed") val reviewed: Boolean = false,
  @JsonProperty("reviewedAt") val reviewedAt: Long = 0L,
  @JsonProperty("continuousUseDateCount") val continuousUseDateCount: Int = 0,
  @JsonProperty("lastLaunchedAt") val lastLaunchedAt: Long = 0L
)