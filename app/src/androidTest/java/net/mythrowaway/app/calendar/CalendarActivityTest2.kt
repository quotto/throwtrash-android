package net.mythrowaway.app.calendar


import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.idling.CountingIdlingResource
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
import net.mythrowaway.app.view.calendar.CalendarActivity
import org.junit.After
import org.junit.Before
import java.util.*

@LargeTest
@RunWith(AndroidJUnit4::class)
class CalendarActivityTest2 {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(CalendarActivity::class.java)

    private var mIdlingResource: CountingIdlingResource? = null

    @Before
    fun setUp(){
        mActivityScenarioRule.scenario.onActivity { activity ->
            mIdlingResource = activity.getIdlingResources()
            IdlingRegistry.getInstance().register(mIdlingResource)
        }
    }

    @After
    fun tearDown (){
        if(mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource)
        }
    }

    /*
    毎月3日と第1土曜日にもえないゴミを設定するシナリオ
     */
    @Test
    fun calendarActivityTest2() {
        val appCompatImageButton = onView(
            allOf(
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
        appCompatImageButton.perform(click())

        val navigationMenuItemView = onView(
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
        navigationMenuItemView.perform(click())

        val appCompatSpinner = onView(
            allOf(
                withId(R.id.trashTypeList),
                childAtPosition(
                    allOf(
                        withId(R.id.trashTypeContainer),
                        childAtPosition(
                            withId(R.id.mainScheduleContainer),
                            2
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatSpinner.perform(click())

        val appCompatTextView = onData(anything())
            .inAdapterView(
                childAtPosition(
                    withClassName(`is`("android.widget.PopupWindow\$PopupBackgroundView")),
                    0
                )
            )
            .atPosition(1)
        appCompatTextView.perform(click())

        val appCompatToggleButton = onView(
            allOf(
                withId(R.id.toggleEveryMonth), withText("毎月"),
                    childAtPosition(
                        allOf(
                            withId(R.id.scheduleTypeRow),
                            childAtPosition(
                                allOf(
                                    childAtPosition(withId(R.id.scheduleContainer),0),
                                    withId(R.id.scheduleType)),
                                0
                            )
                        ),
                    1
                )
            )
        )
        appCompatToggleButton.perform(scrollTo(), click())

        val appCompatSpinner2 = onView(
            allOf(
                withId(R.id.monthDateList),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.scheduleInput),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatSpinner2.perform(click())

        val appCompatTextView2 = onData(anything())
            .inAdapterView(
                childAtPosition(
                    withClassName(`is`("android.widget.PopupWindow\$PopupBackgroundView")),
                    0
                )
            )
            .atPosition(2)
        appCompatTextView2.perform(click())

        val appCompatImageButton2 = onView(
            allOf(
                withId(R.id.addButton),
                childAtPosition(
                    allOf(
                        withId(R.id.scheduleContainer),
                        childAtPosition(
                            withId(R.id.scrollView2),
                            0
                        )
                    ),
                    1
                )
            )
        )
        appCompatImageButton2.perform(scrollTo(), click())

        val appCompatToggleButton2 = onView(
            allOf(
                withId(R.id.toggleNumOfWeek), withText("固定の週"),
                childAtPosition(
                    allOf(
                        withId(R.id.scheduleTypeRow),
                        childAtPosition(
                            allOf(withParentIndex(2),withId(R.id.scheduleType)),
                            0
                        )
                    ),
                    2
                )
            )
        )
        appCompatToggleButton2.perform(scrollTo(), click())

        val appCompatSpinner4 = onView(
            allOf(
                withId(R.id.numOfWeekWeekdayList),
                childAtPosition(
                    allOf(
                        withId(R.id.numOfWeekContainer),
                        childAtPosition(
                            allOf(
                                withId(R.id.scheduleInput),
                                childAtPosition(
                                    allOf(withParentIndex(2),withId(R.id.scheduleType)),
                                    1
                                )
                            ),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatSpinner4.perform(click())

        val appCompatTextView3 = onData(anything())
            .inAdapterView(
                childAtPosition(
                    withClassName(`is`("android.widget.PopupWindow\$PopupBackgroundView")),
                    0
                )
            )
            .atPosition(6)
        appCompatTextView3.perform(click())

        val appCompatButton = onView(
            allOf(
                withId(R.id.registerButton), withText("登録"),
                childAtPosition(
                    allOf(
                        withId(R.id.buttonContainer),
                        childAtPosition(
                            withId(R.id.mainScheduleContainer),
                            3
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatButton.perform(click())

        val trashTextAtFirstSaturday = onView(
            allOf(
                withId(R.id.trashText),
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
                    allOf(
                        withId(R.id.linearLayout),
                        childAtPosition(
                            withId(R.id.calendar),
                            thirdDayPosition
                        )
                    ),
                    1
                ),
                isDisplayed()
            ),
        )
        trashTextAtThirdDay.check(matches(withText("もえないゴミ")))
    }
}
