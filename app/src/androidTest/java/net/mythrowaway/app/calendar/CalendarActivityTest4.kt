package net.mythrowaway.app.calendar


import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import net.mythrowaway.app.R
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import net.mythrowaway.app.AndroidTestUtil.Companion.childAtPosition
import net.mythrowaway.app.domain.trash.presentation.view.calendar.CalendarActivity
import net.mythrowaway.app.domain.trash.presentation.view.edit.EditActivity
import org.junit.After
import org.junit.Before

@LargeTest
@RunWith(AndroidJUnit4::class)
class CalendarActivityTest4 {

  @get:Rule
  var mActivityScenarioRule = ActivityScenarioRule(CalendarActivity::class.java)

  @get:Rule
  val editActivityRule = createAndroidComposeRule(EditActivity::class.java)

  private val menuButton: ViewInteraction = onView(
    allOf(
      childAtPosition(
        allOf(withId(R.id.calendarToolbar),
          childAtPosition(
            withId(R.id.calendarContainer),
            0)),
        1),
      isDisplayed()))
  private val editMenuButton: ViewInteraction = onView(
    allOf(withId(R.id.menuItemAdd),
      childAtPosition(
        allOf(withId(R.id.design_navigation_view),
          childAtPosition(
            withId(R.id.main_nav_view),
            0)),
        1),
      isDisplayed()))

  @Before
  fun setUp(){
  }

  @After
  fun tearDown (){
  }

  /*
  登録済みゴミ出し情報を編集するシナリオ
  - 最初の登録したゴミ出し情報で編集画面が表示されること（もえるゴミ、毎週日曜日）
  - 編集後の情報がカレンダー画面に表示されること（もえるゴミ⇒ペットボトル、日曜日⇒月曜日）
   */

  @Test
  fun calendarActivityTest4() {
    menuButton.perform(click())
    editMenuButton.perform(click())

    editActivityRule.onNodeWithText("登録").performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("登録が完了しました").isDisplayed()
    }

    pressBack()

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

    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("もえるゴミ").isDisplayed()
    }
    editActivityRule.onNodeWithTag("TrashListRow").performClick()

    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("もえるゴミ").isDisplayed()
    }
    editActivityRule.onNodeWithText("もえるゴミ").performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("ペットボトル").isDisplayed()
    }
    editActivityRule.onNodeWithText("ペットボトル").performClick()

    editActivityRule.onNodeWithTag("WeekdayOfWeeklySchedule").performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("毎週 月曜日").isDisplayed()
    }
    editActivityRule.onNodeWithText("毎週 月曜日").performClick()
    editActivityRule.onNodeWithText("登録").performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("登録が完了しました").isDisplayed()
    }

    pressBack()

    pressBack()

    Thread.sleep(2000)

    val editText = onView(
      allOf(
        withId(R.id.trashText),
        childAtPosition(
          childAtPosition(
            allOf(
              withId(R.id.trashTextListLayout),
              childAtPosition(
                allOf(
                  withId(R.id.linearLayout),
                  childAtPosition(
                    withId(R.id.calendar),
                    8
                  )
                ),
                1
              )
            ),
            0
          ),
          0
        ),
        isDisplayed()
      )
    )
    editText.check(matches(withText("ペットボトル")))
  }
}