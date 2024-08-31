package net.mythrowaway.app.domain.trash.infra.data.mapper

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import net.mythrowaway.app.domain.trash.infra.data.TrashJsonData


class TrashJsonDataListReference : TypeReference<List<TrashJsonData>>(){}
class TrashJsonDataListMapper {
  companion object {
    fun fromJson(stringData: String): List<TrashJsonData> {
      val mapper = ObjectMapper()
      mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
      return mapper.readValue(stringData, TrashJsonDataListReference())
    }

    fun toJson(trashJsonDataList: List<TrashJsonData>): String {
      val mapper = ObjectMapper()
      mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
      mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
      return mapper.writeValueAsString(trashJsonDataList)
    }
  }
}