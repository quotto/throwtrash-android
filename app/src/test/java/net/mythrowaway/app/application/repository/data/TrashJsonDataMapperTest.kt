package net.mythrowaway.app.application.repository.data

import net.mythrowaway.app.domain.trash.infra.data.mapper.TrashJsonDataMapper
import net.mythrowaway.app.domain.trash.entity.trash.ExcludeDayOfMonth
import net.mythrowaway.app.domain.trash.entity.trash.ExcludeDayOfMonthList
import net.mythrowaway.app.domain.trash.entity.trash.IntervalWeeklySchedule
import net.mythrowaway.app.domain.trash.entity.trash.MonthlySchedule
import net.mythrowaway.app.domain.trash.entity.trash.OrdinalWeeklySchedule
import net.mythrowaway.app.domain.trash.entity.trash.Trash
import net.mythrowaway.app.domain.trash.entity.trash.TrashType
import net.mythrowaway.app.domain.trash.entity.trash.WeeklySchedule
import net.mythrowaway.app.domain.trash.infra.data.ExcludeDayOfMonthJsonData
import net.mythrowaway.app.domain.trash.infra.data.ScheduleJsonData
import net.mythrowaway.app.domain.trash.infra.data.TrashJsonData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate

class TrashJsonDataMapperTest {
  @Nested
  inner class ToData {
    @Test
    fun value_is_0_when_trash_has_sunday_of_weeklySchedule() {
      val trash = Trash(
        _id = "0",
        _type = TrashType.BURN,
        schedules = listOf(
          WeeklySchedule(_dayOfWeek = DayOfWeek.SUNDAY)
        ),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
      )
      val result = TrashJsonDataMapper.toData(trash)
      assertEquals("0", result.id)
      assertEquals(TrashType.BURN, result.type)
      assertEquals(TrashType.BURN.getTrashText(), result.trashVal)
      assertEquals(1, result.schedules.size)
      assertEquals("weekday", result.schedules[0].type)
      assertEquals("0", result.schedules[0].value)
      assertEquals(0, result.excludes.size)
    }

    @Test
    fun value_is_1_when_trash_has_monday_of_weeklySchedule() {
      val trash = Trash(
        _id = "0",
        _type = TrashType.BURN,
        schedules = listOf(
          WeeklySchedule(_dayOfWeek = DayOfWeek.MONDAY)
        ),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
      )
      val result = TrashJsonDataMapper.toData(trash)
      assertEquals(1, result.schedules.size)
      assertEquals("weekday", result.schedules[0].type)
      assertEquals("1", result.schedules[0].value)
    }

    @Test
    fun value_is_1_when_trash_has_1_of_monthlySchedule() {
      val trash = Trash(
        _id = "0",
        _type = TrashType.BURN,
        schedules = listOf(
          MonthlySchedule(_day = 1)
        ),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
      )
      val result = TrashJsonDataMapper.toData(trash)
      assertEquals(1, result.schedules.size)
      assertEquals("month", result.schedules[0].type)
      assertEquals("1", result.schedules[0].value)
    }

    @Test
    fun value_is_0_3_when_trash_has_sunday_and_3_of_ordinalWeeklySchedule() {
      val trash = Trash(
        _id = "0",
        _type = TrashType.BURN,
        schedules = listOf(
          OrdinalWeeklySchedule(_dayOfWeek = DayOfWeek.SUNDAY, _ordinalOfWeek = 3)
        ),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
      )
      val result = TrashJsonDataMapper.toData(trash)
      assertEquals(1, result.schedules.size)
      assertEquals("biweek", result.schedules[0].type)
      assertEquals("0-3", result.schedules[0].value)
    }

    @Test
    fun value_is_6_2_when_trash_has_saturday_and_2_of_ordinalWeeklySchedule() {
      val trash = Trash(
        _id = "0",
        _type = TrashType.BURN,
        schedules = listOf(
          OrdinalWeeklySchedule(_dayOfWeek = DayOfWeek.SATURDAY, _ordinalOfWeek = 2)
        ),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
      )
      val result = TrashJsonDataMapper.toData(trash)
      assertEquals(1, result.schedules.size)
      assertEquals("biweek", result.schedules[0].type)
      assertEquals("6-2", result.schedules[0].value)
    }

    @Test
    fun value_is_map_of_0_and_2_and_20220201_when_trash_has_intervalWeeklySchedule() {
      val trash = Trash(
        _id = "0",
        _type = TrashType.BURN,
        schedules = listOf(
          IntervalWeeklySchedule(
            _dayOfWeek = DayOfWeek.SUNDAY,
            _start = LocalDate.parse("2022-02-01"),
            _interval = 2
          )
        ),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
      )
      val result = TrashJsonDataMapper.toData(trash)
      assertEquals(1, result.schedules.size)
      assertEquals("evweek", result.schedules[0].type)
      assertEquals("0", (result.schedules[0].value as Map<*, *>)["weekday"])
      assertEquals("2022-02-01", (result.schedules[0].value as Map<*, *>)["start"])
      assertEquals(2, (result.schedules[0].value as Map<*, *>)["interval"])
    }

    @Test
    fun value_is_map_of_1_and_4_and_20220201_when_trash_has_intervalWeeklySchedule() {
      val trash = Trash(
        _id = "0",
        _type = TrashType.BURN,
        schedules = listOf(
          IntervalWeeklySchedule(
            _dayOfWeek = DayOfWeek.MONDAY,
            _start = LocalDate.parse("2022-02-01"),
            _interval = 4
          )
        ),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
      )
      val result = TrashJsonDataMapper.toData(trash)
      assertEquals("0", result.id)
      assertEquals(TrashType.BURN, result.type)
      assertEquals(TrashType.BURN.getTrashText(), result.trashVal)
      assertEquals(1, result.schedules.size)
      assertEquals("evweek", result.schedules[0].type)
      assertEquals("1", (result.schedules[0].value as Map<*, *>)["weekday"])
      assertEquals("2022-02-01", (result.schedules[0].value as Map<*, *>)["start"])
      assertEquals(4, (result.schedules[0].value as Map<*, *>)["interval"])
      assertEquals(0, result.excludes.size)
    }

    @Test
    fun trashVal_is_equals_when_trashType_is_other() {
      val trash = Trash(
        _id = "0",
        _type = TrashType.OTHER,
        schedules = listOf(
          WeeklySchedule(_dayOfWeek = DayOfWeek.SUNDAY)
        ),
        _displayName = "家電",
        _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
      )
      val result = TrashJsonDataMapper.toData(trash)
      assertEquals("0", result.id)
      assertEquals(TrashType.OTHER, result.type)
      assertEquals("家電", result.trashVal)
      assertEquals(1, result.schedules.size)
      assertEquals("weekday", result.schedules[0].type)
      assertEquals("0", result.schedules[0].value)
      assertEquals(0, result.excludes.size)
    }

    @Test
    fun value_is_2_1_when_trash_has_2_of_excludeDayOfMonth() {
      val trash = Trash(
        _id = "0",
        _type = TrashType.BURN,
        schedules = listOf(
          WeeklySchedule(_dayOfWeek = DayOfWeek.SUNDAY)
        ),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf(
          ExcludeDayOfMonth(2, 1)
        ))
      )
      val result = TrashJsonDataMapper.toData(trash)
      assertEquals(1, result.excludes.size)
      assertEquals(2, result.excludes[0].month)
      assertEquals(1, result.excludes[0].date)
    }

    @Test
    fun isValid_when_trash_has_multiple_excludeDayOfMonth() {
      val trash = Trash(
        _id = "0",
        _type = TrashType.BURN,
        schedules = listOf(
          WeeklySchedule(_dayOfWeek = DayOfWeek.SUNDAY)
        ),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf(
          ExcludeDayOfMonth(2, 1),
          ExcludeDayOfMonth(3, 2),
          ExcludeDayOfMonth(4, 3)
        ))
      )
      val result = TrashJsonDataMapper.toData(trash)
      assertEquals(3, result.excludes.size)
      assertEquals(2, result.excludes[0].month)
      assertEquals(1, result.excludes[0].date)
      assertEquals(3, result.excludes[1].month)
      assertEquals(2, result.excludes[1].date)
      assertEquals(4, result.excludes[2].month)
      assertEquals(3, result.excludes[2].date)
    }

    @Test
    fun isValid_when_trash_has_multipleSchedule() {
      val trash = Trash(
        _id = "0",
        _type = TrashType.BURN,
        schedules = listOf(
          WeeklySchedule(_dayOfWeek = DayOfWeek.SUNDAY),
          MonthlySchedule(_day = 1),
          OrdinalWeeklySchedule(_dayOfWeek = DayOfWeek.SATURDAY, _ordinalOfWeek = 2),
        ),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf(
          ExcludeDayOfMonth(2, 1),
          ExcludeDayOfMonth(3, 2),
          ExcludeDayOfMonth(4, 3)
        ))
      )
      val result = TrashJsonDataMapper.toData(trash)
      assertEquals(3, result.schedules.size)
      assertEquals("weekday", result.schedules[0].type)
      assertEquals("0", result.schedules[0].value)
      assertEquals("month", result.schedules[1].type)
      assertEquals("1", result.schedules[1].value)
      assertEquals("biweek", result.schedules[2].type)
      assertEquals("6-2", result.schedules[2].value)
    }
  }

  @Nested
  inner class ToTrash {
    @Test
    fun dayOfWeek_is_sunday_when_type_is_weekday_and_value_is_0() {
      val trashJsonData = TrashJsonData(
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
      val result = TrashJsonDataMapper.toTrash(trashJsonData)
      assertEquals("0", result.id)
      assertEquals(TrashType.BURN, result.type)
      assertEquals(TrashType.BURN.getTrashText(), result.displayName)
      assertEquals(1, result.schedules.size)
      assertEquals(DayOfWeek.SUNDAY, (result.schedules[0] as WeeklySchedule).dayOfWeek)
      assertEquals(0, result.excludeDayOfMonth.members.size)
    }

    @Test
    fun dayOfWeek_is_monday_when_type_is_weekday_and_value_is_1() {
      val trashJsonData = TrashJsonData(
        _id = "0",
        _type = TrashType.BURN,
        _trashVal = "",
        _schedules = listOf(
          ScheduleJsonData(
            _type = "weekday",
            _value = "1"
          )
        ),
        _excludes = listOf()
      )
      val result = TrashJsonDataMapper.toTrash(trashJsonData)
      assertEquals(1, result.schedules.size)
      assertEquals(DayOfWeek.MONDAY, (result.schedules[0] as WeeklySchedule).dayOfWeek)
    }

    @Test
    fun day_is_1_when_type_is_month_and_value_is_1() {
      val trashJsonData = TrashJsonData(
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
      val result = TrashJsonDataMapper.toTrash(trashJsonData)
      assertEquals(1, result.schedules.size)
      assertEquals(1, (result.schedules[0] as MonthlySchedule).day)
    }

    @Test
    fun dayOfWeek_is_sunday_and_ordinal_is_3_when_type_is_biweek_and_value_is_0_3() {
      val trashJsonData = TrashJsonData(
        _id = "0",
        _type = TrashType.BURN,
        _trashVal = "",
        _schedules = listOf(
          ScheduleJsonData(
            _type = "biweek",
            _value = "0-3"
          )
        ),
        _excludes = listOf()
      )
      val result = TrashJsonDataMapper.toTrash(trashJsonData)
      assertEquals(1, result.schedules.size)
      assertEquals(DayOfWeek.SUNDAY, (result.schedules[0] as OrdinalWeeklySchedule).dayOfWeek)
      assertEquals(3, (result.schedules[0] as OrdinalWeeklySchedule).ordinalOfWeek)
    }

    @Test
    fun dayOfWeek_is_saturday_and_ordinal_is_2_when_type_is_biweek_and_value_is_6_2() {
      val trashJsonData = TrashJsonData(
        _id = "0",
        _type = TrashType.BURN,
        _trashVal = "",
        _schedules = listOf(
          ScheduleJsonData(
            _type = "biweek",
            _value = "6-2"
          )
        ),
        _excludes = listOf()
      )
      val result = TrashJsonDataMapper.toTrash(trashJsonData)
      assertEquals(1, result.schedules.size)
      assertEquals(DayOfWeek.SATURDAY, (result.schedules[0] as OrdinalWeeklySchedule).dayOfWeek)
      assertEquals(2, (result.schedules[0] as OrdinalWeeklySchedule).ordinalOfWeek)
    }

    @Test
    fun dayOfWeek_is_saturday_and_ordinal_is_5_when_type_is_biweek_and_value_is_map_of_6_and_5() {
      val trashJsonData = TrashJsonData(
        _id = "0",
        _type = TrashType.BURN,
        _trashVal = "",
        _schedules = listOf(
          ScheduleJsonData(
            _type = "biweek",
            _value = "6-5"
          )
        ),
        _excludes = listOf()
      )
      val result = TrashJsonDataMapper.toTrash(trashJsonData)
      assertEquals(1, result.schedules.size)
      assertEquals(DayOfWeek.SATURDAY, (result.schedules[0] as OrdinalWeeklySchedule).dayOfWeek)
      assertEquals(5, (result.schedules[0] as OrdinalWeeklySchedule).ordinalOfWeek)
    }

    @Test
    fun dayOfWeek_is_sunday_and_start_is_20220201_and_interval_is_2_when_type_is_evweek_and_value_is_map_of_0_and_20220201() {
      val trashJsonData = TrashJsonData(
        _id = "0",
        _type = TrashType.BURN,
        _trashVal = "",
        _schedules = listOf(
          ScheduleJsonData(
            _type = "evweek",
            _value = mapOf(
              "weekday" to "0",
              "start" to "2022-02-01",
              "interval" to 2
            )
          )
        ),
        _excludes = listOf()
      )
      val result = TrashJsonDataMapper.toTrash(trashJsonData)
      assertEquals(1, result.schedules.size)
      assertEquals(DayOfWeek.SUNDAY, (result.schedules[0] as IntervalWeeklySchedule).dayOfWeek)
      assertEquals(LocalDate.parse("2022-02-01"), (result.schedules[0] as IntervalWeeklySchedule).start)
      assertEquals(2, (result.schedules[0] as IntervalWeeklySchedule).interval)
    }

    @Test
    fun dayOfWeek_is_monday_and_start_is_20220201_and_interval_is_4_when_type_is_evweek_and_value_is_map_of_1_and_20220201() {
      val trashJsonData = TrashJsonData(
        _id = "0",
        _type = TrashType.BURN,
        _trashVal = "",
        _schedules = listOf(
          ScheduleJsonData(
            _type = "evweek",
            _value = mapOf(
              "weekday" to "1",
              "start" to "2022-02-01",
              "interval" to 4
            )
          )
        ),
        _excludes = listOf()
      )
      val result = TrashJsonDataMapper.toTrash(trashJsonData)
      assertEquals(1, result.schedules.size)
      assertEquals(DayOfWeek.MONDAY, (result.schedules[0] as IntervalWeeklySchedule).dayOfWeek)
      assertEquals(LocalDate.parse("2022-02-01"), (result.schedules[0] as IntervalWeeklySchedule).start)
      assertEquals(4, (result.schedules[0] as IntervalWeeklySchedule).interval)
    }

    @Test
    fun displayName_is_equals_when_trashType_is_other() {
      val trashJsonData = TrashJsonData(
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
      val result = TrashJsonDataMapper.toTrash(trashJsonData)
      assertEquals("家電", result.displayName)
      assertEquals(1, result.schedules.size)
    }

    @Test
    fun month_is_2_and_date_is_1_when_type_is_excludeDayOfMonth_and_value_is_2_1() {
      val trashJsonData = TrashJsonData(
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
            _month = 2,
            _date = 1
          )
        )
      )
      val result = TrashJsonDataMapper.toTrash(trashJsonData)
      assertEquals(1, result.excludeDayOfMonth.members.size)
      assertEquals(2, result.excludeDayOfMonth.members[0].month)
      assertEquals(1, result.excludeDayOfMonth.members[0].dayOfMonth)
    }

    @Test
    fun isValid_when_trash_has_multiple_excludeDayOfMonth() {
      val trashJsonData = TrashJsonData(
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
            _month = 2,
            _date = 1
          ),
          ExcludeDayOfMonthJsonData(
            _month = 3,
            _date = 2
          ),
          ExcludeDayOfMonthJsonData(
            _month = 4,
            _date = 3
          )
        )
      )
      val result = TrashJsonDataMapper.toTrash(trashJsonData)
      assertEquals(3, result.excludeDayOfMonth.members.size)
      assertEquals(2, result.excludeDayOfMonth.members[0].month)
      assertEquals(1, result.excludeDayOfMonth.members[0].dayOfMonth)
      assertEquals(3, result.excludeDayOfMonth.members[1].month)
      assertEquals(2, result.excludeDayOfMonth.members[1].dayOfMonth)
      assertEquals(4, result.excludeDayOfMonth.members[2].month)
      assertEquals(3, result.excludeDayOfMonth.members[2].dayOfMonth)
    }

    @Test
    fun isValid_when_trash_has_multipleSchedule() {
      val trashJsonData = TrashJsonData(
        _id = "0",
        _type = TrashType.BURN,
        _trashVal = "",
        _schedules = listOf(
          ScheduleJsonData(
            _type = "weekday",
            _value = "0"
          ),
          ScheduleJsonData(
            _type = "month",
            _value = "1"
          ),
          ScheduleJsonData(
            _type = "biweek",
            _value = "6-2"
          )
        ),
        _excludes = listOf(
          ExcludeDayOfMonthJsonData(
            _month = 2,
            _date = 1
          ),
          ExcludeDayOfMonthJsonData(
            _month = 3,
            _date = 2
          ),
          ExcludeDayOfMonthJsonData(
            _month = 4,
            _date = 3
          )
        )
      )
      val result = TrashJsonDataMapper.toTrash(trashJsonData)
      assertEquals(3, result.schedules.size)
      assertEquals(DayOfWeek.SUNDAY, (result.schedules[0] as WeeklySchedule).dayOfWeek)
      assertEquals(1, (result.schedules[1] as MonthlySchedule).day)
      assertEquals(DayOfWeek.SATURDAY, (result.schedules[2] as OrdinalWeeklySchedule).dayOfWeek)
      assertEquals(2, (result.schedules[2] as OrdinalWeeklySchedule).ordinalOfWeek)
    }
    @Test
    fun displayName_has_value_when_trashVal_is_empty() {
      val trashJsonData = TrashJsonData(
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
      val result = TrashJsonDataMapper.toTrash(trashJsonData)
      assertEquals("もえるゴミ", result.displayName)
    }

    @Test
    fun displayName_has_value_when_trashVal_is_null() {
      val trashJsonData = TrashJsonData(
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
      val result = TrashJsonDataMapper.toTrash(trashJsonData)
      assertEquals("もえるゴミ", result.displayName)
    }

    @Test
    fun excludeDayOfMonth_is_empty_when_excludes_is_null() {
      val trashJsonData = TrashJsonData(
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
      val result = TrashJsonDataMapper.toTrash(trashJsonData)
      assertEquals(0, result.excludeDayOfMonth.members.size)
    }
  }
}