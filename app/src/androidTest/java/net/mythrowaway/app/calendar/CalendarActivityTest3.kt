package net.mythrowaway.app.calendar


import android.view.View
import android.widget.ScrollView
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
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
import net.mythrowaway.app.module.trash.presentation.view.calendar.CalendarActivity
import net.mythrowaway.app.lib.AndroidTestHelper.Companion.waitUntilDisplayed
import net.mythrowaway.app.AndroidTestUtil
import org.hamcrest.core.IsInstanceOf
import org.junit.After
import org.junit.Before

@LargeTest
@RunWith(AndroidJUnit4::class)
class CalendarActivityTest3 {

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
    複数のゴミを登録するシナリオ
    - 複数のゴミを登録した場合にカレンダー画面のゴミ表記が複数行となること
    - もえるゴミ:毎週日曜日、その他-テスト:毎週日曜日を登録する
    - ゴミの表記は1行目:もえるゴミ,2行目:テストとなること
    - カレンダー画面から日付タップで起動されるダイアログのゴミ表記はもえるゴミ\nテストであること
    - 編集画面に登録したゴミが表示されること
     */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun add_two_trashes() {
        openDrawer()
        onView(withId(R.id.menuItemAdd)).perform(click())

        composeRule.onNodeWithTag(resource.getString(R.string.testTag_weekday_of_weekly_dropdown)).performClick()
        composeRule.waitUntil {
            composeRule.onAllNodesWithText("毎週 日曜日").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onAllNodesWithText("毎週 日曜日")[0].performClick()

        composeRule.onNodeWithText(resource.getString(R.string.text_register_trash_button)).performClick()
        composeRule.waitUntil {
            composeRule.onNodeWithText(resource.getString(R.string.message_complete_save_trash)).isDisplayed()
        }

        Espresso.pressBackUnconditionally()

        openDrawer()
        onView(withId(R.id.menuItemAdd)).perform(click())

        composeRule.onNodeWithTag(resource.getString(R.string.testTag_trash_type_dropdown)).performClick()
        // ドロップダウンが開くまで待機
        composeRule.waitUntil {
            composeRule.onNodeWithText("自分で入力").isDisplayed()
        }
        composeRule.onNodeWithText("自分で入力").performClick()
        composeRule.onNodeWithTag(resource.getString(R.string.testTag_trash_name_input)).performTextInput("テスト")
        Espresso.closeSoftKeyboard()
        composeRule.onNodeWithTag(resource.getString(R.string.testTag_weekday_of_weekly_dropdown)).performClick()
        composeRule.waitUntil {
            composeRule.onAllNodesWithText("毎週 日曜日").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onAllNodesWithText("毎週 日曜日")[0].performClick()
        composeRule.onNodeWithText(resource.getString(R.string.text_register_trash_button)).performClick()
        composeRule.waitUntil {
            composeRule.onNodeWithText(resource.getString(R.string.message_complete_save_trash)).isDisplayed()
        }

        Espresso.pressBackUnconditionally()

        waitUntilDisplayed("テスト",5000)

        waitUntilDisplayed("もえるゴミ", 5000)
        waitUntilDisplayed("テスト", 5000)

        val firstTrashText = onView(
            AndroidTestUtil.withIndex(
                allOf(
                    withId(R.id.trashText),
                    withText("もえるゴミ")
                ),
                0
            )!!
        )
        // ダイアログを開く
        firstTrashText.perform(click())
        val dialogTrashText = onView(
            allOf(
                withId(android.R.id.message),
                withParent(
                    withParent(
                        IsInstanceOf.instanceOf(
                            ScrollView::class.java
                        )
                    )
                ),
                isDisplayed()
            )
        )
        dialogTrashText.check(matches(withText("もえるゴミ\nテスト")))

        Espresso.pressBack()

        openDrawer()
        onView(withId(R.id.menuItemList)).perform(click())

        composeRule.waitUntil {
            composeRule.onNodeWithText("もえるゴミ").isDisplayed() &&
                    composeRule.onNodeWithText("テスト").isDisplayed()
        }
        composeRule.waitUntilNodeCount(
            matcher = hasText("毎週日曜日"),
            count =2
        )
    }
}
