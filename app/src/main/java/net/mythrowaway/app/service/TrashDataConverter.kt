package net.mythrowaway.app.service

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import net.mythrowaway.app.domain.TrashData

class TrashDataListTypeReference: TypeReference<ArrayList<TrashData>>(){}
open class TrashDataConverter {
    protected fun jsonToTrashData(stringData: String): TrashData {
        val mapper = ObjectMapper()
        return mapper.readValue(stringData, TrashData::class.java)
    }

    protected fun jsonToTrashList(stringData: String): ArrayList<TrashData> {
        print(stringData)
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        return mapper.readValue(stringData,TrashDataListTypeReference())
    }

    protected fun trashDataToJson(trashData: TrashData): String {
        val mapper = ObjectMapper()
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        return mapper.writeValueAsString(trashData)
    }

    protected fun trashListToJson(trashList:ArrayList<TrashData>): String {
        val mapper = ObjectMapper()
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        return mapper.writeValueAsString(trashList)
    }
}