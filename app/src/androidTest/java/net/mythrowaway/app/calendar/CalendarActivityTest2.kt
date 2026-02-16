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
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
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
import java.util.*

@LargeTest
@RunWith(AndroidJUnit4::class)
class CalendarActivityTest2 {

    @get:Rule
    val composeRule = createAndroidComposeRule(CalendarActivity::class.java)

    private val drawerLayout: ViewInteraction = onView(withId(R.id.calendarActivityRoot))
    private val navigationView: ViewInteraction = onView(withId(R.id.main_nav_view))

    private val resource = InstrumentationRegistry.getInstrumentation().targetContext.resources
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
    fun add_trash_type_of_unburn_with_schedule_of_monthly_3_and_first_saturday() {
        drawerLayout.perform(DrawerActions.open())
        navigationView.perform(NavigationViewActions.navigateTo(R.id.menuItemAdd))

        composeRule.onNodeWithTag(resource.getString(R.string.testTag_trash_type_dropdown)).performClick()
        // ドロップダウンが開くまで待機
        composeRule.waitUntil {
            composeRule.onNodeWithText("もえないゴミ").isDisplayed()
        }
        composeRule.onNodeWithText("もえないゴミ").performClick()
        composeRule.onNodeWithText(resource.getString(R.string.text_monthly_toggle_button)).performClick()
        composeRule.onNodeWithTag(resource.getString(R.string.testTag_day_of_month_of_monthly_dropdown)).performClick()
        // ドロップダウンが開くまで待機
        composeRule.waitUntil {
            composeRule.onNodeWithText("毎月 3 日").isDisplayed()
        }
        composeRule.onNodeWithText("毎月 3 日").performClick()
        composeRule.onNodeWithTag(resource.getString(R.string.testTag_add_schedule_button)).performClick()

        composeRule.onAllNodesWithText(resource.getString(R.string.text_ordinal_weekday_toggle_button))[1].performClick()
        composeRule.onNodeWithTag(resource.getString(R.string.testTag_weekday_of_ordinal_weekly_dropdown)).performClick()
        // ドロップダウンが開くまで待機
        composeRule.waitUntil {
            composeRule.onNodeWithText("土曜日").isDisplayed()
        }

        composeRule.onNodeWithText("土曜日").performClick()

        // 登録ボタンを押下
        composeRule.onNodeWithText(resource.getString(R.string.text_register_trash_button)).performClick()

        composeRule.waitUntil {
            composeRule.onNodeWithText(resource.getString(R.string.message_complete_save_trash)).isDisplayed()
        }

        Espresso.pressBack()

        waitUntilDisplayed("もえないゴミ", 5000)

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

    /*
    例外日メニューから共通例外日設定画面へ遷移するシナリオ
     */
    @Test
    fun navigate_to_common_exclude_day_screen_from_menu() {
        drawerLayout.perform(DrawerActions.open())
        navigationView.perform(NavigationViewActions.navigateTo(R.id.menuItemExceptionDays))

        composeRule.waitUntil {
            composeRule.onNodeWithText(
                resource.getString(R.string.text_title_common_exclude_day_of_month)
            ).isDisplayed()
        }
    }
}
