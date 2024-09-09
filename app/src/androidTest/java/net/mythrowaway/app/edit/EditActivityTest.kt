package net.mythrowaway.app.edit


import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import net.mythrowaway.app.R
import net.mythrowaway.app.domain.trash.presentation.view.calendar.CalendarActivity
import net.mythrowaway.app.domain.trash.presentation.view.edit.EditActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class EditActivityTest {

  @get:Rule
  val mActivityScenarioRule = ActivityScenarioRule(CalendarActivity::class.java)

  @get:Rule
  val editActivityRule = createAndroidComposeRule(EditActivity::class.java)

  private val menuButton = onView(
    allOf(
      withContentDescription("Chromeで開く"),
      childAtPosition(
        allOf(
          withId(R.id.calendarToolbar),
          childAtPosition(
            withId(R.id.calendarContainer),
            0
          )
        ),
        1
      ),
      isDisplayed()
    )
  )

  private val resource = InstrumentationRegistry.getInstrumentation().targetContext.resources


  /*
  例外設定日のシナリオ
  - 例外日登録後に一覧画面から該当するゴミの編集画面を開く
  - 例外設定画面に遷移すると登録時のデータが復元されること。
   */
  @Test
  fun add_exclude_day_of_month_and_edit() {
    menuButton.perform(click())

    val editMenuButton = onView(
      allOf(
        withId(R.id.menuItemAdd),
        childAtPosition(
          allOf(
            withId(R.id.design_navigation_view),
            childAtPosition(
              withId(R.id.main_nav_view),
              0
            )
          ),
          1
        ),
        isDisplayed()
      )
    )
    editMenuButton.perform(click())

    editActivityRule.onNodeWithText(resource.getString(R.string.text_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_exclude_day_of_month_button)).performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("3 月").isDisplayed()
    }
    editActivityRule.onNodeWithText("3 月").performClick()
    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown)).performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("10 日").isDisplayed()
    }
    editActivityRule.onNodeWithText("10 日").performClick()

    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_exclude_day_of_month_button)).performClick()

    Espresso.pressBack()

    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_register_trash_button)).performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText(resource.getString(R.string.message_complete_save_trash)).isDisplayed()
    }

    Espresso.pressBack()

    menuButton.perform(click())
    val listMenuButton = onView(
      allOf(
        withId(R.id.menuItemList),
        childAtPosition(
          allOf(
            withId(R.id.design_navigation_view),
            childAtPosition(
              withId(R.id.main_nav_view),
              0
            )
          ),
          2
        ),
        isDisplayed()
      )
    )
    listMenuButton.perform(click())

    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_trash_list_item)).performClick()
    editActivityRule.onNodeWithText(resource.getString(R.string.text_exclude_day_of_month_button)).performClick()

    editActivityRule.onAllNodesWithTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)).assertCountEquals(2)

    editActivityRule.onAllNodesWithTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown))[0].assertTextEquals("3 月")
    editActivityRule.onAllNodesWithTag(resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown))[0].assertTextEquals("10 日")
    editActivityRule.onAllNodesWithTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown))[1].assertTextEquals("1 月")
    editActivityRule.onAllNodesWithTag(resource.getString(R.string.testTag_day_of_exclude_day_of_month_dropdown))[1].assertTextEquals("1 日")
  }

  private fun childAtPosition(
    parentMatcher: Matcher<View>, position: Int
  ): Matcher<View> {

    return object : TypeSafeMatcher<View>() {
      override fun describeTo(description: Description) {
        description.appendText("Child at position $position in parent ")
        parentMatcher.describeTo(description)
      }

      public override fun matchesSafely(view: View): Boolean {
        val parent = view.parent
        return parent is ViewGroup && parentMatcher.matches(parent)
                && view == parent.getChildAt(position)
      }
    }
  }
}
