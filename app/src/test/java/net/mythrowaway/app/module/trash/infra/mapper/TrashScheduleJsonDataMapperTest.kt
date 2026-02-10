package net.mythrowaway.app.module.trash.infra.mapper

import net.mythrowaway.app.module.trash.infra.data.ExcludeDayOfMonthJsonData
import net.mythrowaway.app.module.trash.infra.data.ScheduleJsonData
import net.mythrowaway.app.module.trash.infra.data.TrashJsonData
import net.mythrowaway.app.module.trash.infra.data.TrashScheduleJsonData
import net.mythrowaway.app.module.trash.infra.data.mapper.TrashScheduleJsonDataMapper
import net.mythrowaway.app.module.trash.entity.trash.TrashType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TrashScheduleJsonDataMapperTest {

  @Test
  fun fromJson_returns_empty_globalExcludes_when_array_format() {
    val json = "[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"もえるゴミ\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[]}]"
    val result = TrashScheduleJsonDataMapper.fromJson(json)
    assertEquals(1, result.trashData.size)
    assertEquals(0, result.globalExcludes.size)
  }

  @Test
  fun fromJson_returns_globalExcludes_when_object_format() {
    val json = "{\"trashData\":[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"もえるゴミ\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[]}],\"globalExcludes\":[{\"month\":1,\"date\":2}]}"
    val result = TrashScheduleJsonDataMapper.fromJson(json)
    assertEquals(1, result.trashData.size)
    assertEquals(1, result.globalExcludes.size)
    assertEquals(1, result.globalExcludes[0].month)
    assertEquals(2, result.globalExcludes[0].date)
  }

  @Test
  fun toJson_returns_object_when_globalExcludes_is_empty() {
    val schedule = TrashScheduleJsonData(
      _trashData = listOf(
        TrashJsonData(
          _id = "1",
          _type = TrashType.BURN,
          _trashVal = "もえるゴミ",
          _schedules = listOf(
            ScheduleJsonData(
              _type = "weekday",
              _value = "0"
            )
          ),
          _excludes = listOf()
        )
      ),
      _globalExcludes = listOf()
    )
    val result = TrashScheduleJsonDataMapper.toJson(schedule)
    assertEquals(
      "{\"trashData\":[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"もえるゴミ\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[]}],\"globalExcludes\":[]}",
      result
    )
  }

  @Test
  fun toJson_returns_object_when_globalExcludes_is_not_empty() {
    val schedule = TrashScheduleJsonData(
      _trashData = listOf(
        TrashJsonData(
          _id = "1",
          _type = TrashType.BURN,
          _trashVal = "もえるゴミ",
          _schedules = listOf(
            ScheduleJsonData(
              _type = "weekday",
              _value = "0"
            )
          ),
          _excludes = listOf()
        )
      ),
      _globalExcludes = listOf(
        ExcludeDayOfMonthJsonData(
          _month = 1,
          _date = 2
        )
      )
    )
    val result = TrashScheduleJsonDataMapper.toJson(schedule)
    assertEquals(
      "{\"trashData\":[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"もえるゴミ\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[]}],\"globalExcludes\":[{\"month\":1,\"date\":2}]}",
      result
    )
  }
}
