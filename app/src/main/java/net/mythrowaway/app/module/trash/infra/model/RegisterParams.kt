package net.mythrowaway.app.module.trash.infra.model

import com.fasterxml.jackson.annotation.JsonProperty

class RegisterParams {
  @JsonProperty("description")
  var description: String = ""
  @JsonProperty("platform")
  var platform: String = ""
}

