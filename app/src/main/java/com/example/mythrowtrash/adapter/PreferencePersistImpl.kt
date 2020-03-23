package com.example.mythrowtrash.adapter

import android.content.SharedPreferences
import com.example.mythrowtrash.domain.TrashData
import com.example.mythrowtrash.usecase.IPersistentRepository
import java.util.*
import kotlin.collections.ArrayList

class PreferencePersistImpl(private val preference: SharedPreferences): IPersistentRepository,TrashDataConverter() {
    var separator = "&"
    companion object {
        const val KEY_TRASH_DATA = "KEY_TRASH_DATA"
    }

    override fun saveTrashData(trashData: TrashData) {
        trashData.id = Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis.toString()
        val currentData:String? = preference.getString(KEY_TRASH_DATA,null)
        var trashList:ArrayList<String>  = ArrayList()
        if(currentData != null && currentData.isNotEmpty()) {
            trashList = currentData.run { ArrayList(split("&"))}
        }
        trashList.add(
            trashDataToJson(
                trashData
            )
        )
        println("[MyApp] save trash data $trashList")
        save(KEY_TRASH_DATA,trashList.joinToString(separator))
    }

    override fun updateTrashData(trashData: TrashData) {
        val currentData:String? = preference.getString(KEY_TRASH_DATA,null)
        val trashList:ArrayList<TrashData> = jsonToTrashList(currentData?: "")
        trashList.forEachIndexed {index, data->
            if(data.id == trashData.id) {
                data.id = trashData.id
                data.schedules = trashData.schedules
                data.type = trashData.type
                data.trash_val = trashData.trash_val
                return@forEachIndexed
            }
        }
        save(KEY_TRASH_DATA,trashListToJson(trashList))
    }

    override fun deleteTrashData(id: String) {
        val currentData:String? = preference.getString(KEY_TRASH_DATA,null)
        val trashList:ArrayList<TrashData> = jsonToTrashList(currentData?: "")
        val deleteData = trashList.filter{trashData ->
            trashData.id == id
        }
        deleteData.forEach {
            trashList.remove(it)
        }
        save(KEY_TRASH_DATA,trashListToJson(trashList))
    }

    private fun save(key:String, stringData: String) {
        println("[MyApp] save key:$key data:$stringData")
        preference.edit().apply {
            putString(key, stringData)
            commit()
        }
    }

    override fun getAllTrashSchedule(): ArrayList<TrashData> {
        val currentData:String = preference.getString(KEY_TRASH_DATA, "[]") ?: "[]"
//        val allTrashSchedule:ArrayList<TrashData> = ArrayList()

        println("[MyApp] get data: $currentData")
//        if(currentData != null && currentData.isNotEmpty()) {
//            currentData.split("&").forEach {
//                allTrashSchedule.add(
//                    jsonToTrashData(
//                        (it)
//                    )
//                )
//            }
//        }
//        return allTrashSchedule
        return jsonToTrashList(currentData)
    }

    override fun getTrashData(id: String): TrashData? {
        val allTrashData:ArrayList<TrashData> = getAllTrashSchedule()
        allTrashData.forEach { trashData ->
            if(trashData.id == id) {
                return trashData
            }
        }
//        if(currentData != null && currentData.isNotEmpty()) {
//            val allData = currentData.split("&")
//            allData.forEach {data ->
//                if(Regex("\"id\":\"$id\"").find(data) != null) {
//                    return jsonToTrashData(data)
//                }
//            }
//        }
        return null
    }

    override fun importScheduleList(scheduleList: ArrayList<TrashData>) {
        save(KEY_TRASH_DATA, trashListToJson(scheduleList))
    }
}