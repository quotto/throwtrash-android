package com.example.mythrowtrash.adapter

import android.content.SharedPreferences
import com.example.mythrowtrash.domain.TrashData
import com.example.mythrowtrash.usecase.IPersistentRepository
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.util.*
import kotlin.collections.ArrayList

class PreferencePersistImpl(private val preference: SharedPreferences): IPersistentRepository {
    var separator = "&"
    companion object {
        const val KEY_TRASH_DATA = "KEY_TRASH_DATA"
        const val KEY_TRASH_ID = "KEY_TRASH_ID"
    }

    private fun jsonToTrashData(stringData: String): TrashData {
        val mapper = ObjectMapper()
        return mapper.readValue(stringData, TrashData::class.java)
    }

    private fun jsonToTrashList(stringData: String): ArrayList<TrashData> {
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        return mapper.readValue(stringData)
    }

    private fun trashDataToJson(trashData: TrashData): String {
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        return mapper.writeValueAsString(trashData)
    }

    override fun saveTrashData(trashData: TrashData) {
        trashData.id = Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis.toString()
        val currentData:String? = preference.getString(KEY_TRASH_DATA,null)
        var trashList:ArrayList<String>  = ArrayList<String>()
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
        val trashList:ArrayList<String>  = currentData?.run { ArrayList(split("&"))} ?: ArrayList<String>()
        trashList.forEachIndexed {index, data->
            if(Regex("\"id\":\"${trashData.id}\"").find(data) != null) {
                trashList[index] = trashDataToJson(trashData)
                return@forEachIndexed
            }
        }
        save(KEY_TRASH_DATA,trashList.joinToString(separator))
    }

    override fun deleteTrashData(id: String) {
        val currentData:String? = preference.getString(KEY_TRASH_DATA,null)
        val trashList:ArrayList<String>  = currentData?.run { ArrayList(split("&"))} ?: ArrayList()
        val deleteData = trashList.filter{trashData ->
            Regex("\"id\":\"${id}\"").find(trashData) != null
        }
        deleteData.forEach {
            trashList.remove(it)
        }
        save(KEY_TRASH_DATA,trashList.joinToString(separator))
    }

    private fun save(key:String, stringData: String) {
        println("[MyApp] save key:$key data:$stringData")
        preference.edit().apply {
            putString(key, stringData)
            commit()
        }
    }

    override fun getAllTrashSchedule(): ArrayList<TrashData> {
        val currentData:String? = preference.getString(KEY_TRASH_DATA, null)
        val allTrashSchedule:ArrayList<TrashData> = ArrayList()
        println("[MyApp] get data: $currentData")
        if(currentData != null && currentData.isNotEmpty()) {
            currentData.split("&").forEach {
                allTrashSchedule.add(
                    jsonToTrashData(
                        (it)
                    )
                )
            }
        }
        return allTrashSchedule
    }

    override fun getTrashData(id: String): TrashData? {
        val currentData:String? = preference.getString(KEY_TRASH_DATA, null)
        if(currentData != null && currentData.isNotEmpty()) {
            val allData = currentData.split("&")
            allData.forEach {data ->
                if(Regex("\"id\":\"$id\"").find(data) != null) {
                    return jsonToTrashData(data)
                }
            }
        }
        return null
    }
}