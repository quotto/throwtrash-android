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
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import net.mythrowaway.app.R
import net.mythrowaway.app.module.trash.presentation.view.edit.EditActivity
import org.junit.Rule
import org.junit.Test

@LargeTest
class ExcludeDayOfMonthScreenTest {
  @get:Rule
  val editActivityRule = createAndroidComposeRule(EditActivity::class.java)

  private val resource = InstrumentationRegistry.getInstrumentation().targetContext.resources

  @Test
  fun can_add_exclude_day_of_month_max_10() {
    editActivityRule.onNodeWithText(resource.getString(R.string.text_exclude_day_of_month_button)).performClick()
    repeat(10) {
      editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_exclude_day_of_month_button)).performClick()
    }
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_exclude_day_of_month_button)).assertIsNotEnabled()

    editActivityRule.onAllNodesWithTag(resource.getString(R.string.testTag_delete_exclude_day_of_month_button))[0].performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_exclude_day_of_month_button)).assertIsEnabled()
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun can_delete_second_exclude_day_of_month() {
    editActivityRule.onNodeWithText(resource.getString(R.string.text_exclude_day_of_month_button)).performClick()

    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_exclude_day_of_month_button)).performClick()
    editActivityRule.waitUntilNodeCount(
      hasTestTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)),
      1,
      1000
    )

    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_exclude_day_of_month_button)).performClick()
    editActivityRule.waitUntilNodeCount(
      hasTestTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)),
      2,
      1000
    )
    editActivityRule.onAllNodesWithTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown))[1].performClick()
    editActivityRule.onNodeWithText("2 月").performClick()
    editActivityRule.onAllNodesWithTag(resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown))[1].performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("2 日").isDisplayed()
    }
    editActivityRule.onNodeWithText("2 日").performClick()

    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_exclude_day_of_month_button)).performClick()
    editActivityRule.waitUntilNodeCount(
      hasTestTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)),
      3,
      1000
    )

    editActivityRule.onAllNodesWithTag(resource.getString(R.string.testTag_delete_exclude_day_of_month_button))[1].performClick()

    editActivityRule.waitUntilNodeCount(
      hasTestTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)),
      2,
      1000
    )

    editActivityRule.onNodeWithText("2 月").assertDoesNotExist()
    editActivityRule.onNodeWithText("2 日").assertDoesNotExist()

    editActivityRule.onAllNodesWithTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)).assertCountEquals(2)
    editActivityRule.onAllNodesWithTag(resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)).assertCountEquals(2)
  }

  @Test
  fun can_add_exclude_day_of_month_max_10_and_delete_all() {
    editActivityRule.onNodeWithText(resource.getString(R.string.text_exclude_day_of_month_button)).performClick()
    repeat(10) {
      editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_exclude_day_of_month_button)).performClick()
    }

    repeat(10) {
      editActivityRule.onAllNodesWithTag(resource.getString(R.string.testTag_delete_exclude_day_of_month_button))[0].performClick()
    }
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_exclude_day_of_month_button)).assertIsEnabled()
    editActivityRule.onAllNodesWithTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)).assertCountEquals(0)
  }

  @Test
  fun january_has_31_days() {
    editActivityRule.onNodeWithText(resource.getString(R.string.text_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.onNodeWithTag("${resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)}${resource.getString(R.string.testTag_suffix_dropdown_menu_item)}").performScrollToNode(
      hasText("31 日")
    )
  }
  @Test
  fun february_has_29_days() {
    editActivityRule.onNodeWithText(resource.getString(R.string.text_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("2 月").isDisplayed()
    }
    editActivityRule.onNodeWithText("2 月").performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.onNodeWithTag("${resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)}${resource.getString(R.string.testTag_suffix_dropdown_menu_item)}").performScrollToNode(
      hasText("29 日")
    )
    editActivityRule.onNodeWithText("30 日").assertDoesNotExist()
  }
  @Test
  fun march_has_31() {
    editActivityRule.onNodeWithText(resource.getString(R.string.text_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("3 月").isDisplayed()
    }
    editActivityRule.onNodeWithText("3 月").performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.onNodeWithTag("${resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)}${resource.getString(R.string.testTag_suffix_dropdown_menu_item)}").performScrollToNode(
      hasText("31 日")
    )
  }

  @Test
  fun april_has_30() {
    editActivityRule.onNodeWithText(resource.getString(R.string.text_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("4 月").isDisplayed()
    }
    editActivityRule.onNodeWithText("4 月").performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.onNodeWithTag("${resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)}${resource.getString(R.string.testTag_suffix_dropdown_menu_item)}").performScrollToNode(
      hasText("30 日")
    )
    editActivityRule.onNodeWithText("31 日").assertDoesNotExist()
  }

  @Test
  fun may_has_31() {
    editActivityRule.onNodeWithText(resource.getString(R.string.text_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("5 月").isDisplayed()
    }
    editActivityRule.onNodeWithText("5 月").performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.onNodeWithTag("${resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)}${resource.getString(R.string.testTag_suffix_dropdown_menu_item)}").performScrollToNode(
      hasText("31 日")
    )
  }

  @Test
  fun june_has_30() {
    editActivityRule.onNodeWithText(resource.getString(R.string.text_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("6 月").isDisplayed()
    }
    editActivityRule.onNodeWithText("6 月").performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.onNodeWithTag("${resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)}${resource.getString(R.string.testTag_suffix_dropdown_menu_item)}").performScrollToNode(
      hasText("30 日")
    )
    editActivityRule.onNodeWithText("31 日").assertDoesNotExist()
  }

  @Test
  fun july_has_31() {
    editActivityRule.onNodeWithText(resource.getString(R.string.text_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("7 月").isDisplayed()
    }
    editActivityRule.onNodeWithText("7 月").performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.onNodeWithTag("${resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)}${resource.getString(R.string.testTag_suffix_dropdown_menu_item)}").performScrollToNode(
      hasText("31 日")
    )
  }

  @Test
  fun august_has_31() {
    editActivityRule.onNodeWithText(resource.getString(R.string.text_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("8 月").isDisplayed()
    }
    editActivityRule.onNodeWithText("8 月").performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.onNodeWithTag("${resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)}${resource.getString(R.string.testTag_suffix_dropdown_menu_item)}").performScrollToNode(
      hasText("31 日")
    )
  }

  @Test
  fun september_has_30() {
    editActivityRule.onNodeWithText(resource.getString(R.string.text_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("9 月").isDisplayed()
    }
    editActivityRule.onNodeWithText("9 月").performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.onNodeWithTag("${resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)}${resource.getString(R.string.testTag_suffix_dropdown_menu_item)}").performScrollToNode(
      hasText("30 日")
    )
    editActivityRule.onNodeWithText("31 日").assertDoesNotExist()
  }

  @Test
  fun october_has_31() {
    editActivityRule.onNodeWithText(resource.getString(R.string.text_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("10 月").isDisplayed()
    }
    editActivityRule.onNodeWithText("10 月").performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.onNodeWithTag("${resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)}${resource.getString(R.string.testTag_suffix_dropdown_menu_item)}").performScrollToNode(
      hasText("31 日")
    )
  }

  @Test
  fun november_has_30() {
    editActivityRule.onNodeWithText(resource.getString(R.string.text_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.onNodeWithTag(
      "${resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)}${resource.getString(R.string.testTag_suffix_dropdown_menu_item)}"
    ).performScrollToNode(
      hasText("11 月")
    )
    editActivityRule.onNodeWithText("11 月").performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.onNodeWithTag("${resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)}${resource.getString(R.string.testTag_suffix_dropdown_menu_item)}").performScrollToNode(
      hasText("30 日")
    )
    editActivityRule.onNodeWithText("31 日").assertDoesNotExist()
  }

  @Test
  fun december_has_31() {
    editActivityRule.onNodeWithText(resource.getString(R.string.text_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.onNodeWithTag(
      "${resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)}${resource.getString(R.string.testTag_suffix_dropdown_menu_item)}"
    ).performScrollToNode(
      hasText("12 月")
    )
    editActivityRule.onNodeWithText("12 月").performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.onNodeWithTag("${resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)}${resource.getString(R.string.testTag_suffix_dropdown_menu_item)}").performScrollToNode(
      hasText("31 日")
    )
  }

  @Test
  fun max_day_changed_from_november_30_to_december_31() {
    editActivityRule.onNodeWithText(resource.getString(R.string.text_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.onNodeWithTag(
      "${resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)}${resource.getString(R.string.testTag_suffix_dropdown_menu_item)}"
    ).performScrollToNode(
      hasText("11 月")
    )
    editActivityRule.onNodeWithText("11 月").performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.onNodeWithTag("${resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)}${resource.getString(R.string.testTag_suffix_dropdown_menu_item)}").performScrollToNode(
      hasText("30 日")
    )
    editActivityRule.onNodeWithText("31 日").assertDoesNotExist()
    editActivityRule.onNodeWithText("30 日").performClick()

    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.onNodeWithTag(
      "${resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)}${resource.getString(R.string.testTag_suffix_dropdown_menu_item)}"
    ).performScrollToNode(
      hasText("12 月")
    )
    editActivityRule.onNodeWithText("12 月").performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.onNodeWithTag("${resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)}${resource.getString(R.string.testTag_suffix_dropdown_menu_item)}").performScrollToNode(
      hasText("31 日")
    )
  }

  @Test
  fun state_is_saved_when_back_button_is_pressed() {
    editActivityRule.onNodeWithText(resource.getString(R.string.text_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.onNodeWithTag(
      "${resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)}${resource.getString(R.string.testTag_suffix_dropdown_menu_item)}"
    ).performScrollToNode(
      hasText("11 月")
    )
    editActivityRule.onNodeWithText("11 月").performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.onNodeWithTag("${resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)}${resource.getString(R.string.testTag_suffix_dropdown_menu_item)}").performScrollToNode(
      hasText("30 日")
    )
    editActivityRule.onNodeWithText("30 日").performClick()

    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_exclude_day_of_month_button)).performClick()

    Espresso.pressBack()

    editActivityRule.onNodeWithText(resource.getString(R.string.text_exclude_day_of_month_button)).performClick()

    editActivityRule.onAllNodesWithTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)).assertCountEquals(2)
    editActivityRule.onAllNodesWithTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown))[0].assertTextEquals("11 月")
    editActivityRule.onAllNodesWithTag(resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown))[0].assertTextEquals("30 日")
    editActivityRule.onAllNodesWithTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown))[1].assertTextEquals("1 月")
    editActivityRule.onAllNodesWithTag(resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown))[1].assertTextEquals("1 日")

    repeat(2) {
      editActivityRule.onAllNodesWithTag(resource.getString(R.string.testTag_delete_exclude_day_of_month_button))[0].performClick()
    }

    Espresso.pressBack()

    editActivityRule.onNodeWithText(resource.getString(R.string.text_exclude_day_of_month_button)).performClick()
    editActivityRule.onAllNodesWithTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)).assertCountEquals(0)
  }
}