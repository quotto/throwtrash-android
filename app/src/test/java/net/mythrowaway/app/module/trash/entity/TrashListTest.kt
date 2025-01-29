package net.mythrowaway.app.module.trash.entity

import net.mythrowaway.app.module.trash.entity.trash.ExcludeDayOfMonthList
import net.mythrowaway.app.module.trash.entity.trash.Trash
import net.mythrowaway.app.module.trash.entity.trash.TrashList
import net.mythrowaway.app.module.trash.entity.trash.TrashType
import net.mythrowaway.app.module.trash.entity.trash.WeeklySchedule
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.DayOfWeek

class TrashListTest {

  @Nested
  inner class ConstructorTest {
    @Test
    fun if_trashList_is_empty_then_create_instance() {
      val trashList = TrashList(mutableListOf())

      Assertions.assertEquals(0, trashList.trashList.size)
    }

    @Test
    fun if_trashList_has_1_trash_then_create_instance() {
      val trashList = TrashList(mutableListOf(
        Trash(
          "1",
          TrashType.BURN,
          "",
          listOf(WeeklySchedule(DayOfWeek.SUNDAY)),
          ExcludeDayOfMonthList(mutableListOf())
      )
      ))

      Assertions.assertEquals(1, trashList.trashList.size)
    }

    @Test
    fun if_trashList_has_10_trash_then_create_instance() {
      val trashList = TrashList(mutableListOf(
        Trash("1", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("2", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("3", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("4", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("5", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("6", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("7", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("8", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("9", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("10", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf()))
      ))

      Assertions.assertEquals(10, trashList.trashList.size)
    }

    @Test
    fun if_trashList_has_11_trash_then_throw_exception() {
      Assertions.assertThrows(IllegalArgumentException::class.java) {
        TrashList(
          mutableListOf(
            Trash("1", TrashType.BURN, "", listOf(),  ExcludeDayOfMonthList(mutableListOf())),
            Trash("2", TrashType.BURN, "", listOf(),  ExcludeDayOfMonthList(mutableListOf())),
            Trash("3", TrashType.BURN, "", listOf(),  ExcludeDayOfMonthList(mutableListOf())),
            Trash("4", TrashType.BURN, "", listOf(),  ExcludeDayOfMonthList(mutableListOf())),
            Trash("5", TrashType.BURN, "", listOf(),  ExcludeDayOfMonthList(mutableListOf())),
            Trash("6", TrashType.BURN, "", listOf(),  ExcludeDayOfMonthList(mutableListOf())),
            Trash("7", TrashType.BURN, "", listOf(),  ExcludeDayOfMonthList(mutableListOf())),
            Trash("8", TrashType.BURN, "", listOf(),  ExcludeDayOfMonthList(mutableListOf())),
            Trash("9", TrashType.BURN, "", listOf(),  ExcludeDayOfMonthList(mutableListOf())),
            Trash("10", TrashType.BURN, "", listOf(), ExcludeDayOfMonthList(mutableListOf())),
            Trash("11", TrashType.BURN, "", listOf(), ExcludeDayOfMonthList(mutableListOf()))
          )
        )
      }
    }

    @Test
    fun if_trashList_has_duplicate_trash_then_throw_exception() {
      Assertions.assertThrows(IllegalArgumentException::class.java) {
        TrashList(
          mutableListOf(
            Trash("1", TrashType.BURN, "", listOf(), ExcludeDayOfMonthList(mutableListOf())),
            Trash("1", TrashType.BURN, "", listOf(), ExcludeDayOfMonthList(mutableListOf()))
          )
        )
      }
    }
  }

  @Nested
  inner class AddTrashTest {
    @Test
    fun if_trashList_is_empty_then_add_trash() {
      val trashList = TrashList(mutableListOf())

      trashList.addTrash(
        Trash("1", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf()))
      )

      Assertions.assertEquals(1, trashList.trashList.size)
    }

    @Test
    fun if_trashList_has_1_trash_then_add_trash() {
      val trashList = TrashList(mutableListOf(Trash("1", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf()))))

      trashList.addTrash(Trash("2", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())))

      Assertions.assertEquals(2, trashList.trashList.size)
    }

    @Test
    fun if_trashList_has_9_trash_then_add_trash() {
      val trashList = TrashList(mutableListOf(
        Trash("1", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("2", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("3", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("4", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("5", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("6", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("7", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("8", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("9", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf()))
      ))

      trashList.addTrash(Trash("10", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())))

      Assertions.assertEquals(10, trashList.trashList.size)
    }

    @Test
    fun if_trashList_has_10_trash_then_throw_exception() {
      val trashList = TrashList(mutableListOf(
        Trash("1", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("2", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("3", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("4", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("5", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("6", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("7", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("8", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("9", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("10", TrashType.BURN, "",listOf(WeeklySchedule(DayOfWeek.SUNDAY)),ExcludeDayOfMonthList(mutableListOf()))
      ))

      Assertions.assertThrows(IllegalArgumentException::class.java) {
        trashList.addTrash(Trash("11", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())))
      }
    }
  }

  @Nested
  inner class RemoveTrashTest {
    @Test
    fun if_trashList_has_1_trash_and_removedTrash_exists_then_remove_trash() {
      val trashList = TrashList(mutableListOf(
        Trash("1", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(
        mutableListOf())
      )
      ))

      trashList.removeTrash(
        Trash("1", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(
        mutableListOf())
        )
      )

      Assertions.assertEquals(0, trashList.trashList.size)
    }

    @Test
    fun if_trashList_has_10_trash_and_removedTrash_exists_then_remove_trash() {
      val trashList = TrashList(mutableListOf(
        Trash("1", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("2", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("3", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("4", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("5", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("6", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("7", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("8", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("9", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf())),
        Trash("10", TrashType.BURN, "",listOf(WeeklySchedule(DayOfWeek.SUNDAY)),ExcludeDayOfMonthList(mutableListOf()))
      ))

      trashList.removeTrash(
        Trash("10", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(
        mutableListOf())
        )
      )

      Assertions.assertEquals(9, trashList.trashList.size)
    }
    @Test
    fun if_removedTrash_does_not_exist_then_throw_exception() {
      val trashList = TrashList(mutableListOf(Trash("1", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(mutableListOf()))))

      Assertions.assertThrows(IllegalArgumentException::class.java) {
        trashList.removeTrash(
          Trash("2", TrashType.BURN, "", listOf(WeeklySchedule(DayOfWeek.SUNDAY)), ExcludeDayOfMonthList(
          mutableListOf())
          )
        )
      }
    }
  }
}