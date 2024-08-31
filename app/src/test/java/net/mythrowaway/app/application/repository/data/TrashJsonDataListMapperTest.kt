package net.mythrowaway.app.application.repository.data

import net.mythrowaway.app.domain.trash.infra.data.mapper.TrashJsonDataListMapper
import net.mythrowaway.app.domain.trash.entity.TrashType
import net.mythrowaway.app.domain.trash.infra.data.ExcludeDayOfMonthJsonData
import net.mythrowaway.app.domain.trash.infra.data.ScheduleJsonData
import net.mythrowaway.app.domain.trash.infra.data.TrashJsonData
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TrashJsonDataListMapperTest {

  @Nested
  inner class FromJson {
    @Test
    fun isValid_when_json_has_weekday_schedule() {
      val json = """
        [
          {
            "id": "0",
            "type": "burn",
            "trash_val": "",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
      """.trimIndent()
      val result = TrashJsonDataListMapper.fromJson(json)
      Assertions.assertEquals(1, result[0].schedules.size)
      Assertions.assertEquals("weekday", result[0].schedules[0].type)
      Assertions.assertEquals("0", result[0].schedules[0].value)
      Assertions.assertEquals(0, result[0].excludes.size)
    }

    @Test
    fun isValid_when_json_has_month_schedule() {
      val json = """
        [
          {
            "id": "0",
            "type": "burn",
            "trash_val": "",
            "schedules": [
              {
                "type": "month",
                "value": "1"
              }
            ],
            "excludes": []
          }
        ]
      """.trimIndent()
      val result = TrashJsonDataListMapper.fromJson(json)
      Assertions.assertEquals(1, result[0].schedules.size)
      Assertions.assertEquals("month", result[0].schedules[0].type)
      Assertions.assertEquals("1", result[0].schedules[0].value)
      Assertions.assertEquals(0, result[0].excludes.size)
    }

    @Test
    fun isValid_when_json_has_biweek_schedule() {
      val json = """
        [
          {
            "id": "0",
            "type": "burn",
            "trash_val": "",
            "schedules": [
              {
                "type": "biweek",
                "value": "0-1"
              }
            ],
            "excludes": []
          }
        ]
      """.trimIndent()
      val result = TrashJsonDataListMapper.fromJson(json)
      Assertions.assertEquals(1, result[0].schedules.size)
      Assertions.assertEquals("biweek", result[0].schedules[0].type)
      Assertions.assertEquals("0-1", result[0].schedules[0].value)
      Assertions.assertEquals(0, result[0].excludes.size)
    }

    @Test
    fun isValid_when_json_has_evweek_schedule() {
      val json = """
        [
          {
            "id": "0",
            "type": "burn",
            "trash_val": "",
            "schedules": [
              {
                "type": "evweek",
                "value": {
                  "weekday": "0",
                  "start": "2021-01-01",
                  "interval": 1
                }
              }
            ],
            "excludes": []
          }
        ]
      """.trimIndent()
      val result = TrashJsonDataListMapper.fromJson(json)
      Assertions.assertEquals(1, result[0].schedules.size)
      Assertions.assertEquals("evweek", result[0].schedules[0].type)
      Assertions.assertEquals("0", (result[0].schedules[0].value as HashMap<*, *>)["weekday"])
      Assertions.assertEquals("2021-01-01", (result[0].schedules[0].value as HashMap<*, *>)["start"])
      Assertions.assertEquals(1, (result[0].schedules[0].value as HashMap<*, *>)["interval"])
      Assertions.assertEquals(0, result[0].excludes.size)
    }

    @Test
    fun isValid_when_json_has_exclude() {
      val json = """
        [
          {
            "id": "0",
            "type": "burn",
            "trash_val": "",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": [
              {
                "month": "1",
                "date": "1"
              }
            ]
          }
        ]
      """.trimIndent()
      val result = TrashJsonDataListMapper.fromJson(json)
      Assertions.assertEquals(1, result[0].excludes.size)
      Assertions.assertEquals(1, result[0].excludes[0].month)
      Assertions.assertEquals(1, result[0].excludes[0].date)
    }

    @Test
    fun isValid_when_list_has_trashType_of_other() {
      val json = """
        [
          {
            "id": "0",
            "type": "other",
            "trash_val": "家電",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
      """.trimIndent()
      val result = TrashJsonDataListMapper.fromJson(json)
      Assertions.assertEquals(TrashType.OTHER, result[0].type)
      Assertions.assertEquals("家電", result[0].trashVal)
    }

    @Test
    fun isValid_when_list_has_trashType_of_unburn() {
      val json = """
        [
          {
            "id": "0",
            "type": "unburn",
            "trash_val": "燃えないゴミ",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
      """.trimIndent()
      val result = TrashJsonDataListMapper.fromJson(json)
      Assertions.assertEquals(TrashType.UNBURN, result[0].type)
      Assertions.assertEquals("燃えないゴミ", result[0].trashVal)
    }

    @Test
    fun isValid_when_list_has_trashType_of_petbottle() {
      val json = """
        [
          {
            "id": "0",
            "type": "petbottle",
            "trash_val": "ペットボトル",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
      """.trimIndent()
      val result = TrashJsonDataListMapper.fromJson(json)
      Assertions.assertEquals(TrashType.PETBOTTLE, result[0].type)
      Assertions.assertEquals("ペットボトル", result[0].trashVal)
    }

    @Test
    fun isValid_when_list_has_trashType_of_can() {
      val json = """
        [
          {
            "id": "0",
            "type": "can",
            "trash_val": "缶",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
      """.trimIndent()
      val result = TrashJsonDataListMapper.fromJson(json)
      Assertions.assertEquals(TrashType.CAN, result[0].type)
      Assertions.assertEquals("缶", result[0].trashVal)
    }

    @Test
    fun isValid_when_list_has_trashType_of_bin() {
      val json = """
        [
          {
            "id": "0",
            "type": "bin",
            "trash_val": "ビン",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
      """.trimIndent()
      val result = TrashJsonDataListMapper.fromJson(json)
      Assertions.assertEquals(TrashType.BOTTLE, result[0].type)
      Assertions.assertEquals("ビン", result[0].trashVal)
    }

    @Test
    fun isValid_when_list_has_trashType_of_plastic() {
      val json = """
        [
          {
            "id": "0",
            "type": "plastic",
            "trash_val": "プラスチック",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
      """.trimIndent()
      val result = TrashJsonDataListMapper.fromJson(json)
      Assertions.assertEquals(TrashType.PLASTIC, result[0].type)
      Assertions.assertEquals("プラスチック", result[0].trashVal)
    }
    @Test
    fun isValid_when_list_has_trashType_of_paper() {
      val json = """
        [
          {
            "id": "0",
            "type": "paper",
            "trash_val": "紙くず",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
      """.trimIndent()
      val result = TrashJsonDataListMapper.fromJson(json)
      Assertions.assertEquals(TrashType.PAPER, result[0].type)
      Assertions.assertEquals("紙くず", result[0].trashVal)
    }

    @Test
    fun isValid_when_list_has_trashType_of_coarse() {
      val json = """
        [
          {
            "id": "0",
            "type": "coarse",
            "trash_val": "粗大ゴミ",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
      """.trimIndent()
      val result = TrashJsonDataListMapper.fromJson(json)
      Assertions.assertEquals(TrashType.COARSE, result[0].type)
      Assertions.assertEquals("粗大ゴミ", result[0].trashVal)
    }

    @Test
    fun isValid_when_list_has_trashType_of_resource() {
      val json = """
        [
          {
            "id": "0",
            "type": "resource",
            "trash_val": "資源ごみ",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
      """.trimIndent()
      val result = TrashJsonDataListMapper.fromJson(json)
      Assertions.assertEquals(TrashType.RESOURCE, result[0].type)
      Assertions.assertEquals("資源ごみ", result[0].trashVal)
    }

    @Test
    fun isValid_when_list_has_single_trash() {
      val json = """
        [
          {
            "id": "0",
            "type": "burn",
            "trash_val": "",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
      """.trimIndent()
      val result = TrashJsonDataListMapper.fromJson(json)
      Assertions.assertEquals(1, result.size)
      Assertions.assertEquals("0", result[0].id)
      Assertions.assertEquals(TrashType.BURN, result[0].type)
      Assertions.assertEquals("", result[0].trashVal)
      Assertions.assertEquals(1, result[0].schedules.size)
      Assertions.assertEquals("weekday", result[0].schedules[0].type)
      Assertions.assertEquals("0", result[0].schedules[0].value)
      Assertions.assertEquals(0, result[0].excludes.size)
    }
    @Test
    fun isValid_when_list_has_multiple_trash() {
      val json = """
        [
          {
            "id": "0",
            "type": "burn",
            "trash_val": "",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          },
          {
            "id": "1",
            "type": "other",
            "trash_val": "家電",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
      """.trimIndent()
      val result = TrashJsonDataListMapper.fromJson(json)
      Assertions.assertEquals(2, result.size)
      Assertions.assertEquals("0", result[0].id)
      Assertions.assertEquals(TrashType.BURN, result[0].type)
      Assertions.assertEquals("", result[0].trashVal)
      Assertions.assertEquals(1, result[0].schedules.size)
      Assertions.assertEquals("weekday", result[0].schedules[0].type)
      Assertions.assertEquals("0", result[0].schedules[0].value)
      Assertions.assertEquals(0, result[0].excludes.size)
      Assertions.assertEquals("1", result[1].id)
      Assertions.assertEquals(TrashType.OTHER, result[1].type)
      Assertions.assertEquals("家電", result[1].trashVal)
      Assertions.assertEquals(1, result[1].schedules.size)
      Assertions.assertEquals("weekday", result[1].schedules[0].type)
      Assertions.assertEquals("0", result[1].schedules[0].value)
      Assertions.assertEquals(0, result[1].excludes.size)
    }

    @Test
    fun is_empty_when_json_is_empty() {
      val json = "[]"
      val result = TrashJsonDataListMapper.fromJson(json)
      Assertions.assertEquals(0, result.size)
    }

    @Test
    fun trash_val_is_empty_when_json_has_empty_trash_val() {
      val json = """
        [
          {
            "id": "0",
            "type": "burn",
            "trash_val": "",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
      """.trimIndent()
      val result = TrashJsonDataListMapper.fromJson(json)
      Assertions.assertEquals("", result[0].trashVal)
    }

    @Test
    fun trash_val_has_value_when_json_has_null_trash_val() {
      val json = """
        [
          {
            "id": "0",
            "type": "burn",
            "trash_val": null,
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
      """.trimIndent()
      val result = TrashJsonDataListMapper.fromJson(json)
      Assertions.assertEquals("もえるゴミ", result[0].trashVal)
    }

    @Test
    fun excludes_is_empty_when_json_has_null_of_excludes() {
      val json = """
        [
          {
            "id": "0",
            "type": "burn",
            "trash_val": "",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ]
          }
        ]
      """.trimIndent()
      val result = TrashJsonDataListMapper.fromJson(json)
      Assertions.assertEquals(0, result[0].excludes.size)
    }
  }

  @Nested
  inner class ToJson {

    @Test
    fun isValid_when_list_has_weekday_schedule() {
      val trash = TrashJsonData(
        _id = "0",
        _type = TrashType.BURN,
        _trashVal = "",
        _schedules = listOf(
          ScheduleJsonData(
            _type = "weekday",
            _value = "0"
          )
        ),
        _excludes = listOf()
      )
      val result = TrashJsonDataListMapper.toJson(listOf(trash))
      Assertions.assertEquals(
        """
        [
          {
            "id": "0",
            "type": "burn",
            "trash_val": "",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
        """.trimIndent().replace("\n","").replace(" ",""),
        result
      )
    }

    @Test
    fun isValid_when_list_has_month_schedule() {
      val trash = TrashJsonData(
        _id = "0",
        _type = TrashType.BURN,
        _trashVal = "",
        _schedules = listOf(
          ScheduleJsonData(
            _type = "month",
            _value = "1"
          )
        ),
        _excludes = listOf()
      )
      val result = TrashJsonDataListMapper.toJson(listOf(trash))
      Assertions.assertEquals(
        """
        [
          {
            "id": "0",
            "type": "burn",
            "trash_val": "",
            "schedules": [
              {
                "type": "month",
                "value": "1"
              }
            ],
            "excludes": []
          }
        ]
        """.trimIndent().replace("\n","").replace(" ",""),
        result
      )
    }

    @Test
    fun isValid_when_list_has_biweek_schedule() {
      val trash = TrashJsonData(
        _id = "0",
        _type = TrashType.BURN,
        _trashVal = "",
        _schedules = listOf(
          ScheduleJsonData(
            _type = "biweek",
            _value = "0-1"
          )
        ),
        _excludes = listOf()
      )
      val result = TrashJsonDataListMapper.toJson(listOf(trash))
      Assertions.assertEquals(
        """
        [
          {
            "id": "0",
            "type": "burn",
            "trash_val": "",
            "schedules": [
              {
                "type": "biweek",
                "value": "0-1"
              }
            ],
            "excludes": []
          }
        ]
        """.trimIndent().replace("\n","").replace(" ",""),
        result
      )
    }

    @Test
    fun isValid_when_list_has_evweek_schedule() {
      val trash = TrashJsonData(
        _id = "0",
        _type = TrashType.BURN,
        _trashVal = "",
        _schedules = listOf(
          ScheduleJsonData(
            _type = "evweek",
            _value = hashMapOf(
              "weekday" to "0",
              "start" to "2021-01-01",
              "interval" to 1
            )
          )
        ),
        _excludes = listOf()
      )
      val result = TrashJsonDataListMapper.toJson(listOf(trash))
      Assertions.assertEquals(
        """
        [
          {
            "id": "0",
            "type": "burn",
            "trash_val": "",
            "schedules": [
              {
                "type": "evweek",
                "value": {
                  "weekday": "0",
                  "start": "2021-01-01",
                  "interval": 1
                }
              }
            ],
            "excludes": []
          }
        ]
        """.trimIndent().replace("\n","").replace(" ",""),
        result
      )
    }

    @Test
    fun isValid_when_list_has_exclude() {
      val trash = TrashJsonData(
        _id = "0",
        _type = TrashType.BURN,
        _trashVal = "",
        _schedules = listOf(
          ScheduleJsonData(
            _type = "weekday",
            _value = "0"
          )
        ),
        _excludes = listOf(
          ExcludeDayOfMonthJsonData(
            _month = 1,
            _date = 1
          )
        )
      )
      val result = TrashJsonDataListMapper.toJson(listOf(trash))
      Assertions.assertEquals(
        """
        [
          {
            "id": "0",
            "type": "burn",
            "trash_val": "",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": [
              {
                "month": 1,
                "date": 1
              }
            ]
          }
        ]
        """.trimIndent().replace("\n","").replace(" ",""),
        result
      )
    }

    @Test
    fun isValid_when_list_has_trashType_of_other() {
      val trash = TrashJsonData(
        _id = "0",
        _type = TrashType.OTHER,
        _trashVal = "家電",
        _schedules = listOf(
          ScheduleJsonData(
            _type = "weekday",
            _value = "0"
          )
        ),
        _excludes = listOf()
      )
      val result = TrashJsonDataListMapper.toJson(listOf(trash))
      Assertions.assertEquals(
        """
        [
          {
            "id": "0",
            "type": "other",
            "trash_val": "家電",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
        """.trimIndent().replace("\n","").replace(" ",""),
        result
      )
    }

    @Test
    fun isValid_when_list_has_trashType_of_unburn() {
      val trash = TrashJsonData(
        _id = "0",
        _type = TrashType.UNBURN,
        _trashVal = "燃えないゴミ",
        _schedules = listOf(
          ScheduleJsonData(
            _type = "weekday",
            _value = "0"
          )
        ),
        _excludes = listOf()
      )
      val result = TrashJsonDataListMapper.toJson(listOf(trash))
      Assertions.assertEquals(
        """
        [
          {
            "id": "0",
            "type": "unburn",
            "trash_val": "燃えないゴミ",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
        """.trimIndent().replace("\n","").replace(" ",""),
        result
      )
    }

    @Test
    fun isValid_when_list_has_trashType_of_petbottle() {
      val trash = TrashJsonData(
        _id = "0",
        _type = TrashType.PETBOTTLE,
        _trashVal = "ペットボトル",
        _schedules = listOf(
          ScheduleJsonData(
            _type = "weekday",
            _value = "0"
          )
        ),
        _excludes = listOf()
      )
      val result = TrashJsonDataListMapper.toJson(listOf(trash))
      Assertions.assertEquals(
        """
        [
          {
            "id": "0",
            "type": "petbottle",
            "trash_val": "ペットボトル",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
        """.trimIndent().replace("\n","").replace(" ",""),
        result
      )
    }

    @Test
    fun isValid_when_list_has_trashType_of_can() {
      val trash = TrashJsonData(
        _id = "0",
        _type = TrashType.CAN,
        _trashVal = "缶",
        _schedules = listOf(
          ScheduleJsonData(
            _type = "weekday",
            _value = "0"
          )
        ),
        _excludes = listOf()
      )
      val result = TrashJsonDataListMapper.toJson(listOf(trash))
      Assertions.assertEquals(
        """
        [
          {
            "id": "0",
            "type": "can",
            "trash_val": "缶",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
        """.trimIndent().replace("\n","").replace(" ",""),
        result
      )
    }

    @Test
    fun isValid_when_list_has_trashType_of_plastic() {
      val trash = TrashJsonData(
        _id = "0",
        _type = TrashType.PLASTIC,
        _trashVal = "プラスチック",
        _schedules = listOf(
          ScheduleJsonData(
            _type = "weekday",
            _value = "0"
          )
        ),
        _excludes = listOf()
      )
      val result = TrashJsonDataListMapper.toJson(listOf(trash))
      Assertions.assertEquals(
        """
        [
          {
            "id": "0",
            "type": "plastic",
            "trash_val": "プラスチック",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
        """.trimIndent().replace("\n","").replace(" ",""),
        result
      )
    }

    @Test
    fun isValid_when_list_has_trashType_of_bin() {
      val trash = TrashJsonData(
        _id = "0",
        _type = TrashType.BOTTLE,
        _trashVal = "ビン",
        _schedules = listOf(
          ScheduleJsonData(
            _type = "weekday",
            _value = "0"
          )
        ),
        _excludes = listOf()
      )
      val result = TrashJsonDataListMapper.toJson(listOf(trash))
      Assertions.assertEquals(
        """
        [
          {
            "id": "0",
            "type": "bin",
            "trash_val": "ビン",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
        """.trimIndent().replace("\n","").replace(" ",""),
        result
      )
    }

    @Test
    fun isValid_when_list_has_trashType_of_paper() {
      val trash = TrashJsonData(
        _id = "0",
        _type = TrashType.PAPER,
        _trashVal = "紙くず",
        _schedules = listOf(
          ScheduleJsonData(
            _type = "weekday",
            _value = "0"
          )
        ),
        _excludes = listOf()
      )
      val result = TrashJsonDataListMapper.toJson(listOf(trash))
      Assertions.assertEquals(
        """
        [
          {
            "id": "0",
            "type": "paper",
            "trash_val": "紙くず",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
        """.trimIndent().replace("\n","").replace(" ",""),
        result
      )
    }

    @Test
    fun isValid_when_list_has_trashType_of_coarse() {
      val trash = TrashJsonData(
        _id = "0",
        _type = TrashType.COARSE,
        _trashVal = "粗大ゴミ",
        _schedules = listOf(
          ScheduleJsonData(
            _type = "weekday",
            _value = "0"
          )
        ),
        _excludes = listOf()
      )
      val result = TrashJsonDataListMapper.toJson(listOf(trash))
      Assertions.assertEquals(
        """
        [
          {
            "id": "0",
            "type": "coarse",
            "trash_val": "粗大ゴミ",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
        """.trimIndent().replace("\n","").replace(" ",""),
        result
      )
    }

    @Test
    fun isValid_when_list_has_trashType_of_resource() {
      val trash = TrashJsonData(
        _id = "0",
        _type = TrashType.RESOURCE,
        _trashVal = "資源ごみ",
        _schedules = listOf(
          ScheduleJsonData(
            _type = "weekday",
            _value = "0"
          )
        ),
        _excludes = listOf()
      )
      val result = TrashJsonDataListMapper.toJson(listOf(trash))
      Assertions.assertEquals(
        """
        [
          {
            "id": "0",
            "type": "resource",
            "trash_val": "資源ごみ",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
        """.trimIndent().replace("\n","").replace(" ",""),
        result
      )
    }

    @Test
    fun isValid_when_list_has_multiple_exclude() {
      val trash = TrashJsonData(
        _id = "0",
        _type = TrashType.BURN,
        _trashVal = "",
        _schedules = listOf(
          ScheduleJsonData(
            _type = "weekday",
            _value = "0"
          )
        ),
        _excludes = listOf(
          ExcludeDayOfMonthJsonData(
            _month = 1,
            _date = 1
          ),
          ExcludeDayOfMonthJsonData(
            _month = 2,
            _date = 2
          )
        )
      )
      val result = TrashJsonDataListMapper.toJson(listOf(trash))
      Assertions.assertEquals(
        """
        [
          {
            "id": "0",
            "type": "burn",
            "trash_val": "",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": [
              {
                "month": 1,
                "date": 1
              },
              {
                "month": 2,
                "date": 2
              }
            ]
          }
        ]
        """.trimIndent().replace("\n","").replace(" ",""),
        result
      )
    }
    @Test
    fun isValid_when_list_has_single_trash() {
      val trash = TrashJsonData(
        _id = "0",
        _type = TrashType.BURN,
        _trashVal = "",
        _schedules = listOf(
          ScheduleJsonData(
            _type = "weekday",
            _value = "0"
          )
        ),
        _excludes = listOf()
      )
      val result = TrashJsonDataListMapper.toJson(listOf(trash))
      Assertions.assertEquals(
        """
        [
          {
            "id": "0",
            "type": "burn",
            "trash_val": "",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
        """.trimIndent().replace("\n","").replace(" ",""),
        result
      )
    }

    @Test
    fun isValid_when_list_has_multiple_trash() {
      val trash1 = TrashJsonData(
        _id = "0",
        _type = TrashType.BURN,
        _trashVal = "",
        _schedules = listOf(
          ScheduleJsonData(
            _type = "weekday",
            _value = "0"
          )
        ),
        _excludes = listOf()
      )
      val trash2 = TrashJsonData(
        _id = "1",
        _type = TrashType.OTHER,
        _trashVal = "家電",
        _schedules = listOf(
          ScheduleJsonData(
            _type = "weekday",
            _value = "0"
          )
        ),
        _excludes = listOf()
      )
      val result = TrashJsonDataListMapper.toJson(listOf(trash1, trash2))
      Assertions.assertEquals(
        """
        [
          {
            "id": "0",
            "type": "burn",
            "trash_val": "",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          },
          {
            "id": "1",
            "type": "other",
            "trash_val": "家電",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
        """.trimIndent().replace("\n","").replace(" ",""),
        result
      )
    }

    @Test
    fun is_empty_when_list_is_empty() {
      val result = TrashJsonDataListMapper.toJson(listOf())
      Assertions.assertEquals("[]", result)
    }

    @Test
    fun trash_val_is_empty_when_list_has_empty_trash_val() {
      val trash = TrashJsonData(
        _id = "0",
        _type = TrashType.BURN,
        _trashVal = "",
        _schedules = listOf(
          ScheduleJsonData(
            _type = "weekday",
            _value = "0"
          )
        ),
        _excludes = listOf()
      )
      val result = TrashJsonDataListMapper.toJson(listOf(trash))
      Assertions.assertEquals(
        """
        [
          {
            "id": "0",
            "type": "burn",
            "trash_val": "",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
        """.trimIndent().replace("\n","").replace(" ",""),
        result
      )
    }
    @Test
    fun trash_val_has_value_when_list_has_null_trash_val() {
      val trash = TrashJsonData(
        _id = "0",
        _type = TrashType.BURN,
        _trashVal = null,
        _schedules = listOf(
          ScheduleJsonData(
            _type = "weekday",
            _value = "0"
          )
        ),
        _excludes = listOf()
      )
      val result = TrashJsonDataListMapper.toJson(listOf(trash))
      Assertions.assertEquals(
        """
        [
          {
            "id": "0",
            "type": "burn",
            "trash_val": "もえるゴミ",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
        """.trimIndent().replace("\n","").replace(" ",""),
        result
      )
    }

    @Test
    fun excludes_is_empty_when_list_has_null_of_excludes() {
      val trash = TrashJsonData(
        _id = "0",
        _type = TrashType.BURN,
        _trashVal = "",
        _schedules = listOf(
          ScheduleJsonData(
            _type = "weekday",
            _value = "0"
          )
        ),
        _excludes = null
      )
      val result = TrashJsonDataListMapper.toJson(listOf(trash))
      Assertions.assertEquals(
        """
        [
          {
            "id": "0",
            "type": "burn",
            "trash_val": "",
            "schedules": [
              {
                "type": "weekday",
                "value": "0"
              }
            ],
            "excludes": []
          }
        ]
        """.trimIndent().replace("\n","").replace(" ",""),
        result
      )
    }
  }
}