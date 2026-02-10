package net.mythrowaway.app.module.trash.infra.data.mapper

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import net.mythrowaway.app.module.trash.infra.data.TrashJsonData
import net.mythrowaway.app.module.trash.infra.data.TrashScheduleJsonData
import net.mythrowaway.app.module.trash.infra.data.mapper.TrashJsonDataListMapper

class TrashScheduleJsonDataListReference : TypeReference<List<TrashJsonData>>(){}

class TrashScheduleJsonDataMapper {
  companion object {
    fun fromJson(stringData: String): TrashScheduleJsonData {
      val mapper = ObjectMapper()
      mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
      val trimmed = stringData.trim()
      if (trimmed.isEmpty()) {
        return TrashScheduleJsonData(
          _trashData = listOf(),
          _globalExcludes = listOf()
        )
      }
      if (trimmed.startsWith("[")) {
        val trashData = mapper.readValue(trimmed, TrashScheduleJsonDataListReference())
        return TrashScheduleJsonData(
          _trashData = trashData,
          _globalExcludes = listOf()
        )
      }
      return mapper.readValue(trimmed, TrashScheduleJsonData::class.java)
    }

    fun toJson(trashScheduleJsonData: TrashScheduleJsonData): String {
      val mapper = ObjectMapper()
      mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
      return mapper.writeValueAsString(trashScheduleJsonData)
    }
  }
}
