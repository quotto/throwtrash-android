package net.mythrowaway.app.calendar


import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import net.mythrowaway.app.R
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import net.mythrowaway.app.AndroidTestUtil.Companion.childAtPosition
import net.mythrowaway.app.module.trash.presentation.view.calendar.CalendarActivity
import net.mythrowaway.app.lib.AndroidTestHelper.Companion.waitUntilDisplayed
import org.junit.After
import org.junit.Before

@LargeTest
@RunWith(AndroidJUnit4::class)
class CalendarActivityTest4 {

  @get:Rule
  val composeRule = createAndroidComposeRule(CalendarActivity::class.java)

  private fun openDrawer() {
    composeRule.waitUntil {
      composeRule.activity.findViewById<View>(R.id.calendarActivityRoot) != null
    }
    onView(withId(R.id.calendarActivityRoot)).perform(DrawerActions.open())
  }

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
    openDrawer()
    onView(withId(R.id.menuItemAdd)).perform(click())

    composeRule.onNodeWithText(resource.getString(R.string.text_register_trash_button)).performClick()
    composeRule.waitUntil {
      composeRule.onNodeWithText(resource.getString(R.string.message_complete_save_trash)).isDisplayed()
    }

    pressBack()

    openDrawer()
    onView(withId(R.id.menuItemList)).perform(click())

    composeRule.waitUntil {
      composeRule.onNodeWithText("もえるゴミ").isDisplayed()
    }
    composeRule.onNodeWithTag(resource.getString(R.string.testTag_trash_list_item)).performClick()

    composeRule.waitUntil {
      composeRule.onNodeWithText("もえるゴミ").isDisplayed()
    }
    composeRule.onNodeWithText("もえるゴミ").performClick()
    composeRule.waitUntil {
      composeRule.onNodeWithText("ペットボトル").isDisplayed()
    }
    composeRule.onNodeWithText("ペットボトル").performClick()

    composeRule.onNodeWithTag(resource.getString(R.string.testTag_weekday_of_weekly_dropdown)).performClick()
    composeRule.waitUntil {
      composeRule.onNodeWithText("毎週 月曜日").isDisplayed()
    }
    composeRule.onNodeWithText("毎週 月曜日").performClick()
    composeRule.onNodeWithText(resource.getString(R.string.text_register_trash_button)).performClick()
    composeRule.waitUntil {
      composeRule.onNodeWithText(resource.getString(R.string.message_complete_save_trash)).isDisplayed()
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
