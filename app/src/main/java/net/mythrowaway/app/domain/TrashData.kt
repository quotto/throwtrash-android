package net.mythrowaway.app.domain

import com.fasterxml.jackson.annotation.JsonProperty

class ExcludeDate {
    @JsonProperty("month")
    var month: Int = 1
    @JsonProperty("date")
    var date: Int = 1
}
class TrashSchedule {
    @JsonProperty("type")
    var type: String = ""

    @JsonProperty("value")
    var value: Any = Any()
}

class TrashData {
    @JsonProperty("id")
    var id: String = ""
    @JsonProperty("type")
    var type: TrashType = TrashType.BURN
    @JsonProperty("trash_val")
    var trash_val: String? = null
    @JsonProperty("schedules")
    var schedules: ArrayList<TrashSchedule> = ArrayList()
    @JsonProperty("excludes")
    var excludes: List<ExcludeDate> = listOf()

    fun equals(comparedTrashData: TrashData): Boolean {
        return if(this.type != TrashType.OTHER) {
            this.type == comparedTrashData.type
        } else {
            this.type == comparedTrashData.type  && this.trash_val == comparedTrashData.trash_val
        }
    }
}

class RegisteredData {
    var id: String = ""
    var scheduleList: ArrayList<TrashData> = arrayListOf()
    var timestamp: Long = 0
}

class LatestTrashData {
    var scheduleList: ArrayList<TrashData> = arrayListOf()
    var timestamp: Long = 0
}
