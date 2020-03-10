package com.example.mythrowtrash.domain

import com.fasterxml.jackson.annotation.JsonProperty

class AlarmConfig{
    @JsonProperty("enabled")
    var enabled: Boolean = false
    @JsonProperty("hourOfDay")
    var hourOfDay: Int = 7
    @JsonProperty("minute")
    var minute:Int = 0
    @JsonProperty("notifyEveryday")
    var notifyEveryday: Boolean = false
}