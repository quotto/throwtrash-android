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
import net.mythrowaway.app.AndroidTestUtil
import net.mythrowaway.app.R
import net.mythrowaway.app.domain.trash.presentation.view.calendar.CalendarActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class EditActivityTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(CalendarActivity::class.java)

    /*
    2件のスケジュールを設定して2件目を削除するシナリオ
    1件目が画面上に残り、追加ボタンが表示された状態になること
     */
    @Test
    fun editActivityTest() {
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

//        val appCompatToggleButton = onView(
//            allOf(
//                withId(R.id.toggleNumOfWeek), withText("固定の週"),
//                childAtPosition(
//                    allOf(
//                        withId(R.id.scheduleTypeRow),
//                        childAtPosition(
//                            allOf(
//                                AndroidTestUtil.childAtPosition(withId(R.id.scheduleContainer), 0),
//                            ),
//                            0
//                        )
//                    ),
//                    2
//                )
//            )
//        )
//        appCompatToggleButton.perform(scrollTo(), click())

//        val appCompatImageButton2 = onView(
//            allOf(
//                withId(R.id.addButton),
//                childAtPosition(
//                    allOf(
//                        withId(R.id.scheduleContainer),
//                        childAtPosition(
//                            withId(R.id.scrollView2),
//                            0
//                        )
//                    ),
//                    1
//                )
//            )
//        )
//        appCompatImageButton2.perform(scrollTo(), click())


//        val appCompatImageButton3 = onView(
//            allOf(
//                withId(R.id.deleteButton),
//                childAtPosition(
//                    allOf(
//                        withId(R.id.scheduleContainer),
//                        childAtPosition(
//                            withId(R.id.scrollView2),
//                            0
//                        )
//                    ),
//                    1
//                )
//            )
//        )
//        appCompatImageButton3.perform(scrollTo(), click())

        // scheduleContainerの2つ目が追加ボタン＝1件目のスケジュールが残って2件目が削除されていること
//        val imageButton = onView(
//            allOf(
//                withId(R.id.addButton),
//                withParent(
//                    allOf(
//                        withId(R.id.scheduleContainer),
//                        withParent(withId(R.id.scrollView2))
//                    )
//                ),
//                withParentIndex(1),
//                isDisplayed()
//            )
//        )
//        imageButton.check(matches(isDisplayed()))
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
