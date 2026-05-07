package net.mythrowaway.app.module.trash.infra.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder("enabled", "hourOfDay", "minute", "notifyEveryday", "notifyTomorrow")
class AlarmConfigJsonData(
    @JsonProperty("enabled")
    private val _enabled: Boolean = false,
    @JsonProperty("hourOfDay")
    private val _hourOfDay: Int = 7,
    @JsonProperty("minute")
    private val _minute:Int = 0,
    @JsonProperty("notifyEveryday")
    private val _notifyEveryday: Boolean = false,
    @JsonProperty("notifyTomorrow")
    private val _notifyTomorrow: Boolean = false
) {
    val enabled: Boolean
        get() = _enabled

    val hourOfDay: Int
        get() = _hourOfDay

    val minute: Int
        get() = _minute

    val notifyEveryday: Boolean
        get() = _notifyEveryday

    val notifyTomorrow: Boolean
        get() = _notifyTomorrow
}
