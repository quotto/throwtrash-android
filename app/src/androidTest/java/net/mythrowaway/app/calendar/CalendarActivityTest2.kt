package net.mythrowaway.app.calendar


import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
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
import java.util.*

@LargeTest
@RunWith(AndroidJUnit4::class)
class CalendarActivityTest2 {

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
    毎月3日と第1土曜日にもえないゴミを設定するシナリオ
     */
    @Test
    fun calendarActivityTest2() {
        menuButton.perform(click())
        editMenuButton.perform(click())

        editActivityRule.onNodeWithTag("TrashType").performClick()
        // ドロップダウンが開くまで待機
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("もえないゴミ").isDisplayed()
        }
        editActivityRule.onNodeWithText("もえないゴミ").performClick()
        editActivityRule.onNodeWithText("毎月").performClick()
        editActivityRule.onNodeWithTag("DayOfMonthlySchedule").performClick()
        // ドロップダウンが開くまで待機
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("毎月 3 日").isDisplayed()
        }
        editActivityRule.onNodeWithText("毎月 3 日").performClick()
        editActivityRule.onNodeWithTag("AddScheduleButton").performClick()

        editActivityRule.onAllNodesWithText("毎週(第○曜日)")[1].performClick()
        editActivityRule.onNodeWithTag("WeekdayOfOrdinalWeeklySchedule").performClick()
        // ドロップダウンが開くまで待機
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("土曜日").isDisplayed()
        }

        editActivityRule.onNodeWithText("土曜日").performClick()

        // 登録ボタンを押下
        editActivityRule.onNodeWithText("登録").performClick()

        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("登録が完了しました").isDisplayed()
        }

        Espresso.pressBack()

        Thread.sleep(2000)

        val trashTextAtFirstSaturday = onView(
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
                                        13
                                    )
                                ),
                                1
                            ),
                        ),
                        0
                    ),
                    0
                ),
                isDisplayed()
            ),
        )
        trashTextAtFirstSaturday.check(matches(withText("もえないゴミ")))

        val today:Calendar = Calendar.getInstance()
        today.set(Calendar.DATE, 3)
        val dayOfWeek = today.get(Calendar.DAY_OF_WEEK)
        val targetRow = if(dayOfWeek >= 3) 1 else 2
        val thirdDayPosition = targetRow * 7 + (dayOfWeek-1)
        val trashTextAtThirdDay = onView(
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
                                        thirdDayPosition
                                    )
                                ),
                        1
                            ),
                        ),
                0
                    ),
            0
                ),
                isDisplayed()
            ),
        )
        trashTextAtThirdDay.check(matches(withText("もえないゴミ")))
    }
}
