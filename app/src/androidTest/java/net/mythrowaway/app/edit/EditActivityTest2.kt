package net.mythrowaway.app.edit


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import net.mythrowaway.app.R
import net.mythrowaway.app.view.calendar.CalendarActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class EditActivityTest2 {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(CalendarActivity::class.java)

    /*
     3件のスケジュールを設定して2件目を削除するシナリオ
     1件目と3件目が画面上に残り、追加ボタンが表示された状態になること
     */
    @Test
    fun editActivityTest2() {
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

        val appCompatToggleButton = onView(
            allOf(
                withId(R.id.toggleEveryMonth), withText("毎月"),
                childAtPosition(
                    allOf(
                        withId(R.id.scheduleTypeRow),
                        childAtPosition(
                                childAtPosition(withId(R.id.scheduleContainer), 2),
                            0
                        )
                    ),
                    1
                )
            )
        )
        appCompatToggleButton.perform(scrollTo(), click())

        val appCompatImageButton3 = onView(
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
                    3
                )
            )
        )
        appCompatImageButton3.perform(scrollTo(), click())

        val appCompatToggleButton2 = onView(
            allOf(
                withId(R.id.toggleNumOfWeek), withText("固定の週"),
                childAtPosition(
                    allOf(
                        withId(R.id.scheduleTypeRow),
                        childAtPosition(
                                childAtPosition(
                                    withId(R.id.scheduleContainer),
                        4),
                            0
                        )
                    ),
                    2
                )
            )
        )
        appCompatToggleButton2.perform(scrollTo(), click())

        val appCompatImageButton4 = onView(
            allOf(
                withId(R.id.deleteButton),
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
        appCompatImageButton4.perform(scrollTo(), click())

        // 2件目のスケジュールを削除した場合1件目は残っていること
        val textView = onView(
            allOf(
                withId(android.R.id.text1), withText("日曜日"),
                withParent(
                    allOf(
                        withId(R.id.weekdayWeekdayList),
                        withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView.check(matches(withText("日曜日")))

        // 2件目のスケジュールを削除した場合3件目は残っていること
        val textView2 = onView(
            allOf(
                withId(android.R.id.text1), withText("第1"),
                withParent(
                    allOf(
                        withId(R.id.numOfWeekList),
                        withParent(withId(R.id.numOfWeekContainer))
                    )
                ),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("第1")))

        // 2件目のスケジュールを削除した場合最下部に追加ボタンが表示されていること
        val imageButton2 = onView(
            allOf(
                withId(R.id.addButton),
                withParent(
                    allOf(
                        withId(R.id.scheduleContainer),
                        withParent(withId(R.id.scrollView2))
                    )
                ),
                withParentIndex(3),
                isDisplayed()
            )
        )
        imageButton2.check(matches(isDisplayed()))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
