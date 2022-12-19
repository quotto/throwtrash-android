package net.mythrowaway.app.edit


import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
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
import net.mythrowaway.app.view.CalendarActivity

@LargeTest
@RunWith(AndroidJUnit4::class)
class EditActivityTest3 {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(CalendarActivity::class.java)

    /*
    自分で入力時に空入力の場合はエラーメッセージが表示されるシナリオ
     */
    @Test
    fun editActivityTest3() {
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
            .atPosition(9)
        appCompatTextView.perform(click())

        val appCompatEditText = onView(
            allOf(
                withId(R.id.otherTrashText),
                childAtPosition(
                    allOf(
                        withId(R.id.trashTypeContainer),
                        childAtPosition(
                            withId(R.id.mainScheduleContainer),
                            2
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText.perform(replaceText("a"), closeSoftKeyboard())

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.otherTrashText), withText("a"),
                childAtPosition(
                    allOf(
                        withId(R.id.trashTypeContainer),
                        childAtPosition(
                            withId(R.id.mainScheduleContainer),
                            2
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText2.perform(pressImeActionButton())

        // 1文字以上入力した場合はエラーメッセージが表示されない
        val textView2 = onView(
            allOf(
                withId(R.id.otherTrashErrorText), withText("10文字以内で入力してください"),
                withParent(
                    allOf(
                        withId(R.id.trashTypeContainer),
                        withParent(withId(R.id.mainScheduleContainer))
                    )
                ),
                isDisplayed()
            )
        )
        textView2.check(doesNotExist())


        val appCompatEditText3 = onView(
            allOf(
                withId(R.id.otherTrashText), withText("a"),
                childAtPosition(
                    allOf(
                        withId(R.id.trashTypeContainer),
                        childAtPosition(
                            withId(R.id.mainScheduleContainer),
                            2
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText3.perform(replaceText(""))

        val appCompatEditText4 = onView(
            allOf(
                withId(R.id.otherTrashText),
                childAtPosition(
                    allOf(
                        withId(R.id.trashTypeContainer),
                        childAtPosition(
                            withId(R.id.mainScheduleContainer),
                            2
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText4.perform(closeSoftKeyboard())

        val appCompatEditText5 = onView(
            allOf(
                withId(R.id.otherTrashText),
                childAtPosition(
                    allOf(
                        withId(R.id.trashTypeContainer),
                        childAtPosition(
                            withId(R.id.mainScheduleContainer),
                            2
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText5.perform(pressImeActionButton())

        val textView = onView(
            allOf(
                withId(R.id.otherTrashErrorText), withText("10文字以内で入力してください"),
                withParent(
                    allOf(
                        withId(R.id.trashTypeContainer),
                        withParent(withId(R.id.mainScheduleContainer))
                    )
                ),
                isDisplayed()
            )
        )
        textView.check(matches(withText("10文字以内で入力してください")))

    }
}
