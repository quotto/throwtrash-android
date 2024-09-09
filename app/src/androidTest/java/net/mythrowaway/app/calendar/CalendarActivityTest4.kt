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
import androidx.test.platform.app.InstrumentationRegistry
import net.mythrowaway.app.R
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import net.mythrowaway.app.AndroidTestUtil.Companion.childAtPosition
import net.mythrowaway.app.domain.trash.presentation.view.calendar.CalendarActivity
import net.mythrowaway.app.domain.trash.presentation.view.edit.EditActivity
import net.mythrowaway.app.lib.AndroidTestHelper.Companion.waitUntilDisplayed
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

  private val resource = InstrumentationRegistry.getInstrumentation().targetContext.resources

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
  fun edit_saved_trash() {
    menuButton.perform(click())
    editMenuButton.perform(click())

    editActivityRule.onNodeWithText(resource.getString(R.string.text_register_trash_button)).performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText(resource.getString(R.string.message_complete_save_trash)).isDisplayed()
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

    editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_weekday_of_weekly_dropdown)).performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText("毎週 月曜日").isDisplayed()
    }
    editActivityRule.onNodeWithText("毎週 月曜日").performClick()
    editActivityRule.onNodeWithText(resource.getString(R.string.text_register_trash_button)).performClick()
    editActivityRule.waitUntil {
      editActivityRule.onNodeWithText(resource.getString(R.string.message_complete_save_trash)).isDisplayed()
    }

    pressBack()

    pressBack()

    waitUntilDisplayed("ペットボトル", 5000)

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