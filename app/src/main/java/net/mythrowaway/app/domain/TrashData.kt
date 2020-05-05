package net.mythrowaway.app.domain

import com.fasterxml.jackson.annotation.JsonProperty

class TrashSchedule {
    @JsonProperty("type")
    var type: String = ""

    @JsonProperty("value")
    var value: Any = Any()
}

class TrashData {
    var id: String = ""
    @JsonProperty("type")
    var type: String = ""
    @JsonProperty("trash_val")
    var trash_val: String? = null
    @JsonProperty("schedules")
    var schedules: ArrayList<TrashSchedule> = ArrayList()
}

class RegisteredData {
    var id: String = ""
    var scheduleList: ArrayList<TrashData> = arrayListOf()
    var timestamp: Long = 0
}