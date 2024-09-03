package net.mythrowaway.app.calendar


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
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import net.mythrowaway.app.R
import org.hamcrest.Matchers.*
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import net.mythrowaway.app.AndroidTestUtil.Companion.childAtPosition
import net.mythrowaway.app.AndroidTestUtil.Companion.getText
import net.mythrowaway.app.domain.trash.presentation.view.calendar.CalendarActivity
import net.mythrowaway.app.domain.trash.presentation.view.edit.EditActivity
import org.junit.After
import org.junit.Before
import java.util.*

@LargeTest
@RunWith(AndroidJUnit4::class)
open class CalendarActivityTest {

    @get:Rule
    val mActivityScenarioRule: ActivityScenarioRule<CalendarActivity> = ActivityScenarioRule(
        CalendarActivity::class.java)

    @get:Rule
    val editActivityRule = createAndroidComposeRule(EditActivity::class.java)

    private val menuButton: ViewInteraction  = onView(
        allOf(
            childAtPosition(
                allOf(withId(R.id.calendarToolbar),
                    childAtPosition(
                        withId(R.id.calendarContainer),
                        0)),
                1),
            isDisplayed()))
    private val editMenuButton:ViewInteraction = onView(
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
    毎週月曜日にもえるゴミを追加するシナリオ
    - アプリ初期画面のタイトルが現在日時の年月であること
    - 毎週月曜日にもえるゴミが表示されること
    - 日付タップ時に正しい年月日ともえるゴミのテキストがダイアログに表示されること
     */
    @Test
    fun calendarActivityTest() {
        val today: Calendar = Calendar.getInstance()
        val titleString = "${today.get(Calendar.YEAR)}年${today.get(Calendar.MONTH)+1}月"
        val textView:ViewInteraction  = onView(
            allOf(withText(titleString),
                withParent(allOf(withId(R.id.calendarToolbar),
                    withParent(withId(R.id.calendarContainer)))),
                isDisplayed()))
        textView.check(matches(withText(titleString)))


        menuButton.perform(click())
        editMenuButton.perform(click())

        editActivityRule.onAllNodesWithTag("WeekdayOfWeeklySchedule")[0].performClick()
        // ドロップダウンが開くまで待機
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("毎週 月曜日").isDisplayed()
        }

        // 月曜日を選択
        editActivityRule.onNodeWithText("毎週 月曜日").performClick()


        // 登録ボタンを押下
        editActivityRule.onNodeWithText("登録").performClick()

        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("登録が完了しました").isDisplayed()
        }

        pressBack()

        Thread.sleep(2000)
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

            val appCompatEditText: ViewInteraction = onView(
                        allOf(
                            withId(R.id.linearLayout),
                            childAtPosition(
                                withId(R.id.calendar),
                                (position * 7 + 1)
                            ),
                    isDisplayed()
                )
            )
            appCompatEditText.perform(click())

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
            dialogTrashText.check(matches(withText("もえるゴミ")))

            var month = today.get(Calendar.MONTH) + 1
            var year = today.get(Calendar.YEAR)
            if(dateText.toInt() > 7 && position == 1) {
                month = if(month - 1 < 1) { year--; 12} else month - 1
            } else if(dateText.toInt() < 7 && position == 5 ) {
                month = if(month + 1 > 12) { year++; 1} else month + 1
            }
            val expectedTitle = "${year}年${month}月${dateText.toInt()}日"
            val dialogDateText = onView(
                allOf(
                    IsInstanceOf.instanceOf(TextView::class.java), withText(expectedTitle),
                    withParent(
                        allOf(
                            IsInstanceOf.instanceOf(
                                LinearLayout::class.java
                            ),
                            withParent(IsInstanceOf.instanceOf(LinearLayout::class.java))
                        )
                    ),
                    isDisplayed()
                )
            )
            dialogDateText.check(matches(withText(expectedTitle)))

            pressBack()
        }
    }
}
