package net.mythrowaway.app.calendar


import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import net.mythrowaway.app.R
import org.hamcrest.Matchers.*
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import net.mythrowaway.app.AndroidTestUtil.Companion.childAtPosition
import net.mythrowaway.app.AndroidTestUtil.Companion.getText
import net.mythrowaway.app.AndroidTestUtil
import net.mythrowaway.app.module.trash.presentation.view.calendar.CalendarActivity
import org.junit.After
import org.junit.Before
import java.util.*
import net.mythrowaway.app.lib.AndroidTestHelper.Companion.getTextByRes
import net.mythrowaway.app.lib.AndroidTestHelper.Companion.waitUntilDisplayed
import org.junit.Assert.assertEquals

@LargeTest
@RunWith(AndroidJUnit4::class)
open class CalendarActivityTest {

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
    毎週月曜日にもえるゴミを追加するシナリオ
    - アプリ初期画面のタイトルが現在日時の年月であること
    - 毎週月曜日にもえるゴミが表示されること
    - 日付タップ時に正しい年月日ともえるゴミのテキストがダイアログに表示されること
     */
    @Test
    fun add_trash_type_of_burn_with_schedule_of_every_monday() {
        val today: Calendar = Calendar.getInstance()
        val titleString = "${today.get(Calendar.YEAR)}年${today.get(Calendar.MONTH)+1}月"
        val textView:ViewInteraction  = onView(
            allOf(withText(titleString),
                withParent(allOf(withId(R.id.calendarToolbar),
                    withParent(withId(R.id.calendarContainer)))),
                isDisplayed()))
        textView.check(matches(withText(titleString)))


        openDrawer()
        onView(withId(R.id.menuItemAdd)).perform(click())

        composeRule.onAllNodesWithTag(resource.getString(R.string.testTag_weekday_of_weekly_dropdown))[0].performClick()
        // ドロップダウンが開くまで待機
        composeRule.waitUntil {
            composeRule.onNodeWithText("毎週 月曜日").isDisplayed()
        }

        // 月曜日を選択
        composeRule.onNodeWithText("毎週 月曜日").performClick()


        // 登録ボタンを押下
        composeRule.onNodeWithText(resource.getString(R.string.text_register_trash_button)).performClick()

        composeRule.waitUntil {
            composeRule.onNodeWithText(resource.getString(R.string.message_complete_save_trash)).isDisplayed()
        }

        pressBack()

        waitUntilDisplayed("もえるゴミ", 5000)
        for(position in 1..5) {
            val currentDateText = onView(
                allOf(
                    withId(R.id.dateText),
                    childAtPosition(
                        allOf(
                            withId(R.id.linearLayout),
                            childAtPosition(
                                withId(R.id.calendar),
                                (position * 7 + 1)
                            )
                        ),
                        0
                    ),
                    isDisplayed()
                ),
            )
            // 日にちを取得しておく
            val dateText = getText(currentDateText)

            val trashTextLayout = onView(
                allOf(
                    withId(R.id.trashTextListLayout),
                    childAtPosition(
                        allOf(
                            withId(R.id.linearLayout),
                            childAtPosition(
                                withId(R.id.calendar),
                                (position * 7 + 1)
                            )
                        ),
                        1
                    ),
                    isDisplayed()
                )
            )
            trashTextLayout.perform(click())

            val dialogMessage = runCatching {
                getTextByRes("android", "message", 2000)
            }.getOrElse {
                trashTextLayout.perform(click())
                getTextByRes("android", "message", 5000)
            }
            assertEquals("もえるゴミ", dialogMessage)

            var month = today.get(Calendar.MONTH) + 1
            var year = today.get(Calendar.YEAR)
            if(dateText.toInt() > 7 && position == 1) {
                month = if(month - 1 < 1) { year--; 12} else month - 1
            } else if(dateText.toInt() < 7 && position == 5 ) {
                month = if(month + 1 > 12) { year++; 1} else month + 1
            }
            val expectedTitle = "${year}年${month}月${dateText.toInt()}日"
            val dialogTitle = getTextByRes("android", "alertTitle", 5000)
            assertEquals(expectedTitle, dialogTitle)

            pressBack()
        }
    }
}
