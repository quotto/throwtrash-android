package net.mythrowaway.app.domain

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TrashDataTest {
  @Test
  fun equalsWithTypeAndValue_TypeIsEquals_TrashValIsNotEquals() {
    val trash1 = TrashData().apply {
      type = TrashType.BURN
      trash_val = ""
    }
    val trash2 = TrashData().apply {
      type = TrashType.BURN
      trash_val = "burn"
    }
    assertTrue(trash1.equals(trash2))
  }

  @Test
  fun equalsWithTypeAndValue_TypeIsEquals_TrashValIsEquals() {
    val trash1 = TrashData().apply {
      type = TrashType.BURN
      trash_val = "burn"
    }
    val trash2 = TrashData().apply {
      type = TrashType.BURN
      trash_val = "burn"
    }
    assertTrue(trash1.equals(trash2))
  }

  @Test
  fun equalsWithTypeAndValue_TypeIsNotEquals_TrashValIsEquals() {
    val trash1 = TrashData().apply {
      type = TrashType.BURN
      trash_val = "burn"
    }
    val trash2 = TrashData().apply {
      type = TrashType.UNBURN
      trash_val = "burn"
    }
    assertFalse(trash1.equals(trash2))
  }

  @Test
  fun equalsWithTypeAndValue_TypeIsEquals_TrashValIsNull() {
    val trash1 = TrashData().apply {
      type = TrashType.BURN
      trash_val = null
    }
    val trash2 = TrashData().apply {
      type = TrashType.BURN
      trash_val = "burn"
    }
    assertTrue(trash1.equals(trash2))
  }

  @Test
  fun equalsWithTypeAndValue_TypeIsNotEquals_TrashValIsNotEquals() {
    val trash1 = TrashData().apply {
      type = TrashType.BURN
      trash_val = "burn"
    }
    val trash2 = TrashData().apply {
      type = TrashType.UNBURN
      trash_val = "burn"
    }
    assertFalse(trash1.equals(trash2))
  }

  @Test
  fun equalsWithTypeAndValue_TypeIsNotEquals_TrashValIsNull() {
    val trash1 = TrashData().apply {
      type = TrashType.BURN
      trash_val = null
    }
    val trash2 = TrashData().apply {
      type = TrashType.UNBURN
      trash_val = "burn"
    }
    assertFalse(trash1.equals(trash2))
  }

  @Test
  fun equalsWithTypeAndValue_TypeIsOther_TypeIsEquals_TrashValIsEquals() {
    val trash1 = TrashData().apply {
      type = TrashType.OTHER
      trash_val = "生ごみ"
    }
    val trash2 = TrashData().apply {
      type = TrashType.OTHER
      trash_val = "生ごみ"
    }
    assertTrue(trash1.equals(trash2))
  }

  @Test
  fun equalsWithTypeAndValue_TypeIsOther_TypeIsEquals_TrashValIsNotEquals() {
    val trash1 = TrashData().apply {
      type = TrashType.OTHER
      trash_val = "生ごみ"
    }
    val trash2 = TrashData().apply {
      type = TrashType.OTHER
      trash_val = "燃えないごみ"
    }
    assertFalse(trash1.equals(trash2))
  }

  @Test
  fun equalsWithTypeAndValue_TypeIsOther_TypeIsEquals_TrashValIsNull() {
    val trash1 = TrashData().apply {
      type = TrashType.OTHER
      trash_val = "生ごみ"
    }
    val trash2 = TrashData().apply {
      type = TrashType.OTHER
      trash_val = null
    }
    assertFalse(trash1.equals(trash2))
  }
  @Test
  fun deserialize_TypeIsBurn() {
    val json = """
      {
        "id": "burn",
        "type": "burn",
        "trash_val": "",
        "schedules": [
          {
            "type": "weekday",
            "value": 1
          }
        ],
        "excludes": [
          {
            "month": 1,
            "date": 1
          }
        ]
      }
    """.trimIndent()
    val objectMapper = ObjectMapper()
    val actual = objectMapper.readValue(json, TrashData::class.java)
    assertEquals(actual.type, TrashType.BURN)
  }
  @Test
  fun serialize_TypeIsBurn() {
    val trash = TrashData().apply {
      id = "burn"
      type = TrashType.BURN
      trash_val = ""
      schedules = arrayListOf(TrashSchedule().apply {
        type = "weekday"
        value = 1
      })
      excludes = listOf(ExcludeDate().apply {
        month = 1
        date = 1
      })
    }
    val objectMapper = ObjectMapper()
    val actual = objectMapper.writeValueAsString(trash)
    val expected = """
      {"id":"burn","type":"burn","trash_val":"","schedules":[{"type":"weekday","value":1}],"excludes":[{"month":1,"date":1}]}
       """.trimIndent()
    assertEquals(expected, actual)
  }

  @Test
  fun deserialize_TypeIsUnburn() {
    val json = """
      {
        "id": "unburn",
        "type": "unburn",
        "trash_val": "燃えないごみ",
        "schedules": [
          {
            "type": "weekday",
            "value": 1
          }
        ],
        "excludes": [
          {
            "month": 1,
            "date": 1
          }
        ]
      }
    """.trimIndent()
    val objectMapper = ObjectMapper()
    val actual = objectMapper.readValue(json, TrashData::class.java)
    assertEquals(actual.type, TrashType.UNBURN)
  }

  @Test
  fun serialize_TypeIsUnburn() {
    val trash = TrashData().apply {
      id = "unburn"
      type = TrashType.UNBURN
      trash_val = "燃えないごみ"
      schedules = arrayListOf(TrashSchedule().apply {
        type = "weekday"
        value = 1
      })
      excludes = listOf(ExcludeDate().apply {
        month = 1
        date = 1
      })
    }
    val objectMapper = ObjectMapper()
    val actual = objectMapper.writeValueAsString(trash)
    val expected = """
      {"id":"unburn","type":"unburn","trash_val":"燃えないごみ","schedules":[{"type":"weekday","value":1}],"excludes":[{"month":1,"date":1}]}
       """.trimIndent()
    assertEquals(expected, actual)
  }

  @Test
  fun deserialize_TypeIsOther() {
    val json = """
      {
        "id": "other",
        "type": "other",
        "trash_val": "生ごみ",
        "schedules": [
          {
            "type": "weekday",
            "value": 1
          }
        ],
        "excludes": [
          {
            "month": 1,
            "date": 1
          }
        ]
      }
    """.trimIndent()
    val objectMapper = ObjectMapper()
    val actual = objectMapper.readValue(json, TrashData::class.java)
    assertEquals(actual.type, TrashType.OTHER)
  }

  @Test
  fun serialize_TypeIsOther() {
    val trash = TrashData().apply {
      id = "other"
      type = TrashType.OTHER
      trash_val = "生ごみ"
      schedules = arrayListOf(TrashSchedule().apply {
        type = "weekday"
        value = 1
      })
      excludes = listOf(ExcludeDate().apply {
        month = 1
        date = 1
      })
    }
    val objectMapper = ObjectMapper()
    val actual = objectMapper.writeValueAsString(trash)
    val expected = """
      {"id":"other","type":"other","trash_val":"生ごみ","schedules":[{"type":"weekday","value":1}],"excludes":[{"month":1,"date":1}]}
       """.trimIndent()
    assertEquals(expected, actual)
  }

  @Test
  fun deserialize_TypeIsBottle() {
    val json = """
      {
        "id": "bottle",
        "type": "bin",
        "trash_val": "ビン・カン・ペットボトル",
        "schedules": [
          {
            "type": "weekday",
            "value": 1
          }
        ],
        "excludes": [
          {
            "month": 1,
            "date": 1
          }
        ]
      }
    """.trimIndent()
    val objectMapper = ObjectMapper()
    val actual = objectMapper.readValue(json, TrashData::class.java)
    assertEquals(actual.type, TrashType.BOTTLE)
  }

  @Test
  fun serialize_TypeIsBottle() {
    val trash = TrashData().apply {
      id = "bottle"
      type = TrashType.BOTTLE
      trash_val = "ビン・カン・ペットボトル"
      schedules = arrayListOf(TrashSchedule().apply {
        type = "weekday"
        value = 1
      })
      excludes = listOf(ExcludeDate().apply {
        month = 1
        date = 1
      })
    }
    val objectMapper = ObjectMapper()
    val actual = objectMapper.writeValueAsString(trash)
    val expected = """
      {"id":"bottle","type":"bin","trash_val":"ビン・カン・ペットボトル","schedules":[{"type":"weekday","value":1}],"excludes":[{"month":1,"date":1}]}
       """.trimIndent()
    assertEquals(expected, actual)
  }

  @Test
  fun deserialize_TypeIsCan() {
    val json = """
      {
        "id": "can",
        "type": "can",
        "trash_val": "ビン・カン・ペットボトル",
        "schedules": [
          {
            "type": "weekday",
            "value": 1
          }
        ],
        "excludes": [
          {
            "month": 1,
            "date": 1
          }
        ]
      }
    """.trimIndent()
    val objectMapper = ObjectMapper()
    val actual = objectMapper.readValue(json, TrashData::class.java)
    assertEquals(actual.type, TrashType.CAN)
  }

  @Test
  fun serialize_TypeIsCan() {
    val trash = TrashData().apply {
      id = "can"
      type = TrashType.CAN
      trash_val = "ビン・カン・ペットボトル"
      schedules = arrayListOf(TrashSchedule().apply {
        type = "weekday"
        value = 1
      })
      excludes = listOf(ExcludeDate().apply {
        month = 1
        date = 1
      })
    }
    val objectMapper = ObjectMapper()
    val actual = objectMapper.writeValueAsString(trash)
    val expected = """
      {"id":"can","type":"can","trash_val":"ビン・カン・ペットボトル","schedules":[{"type":"weekday","value":1}],"excludes":[{"month":1,"date":1}]}
       """.trimIndent()
    assertEquals(expected, actual)
  }

  @Test
  fun deserialize_TypeIsPetBottle() {
    val json = """
      {
        "id": "petbottle",
        "type": "petbottle",
        "trash_val": "ビン・カン・ペットボトル",
        "schedules": [
          {
            "type": "weekday",
            "value": 1
          }
        ],
        "excludes": [
          {
            "month": 1,
            "date": 1
          }
        ]
      }
    """.trimIndent()
    val objectMapper = ObjectMapper()
    val actual = objectMapper.readValue(json, TrashData::class.java)
    assertEquals(actual.type, TrashType.PETBOTTLE)
  }

  @Test
  fun serialize_TypeIsPetBottle() {
    val trash = TrashData().apply {
      id = "petbottle"
      type = TrashType.PETBOTTLE
      trash_val = "ビン・カン・ペットボトル"
      schedules = arrayListOf(TrashSchedule().apply {
        type = "weekday"
        value = 1
      })
      excludes = listOf(ExcludeDate().apply {
        month = 1
        date = 1
      })
    }
    val objectMapper = ObjectMapper()
    val actual = objectMapper.writeValueAsString(trash)
    val expected = """
      {"id":"petbottle","type":"petbottle","trash_val":"ビン・カン・ペットボトル","schedules":[{"type":"weekday","value":1}],"excludes":[{"month":1,"date":1}]}
       """.trimIndent()
    assertEquals(expected, actual)
  }

  @Test
  fun deserialize_TypeIsPlastic() {
    val json = """
      {
        "id": "plastic",
        "type": "plastic",
        "trash_val": "プラスチック",
        "schedules": [
          {
            "type": "weekday",
            "value": 1
          }
        ],
        "excludes": [
          {
            "month": 1,
            "date": 1
          }
        ]
      }
    """.trimIndent()
    val objectMapper = ObjectMapper()
    val actual = objectMapper.readValue(json, TrashData::class.java)
    assertEquals(actual.type, TrashType.PLASTIC)
  }

  @Test
  fun serialize_TypeIsPlastic() {
    val trash = TrashData().apply {
      id = "plastic"
      type = TrashType.PLASTIC
      trash_val = "プラスチック"
      schedules = arrayListOf(TrashSchedule().apply {
        type = "weekday"
        value = 1
      })
      excludes = listOf(ExcludeDate().apply {
        month = 1
        date = 1
      })
    }
    val objectMapper = ObjectMapper()
    val actual = objectMapper.writeValueAsString(trash)
    val expected = """
      {"id":"plastic","type":"plastic","trash_val":"プラスチック","schedules":[{"type":"weekday","value":1}],"excludes":[{"month":1,"date":1}]}
       """.trimIndent()
    assertEquals(expected, actual)
  }

  @Test
  fun deserialize_TypeIsPaper() {
    val json = """
      {
        "id": "paper",
        "type": "paper",
        "trash_val": "紙類",
        "schedules": [
          {
            "type": "weekday",
            "value": 1
          }
        ],
        "excludes": [
          {
            "month": 1,
            "date": 1
          }
        ]
      }
    """.trimIndent()
    val objectMapper = ObjectMapper()
    val actual = objectMapper.readValue(json, TrashData::class.java)
    assertEquals(actual.type, TrashType.PAPER)
  }

  @Test
  fun serialize_TypeIsPaper() {
    val trash = TrashData().apply {
      id = "paper"
      type = TrashType.PAPER
      trash_val = "紙類"
      schedules = arrayListOf(TrashSchedule().apply {
        type = "weekday"
        value = 1
      })
      excludes = listOf(ExcludeDate().apply {
        month = 1
        date = 1
      })
    }
    val objectMapper = ObjectMapper()
    val actual = objectMapper.writeValueAsString(trash)
    val expected = """
      {"id":"paper","type":"paper","trash_val":"紙類","schedules":[{"type":"weekday","value":1}],"excludes":[{"month":1,"date":1}]}
       """.trimIndent()
    assertEquals(expected, actual)
  }

  @Test
  fun deserialize_TypeIsResource() {
    val json = """
      {
        "id": "resource",
        "type": "resource",
        "trash_val": "資源ゴミ",
        "schedules": [
          {
            "type": "weekday",
            "value": 1
          }
        ],
        "excludes": [
          {
            "month": 1,
            "date": 1
          }
        ]
      }
    """.trimIndent()
    val objectMapper = ObjectMapper()
    val actual = objectMapper.readValue(json, TrashData::class.java)
    assertEquals(actual.type, TrashType.RESOURCE)
  }

  @Test
  fun serialize_TypeIsResource() {
    val trash = TrashData().apply {
      id = "resource"
      type = TrashType.RESOURCE
      trash_val = "資源ゴミ"
      schedules = arrayListOf(TrashSchedule().apply {
        type = "weekday"
        value = 1
      })
      excludes = listOf(ExcludeDate().apply {
        month = 1
        date = 1
      })
    }
    val objectMapper = ObjectMapper()
    val actual = objectMapper.writeValueAsString(trash)
    val expected = """
      {"id":"resource","type":"resource","trash_val":"資源ゴミ","schedules":[{"type":"weekday","value":1}],"excludes":[{"month":1,"date":1}]}
       """.trimIndent()
    assertEquals(expected, actual)
  }

  @Test
  fun deserialize_TypeIsCoarse() {
    val json = """
      {
        "id": "coarse",
        "type": "coarse",
        "trash_val": "粗大ゴミ",
        "schedules": [
          {
            "type": "weekday",
            "value": 1
          }
        ],
        "excludes": [
          {
            "month": 1,
            "date": 1
          }
        ]
      }
    """.trimIndent()
    val objectMapper = ObjectMapper()
    val actual = objectMapper.readValue(json, TrashData::class.java)
    assertEquals(actual.type, TrashType.COARSE)
  }

  @Test
  fun serialize_TypeIsCoarse() {
    val trash = TrashData().apply {
      id = "coarse"
      type = TrashType.COARSE
      trash_val = "粗大ゴミ"
      schedules = arrayListOf(TrashSchedule().apply {
        type = "weekday"
        value = 1
      })
      excludes = listOf(ExcludeDate().apply {
        month = 1
        date = 1
      })
    }
    val objectMapper = ObjectMapper()
    val actual = objectMapper.writeValueAsString(trash)
    val expected = """
      {"id":"coarse","type":"coarse","trash_val":"粗大ゴミ","schedules":[{"type":"weekday","value":1}],"excludes":[{"month":1,"date":1}]}
       """.trimIndent()
    assertEquals(expected, actual)
  }
}