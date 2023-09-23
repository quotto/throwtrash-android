package net.mythrowaway.app.calendar


import android.widget.ScrollView
import androidx.test.espresso.Espresso
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
import org.hamcrest.core.IsInstanceOf
import org.junit.After
import org.junit.Before

@LargeTest
@RunWith(AndroidJUnit4::class)
class CalendarActivityTest3 {

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
    複数のゴミを登録するシナリオ
    - 複数のゴミを登録した場合にカレンダー画面のゴミ表記がもえるゴミ/テストとなること
    - カレンダー画面から日付タップで起動されるダイアログのゴミ表記はもえるゴミ\nテストであること
    - 編集画面に登録したゴミが表示されること
     */
    @Test
    fun calendarActivityTest3() {
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

        val appCompatImageButton2 = onView(
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
        appCompatImageButton2.perform(click())

        val navigationMenuItemView2 = onView(
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
        navigationMenuItemView2.perform(click())

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
        appCompatEditText.perform(replaceText("テスト"), closeSoftKeyboard())

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.otherTrashText), withText("テスト"),
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

        val appCompatButton2 = onView(
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
        appCompatButton2.perform(click())

        val trashTextAtSunday = onView(
            allOf(
                withId(R.id.trashText),
                childAtPosition(
                    allOf(
                        withId(R.id.linearLayout),
                        childAtPosition(
                            withId(R.id.calendar),
                            7
                        )
                    ),
                    1
                ),
                isDisplayed()
            ),
        )
        trashTextAtSunday.check(matches(withText("もえるゴミ/テスト")))

        // ダイアログを開く
        trashTextAtSunday.perform(click())
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

        val appCompatImageButton3 = onView(
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
        appCompatImageButton3.perform(click())

        val navigationMenuItemView3 = onView(
            allOf(
                withId(R.id.menuItemList),
                childAtPosition(
                    allOf(
                        withId(R.id.design_navigation_view),
                        childAtPosition(
                            withId(R.id.main_nav_view),
                            0
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        navigationMenuItemView3.perform(click())

        val textView = onView(
            allOf(
                withId(R.id.item_trashType), withText("もえるゴミ"),
                withParent(
                    allOf(
                        withId(R.id.item_container),
                        withParent(withId(R.id.scheduleListFragment)),
                        withParentIndex(0)
                    )
                ),
                isDisplayed()
            )
        )
        textView.check(matches(withText("もえるゴミ")))

        val textView2 = onView(
            allOf(
                withText("毎週日曜日"),
                withParent(
                    allOf(
                        withId(R.id.item_schedule),
                        withParent(
                            allOf(
                                withId(R.id.item_container),
                                withParent(withId(R.id.scheduleListFragment)),
                                withParentIndex(0)
                            )
                        ),
                    ),
                ),
                withParentIndex(0),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("毎週日曜日")))

        val textView3 = onView(
            allOf(
                withId(R.id.item_trashType), withText("テスト"),
                withParent(
                    allOf(
                        withId(R.id.item_container),
                        withParent(withId(R.id.scheduleListFragment)),
                        withParentIndex(1)
                    )
                ),
                isDisplayed()
            )
        )
        textView3.check(matches(withText("テスト")))

        val textView4 = onView(
            allOf(
                withText("毎週日曜日"),
                withParent(
                    allOf(
                        withId(R.id.item_schedule),
                        withParent(
                            allOf(
                                withId(R.id.item_container),
                                withParent(withId(R.id.scheduleListFragment)),
                                withParentIndex(1)
                            )
                        ),
                    ),
                ),
                withParentIndex(0),
                isDisplayed()
            )
        )
        textView4.check(matches(withText("毎週日曜日")))
    }
}
