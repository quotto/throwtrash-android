package net.mythrowaway.app.usecase.dto

import net.mythrowaway.app.domain.trash.entity.trash.IntervalWeeklySchedule
import net.mythrowaway.app.domain.trash.entity.trash.MonthlySchedule
import net.mythrowaway.app.domain.trash.entity.trash.OrdinalWeeklySchedule
import net.mythrowaway.app.domain.trash.entity.trash.WeeklySchedule
import net.mythrowaway.app.domain.trash.dto.IntervalWeeklyScheduleDTO
import net.mythrowaway.app.domain.trash.dto.MonthlyScheduleDTO
import net.mythrowaway.app.domain.trash.dto.OrdinalWeeklyScheduleDTO
import net.mythrowaway.app.domain.trash.dto.WeeklyScheduleDTO
import net.mythrowaway.app.domain.trash.dto.mapper.ScheduleMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate

class ScheduleMapperTest {
  @Nested
  inner class ToScheduleDTODTO {
    @Test
    fun dayOfWeek_Sunday_to_0() {
      val result: WeeklyScheduleDTO = ScheduleMapper.toDTO(WeeklySchedule(DayOfWeek.SUNDAY)) as WeeklyScheduleDTO

      Assertions.assertEquals(0, result.dayOfWeek)
    }

    @Test
    fun dayOfWeek_Saturday_to_6() {
      val result: WeeklyScheduleDTO = ScheduleMapper.toDTO(WeeklySchedule(DayOfWeek.SATURDAY)) as WeeklyScheduleDTO

      Assertions.assertEquals(6, result.dayOfWeek)
    }

    @Test
    fun dayOfMonth_1_to_0() {
      val result: MonthlyScheduleDTO = ScheduleMapper.toDTO(MonthlySchedule(1)) as MonthlyScheduleDTO

      Assertions.assertEquals(0, result.dayOfMonth)
    }

    @Test
    fun dayOfMonth_31_to_30() {
      val result: MonthlyScheduleDTO = ScheduleMapper.toDTO(MonthlySchedule(31)) as MonthlyScheduleDTO

      Assertions.assertEquals(30, result.dayOfMonth)
    }

    @Test
    fun order_1_and_dayOfWeek_Sunday_to_0_and_0() {
      val result: OrdinalWeeklyScheduleDTO = ScheduleMapper.toDTO(OrdinalWeeklySchedule(1, DayOfWeek.SUNDAY)) as OrdinalWeeklyScheduleDTO

      Assertions.assertEquals(0, result.ordinal)
      Assertions.assertEquals(0, result.dayOfWeek)
    }

    @Test
    fun order_5_and_dayOfWeek_Saturday_to_4_and_6() {
      val result: OrdinalWeeklyScheduleDTO = ScheduleMapper.toDTO(OrdinalWeeklySchedule(5, DayOfWeek.SATURDAY)) as OrdinalWeeklyScheduleDTO

      Assertions.assertEquals(4, result.ordinal)
      Assertions.assertEquals(6, result.dayOfWeek)
    }

    @Test
    fun interval_2_and_dayOfWeek_Sunday_to_0_and_0() {
      val result: IntervalWeeklyScheduleDTO = ScheduleMapper.toDTO(IntervalWeeklySchedule(LocalDate.parse("2024-06-30"), DayOfWeek.SUNDAY, 2)) as IntervalWeeklyScheduleDTO

      Assertions.assertEquals("2024-06-30", result.startDate)
      Assertions.assertEquals(0, result.dayOfWeek)
      Assertions.assertEquals(0, result.interval)
    }

    @Test
    fun interval_4_and_dayOfWeek_Saturday_to_2_and_6() {
      val result: IntervalWeeklyScheduleDTO = ScheduleMapper.toDTO(IntervalWeeklySchedule(LocalDate.parse("2024-06-30"), DayOfWeek.SATURDAY, 4)) as IntervalWeeklyScheduleDTO

      Assertions.assertEquals("2024-06-30", result.startDate)
      Assertions.assertEquals(6, result.dayOfWeek)
      Assertions.assertEquals(2, result.interval)
    }
  }

  @Nested
  inner class ToSchedule {
    @Test
    fun dayOfWeek_0_to_Sunday() {
      val result: WeeklySchedule = ScheduleMapper.toSchedule(WeeklyScheduleDTO(0)) as WeeklySchedule

      Assertions.assertEquals(DayOfWeek.SUNDAY, result.dayOfWeek)
    }

    @Test
    fun dayOfWeek_6_to_Saturday() {
      val result: WeeklySchedule = ScheduleMapper.toSchedule(WeeklyScheduleDTO(6)) as WeeklySchedule

      Assertions.assertEquals(DayOfWeek.SATURDAY, result.dayOfWeek)
    }

    @Test
    fun dayOfMonth_0_to_1() {
      val result: MonthlySchedule =
        ScheduleMapper.toSchedule(MonthlyScheduleDTO(0)) as MonthlySchedule

      Assertions.assertEquals(1, result.day)
    }

    @Test
    fun dayOfMonth_30_to_31() {
      val result: MonthlySchedule =
        ScheduleMapper.toSchedule(MonthlyScheduleDTO(30)) as MonthlySchedule

      Assertions.assertEquals(31, result.day)
    }

    @Test
    fun order_0_and_dayOfWeek_0_to_1_and_Sunday() {
      val result: OrdinalWeeklySchedule =
        ScheduleMapper.toSchedule(OrdinalWeeklyScheduleDTO(0, 0)) as OrdinalWeeklySchedule

      Assertions.assertEquals(1, result.ordinalOfWeek)
      Assertions.assertEquals(DayOfWeek.SUNDAY, result.dayOfWeek)
    }

    @Test
    fun order_4_and_dayOfWeek_6_to_5_and_Saturday() {
      val result: OrdinalWeeklySchedule =
        ScheduleMapper.toSchedule(OrdinalWeeklyScheduleDTO(4, 6)) as OrdinalWeeklySchedule

      Assertions.assertEquals(5, result.ordinalOfWeek)
      Assertions.assertEquals(DayOfWeek.SATURDAY, result.dayOfWeek)
    }

    @Test
    fun interval_0_and_dayOfWeek_0_to_2_and_Sunday() {
      val result: IntervalWeeklySchedule = ScheduleMapper.toSchedule(
        IntervalWeeklyScheduleDTO(
          LocalDate.parse("2024-06-30"),
          0,
          0
        )
      ) as IntervalWeeklySchedule

      Assertions.assertEquals(LocalDate.parse("2024-06-30"), result.start)
      Assertions.assertEquals(DayOfWeek.SUNDAY, result.dayOfWeek)
      Assertions.assertEquals(2, result.interval)
    }

    @Test
    fun interval_2_and_dayOfWeek_6_to_4_and_Saturday() {
      val result: IntervalWeeklySchedule = ScheduleMapper.toSchedule(
        IntervalWeeklyScheduleDTO(
          LocalDate.parse("2024-06-30"),
          6,
          2
        )
      ) as IntervalWeeklySchedule

      Assertions.assertEquals(LocalDate.parse("2024-06-30"), result.start)
      Assertions.assertEquals(DayOfWeek.SATURDAY, result.dayOfWeek)
      Assertions.assertEquals(4, result.interval)
    }
  }
}