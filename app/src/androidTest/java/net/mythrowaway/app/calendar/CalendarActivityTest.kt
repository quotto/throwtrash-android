package net.mythrowaway.app.calendar


import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.idling.CountingIdlingResource
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
import net.mythrowaway.app.view.calendar.CalendarActivity
import org.junit.After
import org.junit.Before
import java.util.*

@LargeTest
@RunWith(AndroidJUnit4::class)
open class CalendarActivityTest {

    @Rule @JvmField
    val mActivityScenarioRule: ActivityScenarioRule<CalendarActivity> = ActivityScenarioRule(
        CalendarActivity::class.java)
    var mIdlingResource: CountingIdlingResource? = null

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


        val appCompatImageButton: ViewInteraction  = onView(
            allOf(
                childAtPosition(
                    allOf(withId(R.id.calendarToolbar),
                        childAtPosition(
                            withId(R.id.calendarContainer),
                            0)),
                    1),
                isDisplayed()))
        appCompatImageButton.perform(click())

        val navigationMenuItemView:ViewInteraction = onView(
            allOf(withId(R.id.menuItemAdd),
                childAtPosition(
                    allOf(withId(R.id.design_navigation_view),
                        childAtPosition(
                            withId(R.id.main_nav_view),
                            0)),
                    1),
                isDisplayed()))
        navigationMenuItemView.perform(click())

        val appCompatSpinner:ViewInteraction = onView(
            allOf(withId(R.id.weekdayWeekdayList),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.scheduleInput),
                        0),
                    1),
                isDisplayed()))
        appCompatSpinner.perform(click())

        val appCompatTextView:DataInteraction = onData(anything())
            .inAdapterView(childAtPosition(
                withClassName(`is`("android.widget.PopupWindow\$PopupBackgroundView")),
                0))
            .atPosition(1)
        appCompatTextView.perform(click())

        val appCompatButton:ViewInteraction = onView(
            allOf(withId(R.id.registerButton), withText("登録"),
                childAtPosition(
                    allOf(withId(R.id.buttonContainer),
                        childAtPosition(
                            withId(R.id.mainScheduleContainer),
                            3)),
                    0),
                isDisplayed()))
        appCompatButton.perform(click())

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
