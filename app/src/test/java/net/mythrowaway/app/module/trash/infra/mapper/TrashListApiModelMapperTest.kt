package net.mythrowaway.app.module.trash.infra.mapper

import net.mythrowaway.app.module.trash.entity.trash.ExcludeDayOfMonth
import net.mythrowaway.app.module.trash.entity.trash.ExcludeDayOfMonthList
import net.mythrowaway.app.module.trash.entity.trash.IntervalWeeklySchedule
import net.mythrowaway.app.module.trash.entity.trash.MonthlySchedule
import net.mythrowaway.app.module.trash.entity.trash.OrdinalWeeklySchedule
import net.mythrowaway.app.module.trash.entity.trash.Trash
import net.mythrowaway.app.module.trash.entity.trash.TrashList
import net.mythrowaway.app.module.trash.entity.trash.TrashType
import net.mythrowaway.app.module.trash.entity.trash.WeeklySchedule
import net.mythrowaway.app.module.trash.infra.model.ExcludeDayOfMonthApiModel
import net.mythrowaway.app.module.trash.infra.model.ScheduleApiModel
import net.mythrowaway.app.module.trash.infra.model.TrashApiModel
import net.mythrowaway.app.module.trash.infra.model.TrashListApiModel
import net.mythrowaway.app.module.trash.infra.model.TrashListApiModelMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate

class TrashListApiModelMapperTest {

  @Nested
  inner class FromJson {
    @Test
    fun isEmpty_when_json_properties_are_empty() {
      val result = TrashListApiModelMapper.fromJson("[]")
      Assertions.assertEquals(0, result.trashData.size)
    }

    @Test
    fun isValid_when_json_has_validWeeklySchedule() {
      val result = TrashListApiModelMapper.fromJson(
        "[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[]}]"
      )
      Assertions.assertEquals(1, result.trashData.size)
      Assertions.assertEquals("1", result.trashData[0].id)
      Assertions.assertEquals(TrashType.BURN, result.trashData[0].type)
      Assertions.assertEquals("", result.trashData[0].trashVal)
      Assertions.assertEquals("weekday", result.trashData[0].schedules[0].type)
      Assertions.assertEquals("0", result.trashData[0].schedules[0].value)
      Assertions.assertEquals(0, result.trashData[0].excludes.size)
    }

    @Test
    fun isValid_when_json_has_validMonthlySchedule() {
      val result = TrashListApiModelMapper.fromJson(
        "[{\"id\":\"1\",\"type\":\"unburn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"month\",\"value\":\"10\"}],\"excludes\":[]}]"
      )
      Assertions.assertEquals(1, result.trashData.size)
      Assertions.assertEquals(TrashType.UNBURN, result.trashData[0].type)
      Assertions.assertEquals("", result.trashData[0].trashVal)
      Assertions.assertEquals("1", result.trashData[0].id)
      Assertions.assertEquals("month", result.trashData[0].schedules[0].type)
      Assertions.assertEquals("10", result.trashData[0].schedules[0].value)
      Assertions.assertEquals(0, result.trashData[0].excludes.size)
    }

    @Test
    fun isValid_when_json_has_validOrdinalSchedule() {
      val result = TrashListApiModelMapper.fromJson(
        "[{\"id\":\"1\",\"type\":\"can\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"biweek\",\"value\":\"1-4\"}],\"excludes\":[]}]"
      )
      Assertions.assertEquals(1, result.trashData.size)
      Assertions.assertEquals(TrashType.CAN, result.trashData[0].type)
      Assertions.assertEquals("", result.trashData[0].trashVal)
      Assertions.assertEquals("1", result.trashData[0].id)
      Assertions.assertEquals("biweek", result.trashData[0].schedules[0].type)
      Assertions.assertEquals("1-4", result.trashData[0].schedules[0].value)
      Assertions.assertEquals(0, result.trashData[0].excludes.size)
    }

    @Test
    fun isValid_when_json_has_validIntervalWeeklySchedule() {
      val result = TrashListApiModelMapper.fromJson(
        "[{\"id\":\"1\",\"type\":\"plastic\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"evweek\",\"value\": {\"weekday\":\"6\", \"interval\": 2, \"start\": \"2022-01-01\"}}],\"excludes\":[]}]"
      )
      Assertions.assertEquals(1, result.trashData.size)
      Assertions.assertEquals(TrashType.PLASTIC, result.trashData[0].type)
      Assertions.assertEquals("", result.trashData[0].trashVal)
      Assertions.assertEquals("1", result.trashData[0].id)
      Assertions.assertEquals("evweek", result.trashData[0].schedules[0].type)
      Assertions.assertEquals(
        "6",
        (result.trashData[0].schedules[0].value as Map<*, *>)["weekday"]
      )
      Assertions.assertEquals(
        2,
        (result.trashData[0].schedules[0].value as Map<*, *>)["interval"]
      )
      Assertions.assertEquals(
        "2022-01-01",
        (result.trashData[0].schedules[0].value as Map<*, *>)["start"]
      )
      Assertions.assertEquals(0, result.trashData[0].excludes.size)
    }

    @Test
    fun interval_of_model_is_2_when_json_has_not_interval_of_intervalWeeklySchedule() {
      val result = TrashListApiModelMapper.fromJson(
        "[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"evweek\",\"value\": {\"weekday\":\"6\", \"start\": \"2022-01-01\"}}],\"excludes\":[]}]"
      )
      Assertions.assertEquals(1, result.trashData.size)
      Assertions.assertEquals(TrashType.BURN, result.trashData[0].type)
      Assertions.assertEquals("", result.trashData[0].trashVal)
      Assertions.assertEquals("1", result.trashData[0].id)
      Assertions.assertEquals("evweek", result.trashData[0].schedules[0].type)
      Assertions.assertEquals(
        "6",
        (result.trashData[0].schedules[0].value as Map<*, *>)["weekday"]
      )
      Assertions.assertEquals(
        "2022-01-01",
        (result.trashData[0].schedules[0].value as Map<*, *>)["start"]
      )
      Assertions.assertEquals(
        2,
        (result.trashData[0].schedules[0].value as Map<*, *>)["interval"]
      )
      Assertions.assertEquals(0, result.trashData[0].excludes.size)
    }

    @Test
    fun isValid_when_json_has_trashType_of_BOTTLE() {
      val result = TrashListApiModelMapper.fromJson(
        "[{\"id\":\"1\",\"type\":\"bin\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[]}]"
      )
      Assertions.assertEquals(1, result.trashData.size)
      Assertions.assertEquals(TrashType.BOTTLE, result.trashData[0].type)
    }

    @Test
    fun isValid_when_json_has_trashType_of_PETBOTTLE() {
      val result = TrashListApiModelMapper.fromJson(
        "[{\"id\":\"1\",\"type\":\"petbottle\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[]}]"
      )
      Assertions.assertEquals(1, result.trashData.size)
      Assertions.assertEquals(TrashType.PETBOTTLE, result.trashData[0].type)
    }

    @Test
    fun isValid_when_json_has_trashType_of_RESOURCE() {
      val result = TrashListApiModelMapper.fromJson(
        "[{\"id\":\"1\",\"type\":\"resource\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[]}]"
      )
      Assertions.assertEquals(1, result.trashData.size)
      Assertions.assertEquals(TrashType.RESOURCE, result.trashData[0].type)
    }

    @Test
    fun isValid_when_json_has_trashType_of_COARSE() {
      val result = TrashListApiModelMapper.fromJson(
        "[{\"id\":\"1\",\"type\":\"coarse\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[]}]"
      )
      Assertions.assertEquals(1, result.trashData.size)
      Assertions.assertEquals(TrashType.COARSE, result.trashData[0].type)
    }

    @Test
    fun isValid_when_json_has_trashType_of_PAPER() {
      val result = TrashListApiModelMapper.fromJson(
        "[{\"id\":\"1\",\"type\":\"paper\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[]}]"
      )
      Assertions.assertEquals(1, result.trashData.size)
      Assertions.assertEquals(TrashType.PAPER, result.trashData[0].type)
    }

    @Test
    fun isValid_when_json_has_trashType_of_OTHER() {
      val result = TrashListApiModelMapper.fromJson(
        "[{\"id\":\"1\",\"type\":\"other\",\"trash_val\":\"その他\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[]}]"
      )
      Assertions.assertEquals(1, result.trashData.size)
      Assertions.assertEquals(TrashType.OTHER, result.trashData[0].type)
      Assertions.assertEquals("その他", result.trashData[0].trashVal)
    }

    @Test
    fun isValid_when_json_has_multiple_schedules() {
      val result = TrashListApiModelMapper.fromJson(
        "[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"},{\"type\":\"month\",\"value\":\"10\"},{\"type\":\"biweek\",\"value\":\"1-4\"},{\"type\":\"evweek\",\"value\": {\"weekday\":\"0\", \"interval\": 2, \"start\": \"2022-01-01\"}}],\"excludes\":[]}]"
      )
      Assertions.assertEquals(1, result.trashData.size)
      Assertions.assertEquals(TrashType.BURN, result.trashData[0].type)
      Assertions.assertEquals("", result.trashData[0].trashVal)
      Assertions.assertEquals("1", result.trashData[0].id)
      Assertions.assertEquals("weekday", result.trashData[0].schedules[0].type)
      Assertions.assertEquals("0", result.trashData[0].schedules[0].value)
      Assertions.assertEquals("month", result.trashData[0].schedules[1].type)
      Assertions.assertEquals("10", result.trashData[0].schedules[1].value)
      Assertions.assertEquals("biweek", result.trashData[0].schedules[2].type)
      Assertions.assertEquals("1-4", result.trashData[0].schedules[2].value)
      Assertions.assertEquals("evweek", result.trashData[0].schedules[3].type)
      Assertions.assertEquals(
        "0",
        (result.trashData[0].schedules[3].value as Map<*, *>)["weekday"]
      )
      Assertions.assertEquals(
        2,
        (result.trashData[0].schedules[3].value as Map<*, *>)["interval"]
      )
      Assertions.assertEquals(
        "2022-01-01",
        (result.trashData[0].schedules[3].value as Map<*, *>)["start"]
      )
      Assertions.assertEquals(0, result.trashData[0].excludes.size)
    }

    @Test
    fun isValid_when_json_has_excludeDayOfMonth() {
      val result = TrashListApiModelMapper.fromJson(
        "[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[{\"month\":1,\"date\":1}]}]"
      )
      Assertions.assertEquals(1, result.trashData.size)
      Assertions.assertEquals(1, result.trashData[0].excludes.size)
      Assertions.assertEquals(1, result.trashData[0].excludes[0].month)
      Assertions.assertEquals(1, result.trashData[0].excludes[0].date)
    }

    @Test
    fun isValid_when_json_has_multiple_excludeDayOfMonth() {
      val result = TrashListApiModelMapper.fromJson(
        "[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[{\"month\":1,\"date\":1},{\"month\":2,\"date\":2}]}]"
      )
      Assertions.assertEquals(1, result.trashData.size)
      Assertions.assertEquals(2, result.trashData[0].excludes.size)
      Assertions.assertEquals(1, result.trashData[0].excludes[0].month)
      Assertions.assertEquals(1, result.trashData[0].excludes[0].date)
      Assertions.assertEquals(2, result.trashData[0].excludes[1].month)
      Assertions.assertEquals(2, result.trashData[0].excludes[1].date)
    }

    @Test
    fun isValid_when_json_has_multiple_trash() {
      val result = TrashListApiModelMapper.fromJson(
        "[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[]},{\"id\":\"2\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[]}]"
      )
      Assertions.assertEquals(2, result.trashData.size)
      Assertions.assertEquals("1", result.trashData[0].id)
      Assertions.assertEquals("2", result.trashData[1].id)
    }

    @Test
    fun isValid_when_json_has_not_interval_of_intervalWeeklySchedule() {
      val result = TrashListApiModelMapper.fromJson(
        "[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"evweek\",\"value\": {\"weekday\":\"6\", \"start\": \"2022-01-01\"}}],\"excludes\":[]}]"
      )
      Assertions.assertEquals(1, result.trashData.size)
      Assertions.assertEquals(TrashType.BURN, result.trashData[0].type)
      Assertions.assertEquals("", result.trashData[0].trashVal)
      Assertions.assertEquals("1", result.trashData[0].id)
      Assertions.assertEquals("evweek", result.trashData[0].schedules[0].type)
      Assertions.assertEquals(
        "6",
        (result.trashData[0].schedules[0].value as Map<*, *>)["weekday"]
      )
      Assertions.assertEquals(
        "2022-01-01",
        (result.trashData[0].schedules[0].value as Map<*, *>)["start"]
      )
      Assertions.assertEquals(0, result.trashData[0].excludes.size)
    }

    @Test
    fun isValid_when_json_has_not_excludes() {
      val result = TrashListApiModelMapper.fromJson(
        "[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}]}]"
      )
      Assertions.assertEquals(1, result.trashData.size)
      Assertions.assertEquals(TrashType.BURN, result.trashData[0].type)
      Assertions.assertEquals("", result.trashData[0].trashVal)
      Assertions.assertEquals("1", result.trashData[0].id)
      Assertions.assertEquals("weekday", result.trashData[0].schedules[0].type)
      Assertions.assertEquals("0", result.trashData[0].schedules[0].value)
      Assertions.assertEquals(0, result.trashData[0].excludes.size)
    }

    @Test
    fun isValid_when_json_has_not_padding_start_value_of_intervalWeeklySchedule() {
      val result = TrashListApiModelMapper.fromJson(
        "[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"evweek\",\"value\": {\"weekday\":\"6\", \"interval\": 2, \"start\": \"2022-1-1\"}}],\"excludes\":[]}]"
      )
      Assertions.assertEquals(1, result.trashData.size)
      Assertions.assertEquals(TrashType.BURN, result.trashData[0].type)
      Assertions.assertEquals("", result.trashData[0].trashVal)
      Assertions.assertEquals("1", result.trashData[0].id)
      Assertions.assertEquals("evweek", result.trashData[0].schedules[0].type)
      Assertions.assertEquals(
        "6",
        (result.trashData[0].schedules[0].value as Map<*, *>)["weekday"]
      )
      Assertions.assertEquals(
        2,
        (result.trashData[0].schedules[0].value as Map<*, *>)["interval"]
      )
      Assertions.assertEquals(
        "2022-01-01",
        (result.trashData[0].schedules[0].value as Map<*, *>)["start"]
      )
      Assertions.assertEquals(0, result.trashData[0].excludes.size)
    }

    @Test
    fun trash_val_has_value_when_json_has_null_of_trash_val() {
      val result = TrashListApiModelMapper.fromJson(
        "[{\"id\":\"1\",\"type\":\"burn\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}]}]"
      )
      Assertions.assertEquals(1, result.trashData.size)
      Assertions.assertEquals("もえるゴミ", result.trashData[0].trashVal)
    }

    @Test
    fun trash_val_is_empty_when_json_has_empty_string_of_trash_val() {
      val result = TrashListApiModelMapper.fromJson(
        "[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}]}]"
      )
      Assertions.assertEquals(1, result.trashData.size)
      Assertions.assertEquals("", result.trashData[0].trashVal)
    }

    @Test
    fun excludes_is_empty_when_json_has_null_of_excludes() {
      val result = TrashListApiModelMapper.fromJson(
        "[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}]}]"
      )
      Assertions.assertEquals(1, result.trashData.size)
      Assertions.assertEquals(0, result.trashData[0].excludes.size)
    }

    @Test
    fun globalExcludes_is_set_when_json_has_globalExcludes() {
      val result = TrashListApiModelMapper.fromJson(
        "{\"trashData\":[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[]}],\"globalExcludes\":[{\"month\":1,\"date\":1}]}"
      )
      Assertions.assertEquals(1, result.trashData.size)
      Assertions.assertEquals(1, result.globalExcludes.size)
      Assertions.assertEquals(1, result.globalExcludes[0].month)
      Assertions.assertEquals(1, result.globalExcludes[0].date)
    }

    @Test
    fun globalExcludes_is_set_when_json_is_split() {
      val trashDataJson =
        "[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[]}]"
      val globalExcludesJson = "[{\"month\":1,\"date\":2}]"
      val result = TrashListApiModelMapper.fromTrashDataJsonAndGlobalExcludes(
        trashDataJson,
        globalExcludesJson
      )
      Assertions.assertEquals(1, result.trashData.size)
      Assertions.assertEquals(1, result.globalExcludes.size)
      Assertions.assertEquals(1, result.globalExcludes[0].month)
      Assertions.assertEquals(2, result.globalExcludes[0].date)
    }
  }

  @Nested
  inner class ToJson {
    @Test
    fun isEmptyString_when_descriptionIsEmpty() {
      val result = TrashListApiModelMapper.toJson(listOf())
      Assertions.assertEquals("[]", result)
    }

    @Test
    fun isValid_when_model_has_weeklySchedule() {
      val result = TrashListApiModelMapper.toJson(
          listOf(
            TrashApiModel(
              _id = "1",
              _type = TrashType.BURN,
              _trashVal = "",
              _schedules = listOf(
                ScheduleApiModel(
                  _type = "weekday",
                  _value = "0"
                )
              ),
              _excludes = listOf()
            )
          )
      )
      Assertions.assertEquals(
        "[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[]}]",
        result
      )
    }

    @Test
    fun isValid_when_model_has_monthlySchedule() {
      val result = TrashListApiModelMapper.toJson(
          listOf(
            TrashApiModel(
              _id = "1",
              _type = TrashType.BURN,
              _trashVal = "",
              _schedules = listOf(
                ScheduleApiModel(
                  _type = "month",
                  _value = "10"
                )
              ),
              _excludes = listOf()
            )
        )
      )
      Assertions.assertEquals(
        "[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"month\",\"value\":\"10\"}],\"excludes\":[]}]",
        result
      )
    }

    @Test
    fun isValid_when_model_has_ordinalSchedule() {
      val result = TrashListApiModelMapper.toJson(
          listOf(
            TrashApiModel(
              _id = "1",
              _type = TrashType.BURN,
              _trashVal = "",
              _schedules = listOf(
                ScheduleApiModel(
                  _type = "biweek",
                  _value = "1-4"
                )
              ),
              _excludes = listOf()
            )
          )
      )
      Assertions.assertEquals(
        "[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"biweek\",\"value\":\"1-4\"}],\"excludes\":[]}]",
        result
      )
    }

    @Test
    fun isValid_when_model_has_intervalWeeklySchedule() {
      val result = TrashListApiModelMapper.toJson(
          listOf(
            TrashApiModel(
              _id = "1",
              _type = TrashType.BURN,
              _trashVal = "",
              _schedules = listOf(
                ScheduleApiModel(
                  _type = "evweek",
                  _value = hashMapOf(
                    "weekday" to "6",
                    "interval" to 2,
                    "start" to "2022-01-01"
                  )
                )
              ),
              _excludes = listOf()
            )
          )
      )
      Assertions.assertEquals(
        "[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"evweek\",\"value\":{\"weekday\":\"6\",\"start\":\"2022-01-01\",\"interval\":2}}],\"excludes\":[]}]",
        result
      )
    }

    @Test
    fun isValid_when_model_has_multiple_schedules() {
      val result = TrashListApiModelMapper.toJson(
          listOf(
            TrashApiModel(
              _id = "1",
              _type = TrashType.BURN,
              _trashVal = "",
              _schedules = listOf(
                ScheduleApiModel(
                  _type = "weekday",
                  _value = "0"
                ),
                ScheduleApiModel(
                  _type = "month",
                  _value = "10"
                ),
                ScheduleApiModel(
                  _type = "biweek",
                  _value = "1-4"
                ),
                ScheduleApiModel(
                  _type = "evweek",
                  _value = hashMapOf(
                    "weekday" to "0",
                    "interval" to 2,
                    "start" to "2022-01-01"
                  )
                )
              ),
              _excludes = listOf()
            )
        )
      )
      Assertions.assertEquals(
        "[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"},{\"type\":\"month\",\"value\":\"10\"},{\"type\":\"biweek\",\"value\":\"1-4\"},{\"type\":\"evweek\",\"value\":{\"weekday\":\"0\",\"start\":\"2022-01-01\",\"interval\":2}}],\"excludes\":[]}]",
        result
      )
    }

    @Test
    fun isValid_when_model_has_excludeDayOfMonth() {
      val result = TrashListApiModelMapper.toJson(
          listOf(
            TrashApiModel(
              _id = "1",
              _type = TrashType.BURN,
              _trashVal = "",
              _schedules = listOf(
                ScheduleApiModel(
                  _type = "weekday",
                  _value = "0"
                )
              ),
              _excludes = listOf(
                ExcludeDayOfMonthApiModel(
                  _month = 1,
                  _date = 1
                )
              )
            )
          )
      )
      Assertions.assertEquals(
        "[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[{\"month\":1,\"date\":1}]}]",
        result
      )
    }

    @Test
    fun isValid_when_model_has_multiple_excludeDayOfMonth() {
      val result = TrashListApiModelMapper.toJson(
          listOf(
            TrashApiModel(
              _id = "1",
              _type = TrashType.BURN,
              _trashVal = "",
              _schedules = listOf(
                ScheduleApiModel(
                  _type = "weekday",
                  _value = "0"
                )
              ),
              _excludes = listOf(
                ExcludeDayOfMonthApiModel(
                  _month = 1,
                  _date = 1
                ),
                ExcludeDayOfMonthApiModel(
                  _month = 2,
                  _date = 2
                )
              )
            )
        )
      )
      Assertions.assertEquals(
        "[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[{\"month\":1,\"date\":1},{\"month\":2,\"date\":2}]}]",
        result
      )
    }

    @Test
    fun isValid_when_model_has_multiple_trash() {
      val result = TrashListApiModelMapper.toJson(
          listOf(
            TrashApiModel(
              _id = "1",
              _type = TrashType.BURN,
              _trashVal = "",
              _schedules = listOf(
                ScheduleApiModel(
                  _type = "weekday",
                  _value = "0"
                )
              ),
              _excludes = listOf()
            ),
            TrashApiModel(
              _id = "2",
              _type = TrashType.BURN,
              _trashVal = "",
              _schedules = listOf(
                ScheduleApiModel(
                  _type = "weekday",
                  _value = "0"
                )
              ),
              _excludes = listOf()
            )
          )
      )
      Assertions.assertEquals(
        "[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[]},{\"id\":\"2\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[]}]",
        result
      )
    }

    @Test
    fun isValid_when_model_has_trashType_of_BOTTLE() {
      val result = TrashListApiModelMapper.toJson(
          listOf(
            TrashApiModel(
              _id = "1",
              _type = TrashType.BOTTLE,
              _trashVal = "",
              _schedules = listOf(
                ScheduleApiModel(
                  _type = "weekday",
                  _value = "0"
                )
              ),
              _excludes = listOf()
            )
          )
      )
      Assertions.assertEquals(
        "[{\"id\":\"1\",\"type\":\"bin\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[]}]",
        result
      )
    }

    @Test
    fun isValid_when_model_has_trashType_of_PETBOTTLE() {
      val result = TrashListApiModelMapper.toJson(
          listOf(
            TrashApiModel(
              _id = "1",
              _type = TrashType.PETBOTTLE,
              _trashVal = "",
              _schedules = listOf(
                ScheduleApiModel(
                  _type = "weekday",
                  _value = "0"
                )
              ),
              _excludes = listOf()
            )
          )
      )
      Assertions.assertEquals(
        "[{\"id\":\"1\",\"type\":\"petbottle\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[]}]",
        result
      )
    }

    @Test
    fun isValid_when_model_has_trashType_of_RESOURCE() {
      val result = TrashListApiModelMapper.toJson(
          listOf(
            TrashApiModel(
              _id = "1",
              _type = TrashType.RESOURCE,
              _trashVal = "",
              _schedules = listOf(
                ScheduleApiModel(
                  _type = "weekday",
                  _value = "0"
                )
              ),
              _excludes = listOf()
            )
          )
      )
      Assertions.assertEquals(
        "[{\"id\":\"1\",\"type\":\"resource\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[]}]",
        result
      )
    }

    @Test
    fun isValid_when_model_has_trashType_of_COARSE() {
      val result = TrashListApiModelMapper.toJson(
          listOf(
            TrashApiModel(
              _id = "1",
              _type = TrashType.COARSE,
              _trashVal = "",
              _schedules = listOf(
                ScheduleApiModel(
                  _type = "weekday",
                  _value = "0"
                )
              ),
              _excludes = listOf()
            )
          )
      )
      Assertions.assertEquals(
        "[{\"id\":\"1\",\"type\":\"coarse\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[]}]",
        result
      )
    }

    @Test
    fun isValid_when_model_has_trashType_of_PAPER() {
      val result = TrashListApiModelMapper.toJson(
          listOf(
            TrashApiModel(
              _id = "1",
              _type = TrashType.PAPER,
              _trashVal = "",
              _schedules = listOf(
                ScheduleApiModel(
                  _type = "weekday",
                  _value = "0"
                )
              ),
              _excludes = listOf()
            )
          )
      )
      Assertions.assertEquals(
        "[{\"id\":\"1\",\"type\":\"paper\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[]}]",
        result
      )
    }

    @Test
    fun isValid_when_model_has_trashType_of_OTHER() {
      val result = TrashListApiModelMapper.toJson(
          listOf(
            TrashApiModel(
              _id = "1",
              _type = TrashType.OTHER,
              _trashVal = "その他",
              _schedules = listOf(
                ScheduleApiModel(
                  _type = "weekday",
                  _value = "0"
                )
              ),
              _excludes = listOf()
            )
          )
      )
      Assertions.assertEquals(
        "[{\"id\":\"1\",\"type\":\"other\",\"trash_val\":\"その他\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[]}]",
        result
      )
    }

    @Test
    fun trash_val_has_value_when_model_has_null_of_trashVal() {
      val result = TrashListApiModelMapper.toJson(
          listOf(
            TrashApiModel(
              _id = "1",
              _type = TrashType.BURN,
              _trashVal = null,
              _schedules = listOf(
                ScheduleApiModel(
                  _type = "weekday",
                  _value = "0"
                )
              ),
              _excludes = listOf()
            )
          )
      )
      Assertions.assertEquals(
        "[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"もえるゴミ\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[]}]",
        result
      )
    }
    @Test
    fun trash_val_is_empty_when_model_has_empty_string_of_trashVal() {
      val result = TrashListApiModelMapper.toJson(
          listOf(
            TrashApiModel(
              _id = "1",
              _type = TrashType.BURN,
              _trashVal = "",
              _schedules = listOf(
                ScheduleApiModel(
                  _type = "weekday",
                  _value = "0"
                )
              ),
              _excludes = listOf()
            )
          )
      )
      Assertions.assertEquals(
        "[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[]}]",
        result
      )
    }
    @Test
    fun excludes_is_empty_when_model_has_null_of_excludes() {
      val result = TrashListApiModelMapper.toJson(
          listOf(
            TrashApiModel(
              _id = "1",
              _type = TrashType.BURN,
              _trashVal = "",
              _schedules = listOf(
                ScheduleApiModel(
                  _type = "weekday",
                  _value = "0"
                )
              ),
              _excludes = null
            )
          )
      )
      Assertions.assertEquals(
        "[{\"id\":\"1\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"}],\"excludes\":[]}]",
        result
      )
    }
  }

  @Nested
  inner class ToJsonTrashList {
    @Test
    fun include_globalExcludes_when_trashList_has_globalExcludes() {
      val trashList = TrashList(
        listOf(
          Trash(
            _id = "1",
            _type = TrashType.BURN,
            _displayName = "",
            schedules = listOf(WeeklySchedule(_dayOfWeek = DayOfWeek.SUNDAY)),
            _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
          )
        ),
        ExcludeDayOfMonthList(mutableListOf(ExcludeDayOfMonth(1, 2)))
      )

      val trashDataJson = TrashListApiModelMapper.toJson(trashList)
      val globalExcludesJson =
        TrashListApiModelMapper.toGlobalExcludesApiModelList(trashList)
          .let { com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(it) }
      val apiModel = TrashListApiModelMapper.fromTrashDataJsonAndGlobalExcludes(
        trashDataJson,
        globalExcludesJson
      )

      Assertions.assertEquals(1, apiModel.globalExcludes.size)
      Assertions.assertEquals(1, apiModel.globalExcludes[0].month)
      Assertions.assertEquals(2, apiModel.globalExcludes[0].date)
    }
  }


  @Nested
  inner class ToTrashApiModelList {
    @Test
    fun isEmpty_when_trashListIsEmpty() {
      val result = TrashListApiModelMapper.toTrashApiModelList(TrashList(listOf()))
      Assertions.assertEquals(0, result.size)
    }

    @Test
    fun isOneItem_when_trashListHasOneItem() {
      val result = TrashListApiModelMapper.toTrashApiModelList(
        TrashList(listOf(
          Trash(
            _id = "1",
            _type = TrashType.BURN,
            _displayName = "",
            schedules = listOf(
              WeeklySchedule(DayOfWeek.SUNDAY)
            ),
            _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
          )
        )
        )
      )
      Assertions.assertEquals(1, result.size)
    }

    @Test
    fun isTenItems_when_trashListHasTenItems() {
      val result = TrashListApiModelMapper.toTrashApiModelList(
        TrashList(listOf(
          Trash(
            _id = "1",
            _type = TrashType.BURN,
            _displayName = "",
            schedules = listOf(
              WeeklySchedule(DayOfWeek.SUNDAY)
            ),
            _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
          ),
          Trash(
            _id = "2",
            _type = TrashType.BURN,
            _displayName = "",
            schedules = listOf(
              WeeklySchedule(DayOfWeek.SUNDAY)
            ),
            _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
          ),
          Trash(
            _id = "3",
            _type = TrashType.BURN,
            _displayName = "",
            schedules = listOf(
              WeeklySchedule(DayOfWeek.SUNDAY)
            ),
            _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
          ),
          Trash(
            _id = "4",
            _type = TrashType.BURN,
            _displayName = "",
            schedules = listOf(
              WeeklySchedule(DayOfWeek.SUNDAY)
            ),
            _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
          ),
          Trash(
            _id = "5",
            _type = TrashType.BURN,
            _displayName = "",
            schedules = listOf(
              WeeklySchedule(DayOfWeek.SUNDAY)
            ),
            _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
          ),
          Trash(
            _id = "6",
            _type = TrashType.BURN,
            _displayName = "",
            schedules = listOf(
              WeeklySchedule(DayOfWeek.SUNDAY)
            ),
            _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
          ),
          Trash(
            _id = "7",
            _type = TrashType.BURN,
            _displayName = "",
            schedules = listOf(
              WeeklySchedule(DayOfWeek.SUNDAY)
            ),
            _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
          ),
          Trash(
            _id = "8",
            _type = TrashType.BURN,
            _displayName = "",
            schedules = listOf(
              WeeklySchedule(DayOfWeek.SUNDAY)
            ),
            _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
          ),
          Trash(
            _id = "9",
            _type = TrashType.BURN,
            _displayName = "",
            schedules = listOf(
              WeeklySchedule(DayOfWeek.SUNDAY)
            ),
            _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
          ),
          Trash(
            _id = "10",
            _type = TrashType.BURN,
            _displayName = "",
            schedules = listOf(
              WeeklySchedule(DayOfWeek.SUNDAY)
            ),
            _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
          )
        )
        )
      )
      Assertions.assertEquals(10, result.size)
    }

    @Test
    fun dayOfWeek_SUNDAY_is_0_when_entity_has_weeklySchedule() {
      val result = TrashListApiModelMapper.toTrashApiModelList(
        TrashList(listOf(
          Trash(
            _id = "1",
            _type = TrashType.BURN,
            _displayName = "",
            schedules = listOf(
              WeeklySchedule(DayOfWeek.SUNDAY)
            ),
            _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
          )
        )
        )
      )
      Assertions.assertEquals("0", result[0].schedules[0].value)
    }

    @Test
    fun dayOfWeek_SUNDAY_is_0_when_entity_has_intervalWeeklySchedule() {
      val result = TrashListApiModelMapper.toTrashApiModelList(
        TrashList(listOf(
          Trash(
            _id = "1",
            _type = TrashType.BURN,
            _displayName = "",
            schedules = listOf(
              IntervalWeeklySchedule(_dayOfWeek = DayOfWeek.SUNDAY, _interval = 2, _start = LocalDate.parse("2022-01-01"))
            ),
            _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
          )
        )
        )
      )
      Assertions.assertEquals("0", (result[0].schedules[0].value as Map<*, *>)["weekday"])
      Assertions.assertEquals(2, (result[0].schedules[0].value as Map<*, *>)["interval"])
      Assertions.assertEquals("2022-01-01", (result[0].schedules[0].value as Map<*, *>)["start"])
    }
  }

  @Nested
  inner class ToTrashList {
    @Test
    fun isEmptyTrashList_when_descriptionIsEmpty() {
      val result = TrashListApiModelMapper.toTrashList(TrashListApiModel(_trashData = listOf(), _globalExcludes = listOf()))
      Assertions.assertEquals(0, result.trashList.size)
    }

    @Test
    fun isOneItemTrashList_when_descriptionHasOneItem() {
      val result = TrashListApiModelMapper.toTrashList(
        TrashListApiModel(_trashData = listOf(
        TrashApiModel(
          _id = "1",
          _type = TrashType.BURN,
          _trashVal = "",
          _schedules = listOf(
            ScheduleApiModel(
              _type = "weekday",
              _value = "0"
            )
          ),
          _excludes = listOf()
        )
      ), _globalExcludes = listOf())
      )
      Assertions.assertEquals(1, result.trashList.size)
    }

    @Test
    fun isTenItemTrashList_when_descriptionHasTenItem() {
      val result = TrashListApiModelMapper.toTrashList(
        TrashListApiModel(_trashData = listOf(
        TrashApiModel(
          _id = "1",
          _type = TrashType.BURN,
          _trashVal = "",
          _schedules = listOf(
            ScheduleApiModel(
              _type = "weekday",
              _value = "0"
            )
          ),
          _excludes = listOf()
        ),
        TrashApiModel(
          _id = "2",
          _type = TrashType.BURN,
          _trashVal = "",
          _schedules = listOf(
            ScheduleApiModel(
              _type = "weekday",
              _value = "0"
            )
          ),
          _excludes = listOf()
        ),
        TrashApiModel(
          _id = "3",
          _type = TrashType.BURN,
          _trashVal = "",
          _schedules = listOf(
            ScheduleApiModel(
              _type = "weekday",
              _value = "0"
            )
          ),
          _excludes = listOf()
        ),
        TrashApiModel(
          _id = "4",
          _type = TrashType.BURN,
          _trashVal = "",
          _schedules = listOf(
            ScheduleApiModel(
              _type = "weekday",
              _value = "0"
            )
          ),
          _excludes = listOf()
        ),
        TrashApiModel(
          _id = "5",
          _type = TrashType.BURN,
          _trashVal = "",
          _schedules = listOf(
            ScheduleApiModel(
              _type = "weekday",
              _value = "0"
            )
          ),
          _excludes = listOf()
        ),
        TrashApiModel(
          _id = "6",
          _type = TrashType.BURN,
          _trashVal = "",
          _schedules = listOf(
            ScheduleApiModel(
              _type = "weekday",
              _value = "0"
            )
          ),
          _excludes = listOf()
        ),
        TrashApiModel(
          _id = "7",
          _type = TrashType.BURN,
          _trashVal = "",
          _schedules = listOf(
            ScheduleApiModel(
              _type = "weekday",
              _value = "0"
            )
          ),
          _excludes = listOf()
        ),
        TrashApiModel(
          _id = "8",
          _type = TrashType.BURN,
          _trashVal = "",
          _schedules = listOf(
            ScheduleApiModel(
              _type = "weekday",
              _value = "0"
            )
          ),
          _excludes = listOf()
        ),
        TrashApiModel(
          _id = "9",
          _type = TrashType.BURN,
          _trashVal = "",
          _schedules = listOf(
            ScheduleApiModel(
              _type = "weekday",
              _value = "0"
            )
          ),
          _excludes = listOf()
        ),
        TrashApiModel(
          _id = "10",
          _type = TrashType.BURN,
          _trashVal = "",
          _schedules = listOf(
            ScheduleApiModel(
              _type = "weekday",
              _value = "0"
            )
          ),
          _excludes = listOf()
        )
      ), _globalExcludes = listOf())
      )
      Assertions.assertEquals(10, result.trashList.size)
    }
    @Test
    fun value_of_0_is_DayOfWeek_SUNDAY_when_model_has_weeklySchedule() {
      val result = TrashListApiModelMapper.toTrashList(
        TrashListApiModel(_trashData = listOf(
        TrashApiModel(
          _id = "1",
          _type = TrashType.BURN,
          _trashVal = "",
          _schedules = listOf(
            ScheduleApiModel(
              _type = "weekday",
              _value = "0"
            )
          ),
          _excludes = listOf()
        )
      ))
      )
      Assertions.assertEquals(DayOfWeek.SUNDAY, (result.trashList[0].schedules[0] as WeeklySchedule).dayOfWeek)
    }

    @Test
    fun value_of_1_is_DayOfMonth_1_when_model_has_monthlySchedule() {
      val result = TrashListApiModelMapper.toTrashList(
        TrashListApiModel(_trashData = listOf(
        TrashApiModel(
          _id = "1",
          _type = TrashType.BURN,
          _trashVal = "",
          _schedules = listOf(
            ScheduleApiModel(
              _type = "month",
              _value = "1"
            )
          ),
          _excludes = listOf()
        )
      ))
      )
      Assertions.assertEquals(1, (result.trashList[0].schedules[0] as MonthlySchedule).day)
    }
    @Test
    fun value_of_0_is_DayOfWeek_SUNDAY_when_model_has_ordinalWeeklySchedule() {
      val result = TrashListApiModelMapper.toTrashList(
        TrashListApiModel(_trashData = listOf(
        TrashApiModel(
          _id = "1",
          _type = TrashType.BURN,
          _trashVal = "",
          _schedules = listOf(
            ScheduleApiModel(
              _type = "biweek",
              _value = "0-4"
            )
          ),
          _excludes = listOf()
        )
      ))
      )
      Assertions.assertEquals(DayOfWeek.SUNDAY, (result.trashList[0].schedules[0] as OrdinalWeeklySchedule).dayOfWeek)
      Assertions.assertEquals(4, (result.trashList[0].schedules[0] as OrdinalWeeklySchedule).ordinalOfWeek)
    }

    @Test
    fun value_of_0_is_DayOfWeek_SUNDAY_when_model_has_intervalWeeklySchedule() {
      val result = TrashListApiModelMapper.toTrashList(
        TrashListApiModel(_trashData = listOf(
        TrashApiModel(
          _id = "1",
          _type = TrashType.BURN,
          _trashVal = "",
          _schedules = listOf(
            ScheduleApiModel(
              _type = "evweek",
              _value = hashMapOf(
                "weekday" to "0",
                "interval" to 2,
                "start" to "2022-01-01"
              )
            )
          ),
          _excludes = listOf()
        )
      ))
      )
      Assertions.assertEquals(DayOfWeek.SUNDAY, (result.trashList[0].schedules[0] as IntervalWeeklySchedule).dayOfWeek)
      Assertions.assertEquals(2, (result.trashList[0].schedules[0] as IntervalWeeklySchedule).interval)
      Assertions.assertEquals(LocalDate.parse("2022-01-01"), (result.trashList[0].schedules[0] as IntervalWeeklySchedule).start)
    }
    @Test
    fun displayName_has_value_when_model_has_null_of_trashVal() {
      val result = TrashListApiModelMapper.toTrashList(
        TrashListApiModel(_trashData = listOf(
          TrashApiModel(
            _id = "1",
            _type = TrashType.BURN,
            _trashVal = null,
            _schedules = listOf(
              ScheduleApiModel(
                _type = "weekday",
                _value = "0"
              )
            ),
            _excludes = listOf()
          )
        ))
      )
      Assertions.assertEquals("もえるゴミ", result.trashList[0].displayName)
    }
    @Test
    fun displayName_has_value_when_model_has_empty_string_of_trashVal() {
      val result = TrashListApiModelMapper.toTrashList(
        TrashListApiModel(_trashData = listOf(
          TrashApiModel(
            _id = "1",
            _type = TrashType.BURN,
            _trashVal = "",
            _schedules = listOf(
              ScheduleApiModel(
                _type = "weekday",
                _value = "0"
              )
            ),
            _excludes = listOf()
          )
        ))
      )
      Assertions.assertEquals("もえるゴミ", result.trashList[0].displayName)
    }

    @Test
    fun excludeDayOfMonth_is_empty_when_model_has_null_of_excludes() {
      val result = TrashListApiModelMapper.toTrashList(
        TrashListApiModel(_trashData = listOf(
          TrashApiModel(
            _id = "1",
            _type = TrashType.BURN,
            _trashVal = "",
            _schedules = listOf(
              ScheduleApiModel(
                _type = "weekday",
                _value = "0"
              )
            ),
            _excludes = null
          )
        ))
      )
      Assertions.assertEquals(0, result.trashList[0].excludeDayOfMonth.members.size)
    }
  }
}
