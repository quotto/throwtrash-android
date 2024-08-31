package net.mythrowaway.app.domain

import net.mythrowaway.app.domain.trash.entity.ExcludeDayOfMonth
import net.mythrowaway.app.domain.trash.entity.ExcludeDayOfMonthList
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ExcludeDayOfMonthListTest {
  @Nested
  inner class AddTest {
    @Test
    fun add_when_members_size_is_less_than_10() {
      val listOfExcludeDayOfMonth = mutableListOf<ExcludeDayOfMonth>()
      repeat(9) {
        listOfExcludeDayOfMonth.add(ExcludeDayOfMonth(1, 1))
      }
      val excludeDayOfMonthList = ExcludeDayOfMonthList(listOfExcludeDayOfMonth)
      val excludeDayOfMonth = ExcludeDayOfMonth(1, 1)

      excludeDayOfMonthList.add(excludeDayOfMonth)

      Assertions.assertEquals(10, excludeDayOfMonthList.members.size)
    }

    @Test
    fun throw_exception_when_members_size_is_10() {
      val listOfExcludeDayOfMonth = mutableListOf<ExcludeDayOfMonth>()
      repeat(10) {
        listOfExcludeDayOfMonth.add(ExcludeDayOfMonth(1, 1))
      }
      val excludeDayOfMonthList = ExcludeDayOfMonthList(listOfExcludeDayOfMonth)
      val excludeDayOfMonth = ExcludeDayOfMonth(1, 1)

      Assertions.assertThrows(IllegalArgumentException::class.java) {
        excludeDayOfMonthList.add(excludeDayOfMonth)
      }
    }
  }

  @Nested
  inner class RemoveTest {
    @Test
    fun remove_when_members_is_not_empty() {
      val excludeDayOfMonth = ExcludeDayOfMonth(1, 1)
      val listOfExcludeDayOfMonth = mutableListOf(excludeDayOfMonth)
      val excludeDayOfMonthList = ExcludeDayOfMonthList(listOfExcludeDayOfMonth)

      excludeDayOfMonthList.removeAt(0)

      Assertions.assertTrue(excludeDayOfMonthList.members.isEmpty())
    }

    @Test
    fun throw_exception_when_members_is_empty() {
      val excludeDayOfMonth = ExcludeDayOfMonth(1, 1)
      val listOfExcludeDayOfMonth = mutableListOf<ExcludeDayOfMonth>()
      val excludeDayOfMonthList = ExcludeDayOfMonthList(listOfExcludeDayOfMonth)

      Assertions.assertThrows(IllegalArgumentException::class.java) {
        excludeDayOfMonthList.removeAt(0)
      }
    }
  }

  @Nested
  inner class CanAddTest {
    @Test
    fun return_true_when_members_size_is_less_than_10() {
      val listOfExcludeDayOfMonth = mutableListOf<ExcludeDayOfMonth>()
      repeat(9) {
        listOfExcludeDayOfMonth.add(ExcludeDayOfMonth(1, 1))
      }
      val excludeDayOfMonthList = ExcludeDayOfMonthList(listOfExcludeDayOfMonth)

      val result = excludeDayOfMonthList.canAdd()

      Assertions.assertTrue(result)
    }

    @Test
    fun return_false_when_members_size_is_10() {
      val listOfExcludeDayOfMonth = mutableListOf<ExcludeDayOfMonth>()
      repeat(10) {
        listOfExcludeDayOfMonth.add(ExcludeDayOfMonth(1, 1))
      }
      val excludeDayOfMonthList = ExcludeDayOfMonthList(listOfExcludeDayOfMonth)

      val result = excludeDayOfMonthList.canAdd()

      Assertions.assertFalse(result)
    }
  }

  @Nested
  inner class CanRemoveTest {
    @Test
    fun return_true_when_members_is_not_empty() {
      val excludeDayOfMonth = ExcludeDayOfMonth(1, 1)
      val listOfExcludeDayOfMonth = mutableListOf(excludeDayOfMonth)
      val excludeDayOfMonthList = ExcludeDayOfMonthList(listOfExcludeDayOfMonth)

      val result = excludeDayOfMonthList.canRemove()

      Assertions.assertTrue(result)
    }

    @Test
    fun return_false_when_members_is_empty() {
      val listOfExcludeDayOfMonth = mutableListOf<ExcludeDayOfMonth>()
      val excludeDayOfMonthList = ExcludeDayOfMonthList(listOfExcludeDayOfMonth)

      val result = excludeDayOfMonthList.canRemove()

      Assertions.assertFalse(result)
    }
  }

  @Nested
  inner class InitTest {

    @Test
    fun init_when_members_size_is_less_equality_than_10() {
      val listOfExcludeDayOfMonth = mutableListOf<ExcludeDayOfMonth>()
      repeat(10) {
        listOfExcludeDayOfMonth.add(ExcludeDayOfMonth(1, 1))
      }

      val excludeDayOfMonthList = ExcludeDayOfMonthList(listOfExcludeDayOfMonth)

      Assertions.assertEquals(10, excludeDayOfMonthList.members.size)
    }
    @Test
    fun throw_exception_when_members_size_is_11() {
      val listOfExcludeDayOfMonth = mutableListOf<ExcludeDayOfMonth>()
      repeat(11) {
        listOfExcludeDayOfMonth.add(ExcludeDayOfMonth(1, 1))
      }

      Assertions.assertThrows(IllegalArgumentException::class.java) {
        ExcludeDayOfMonthList(listOfExcludeDayOfMonth)
      }
    }
  }

  @Nested
  inner class IsExcludedTest {
    @Test
    fun return_true_when_members_one_of_one_include_excludeDayOfMonth() {
      val excludeDayOfMonth = ExcludeDayOfMonth(1, 1)
      val listOfExcludeDayOfMonth = mutableListOf(excludeDayOfMonth)
      val excludeDayOfMonthList = ExcludeDayOfMonthList(listOfExcludeDayOfMonth)

      val result = excludeDayOfMonthList.isExcluded(LocalDate.of(2021, 1, 1))

      Assertions.assertTrue(result)
    }

    @Test
    fun return_true_when_members_one_of_10_include_excludeDayOfMonth() {
      val listOfExcludeDayOfMonth: MutableList<ExcludeDayOfMonth> = arrayListOf()
      repeat(9) {
        listOfExcludeDayOfMonth.add(ExcludeDayOfMonth(it + 1, 1))
      }
      listOfExcludeDayOfMonth.add(ExcludeDayOfMonth(1, 1))
      val excludeDayOfMonthList = ExcludeDayOfMonthList(listOfExcludeDayOfMonth)

      val result = excludeDayOfMonthList.isExcluded(LocalDate.of(2021, 1, 1))

      Assertions.assertTrue(result)
    }

    @Test
    fun return_false_when_members_one_of_one_not_include_excludeDayOfMonth() {
      val excludeDayOfMonth = ExcludeDayOfMonth(1, 1)
      val listOfExcludeDayOfMonth = mutableListOf(excludeDayOfMonth)
      val excludeDayOfMonthList = ExcludeDayOfMonthList(listOfExcludeDayOfMonth)

      val result = excludeDayOfMonthList.isExcluded(LocalDate.of(2021, 1, 2))

      Assertions.assertFalse(result)
    }

    @Test
    fun return_false_when_members_10_of_10_not_include_excludeDayOfMonth() {
      val listOfExcludeDayOfMonth: MutableList<ExcludeDayOfMonth> = arrayListOf()
      repeat(10) {
        listOfExcludeDayOfMonth.add(ExcludeDayOfMonth( 1, 1))
      }
      val excludeDayOfMonthList = ExcludeDayOfMonthList(listOfExcludeDayOfMonth)

      val result = excludeDayOfMonthList.isExcluded(LocalDate.of(2021, 1, 2))

      Assertions.assertFalse(result)
    }
  }
}