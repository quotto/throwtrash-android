package net.mythrowaway.app.edit

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.espresso.Espresso
import net.mythrowaway.app.domain.trash.presentation.view.edit.EditActivity
import org.junit.Rule
import org.junit.Test

class ExcludeDayOfMonthScreenTest {
  @get:Rule
  val editActivityRule = createAndroidComposeRule(EditActivity::class.java)

  @Test
  fun can_add_exclude_day_of_month_max_10() {
    editActivityRule.onNodeWithText("除外日の追加").performClick()
    repeat(10) {
      editActivityRule.onNodeWithTag("AddExcludeDayOfMonthButton").performClick()
    }
    editActivityRule.onNodeWithTag("AddExcludeDayOfMonthButton").assertIsNotEnabled()

    editActivityRule.onAllNodesWithTag("DeleteExcludeDayOfMonthButton")[0].performClick()
    editActivityRule.onNodeWithTag("AddExcludeDayOfMonthButton").assertIsEnabled()
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun can_delete_second_exclude_day_of_month() {
    editActivityRule.onNodeWithText("除外日の追加").performClick()

    editActivityRule.onNodeWithTag("AddExcludeDayOfMonthButton").performClick()
    editActivityRule.waitUntilNodeCount(
      hasTestTag("MonthDropDown"),
      1,
      1000
    )

    editActivityRule.onNodeWithTag("AddExcludeDayOfMonthButton").performClick()
    editActivityRule.waitUntilNodeCount(
      hasTestTag("MonthDropDown"),
      2,
      1000
    )
    editActivityRule.onAllNodesWithTag("MonthDropDown")[1].performClick()
    editActivityRule.onNodeWithText("2 月").performClick()
    editActivityRule.onAllNodesWithTag("DayDropDown")[1].performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("2 日").isDisplayed()
    }
    editActivityRule.onNodeWithText("2 日").performClick()

    editActivityRule.onNodeWithTag("AddExcludeDayOfMonthButton").performClick()
    editActivityRule.waitUntilNodeCount(
      hasTestTag("MonthDropDown"),
      3,
      1000
    )

    editActivityRule.onAllNodesWithTag("DeleteExcludeDayOfMonthButton")[1].performClick()

    editActivityRule.waitUntilNodeCount(
      hasTestTag("MonthDropDown"),
      2,
      1000
    )

    editActivityRule.onNodeWithText("2 月").assertDoesNotExist()
    editActivityRule.onNodeWithText("2 日").assertDoesNotExist()

    editActivityRule.onAllNodesWithTag("MonthDropDown").assertCountEquals(2)
    editActivityRule.onAllNodesWithTag("DayDropDown").assertCountEquals(2)
  }

  @Test
  fun can_add_exclude_day_of_month_max_10_and_delete_all() {
    editActivityRule.onNodeWithText("除外日の追加").performClick()
    repeat(10) {
      editActivityRule.onNodeWithTag("AddExcludeDayOfMonthButton").performClick()
    }

    repeat(10) {
      editActivityRule.onAllNodesWithTag("DeleteExcludeDayOfMonthButton")[0].performClick()
    }
    editActivityRule.onNodeWithTag("AddExcludeDayOfMonthButton").assertIsEnabled()
    editActivityRule.onAllNodesWithTag("MonthDropDown").assertCountEquals(0)
  }

  @Test
  fun january_has_31_days() {
    editActivityRule.onNodeWithText("除外日の追加").performClick()
    editActivityRule.onNodeWithTag("AddExcludeDayOfMonthButton").performClick()
    editActivityRule.onNodeWithTag("MonthDropDown").performClick()
    editActivityRule.onNodeWithTag("DayDropDown").performClick()
    editActivityRule.onNodeWithTag("DayDropDownMenu").performScrollToNode(
      hasText("31 日")
    )
  }
  @Test
  fun february_has_29_days() {
    editActivityRule.onNodeWithText("除外日の追加").performClick()
    editActivityRule.onNodeWithTag("AddExcludeDayOfMonthButton").performClick()
    editActivityRule.onNodeWithTag("MonthDropDown").performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("2 月").isDisplayed()
    }
    editActivityRule.onNodeWithText("2 月").performClick()
    editActivityRule.onNodeWithTag("DayDropDown").performClick()
    editActivityRule.onNodeWithTag("DayDropDownMenu").performScrollToNode(
      hasText("29 日")
    )
    editActivityRule.onNodeWithText("30 日").assertDoesNotExist()
  }
  @Test
  fun march_has_31() {
    editActivityRule.onNodeWithText("除外日の追加").performClick()
    editActivityRule.onNodeWithTag("AddExcludeDayOfMonthButton").performClick()
    editActivityRule.onNodeWithTag("MonthDropDown").performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("3 月").isDisplayed()
    }
    editActivityRule.onNodeWithText("3 月").performClick()
    editActivityRule.onNodeWithTag("DayDropDown").performClick()
    editActivityRule.onNodeWithTag("DayDropDownMenu").performScrollToNode(
      hasText("31 日")
    )
  }

  @Test
  fun april_has_30() {
    editActivityRule.onNodeWithText("除外日の追加").performClick()
    editActivityRule.onNodeWithTag("AddExcludeDayOfMonthButton").performClick()
    editActivityRule.onNodeWithTag("MonthDropDown").performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("4 月").isDisplayed()
    }
    editActivityRule.onNodeWithText("4 月").performClick()
    editActivityRule.onNodeWithTag("DayDropDown").performClick()
    editActivityRule.onNodeWithTag("DayDropDownMenu").performScrollToNode(
      hasText("30 日")
    )
    editActivityRule.onNodeWithText("31 日").assertDoesNotExist()
  }

  @Test
  fun may_has_31() {
    editActivityRule.onNodeWithText("除外日の追加").performClick()
    editActivityRule.onNodeWithTag("AddExcludeDayOfMonthButton").performClick()
    editActivityRule.onNodeWithTag("MonthDropDown").performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("5 月").isDisplayed()
    }
    editActivityRule.onNodeWithText("5 月").performClick()
    editActivityRule.onNodeWithTag("DayDropDown").performClick()
    editActivityRule.onNodeWithTag("DayDropDownMenu").performScrollToNode(
      hasText("31 日")
    )
  }

  @Test
  fun june_has_30() {
    editActivityRule.onNodeWithText("除外日の追加").performClick()
    editActivityRule.onNodeWithTag("AddExcludeDayOfMonthButton").performClick()
    editActivityRule.onNodeWithTag("MonthDropDown").performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("6 月").isDisplayed()
    }
    editActivityRule.onNodeWithText("6 月").performClick()
    editActivityRule.onNodeWithTag("DayDropDown").performClick()
    editActivityRule.onNodeWithTag("DayDropDownMenu").performScrollToNode(
      hasText("30 日")
    )
    editActivityRule.onNodeWithText("31 日").assertDoesNotExist()
  }

  @Test
  fun july_has_31() {
    editActivityRule.onNodeWithText("除外日の追加").performClick()
    editActivityRule.onNodeWithTag("AddExcludeDayOfMonthButton").performClick()
    editActivityRule.onNodeWithTag("MonthDropDown").performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("7 月").isDisplayed()
    }
    editActivityRule.onNodeWithText("7 月").performClick()
    editActivityRule.onNodeWithTag("DayDropDown").performClick()
    editActivityRule.onNodeWithTag("DayDropDownMenu").performScrollToNode(
      hasText("31 日")
    )
  }

  @Test
  fun august_has_31() {
    editActivityRule.onNodeWithText("除外日の追加").performClick()
    editActivityRule.onNodeWithTag("AddExcludeDayOfMonthButton").performClick()
    editActivityRule.onNodeWithTag("MonthDropDown").performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("8 月").isDisplayed()
    }
    editActivityRule.onNodeWithText("8 月").performClick()
    editActivityRule.onNodeWithTag("DayDropDown").performClick()
    editActivityRule.onNodeWithTag("DayDropDownMenu").performScrollToNode(
      hasText("31 日")
    )
  }

  @Test
  fun september_has_30() {
    editActivityRule.onNodeWithText("除外日の追加").performClick()
    editActivityRule.onNodeWithTag("AddExcludeDayOfMonthButton").performClick()
    editActivityRule.onNodeWithTag("MonthDropDown").performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("9 月").isDisplayed()
    }
    editActivityRule.onNodeWithText("9 月").performClick()
    editActivityRule.onNodeWithTag("DayDropDown").performClick()
    editActivityRule.onNodeWithTag("DayDropDownMenu").performScrollToNode(
      hasText("30 日")
    )
    editActivityRule.onNodeWithText("31 日").assertDoesNotExist()
  }

  @Test
  fun october_has_31() {
    editActivityRule.onNodeWithText("除外日の追加").performClick()
    editActivityRule.onNodeWithTag("AddExcludeDayOfMonthButton").performClick()
    editActivityRule.onNodeWithTag("MonthDropDown").performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("10 月").isDisplayed()
    }
    editActivityRule.onNodeWithText("10 月").performClick()
    editActivityRule.onNodeWithTag("DayDropDown").performClick()
    editActivityRule.onNodeWithTag("DayDropDownMenu").performScrollToNode(
      hasText("31 日")
    )
  }

  @Test
  fun november_has_30() {
    editActivityRule.onNodeWithText("除外日の追加").performClick()
    editActivityRule.onNodeWithTag("AddExcludeDayOfMonthButton").performClick()
    editActivityRule.onNodeWithTag("MonthDropDown").performClick()
    editActivityRule.onNodeWithTag("MonthDropDownMenu").performScrollToNode(
      hasText("11 月")
    )
    editActivityRule.onNodeWithText("11 月").performClick()
    editActivityRule.onNodeWithTag("DayDropDown").performClick()
    editActivityRule.onNodeWithTag("DayDropDownMenu").performScrollToNode(
      hasText("30 日")
    )
    editActivityRule.onNodeWithText("31 日").assertDoesNotExist()
  }

  @Test
  fun december_has_31() {
    editActivityRule.onNodeWithText("除外日の追加").performClick()
    editActivityRule.onNodeWithTag("AddExcludeDayOfMonthButton").performClick()
    editActivityRule.onNodeWithTag("MonthDropDown").performClick()
    editActivityRule.onNodeWithTag("MonthDropDownMenu").performScrollToNode(
      hasText("12 月")
    )
    editActivityRule.onNodeWithText("12 月").performClick()
    editActivityRule.onNodeWithTag("DayDropDown").performClick()
    editActivityRule.onNodeWithTag("DayDropDownMenu").performScrollToNode(
      hasText("31 日")
    )
  }

  @Test
  fun max_day_changed_from_november_30_to_december_31() {
    editActivityRule.onNodeWithText("除外日の追加").performClick()
    editActivityRule.onNodeWithTag("AddExcludeDayOfMonthButton").performClick()
    editActivityRule.onNodeWithTag("MonthDropDown").performClick()
    editActivityRule.onNodeWithTag("MonthDropDownMenu").performScrollToNode(
      hasText("11 月")
    )
    editActivityRule.onNodeWithText("11 月").performClick()
    editActivityRule.onNodeWithTag("DayDropDown").performClick()
    editActivityRule.onNodeWithTag("DayDropDownMenu").performScrollToNode(
      hasText("30 日")
    )
    editActivityRule.onNodeWithText("31 日").assertDoesNotExist()
    editActivityRule.onNodeWithText("30 日").performClick()

    editActivityRule.onNodeWithTag("MonthDropDown").performClick()
    editActivityRule.onNodeWithTag("MonthDropDownMenu").performScrollToNode(
      hasText("12 月")
    )
    editActivityRule.onNodeWithText("12 月").performClick()
    editActivityRule.onNodeWithTag("DayDropDown").performClick()
    editActivityRule.onNodeWithTag("DayDropDownMenu").performScrollToNode(
      hasText("31 日")
    )
  }

  @Test
  fun state_is_saved_when_back_button_is_pressed() {
    editActivityRule.onNodeWithText("除外日の追加").performClick()
    editActivityRule.onNodeWithTag("AddExcludeDayOfMonthButton").performClick()
    editActivityRule.onNodeWithTag("MonthDropDown").performClick()
    editActivityRule.onNodeWithTag("MonthDropDownMenu").performScrollToNode(
      hasText("11 月")
    )
    editActivityRule.onNodeWithText("11 月").performClick()
    editActivityRule.onNodeWithTag("DayDropDown").performClick()
    editActivityRule.onNodeWithTag("DayDropDownMenu").performScrollToNode(
      hasText("30 日")
    )
    editActivityRule.onNodeWithText("30 日").performClick()

    editActivityRule.onNodeWithTag("AddExcludeDayOfMonthButton").performClick()

    Espresso.pressBack()

    editActivityRule.onNodeWithText("除外日の追加").performClick()

    editActivityRule.onAllNodesWithTag("MonthDropDown").assertCountEquals(2)
    editActivityRule.onAllNodesWithTag("MonthDropDown")[0].assertTextEquals("11 月")
    editActivityRule.onAllNodesWithTag("DayDropDown")[0].assertTextEquals("30 日")
    editActivityRule.onAllNodesWithTag("MonthDropDown")[1].assertTextEquals("1 月")
    editActivityRule.onAllNodesWithTag("DayDropDown")[1].assertTextEquals("1 日")

    repeat(2) {
      editActivityRule.onAllNodesWithTag("DeleteExcludeDayOfMonthButton")[0].performClick()
    }

    Espresso.pressBack()

    editActivityRule.onNodeWithText("除外日の追加").performClick()
    editActivityRule.onAllNodesWithTag("MonthDropDown").assertCountEquals(0)
  }
}