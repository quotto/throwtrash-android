package net.mythrowaway.app.util

import net.mythrowaway.app.domain.AccountLinkInfo
import net.mythrowaway.app.domain.RegisteredData
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.domain.TrashSchedule
import net.mythrowaway.app.usecase.IAPIAdapter

class TestApiAdapterImpl: IAPIAdapter {
    var currentUpdatedData: ArrayList<TrashData> = arrayListOf()
    var currentUpdatedTimestamp: Long = 0

    companion object {
        const val ACTIVATE_CODE = "55555"
        const val ACTIVATE_ID_001 = "id001"
        var ACTIVATE_DATA_001: ArrayList<TrashData> = arrayListOf(
            TrashData().apply {
                id = "trash_id001"
                type = "burn"
                schedules = arrayListOf(
                    TrashSchedule().apply {
                        type = "weekday"
                        value = "2"
                    },
                    TrashSchedule().apply {
                        type = "month"
                        value = "3"
                    }
                )
            },
            TrashData().apply {
                id = "trashid_002"
                type = "other"
                trash_val = "大きなごみ"
                schedules = arrayListOf(
                    TrashSchedule().apply {
                        type = "biweek"
                        value = "0-2"
                    }
                )
            }
        )
        var ACTIVATE_TIMESTAMP_001: Long = 1234567890

        var SYNC_ID_001 = "id001"
        var SYNC_DATA_001: ArrayList<TrashData> = ACTIVATE_DATA_001
        var SYNC_TIMESTAMP_001 = 1234567890123

        var UPDATE_ID_001 = "id001"
        var UPDATE_TIMESTAMP_001 = 1234567890123

        var PUBLISH_ID_001 = "id001"
        var PUBLISH_ACTIVATION_CODE = "55555"

        var REGISTER_ID_999 = "id999"
        var REGISTER_TIMESTAMP = 123456789012
    }

    override fun sync(id: String): Pair<ArrayList<TrashData>, Long>? {
        if (id === SYNC_ID_001) {
            return Pair(SYNC_DATA_001, SYNC_TIMESTAMP_001)
        }
        return null
    }

    override fun update(id: String, scheduleList: ArrayList<TrashData>): Long? {
        if (id == UPDATE_ID_001) {
            currentUpdatedData = scheduleList
            return UPDATE_TIMESTAMP_001
        }
        return null
    }

    override fun register(scheduleList: ArrayList<TrashData>): Pair<String, Long>? {
        if (scheduleList.size == 0) {
            return null
        }
        return Pair(REGISTER_ID_999, REGISTER_TIMESTAMP)
    }

    override fun publishActivationCode(id: String): String? {
        if (id == PUBLISH_ID_001) {
            return PUBLISH_ACTIVATION_CODE
        }
        return null
    }

    override fun activate(code: String): RegisteredData? {
        if (code == ACTIVATE_CODE) {
            return RegisteredData().apply {
                this.id = ACTIVATE_ID_001
                this.scheduleList = ACTIVATE_DATA_001
                this.timestamp = ACTIVATE_TIMESTAMP_001
            }
        }
        return null
    }

    override fun accountLink(id: String): AccountLinkInfo? {
        TODO("Not yet implemented")
    }
}
