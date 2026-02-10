package net.mythrowaway.app.module.trash.infra.model

import com.fasterxml.jackson.annotation.JsonProperty

class RegisterParams {
  @JsonProperty("description")
  var description: String = ""
  @JsonProperty("globalExcludes")
  var globalExcludes: List<ExcludeDayOfMonthApiModel> = listOf()
  @JsonProperty("platform")
  var platform: String = ""
}
