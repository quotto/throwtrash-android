package net.mythrowaway.app.domain

import net.mythrowaway.app.domain.trash.entity.trash.ExcludeDayOfMonth
import net.mythrowaway.app.domain.trash.entity.trash.ExcludeDayOfMonthList
import net.mythrowaway.app.domain.trash.entity.trash.IntervalWeeklySchedule
import net.mythrowaway.app.domain.trash.entity.trash.MonthlySchedule
import net.mythrowaway.app.domain.trash.entity.trash.OrdinalWeeklySchedule
import net.mythrowaway.app.domain.trash.entity.trash.Schedule
import net.mythrowaway.app.domain.trash.entity.trash.Trash
import net.mythrowaway.app.domain.trash.entity.trash.TrashType
import net.mythrowaway.app.domain.trash.entity.trash.WeeklySchedule
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate

class TrashTest {
  @Nested
  inner class ConstructorTest {
    @Test
    fun empty_id_is_illegal() {
      Assertions.assertThrows(IllegalArgumentException::class.java) {
        Trash(
          "",
          TrashType.BURN,
          "",
          listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
          ExcludeDayOfMonthList(arrayListOf())
        )
      }
    }

    @Test
    fun empty_schedules_is_illegal() {
      Assertions.assertThrows(IllegalArgumentException::class.java) {
        Trash(
          "id",
          TrashType.BURN,
          "burnable",
          listOf(),
          ExcludeDayOfMonthList(arrayListOf())
        )
      }
    }

    @Test
    fun schedules_size_over_3_is_illegal() {
      Assertions.assertThrows(IllegalArgumentException::class.java) {
        Trash(
          "id",
          TrashType.BURN,
          "burnable",
          listOf(
            WeeklySchedule(DayOfWeek.FRIDAY),
            OrdinalWeeklySchedule(3, DayOfWeek.FRIDAY),
            MonthlySchedule(21),
            IntervalWeeklySchedule(LocalDate.of(2024, 6, 2), DayOfWeek.FRIDAY, 2),
          ),
          ExcludeDayOfMonthList(arrayListOf())
        )
      }
    }

    @Test
    fun other_type_with_empty_displayName_is_illegal() {
      Assertions.assertThrows(IllegalArgumentException::class.java) {
        Trash(
          "id",
          TrashType.OTHER,
          "",
          listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
          ExcludeDayOfMonthList(arrayListOf())
        )
      }
    }

    @Test
    fun other_type_with_11_length_displayName_is_illegal() {
      Assertions.assertThrows(IllegalArgumentException::class.java) {
        Trash(
          "id",
          TrashType.OTHER,
          "12345678901",
          listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
          ExcludeDayOfMonthList(arrayListOf())
        )
      }
    }

    @Test
    fun other_type_with_invalid_character_displayName_is_illegal() {
      // 記号
      listOf("~","!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "-", "=", "+", "[", "]", "{", "}", ";", ":", "'", "\"", ",", "<", ">", ".", "/", "?", "\\", "|").forEach {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
          Trash(
            "id",
            TrashType.OTHER,
            "あいうえお$it",
            listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
            ExcludeDayOfMonthList(arrayListOf())
          )
        }
      }
      // 半角カタカナ
      listOf("ｱ", "ｶ", "ｻ", "ﾀ", "ﾅ", "ﾊ", "ﾏ", "ﾔ", "ﾜ", "ﾝ").forEach {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
          Trash(
            "id",
            TrashType.OTHER,
            "あいうえお$it",
            listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
            ExcludeDayOfMonthList(arrayListOf())
          )
        }
      }
    }

    @Test
    fun other_type_with_10_length_and_all_number_displayName_is_legal() {
      Trash(
        "id",
        TrashType.OTHER,
        "1234567890",
        listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
        ExcludeDayOfMonthList(arrayListOf())
      )
    }

    @Test
    fun other_type_with_10_length_and_all_alphabet_displayName_is_legal() {
      Trash(
        "id",
        TrashType.OTHER,
        "abcdefghij",
        listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
        ExcludeDayOfMonthList(arrayListOf())
      )
    }

    @Test
    fun other_type_with_10_length_and_all_upper_alphabet_displayName_is_legal() {
      Trash(
        "id",
        TrashType.OTHER,
        "ABCDEFGHIJ",
        listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
        ExcludeDayOfMonthList(arrayListOf())
      )
    }

    @Test
    fun other_type_with_10_length_and_all_hiragana_displayName_is_legal() {
      Trash(
        "id",
        TrashType.OTHER,
        "あいうえおかきくけこ",
        listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
        ExcludeDayOfMonthList(arrayListOf())
      )
    }

    @Test
    fun other_type_with_10_length_and_all_katakana_displayName_is_legal() {
      Trash(
        "id",
        TrashType.OTHER,
        "アイウエオカキクケコ",
        listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
        ExcludeDayOfMonthList(arrayListOf())
      )
    }

    @Test
    fun other_type_with_10_length_and_all_kanji_displayName_is_legal() {
      Trash(
        "id",
        TrashType.OTHER,
        "漢字漢字漢字漢字漢字",
        listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
        ExcludeDayOfMonthList(arrayListOf())
      )
    }
  }

  @Nested
  inner class TestIsTrashDay {

    @Test
    fun single_schedule_matches_a_day_is_trashDay() {
      val trash = Trash(
        "id",
        TrashType.BURN,
        "burnable",
        listOf<Schedule>(
          WeeklySchedule(DayOfWeek.FRIDAY),
        ),
        ExcludeDayOfMonthList(arrayListOf())
      )

      val result = trash.isTrashDay(LocalDate.of(2024, 6, 21))

      Assertions.assertTrue(result)
    }

    @Test
    fun one_of_two_schedules_matches_a_day_is_trashDay() {
      val trash = Trash(
        "id",
        TrashType.BURN,
        "burnable",
        listOf(
          WeeklySchedule(DayOfWeek.SATURDAY),
          OrdinalWeeklySchedule(3, DayOfWeek.FRIDAY),
        ),
        ExcludeDayOfMonthList(arrayListOf())
      )

      val result = trash.isTrashDay(LocalDate.of(2024, 6, 21))

      Assertions.assertTrue(result)
    }

    @Test
    fun three_of_three_schedules_matches_a_day_is_trashDay() {
      val trash = Trash(
        "id",
        TrashType.BURN,
        "burnable",
        listOf(
          OrdinalWeeklySchedule(3, DayOfWeek.FRIDAY),
          MonthlySchedule( 21),
          IntervalWeeklySchedule(LocalDate.of(2024, 6, 2), DayOfWeek.FRIDAY, 2),
        ),
        ExcludeDayOfMonthList(arrayListOf())
      )

      val result = trash.isTrashDay(LocalDate.of(2024, 6, 21))

      Assertions.assertTrue(result)
    }

    @Test
    fun single_schedule_matches_a_day_and_any_excludeDate_not_matches_is_trashDay() {
      val trash = Trash(
        "id",
        TrashType.BURN,
        "burnable",
        listOf<Schedule>(
          WeeklySchedule(DayOfWeek.FRIDAY),
        ),
        ExcludeDayOfMonthList(
          arrayListOf(
            ExcludeDayOfMonth(12, 21),
            ExcludeDayOfMonth(6, 22)
          )
        )
      )

      val result = trash.isTrashDay(LocalDate.of(2024, 6, 21))

      Assertions.assertTrue(result)
    }

    @Test
    fun any_schedules_not_matches_a_day_is_not_trashDay() {
      val trash = Trash(
        "id",
        TrashType.BURN,
        "burnable",
        listOf(
          WeeklySchedule(DayOfWeek.TUESDAY),
          MonthlySchedule(21),
          IntervalWeeklySchedule(LocalDate.of(2024, 6, 2), DayOfWeek.FRIDAY, 2),
        ),
        ExcludeDayOfMonthList(arrayListOf())
      )

      val result = trash.isTrashDay(LocalDate.of(2024, 6, 22))

      Assertions.assertFalse(result)
    }

    @Test
    fun single_schedule_matches_a_day_and_single_excludeDate_matches_too_is_not_trashDay() {
      val trash = Trash(
        "id",
        TrashType.BURN,
        "burnable",
        listOf<Schedule>(
          WeeklySchedule(DayOfWeek.FRIDAY),
        ),
        ExcludeDayOfMonthList(
          arrayListOf(
            ExcludeDayOfMonth(6, 21)
          )
        )
      )

      val result = trash.isTrashDay(LocalDate.of(2024, 6, 21))

      Assertions.assertFalse(result)
    }

    @Test
    fun two_of_two_schedules_matches_a_day_and_one_of_two_excludeDate_matches_too_is_not_trashDay() {
      val trash = Trash(
        "id",
        TrashType.BURN,
        "burnable",
        listOf(
          WeeklySchedule(DayOfWeek.FRIDAY),
          OrdinalWeeklySchedule(3, DayOfWeek.FRIDAY),
        ),
        ExcludeDayOfMonthList(
          arrayListOf(
            ExcludeDayOfMonth(12, 21),
            ExcludeDayOfMonth(6, 21)
          )
        )
      )

      val result = trash.isTrashDay(LocalDate.of(2024, 6, 21))

      Assertions.assertFalse(result)
    }
  }

  @Nested
  inner class TestIsEqualOfType {
    @Test
    fun same_type_is_equalOfType() {
      val trash1 = Trash(
        "id",
        TrashType.BURN,
        "burnable",
        listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
        ExcludeDayOfMonthList(arrayListOf())
      )
      val trash2 = Trash(
        "id",
        TrashType.BURN,
        "burnable",
        listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
        ExcludeDayOfMonthList(arrayListOf())
      )

      val result = trash1.isEqualOfType(trash2)

      Assertions.assertTrue(result)
    }

    @Test
    fun burn_and_burn_type_with_same_schedules_is_equalOfType() {
      val trash1 = Trash(
        "id1",
        TrashType.BURN,
        "burnable",
        listOf(
          WeeklySchedule(DayOfWeek.FRIDAY),
          OrdinalWeeklySchedule(3, DayOfWeek.FRIDAY),
        ),
        ExcludeDayOfMonthList(arrayListOf())
      )
      val trash2 = Trash(
        "id2",
        TrashType.BURN,
        "burnable",
        listOf(
          WeeklySchedule(DayOfWeek.FRIDAY),
          OrdinalWeeklySchedule(3, DayOfWeek.FRIDAY),
        ),
        ExcludeDayOfMonthList(arrayListOf())
      )

      val result = trash1.isEqualOfType(trash2)

      Assertions.assertTrue(result)
    }

    @Test
    fun burn_and_burn_type_with_different_schedules_is_equalOfType() {
      val trash1 = Trash(
        "id1",
        TrashType.BURN,
        "burnable",
        listOf(
          WeeklySchedule(DayOfWeek.FRIDAY),
          OrdinalWeeklySchedule(3, DayOfWeek.FRIDAY),
        ),
        ExcludeDayOfMonthList(arrayListOf())
      )
      val trash2 = Trash(
        "id2",
        TrashType.BURN,
        "burnable",
        listOf(
          WeeklySchedule(DayOfWeek.FRIDAY),
          OrdinalWeeklySchedule(3, DayOfWeek.SATURDAY),
        ),
        ExcludeDayOfMonthList(arrayListOf())
      )

      val result = trash1.isEqualOfType(trash2)

      Assertions.assertTrue(result)
    }
    @Test
    fun burn_and_un_burn_type_with_same_schedules_is_not_equalOfType() {
      val trash1 = Trash(
        "id1",
        TrashType.BURN,
        "burnable",
        listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
        ExcludeDayOfMonthList(arrayListOf())
      )
      val trash2 = Trash(
        "id2",
        TrashType.UNBURN,
        "non-burnable",
        listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
        ExcludeDayOfMonthList(arrayListOf())
      )

      val result = trash1.isEqualOfType(trash2)

      Assertions.assertFalse(result)
    }
  }
  @Test
  fun other_and_other_with_same_schedules_and_same_displayName_is_equal() {
    val trash1 = Trash(
      "id1",
      TrashType.OTHER,
      "other",
      listOf(
        WeeklySchedule(DayOfWeek.FRIDAY),
        OrdinalWeeklySchedule(3, DayOfWeek.FRIDAY),
      ),
      ExcludeDayOfMonthList(arrayListOf())
    )
    val trash2 = Trash(
      "id2",
      TrashType.OTHER,
      "other",
      listOf(
        WeeklySchedule(DayOfWeek.FRIDAY),
        OrdinalWeeklySchedule(3, DayOfWeek.FRIDAY),
      ),
      ExcludeDayOfMonthList(arrayListOf())
    )

    val result = trash1.isEqualOfType(trash2)

    Assertions.assertTrue(result)
  }

  @Test
  fun other_and_other_with_same_schedules_and_different_displayName_is_not_equal() {
    val trash1 = Trash(
      "id1",
      TrashType.OTHER,
      "other",
      listOf(
        WeeklySchedule(DayOfWeek.FRIDAY),
        OrdinalWeeklySchedule(3, DayOfWeek.FRIDAY),
      ),
      ExcludeDayOfMonthList(arrayListOf())
    )
    val trash2 = Trash(
      "id2",
      TrashType.OTHER,
      "other2",
      listOf(
        WeeklySchedule(DayOfWeek.FRIDAY),
        OrdinalWeeklySchedule(3, DayOfWeek.FRIDAY),
      ),
      ExcludeDayOfMonthList(arrayListOf())
    )

    val result = trash1.isEqualOfType(trash2)

    Assertions.assertFalse(result)
  }

  @Nested
  inner class TestDisplayName {
    @Test
    fun burn_type_displayName_is_burnable() {
      val trash = Trash(
        "id",
        TrashType.BURN,
        "burnable",
        listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
        ExcludeDayOfMonthList(arrayListOf())
      )

      val result = trash.displayName

      Assertions.assertEquals("もえるゴミ", result)
    }

    @Test
    fun un_burn_type_displayName_is_non_burnable() {
      val trash = Trash(
        "id",
        TrashType.UNBURN,
        "non-burnable",
        listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
        ExcludeDayOfMonthList(arrayListOf())
      )

      val result = trash.displayName

      Assertions.assertEquals("もえないゴミ", result)
    }

    @Test
    fun can_type_displayName_is_can() {
      val trash = Trash(
        "id",
        TrashType.CAN,
        "can",
        listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
        ExcludeDayOfMonthList(arrayListOf())
      )

      val result = trash.displayName

      Assertions.assertEquals("カン", result)
    }

    @Test
    fun pet_bottle_type_displayName_is_pet_bottle() {
      val trash = Trash(
        "id",
        TrashType.PETBOTTLE,
        "",
        listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
        ExcludeDayOfMonthList(arrayListOf())
      )

      val result = trash.displayName

      Assertions.assertEquals("ペットボトル", result)
    }

    @Test
    fun plastic_type_displayName_is_plastic() {
      val trash = Trash(
        "id",
        TrashType.PLASTIC,
        "plastic",
        listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
        ExcludeDayOfMonthList(arrayListOf())
      )

      val result = trash.displayName

      Assertions.assertEquals("プラスチック", result)
    }

    @Test
    fun bottle_type_displayName_is_bottle() {
      val trash = Trash(
        "id",
        TrashType.BOTTLE,
        "bottle",
        listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
        ExcludeDayOfMonthList(arrayListOf())
      )

      val result = trash.displayName

      Assertions.assertEquals("ビン", result)
    }

    @Test
    fun paper_type_displayName_is_paper() {
      val trash = Trash(
        "id",
        TrashType.PAPER,
        "paper",
        listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
        ExcludeDayOfMonthList(arrayListOf())
      )

      val result = trash.displayName

      Assertions.assertEquals("古紙", result)
    }

    @Test
    fun resource_type_displayName_is_resource() {
      val trash = Trash(
        "id",
        TrashType.RESOURCE,
        "",
        listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
        ExcludeDayOfMonthList(arrayListOf())
      )

      val result = trash.displayName

      Assertions.assertEquals("資源ごみ", result)
    }

    @Test
    fun coarse_type_displayName_is_coarse() {
      val trash = Trash(
        "id",
        TrashType.COARSE,
        "coarse",
        listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
        ExcludeDayOfMonthList(arrayListOf())
      )

      val result = trash.displayName

      Assertions.assertEquals("粗大ゴミ", result)
    }

    @Test
    fun other_type_displayName_is_other() {
      val trash = Trash(
        "id",
        TrashType.OTHER,
        "other",
        listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
        ExcludeDayOfMonthList(arrayListOf())
      )

      val result = trash.displayName

      Assertions.assertEquals("other", result)
    }
  }

  @Nested
  inner class TestInit {
    @Test
    fun other_type_with_empty_displayName_is_illegal() {
      Assertions.assertThrows(IllegalArgumentException::class.java) {
        Trash(
          "id",
          TrashType.OTHER,
          "",
          listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
          ExcludeDayOfMonthList(arrayListOf())
        )
      }
    }

    @Test
    fun other_type_with_non_empty_displayName_is_legal() {
      Trash(
        "id",
        TrashType.OTHER,
        "other",
        listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
        ExcludeDayOfMonthList(arrayListOf())
      )
    }
  }

  @Nested
  inner class TestEquals {
    @Test
    fun same_id_is_equals() {
      val trash1 = Trash(
        "id",
        TrashType.BURN,
        "burnable",
        listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
        ExcludeDayOfMonthList(arrayListOf())
      )
      val trash2 = Trash(
        "id",
        TrashType.BURN,
        "burnable",
        listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
        ExcludeDayOfMonthList(arrayListOf())
      )

      val result = trash1.equals(trash2)

      Assertions.assertTrue(result)
    }

    @Test
    fun different_id_is_not_equals() {
      val trash1 = Trash(
        "id1",
        TrashType.BURN,
        "burnable",
        listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
        ExcludeDayOfMonthList(arrayListOf())
      )
      val trash2 = Trash(
        "id2",
        TrashType.BURN,
        "burnable",
        listOf(WeeklySchedule(DayOfWeek.FRIDAY)),
        ExcludeDayOfMonthList(arrayListOf())
      )

      val result = trash1.equals(trash2)

      Assertions.assertFalse(result)
    }
  }

  @Nested
  inner class TestCanAddSchedule {
    @Test
    fun can_add_schedule_when_schedules_size_is_less_than_3() {
      val trash = Trash(
        "id",
        TrashType.BURN,
        "burnable",
        listOf(
          WeeklySchedule(DayOfWeek.FRIDAY),
          OrdinalWeeklySchedule(3, DayOfWeek.FRIDAY),
        ),
        ExcludeDayOfMonthList(arrayListOf())
      )

      val result = trash.canAddSchedule()

      Assertions.assertTrue(result)
    }

    @Test
    fun cannot_add_schedule_when_schedules_size_is_3() {
      val trash = Trash(
        "id",
        TrashType.BURN,
        "burnable",
        listOf(
          WeeklySchedule(DayOfWeek.FRIDAY),
          OrdinalWeeklySchedule(3, DayOfWeek.FRIDAY),
          MonthlySchedule(21),
        ),
        ExcludeDayOfMonthList(arrayListOf())
      )

      val result = trash.canAddSchedule()

      Assertions.assertFalse(result)
    }
  }

  @Nested
  inner class TestAddSchedule {
    @Test
    fun can_add_schedule_when_schedules_size_is_less_than_3() {
      val trash = Trash(
        "id",
        TrashType.BURN,
        "burnable",
        listOf(
          WeeklySchedule(DayOfWeek.FRIDAY),
          OrdinalWeeklySchedule(3, DayOfWeek.FRIDAY),
        ),
        ExcludeDayOfMonthList(arrayListOf())
      )

      trash.addSchedule(IntervalWeeklySchedule(LocalDate.of(2024, 6, 2), DayOfWeek.FRIDAY, 2))

      Assertions.assertEquals(3, trash.schedules.size)
    }

    @Test
    fun throw_exception_when_schedules_size_is_3() {
      val trash = Trash(
        "id",
        TrashType.BURN,
        "burnable",
        listOf(
          WeeklySchedule(DayOfWeek.FRIDAY),
          OrdinalWeeklySchedule(3, DayOfWeek.FRIDAY),
          MonthlySchedule(21),
        ),
        ExcludeDayOfMonthList(arrayListOf())
      )

      Assertions.assertThrows(IllegalArgumentException::class.java) {
        trash.addSchedule(IntervalWeeklySchedule(LocalDate.of(2024, 6, 2), DayOfWeek.FRIDAY, 2))
      }
    }
  }

  @Nested
  inner class TestCanRemoveSchedule {
    @Test
    fun can_remove_schedule_when_schedules_size_is_more_than_1() {
      val trash = Trash(
        "id",
        TrashType.BURN,
        "burnable",
        listOf(
          WeeklySchedule(DayOfWeek.FRIDAY),
          OrdinalWeeklySchedule(3, DayOfWeek.FRIDAY),
        ),
        ExcludeDayOfMonthList(arrayListOf())
      )

      val result = trash.canRemoveSchedule()

      Assertions.assertTrue(result)
    }

    @Test
    fun cannot_remove_schedule_when_schedules_size_is_1() {
      val trash = Trash(
        "id",
        TrashType.BURN,
        "burnable",
        listOf(
          WeeklySchedule(DayOfWeek.FRIDAY),
        ),
        ExcludeDayOfMonthList(arrayListOf())
      )

      val result = trash.canRemoveSchedule()

      Assertions.assertFalse(result)
    }
  }

  @Nested
  inner class TestRemoveScheduleAtDTO {
    @Test
    fun can_remove_schedule_when_schedules_size_is_more_than_1() {
      val trash = Trash(
        "id",
        TrashType.BURN,
        "burnable",
        listOf(
          WeeklySchedule(DayOfWeek.FRIDAY),
          OrdinalWeeklySchedule(3, DayOfWeek.FRIDAY),
        ),
        ExcludeDayOfMonthList(arrayListOf())
      )

      trash.removeScheduleAt(1)

      Assertions.assertEquals(1, trash.schedules.size)
    }

    @Test
    fun throw_exception_schedule_when_schedules_size_is_1() {
      val trash = Trash(
        "id",
        TrashType.BURN,
        "burnable",
        listOf(
          WeeklySchedule(DayOfWeek.FRIDAY),
        ),
        ExcludeDayOfMonthList(arrayListOf())
      )

      Assertions.assertThrows(IllegalArgumentException::class.java) {
        trash.removeScheduleAt(0)
      }
    }
  }
}