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


  /*
  例外設定日のシナリオ
  - 例外日登録後に一覧画面から該当するゴミの編集画面を開く
  - 例外設定画面に遷移すると登録時のデータが復元されること。
   */
  @Test
  fun editActivityTest5() {
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

    editActivityRule.onNodeWithText("除外日の追加").performClick()
    editActivityRule.onNodeWithTag("AddExcludeDayOfMonthButton").performClick()
    editActivityRule.onNodeWithTag("MonthDropDown").performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("3 月").isDisplayed()
    }
    editActivityRule.onNodeWithText("3 月").performClick()
    editActivityRule.onNodeWithTag("DayDropDown").performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("10 日").isDisplayed()
    }
    editActivityRule.onNodeWithText("10 日").performClick()

    editActivityRule.onNodeWithTag("AddExcludeDayOfMonthButton").performClick()

    Espresso.pressBack()

    editActivityRule.onNodeWithTag("RegisterButton").performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("登録が完了しました").isDisplayed()
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

    editActivityRule.onNodeWithTag("TrashListRow").performClick()
    editActivityRule.onNodeWithText("除外日の追加").performClick()

    editActivityRule.onAllNodesWithTag("MonthDropDown").assertCountEquals(2)

    editActivityRule.onAllNodesWithTag("MonthDropDown")[0].assertTextEquals("3 月")
    editActivityRule.onAllNodesWithTag("DayDropDown")[0].assertTextEquals("10 日")
    editActivityRule.onAllNodesWithTag("MonthDropDown")[1].assertTextEquals("1 月")
    editActivityRule.onAllNodesWithTag("DayDropDown")[1].assertTextEquals("1 日")
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
