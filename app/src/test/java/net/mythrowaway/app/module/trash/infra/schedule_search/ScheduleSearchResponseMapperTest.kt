package net.mythrowaway.app.module.trash.infra.schedule_search

import net.mythrowaway.app.module.trash.entity.trash.IntervalWeeklySchedule
import net.mythrowaway.app.module.trash.entity.trash.MonthlySchedule
import net.mythrowaway.app.module.trash.entity.trash.OrdinalWeeklySchedule
import net.mythrowaway.app.module.trash.entity.trash.TrashType
import net.mythrowaway.app.module.trash.entity.trash.WeeklySchedule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate

class ScheduleSearchResponseMapperTest {
  @Test
  fun map_supported_trash_types_and_schedules_to_trash_list() {
    val result = ScheduleSearchResponseMapper.toTrashList(
      ScheduleSearchResponse(
        trashes = listOf(
          ScheduleSearchTrashItem(
            type = "burn",
            trashName = null,
            schedule = listOf(
              ScheduleSearchScheduleItem(type = "weekday", value = "3"),
              ScheduleSearchScheduleItem(type = "biweek", value = "5-2"),
              ScheduleSearchScheduleItem(type = "month", value = 10),
            )
          ),
          ScheduleSearchTrashItem(
            type = "bottle",
            trashName = null,
            schedule = listOf(ScheduleSearchScheduleItem(type = "weekday", value = "1"))
          ),
          ScheduleSearchTrashItem(
            type = "bin",
            trashName = null,
            schedule = listOf(ScheduleSearchScheduleItem(type = "weekday", value = "2"))
          )
        )
      )
    )

    assertEquals(3, result.trashList.trashList.size)
    assertEquals(TrashType.BURN, result.trashList.trashList[0].type)
    assertEquals(WeeklySchedule::class.java, result.trashList.trashList[0].schedules[0].javaClass)
    assertEquals(DayOfWeek.WEDNESDAY, (result.trashList.trashList[0].schedules[0] as WeeklySchedule).dayOfWeek)
    assertEquals(OrdinalWeeklySchedule::class.java, result.trashList.trashList[0].schedules[1].javaClass)
    assertEquals(DayOfWeek.FRIDAY, (result.trashList.trashList[0].schedules[1] as OrdinalWeeklySchedule).dayOfWeek)
    assertEquals(2, (result.trashList.trashList[0].schedules[1] as OrdinalWeeklySchedule).ordinalOfWeek)
    assertEquals(MonthlySchedule::class.java, result.trashList.trashList[0].schedules[2].javaClass)
    assertEquals(10, (result.trashList.trashList[0].schedules[2] as MonthlySchedule).day)
    assertEquals(TrashType.PETBOTTLE, result.trashList.trashList[1].type)
    assertEquals(TrashType.BOTTLE, result.trashList.trashList[2].type)
    assertTrue(result.messages.isEmpty())
  }

  @Test
  fun map_other_trash_type_as_import_target() {
    val result = ScheduleSearchResponseMapper.toTrashList(
      ScheduleSearchResponse(
        trashes = listOf(
          ScheduleSearchTrashItem(
            type = "other",
            trashName = "乾電池",
            schedule = listOf(ScheduleSearchScheduleItem(type = "weekday", value = "4"))
          )
        )
      )
    )

    assertEquals(1, result.trashList.trashList.size)
    assertEquals(TrashType.OTHER, result.trashList.trashList[0].type)
    assertEquals("乾電池", result.trashList.trashList[0].displayName)
  }

  @Test
  fun skip_unmatched_schedule_and_return_message() {
    val result = ScheduleSearchResponseMapper.toTrashList(
      ScheduleSearchResponse(
        trashes = listOf(
          ScheduleSearchTrashItem(
            type = "other",
            trashName = "剪定枝",
            schedule = listOf(
              ScheduleSearchScheduleItem(type = "weekday", value = "6"),
              ScheduleSearchScheduleItem(type = "unmatched", value = "清掃事務所へ確認")
            )
          )
        )
      )
    )

    assertEquals(1, result.trashList.trashList.size)
    assertEquals(1, result.trashList.trashList[0].schedules.size)
    assertEquals(listOf("剪定枝: 清掃事務所へ確認"), result.messages)
  }

  @Test
  fun map_evweek_when_interval_is_supported() {
    val result = ScheduleSearchResponseMapper.toTrashList(
      ScheduleSearchResponse(
        trashes = listOf(
          ScheduleSearchTrashItem(
            type = "plastic",
            trashName = null,
            schedule = listOf(
              ScheduleSearchScheduleItem(
                type = "evweek",
                value = EvweekScheduleValue(
                  weekday = 2,
                  interval = 4,
                  startDate = "2026-05-05"
                )
              )
            )
          )
        )
      )
    )

    assertEquals(IntervalWeeklySchedule::class.java, result.trashList.trashList[0].schedules[0].javaClass)
    assertEquals(DayOfWeek.TUESDAY, (result.trashList.trashList[0].schedules[0] as IntervalWeeklySchedule).dayOfWeek)
    assertEquals(4, (result.trashList.trashList[0].schedules[0] as IntervalWeeklySchedule).interval)
    assertEquals(LocalDate.of(2026, 5, 5), (result.trashList.trashList[0].schedules[0] as IntervalWeeklySchedule).start)
  }

  @Test
  fun skip_evweek_when_interval_is_not_supported_and_return_message() {
    val result = ScheduleSearchResponseMapper.toTrashList(
      ScheduleSearchResponse(
        trashes = listOf(
          ScheduleSearchTrashItem(
            type = "plastic",
            trashName = null,
            schedule = listOf(
              ScheduleSearchScheduleItem(
                type = "evweek",
                value = EvweekScheduleValue(
                  weekday = 2,
                  interval = 5,
                  startDate = "2026-05-05"
                )
              )
            )
          )
        )
      )
    )

    assertTrue(result.trashList.trashList.isEmpty())
    assertEquals(listOf("プラスチック: 5週間ごとの火曜日"), result.messages)
  }
}
