package net.mythrowaway.app.module.trash.dto

import net.mythrowaway.app.module.trash.dto.MonthCalendarDTO
import net.mythrowaway.app.module.trash.dto.CalendarDayDTO
import org.junit.jupiter.api.Test
import java.time.DayOfWeek

class MonthCalendarDTOTest {
  @Test
  fun month_of_2024_06_start_with_05_26_and_end_with_06_29() {
    val expectedCalendarList = listOf(
      CalendarDayDTO(2024,5,26, DayOfWeek.SUNDAY, listOf()),
      CalendarDayDTO(2024,5,27, DayOfWeek.MONDAY, listOf()),
      CalendarDayDTO(2024,5,28, DayOfWeek.TUESDAY, listOf()),
      CalendarDayDTO(2024,5,29, DayOfWeek.WEDNESDAY, listOf()),
      CalendarDayDTO(2024,5,30, DayOfWeek.THURSDAY, listOf()),
      CalendarDayDTO(2024,5,31, DayOfWeek.FRIDAY, listOf()),
      CalendarDayDTO(2024,6,1, DayOfWeek.SATURDAY, listOf()),
      CalendarDayDTO(2024,6,2, DayOfWeek.SUNDAY, listOf()),
      CalendarDayDTO(2024,6,3, DayOfWeek.MONDAY, listOf()),
      CalendarDayDTO(2024,6,4, DayOfWeek.TUESDAY, listOf()),
      CalendarDayDTO(2024,6,5, DayOfWeek.WEDNESDAY, listOf()),
      CalendarDayDTO(2024,6,6, DayOfWeek.THURSDAY,listOf()),
      CalendarDayDTO(2024,6,7, DayOfWeek.FRIDAY,listOf()),
      CalendarDayDTO(2024,6,8, DayOfWeek.SATURDAY,listOf()),
      CalendarDayDTO(2024,6,9, DayOfWeek.SUNDAY,listOf()),
      CalendarDayDTO(2024,6,10, DayOfWeek.MONDAY,listOf()),
      CalendarDayDTO(2024,6,11, DayOfWeek.TUESDAY,listOf()),
      CalendarDayDTO(2024,6,12, DayOfWeek.WEDNESDAY,listOf()),
      CalendarDayDTO(2024,6,13, DayOfWeek.THURSDAY,listOf()),
      CalendarDayDTO(2024,6,14, DayOfWeek.FRIDAY,listOf()),
      CalendarDayDTO(2024,6,15, DayOfWeek.SATURDAY,listOf()),
      CalendarDayDTO(2024,6,16, DayOfWeek.SUNDAY,listOf()),
      CalendarDayDTO(2024,6,17, DayOfWeek.MONDAY,listOf()),
      CalendarDayDTO(2024,6,18, DayOfWeek.TUESDAY,listOf()),
      CalendarDayDTO(2024,6,19, DayOfWeek.WEDNESDAY,listOf()),
      CalendarDayDTO(2024,6,20, DayOfWeek.THURSDAY,listOf()),
      CalendarDayDTO(2024,6,21, DayOfWeek.FRIDAY,listOf()),
      CalendarDayDTO(2024,6,22, DayOfWeek.SATURDAY,listOf()),
      CalendarDayDTO(2024,6,23, DayOfWeek.SUNDAY,listOf()),
      CalendarDayDTO(2024,6,24, DayOfWeek.MONDAY,listOf()),
      CalendarDayDTO(2024,6,25, DayOfWeek.TUESDAY,listOf()),
      CalendarDayDTO(2024,6,26, DayOfWeek.WEDNESDAY,listOf()),
      CalendarDayDTO(2024,6,27, DayOfWeek.THURSDAY,listOf()),
      CalendarDayDTO(2024,6,28, DayOfWeek.FRIDAY,listOf()),
      CalendarDayDTO(2024,6,29, DayOfWeek.SATURDAY,listOf()),
    )

    val fiveWeeksCalendarDTO = MonthCalendarDTO(2024, 6, listOf())

//    fiveWeeksCalendar.getCalendarDayList().forEachIndexed { index, calendarDay ->
//      Assertions.assertEquals(expectedCalendarList[index].getYear(), calendarDay.getYear())
//      Assertions.assertEquals(expectedCalendarList[index].getMonth(), calendarDay.getMonth())
//      Assertions.assertEquals(expectedCalendarList[index].getDayOfMonth(), calendarDay.getDayOfMonth())
//      Assertions.assertEquals(expectedCalendarList[index].getDayOfWeek(), calendarDay.getDayOfWeek())
//    }
  }

  @Test
  fun month_of_2024_07_start_with_06_30_and_end_with_08_03() {
    val expectedCalendarDayDTOLists = listOf(
      CalendarDayDTO(2024,6,30, DayOfWeek.SUNDAY,listOf()),
      CalendarDayDTO(2024,7,1, DayOfWeek.MONDAY,listOf()),
      CalendarDayDTO(2024,7,2, DayOfWeek.TUESDAY,listOf()),
      CalendarDayDTO(2024,7,3, DayOfWeek.WEDNESDAY,listOf()),
      CalendarDayDTO(2024,7,4, DayOfWeek.THURSDAY,listOf()),
      CalendarDayDTO(2024,7,5, DayOfWeek.FRIDAY,listOf()),
      CalendarDayDTO(2024,7,6, DayOfWeek.SATURDAY,listOf()),
      CalendarDayDTO(2024,7,7, DayOfWeek.SUNDAY,listOf()),
      CalendarDayDTO(2024,7,8, DayOfWeek.MONDAY,listOf()),
      CalendarDayDTO(2024,7,9, DayOfWeek.TUESDAY,listOf()),
      CalendarDayDTO(2024,7,10, DayOfWeek.WEDNESDAY,listOf()),
      CalendarDayDTO(2024,7,11, DayOfWeek.THURSDAY,listOf()),
      CalendarDayDTO(2024,7,12, DayOfWeek.FRIDAY,listOf()),
      CalendarDayDTO(2024,7,13, DayOfWeek.SATURDAY,listOf()),
      CalendarDayDTO(2024,7,14, DayOfWeek.SUNDAY,listOf()),
      CalendarDayDTO(2024,7,15, DayOfWeek.MONDAY,listOf()),
      CalendarDayDTO(2024,7,16, DayOfWeek.TUESDAY,listOf()),
      CalendarDayDTO(2024,7,17, DayOfWeek.WEDNESDAY,listOf()),
      CalendarDayDTO(2024,7,18, DayOfWeek.THURSDAY,listOf()),
      CalendarDayDTO(2024,7,19, DayOfWeek.FRIDAY,listOf()),
      CalendarDayDTO(2024,7,20, DayOfWeek.SATURDAY,listOf()),
      CalendarDayDTO(2024,7,21, DayOfWeek.SUNDAY,listOf()),
      CalendarDayDTO(2024,7,22, DayOfWeek.MONDAY,listOf()),
      CalendarDayDTO(2024,7,23, DayOfWeek.TUESDAY,listOf()),
      CalendarDayDTO(2024,7,24, DayOfWeek.WEDNESDAY,listOf()),
      CalendarDayDTO(2024,7,25, DayOfWeek.THURSDAY,listOf()),
      CalendarDayDTO(2024,7,26, DayOfWeek.FRIDAY,listOf()),
      CalendarDayDTO(2024,7,27, DayOfWeek.SATURDAY,listOf()),
      CalendarDayDTO(2024,7, 28, DayOfWeek.SUNDAY,listOf()),
      CalendarDayDTO(2024,7, 29, DayOfWeek.MONDAY,listOf()),
      CalendarDayDTO(2024,7, 30, DayOfWeek.TUESDAY,listOf()),
      CalendarDayDTO(2024,7, 31, DayOfWeek.WEDNESDAY,listOf()),
      CalendarDayDTO(2024,8, 1, DayOfWeek.THURSDAY,listOf()),
      CalendarDayDTO(2024,8, 2, DayOfWeek.FRIDAY,listOf()),
      CalendarDayDTO(2024,8, 3, DayOfWeek.SATURDAY,listOf()),
    )

    val fiveWeeksCalendarDTO = MonthCalendarDTO(2024, 7,listOf())

//    fiveWeeksCalendar.getCalendarDayList().forEachIndexed { index, calendarDay ->
//      Assertions.assertEquals(expectedCalendarDayList[index].getYear(), calendarDay.getYear())
//      Assertions.assertEquals(expectedCalendarDayList[index].getMonth(), calendarDay.getMonth())
//      Assertions.assertEquals(expectedCalendarDayList[index].getDayOfMonth(), calendarDay.getDayOfMonth())
//      Assertions.assertEquals(expectedCalendarDayList[index].getDayOfWeek(), calendarDay.getDayOfWeek())
//    }
  }

  @Test
  fun month_of_2024_09_start_with_09_01_and_end_with_10_05() {
    val expectedCalendarDayDTOLists = listOf(
      CalendarDayDTO(2024,9,1, DayOfWeek.SUNDAY,listOf()),
      CalendarDayDTO(2024,9,2, DayOfWeek.MONDAY,listOf()),
      CalendarDayDTO(2024,9,3, DayOfWeek.TUESDAY,listOf()),
      CalendarDayDTO(2024,9,4, DayOfWeek.WEDNESDAY,listOf()),
      CalendarDayDTO(2024,9,5, DayOfWeek.THURSDAY,listOf()),
      CalendarDayDTO(2024,9,6, DayOfWeek.FRIDAY,listOf()),
      CalendarDayDTO(2024,9,7, DayOfWeek.SATURDAY,listOf()),
      CalendarDayDTO(2024,9,8, DayOfWeek.SUNDAY,listOf()),
      CalendarDayDTO(2024,9,9, DayOfWeek.MONDAY,listOf()),
      CalendarDayDTO(2024,9,10, DayOfWeek.TUESDAY,listOf()),
      CalendarDayDTO(2024,9,11, DayOfWeek.WEDNESDAY,listOf()),
      CalendarDayDTO(2024,9,12, DayOfWeek.THURSDAY,listOf()),
      CalendarDayDTO(2024,9,13, DayOfWeek.FRIDAY,listOf()),
      CalendarDayDTO(2024,9,14, DayOfWeek.SATURDAY,listOf()),
      CalendarDayDTO(2024,9,15, DayOfWeek.SUNDAY,listOf()),
      CalendarDayDTO(2024,9,16, DayOfWeek.MONDAY,listOf()),
      CalendarDayDTO(2024,9,17, DayOfWeek.TUESDAY,listOf()),
      CalendarDayDTO(2024,9,18, DayOfWeek.WEDNESDAY,listOf()),
      CalendarDayDTO(2024,9,19, DayOfWeek.THURSDAY,listOf()),
      CalendarDayDTO(2024,9,20, DayOfWeek.FRIDAY,listOf()),
      CalendarDayDTO(2024,9,21, DayOfWeek.SATURDAY,listOf()),
      CalendarDayDTO(2024,9,22, DayOfWeek.SUNDAY,listOf()),
      CalendarDayDTO(2024,9,23, DayOfWeek.MONDAY,listOf()),
      CalendarDayDTO(2024,9,24, DayOfWeek.TUESDAY,listOf()),
      CalendarDayDTO(2024,9,25, DayOfWeek.WEDNESDAY,listOf()),
      CalendarDayDTO(2024,9,26, DayOfWeek.THURSDAY,listOf()),
      CalendarDayDTO(2024,9,27, DayOfWeek.FRIDAY,listOf()),
      CalendarDayDTO(2024,9,28, DayOfWeek.SATURDAY,listOf()),
      CalendarDayDTO(2024,9,29, DayOfWeek.SUNDAY,listOf()),
      CalendarDayDTO(2024,9,30, DayOfWeek.MONDAY,listOf()),
      CalendarDayDTO(2024,10,1, DayOfWeek.TUESDAY,listOf()),
      CalendarDayDTO(2024,10,2, DayOfWeek.WEDNESDAY,listOf()),
      CalendarDayDTO(2024,10,3, DayOfWeek.THURSDAY,listOf()),
      CalendarDayDTO(2024,10,4, DayOfWeek.FRIDAY,listOf()),
      CalendarDayDTO(2024,10,5, DayOfWeek.SATURDAY,listOf()),
    )

    val fiveWeeksCalendarDTO = MonthCalendarDTO(2024, 9,listOf())

//    fiveWeeksCalendar.getCalendarDayList().forEachIndexed { index, calendarDay ->
//      Assertions.assertEquals(expectedCalendarDayList[index].getYear(), calendarDay.getYear())
//      Assertions.assertEquals(expectedCalendarDayList[index].getMonth(), calendarDay.getMonth())
//      Assertions.assertEquals(expectedCalendarDayList[index].getDayOfMonth(), calendarDay.getDayOfMonth())
//      Assertions.assertEquals(expectedCalendarDayList[index].getDayOfWeek(), calendarDay.getDayOfWeek())
//    }
  }

}