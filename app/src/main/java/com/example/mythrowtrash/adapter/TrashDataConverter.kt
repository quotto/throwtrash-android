package com.example.mythrowtrash.adapter

import com.example.mythrowtrash.domain.TrashData
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

open class TrashDataConverter {
    protected fun jsonToTrashData(stringData: String): TrashData {
        val mapper = ObjectMapper()
        return mapper.readValue(stringData, TrashData::class.java)
    }

    protected fun jsonToTrashList(stringData: String): ArrayList<TrashData> {
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        return mapper.readValue(stringData)
    }

    protected fun trashDataToJson(trashData: TrashData): String {
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        return mapper.writeValueAsString(trashData)
    }

    protected fun trashListToJson(trashList:ArrayList<TrashData>): String {
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        return mapper.writeValueAsString(trashList)
    }
}