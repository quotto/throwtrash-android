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
import net.mythrowaway.app.domain.trash.presentation.view.calendar.CalendarActivity

@LargeTest
@RunWith(AndroidJUnit4::class)
class EditActivityTest4 {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(CalendarActivity::class.java)

    /*
    複数のゴミ出しスケジュールを登録するシナリオ
    - 4種類のスケジュールを登録する
    - 一覧画面にゴミの名前とスケジュールが正しく表示されていること。
    - 2件目を削除した場合一覧にはそれ以外のデータが正しく表示されること。
     */
    @Test
    fun editActivityTest4() {
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

//        val appCompatSpinner = onView(
//            allOf(
//                withId(R.id.weekdayWeekdayList),
//                childAtPosition(
//                    childAtPosition(
//                        withId(R.id.scheduleInput),
//                        0
//                    ),
//                    1
//                ),
//                isDisplayed()
//            )
//        )
//        appCompatSpinner.perform(click())

        val appCompatTextView = onData(anything())
            .inAdapterView(
                childAtPosition(
                    withClassName(`is`("android.widget.PopupWindow\$PopupBackgroundView")),
                    0
                )
            )
            .atPosition(4)
        appCompatTextView.perform(click())

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

//        val appCompatSpinner2 = onView(
//            allOf(
//                withId(R.id.weekdayWeekdayList),
//                childAtPosition(
//                    childAtPosition(
//                        allOf(
//                            withId(R.id.scheduleInput),
//                            withParent(
//                                allOf(
//                                    withId(R.id.scheduleType),
//                                    withParent(withId(R.id.scheduleContainer)),
//                                    withParentIndex(2)
//                                )
//                            )
//                        ),
//                        0
//                    ),
//                    1
//                ),
//                isDisplayed()
//            )
//        )
//        appCompatSpinner2.perform(click())

        val appCompatTextView2 = onData(anything())
            .inAdapterView(
                childAtPosition(
                    withClassName(`is`("android.widget.PopupWindow\$PopupBackgroundView")),
                    0
                )
            )
            .atPosition(5)
        appCompatTextView2.perform(click())

//        val appCompatButton = onView(
//            allOf(
//                withId(R.id.registerButton), withText("登録"),
//                childAtPosition(
//                    allOf(
//                        withId(R.id.buttonContainer),
//                        childAtPosition(
//                            withId(R.id.mainScheduleContainer),
//                            3
//                        )
//                    ),
//                    0
//                ),
//                isDisplayed()
//            )
//        )
//        appCompatButton.perform(click())

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

//        val appCompatSpinner3 = onView(
//            allOf(
//                withId(R.id.trashTypeList),
//                childAtPosition(
//                    allOf(
//                        withId(R.id.trashTypeContainer),
//                        childAtPosition(
//                            withId(R.id.mainScheduleContainer),
//                            2
//                        )
//                    ),
//                    2
//                ),
//                isDisplayed()
//            )
//        )
//        appCompatSpinner3.perform(click())

        val appCompatTextView3 = onData(anything())
            .inAdapterView(
                childAtPosition(
                    withClassName(`is`("android.widget.PopupWindow\$PopupBackgroundView")),
                    0
                )
            )
            .atPosition(1)
        appCompatTextView3.perform(click())

//        val appCompatToggleButton = onView(
//            allOf(
//                withId(R.id.toggleEveryMonth), withText("毎月"),
//                childAtPosition(
//                    allOf(
//                        withId(R.id.scheduleTypeRow),
//                        childAtPosition(
//                            withId(R.id.scheduleType),
//                            0
//                        )
//                    ),
//                    1
//                )
//            )
//        )
//        appCompatToggleButton.perform(scrollTo(), click())

//        val appCompatSpinner4 = onView(
//            allOf(
//                withId(R.id.monthDateList),
//                childAtPosition(
//                    childAtPosition(
//                        withId(R.id.scheduleInput),
//                        0
//                    ),
//                    1
//                ),
//                isDisplayed()
//            )
//        )
//        appCompatSpinner4.perform(click())

        val appCompatTextView4 = onData(anything())
            .inAdapterView(
                childAtPosition(
                    withClassName(`is`("android.widget.PopupWindow\$PopupBackgroundView")),
                    0
                )
            )
            .atPosition(2)
        appCompatTextView4.perform(click())

//        val appCompatButton2 = onView(
//            allOf(
//                withId(R.id.registerButton), withText("登録"),
//                childAtPosition(
//                    allOf(
//                        withId(R.id.buttonContainer),
//                        childAtPosition(
//                            withId(R.id.mainScheduleContainer),
//                            3
//                        )
//                    ),
//                    0
//                ),
//                isDisplayed()
//            )
//        )
//        appCompatButton2.perform(click())

        val appCompatImageButton5 = onView(
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
        appCompatImageButton5.perform(click())

        val navigationMenuItemView3 = onView(
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
        navigationMenuItemView3.perform(click())

//        val appCompatSpinner5 = onView(
//            allOf(
//                withId(R.id.trashTypeList),
//                childAtPosition(
//                    allOf(
//                        withId(R.id.trashTypeContainer),
//                        childAtPosition(
//                            withId(R.id.mainScheduleContainer),
//                            2
//                        )
//                    ),
//                    2
//                ),
//                isDisplayed()
//            )
//        )
//        appCompatSpinner5.perform(click())

        val appCompatTextView5 = onData(anything())
            .inAdapterView(
                childAtPosition(
                    withClassName(`is`("android.widget.PopupWindow\$PopupBackgroundView")),
                    0
                )
            )
            .atPosition(5)
        appCompatTextView5.perform(click())

//        val appCompatToggleButton2 = onView(
//            allOf(
//                withId(R.id.toggleNumOfWeek), withText("固定の週"),
//                childAtPosition(
//                    allOf(
//                        withId(R.id.scheduleTypeRow),
//                        childAtPosition(
//                            withId(R.id.scheduleType),
//                            0
//                        )
//                    ),
//                    2
//                )
//            )
//        )
//        appCompatToggleButton2.perform(scrollTo(), click())

//        val appCompatSpinner6 = onView(
//            allOf(
//                withId(R.id.numOfWeekList),
//                childAtPosition(
//                    allOf(
//                        withId(R.id.numOfWeekContainer),
//                        childAtPosition(
//                            withId(R.id.scheduleInput),
//                            0
//                        )
//                    ),
//                    0
//                ),
//                isDisplayed()
//            )
//        )
//        appCompatSpinner6.perform(click())

        val appCompatTextView6 = onData(anything())
            .inAdapterView(
                childAtPosition(
                    withClassName(`is`("android.widget.PopupWindow\$PopupBackgroundView")),
                    0
                )
            )
            .atPosition(2)
        appCompatTextView6.perform(click())

//        val appCompatSpinner7 = onView(
//            allOf(
//                withId(R.id.numOfWeekWeekdayList),
//                childAtPosition(
//                    allOf(
//                        withId(R.id.numOfWeekContainer),
//                        childAtPosition(
//                            withId(R.id.scheduleInput),
//                            0
//                        )
//                    ),
//                    1
//                ),
//                isDisplayed()
//            )
//        )
//        appCompatSpinner7.perform(click())

        val appCompatTextView7 = onData(anything())
            .inAdapterView(
                childAtPosition(
                    withClassName(`is`("android.widget.PopupWindow\$PopupBackgroundView")),
                    0
                )
            )
            .atPosition(2)
        appCompatTextView7.perform(click())

//        val appCompatButton3 = onView(
//            allOf(
//                withId(R.id.registerButton), withText("登録"),
//                childAtPosition(
//                    allOf(
//                        withId(R.id.buttonContainer),
//                        childAtPosition(
//                            withId(R.id.mainScheduleContainer),
//                            3
//                        )
//                    ),
//                    0
//                ),
//                isDisplayed()
//            )
//        )
//        appCompatButton3.perform(click())

        val appCompatImageButton6 = onView(
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
        appCompatImageButton6.perform(click())

        val navigationMenuItemView4 = onView(
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
        navigationMenuItemView4.perform(click())

//        val appCompatToggleButton3 = onView(
//            allOf(
//                withId(R.id.toggleEvWeek), withText("隔週"),
//                childAtPosition(
//                    allOf(
//                        withId(R.id.scheduleTypeRow),
//                        childAtPosition(
//                            withId(R.id.scheduleType),
//                            0
//                        )
//                    ),
//                    3
//                )
//            )
//        )
//        appCompatToggleButton3.perform(scrollTo(), click())

//        val appCompatSpinner8 = onView(
//            allOf(
//                withId(R.id.trashTypeList),
//                childAtPosition(
//                    allOf(
//                        withId(R.id.trashTypeContainer),
//                        childAtPosition(
//                            withId(R.id.mainScheduleContainer),
//                            2
//                        )
//                    ),
//                    2
//                ),
//                isDisplayed()
//            )
//        )
//        appCompatSpinner8.perform(click())

        val appCompatTextView8 = onData(anything())
            .inAdapterView(
                childAtPosition(
                    withClassName(`is`("android.widget.PopupWindow\$PopupBackgroundView")),
                    0
                )
            )
            .atPosition(9)
        appCompatTextView8.perform(click())

//        val appCompatEditText = onView(
//            allOf(
//                withId(R.id.otherTrashText),
//                childAtPosition(
//                    allOf(
//                        withId(R.id.trashTypeContainer),
//                        childAtPosition(
//                            withId(R.id.mainScheduleContainer),
//                            2
//                        )
//                    ),
//                    1
//                ),
//                isDisplayed()
//            )
//        )
//        appCompatEditText.perform(replaceText("テスト"), closeSoftKeyboard())

//        val appCompatEditText2 = onView(
//            allOf(
//                withId(R.id.otherTrashText), withText("テスト"),
//                childAtPosition(
//                    allOf(
//                        withId(R.id.trashTypeContainer),
//                        childAtPosition(
//                            withId(R.id.mainScheduleContainer),
//                            2
//                        )
//                    ),
//                    1
//                ),
//                isDisplayed()
//            )
//        )
//        appCompatEditText2.perform(pressImeActionButton())

//        val appCompatSpinner9 = onView(
//            allOf(
//                withId(R.id.evweekWeekdayList),
//                childAtPosition(
//                    childAtPosition(
//                        withId(R.id.scheduleInput),
//                        0
//                    ),
//                    2
//                ),
//                isDisplayed()
//            )
//        )
//        appCompatSpinner9.perform(click())

        val appCompatTextView9 = onData(anything())
            .inAdapterView(
                childAtPosition(
                    withClassName(`is`("android.widget.PopupWindow\$PopupBackgroundView")),
                    0
                )
            )
            .atPosition(6)
        appCompatTextView9.perform(click())

//        val appCompatButton4 = onView(
//            allOf(
//                withId(R.id.registerButton), withText("登録"),
//                childAtPosition(
//                    allOf(
//                        withId(R.id.buttonContainer),
//                        childAtPosition(
//                            withId(R.id.mainScheduleContainer),
//                            3
//                        )
//                    ),
//                    0
//                ),
//                isDisplayed()
//            )
//        )
//        appCompatButton4.perform(click())

        val appCompatImageButton7 = onView(
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
        appCompatImageButton7.perform(click())

        val navigationMenuItemView5 = onView(
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
        navigationMenuItemView5.perform(click())

//        val textView = onView(
//            allOf(
//                withId(R.id.item_trashType), withText("もえるゴミ"),
//                withParent(
//                    allOf(
//                        withId(R.id.item_container),
//                        withParent(withId(R.id.scheduleListFragment)),
//                        withParentIndex(0)
//                    )
//                ),
//                isDisplayed()
//            )
//        )
//        textView.check(matches(withText("もえるゴミ")))

//        val textView2 = onView(
//            allOf(
//                withText("毎週木曜日"),
//                withParent(
//                    allOf(
//                        withId(R.id.item_schedule),
//                        withParent(
//                            allOf(
//                                withId(R.id.item_container),
//                                withParentIndex(0)
//                            )
//                        )
//                    )
//                ),
//                withParentIndex(0),
//                isDisplayed()
//            )
//        )
//        textView2.check(matches(withText("毎週木曜日")))

//        val textView3 = onView(
//            allOf(
//                withText("毎週金曜日"),
//                withParent(
//                    allOf(
//                        withId(R.id.item_schedule),
//                        withParent(
//                            allOf(
//                                withId(R.id.item_container),
//                                withParentIndex(0)
//                            )
//                        )
//                    )
//                ),
//                withParentIndex(1),
//                isDisplayed()
//            )
//        )
//        textView3.check(matches(withText("毎週金曜日")))

//        val textView4 = onView(
//            allOf(
//                withId(R.id.item_trashType), withText("もえないゴミ"),
//                withParent(
//                    allOf(
//                        withId(R.id.item_container),
//                        withParent(withId(R.id.scheduleListFragment)),
//                        withParentIndex(1)
//                    )
//                ),
//                isDisplayed()
//            )
//        )
//        textView4.check(matches(withText("もえないゴミ")))

//        val textView5 = onView(
//            allOf(
//                withText("毎月3日"),
//                withParent(
//                    allOf(
//                        withId(R.id.item_schedule),
//                        withParent(
//                            allOf(
//                                withId(R.id.item_container),
//                                withParentIndex(1)
//                            )
//                        )
//                    )
//                ),
//                withParentIndex(0),
//                isDisplayed()
//            )
//        )
//        textView5.check(matches(withText("毎月3日")))

//        val textView6 = onView(
//            allOf(
//                withId(R.id.item_trashType), withText("ペットボトル"),
//                withParent(
//                    allOf(
//                        withId(R.id.item_container),
//                        withParent(withId(R.id.scheduleListFragment)),
//                        withParentIndex(2)
//                    )
//                ),
//                isDisplayed()
//            )
//        )
//        textView6.check(matches(withText("ペットボトル")))

//        val textView7 = onView(
//            allOf(
//                withText("第3火曜日"),
//                withParent(
//                    allOf(
//                        withId(R.id.item_schedule),
//                        withParent(
//                            allOf(
//                                withId(R.id.item_container),
//                                withParentIndex(2)
//                            )
//                        )
//                    )
//                ),
//                withParentIndex(0),
//                isDisplayed()
//            )
//        )
//        textView7.check(matches(withText("第3火曜日")))

//        val textView8 = onView(
//            allOf(
//                withId(R.id.item_trashType), withText("テスト"),
//                withParent(
//                    allOf(
//                        withId(R.id.item_container),
//                        withParent(withId(R.id.scheduleListFragment)),
//                        withParentIndex(3)
//                    )
//                ),
//                isDisplayed()
//            )
//        )
//        textView8.check(matches(withText("テスト")))

//        val textView9 = onView(
//            allOf(
//                withText("隔週土曜日"),
//                withParent(
//                    allOf(
//                        withId(R.id.item_schedule),
//                        withParent(
//                            allOf(
//                                withId(R.id.item_container),
//                                withParentIndex(3)
//                            )
//                        )
//                    )
//                ),
//                withParentIndex(0),
//                isDisplayed()
//            )
//        )
//        textView9.check(matches(withText("隔週土曜日")))

//        val appCompatImageButton8 = onView(
//            allOf(
//                withId(R.id.item_deleteScheduleButton),
//                childAtPosition(
//                    allOf(
//                        withId(R.id.item_container),
//                        childAtPosition(
//                            withId(R.id.scheduleListFragment),
//                            1
//                        )
//                    ),
//                    1
//                ),
//                isDisplayed()
//            )
//        )
//        appCompatImageButton8.perform(click())

//        val textView10 = onView(
//            allOf(
//                withId(R.id.item_trashType), withText("もえるゴミ"),
//                withParent(
//                    allOf(
//                        withId(R.id.item_container),
//                        withParent(withId(R.id.scheduleListFragment)),
//                        withParentIndex(0)
//                    )
//                ),
//                isDisplayed()
//            )
//        )
//        textView10.check(matches(withText("もえるゴミ")))

//        val textView11 = onView(
//            allOf(
//                withText("毎週木曜日"),
//                withParent(
//                    allOf(
//                        withId(R.id.item_schedule),
//                        withParent(
//                            allOf(
//                                withId(R.id.item_container),
//                                withParentIndex(0)
//                            )
//                        )
//                    )
//                ),
//                withParentIndex(0),
//                isDisplayed()
//            )
//        )
//        textView11.check(matches(withText("毎週木曜日")))

//        val textView12 = onView(
//            allOf(
//                withText("毎週金曜日"),
//                withParent(
//                    allOf(
//                        withId(R.id.item_schedule),
//                        withParent(
//                            allOf(
//                                withId(R.id.item_container),
//                                withParentIndex(0)
//                            )
//                        )
//                    )
//                ),
//                withParentIndex(1),
//                isDisplayed()
//            )
//        )
//        textView12.check(matches(withText("毎週金曜日")))

//        val textView13 = onView(
//            allOf(
//                withId(R.id.item_trashType), withText("ペットボトル"),
//                withParent(
//                    allOf(
//                        withId(R.id.item_container),
//                        withParent(withId(R.id.scheduleListFragment)),
//                        withParentIndex(1)
//                    )
//                ),
//                isDisplayed()
//            )
//        )
//        textView13.check(matches(withText("ペットボトル")))

//        val textView14 = onView(
//            allOf(
//                withText("第3火曜日"),
//                withParent(
//                    allOf(
//                        withId(R.id.item_schedule),
//                        withParent(
//                            allOf(
//                                withId(R.id.item_container),
//                                withParentIndex(1)
//                            )
//                        )
//                    )
//                ),
//                withParentIndex(0),
//                isDisplayed()
//            )
//        )
//        textView14.check(matches(withText("第3火曜日")))

//        val textView15 = onView(
//            allOf(
//                withId(R.id.item_trashType), withText("テスト"),
//                withParent(
//                    allOf(
//                        withId(R.id.item_container),
//                        withParent(withId(R.id.scheduleListFragment)),
//                        withParentIndex(2)
//                    )
//                ),
//                isDisplayed()
//            )
//        )
//        textView15.check(matches(withText("テスト")))

//        val textView16 = onView(
//            allOf(
//                withText("隔週土曜日"),
//                withParent(
//                    allOf(
//                        withId(R.id.item_schedule),
//                        withParent(
//                            allOf(
//                                withId(R.id.item_container),
//                                withParentIndex(2)
//                            )
//                        )
//                    )
//                ),
//                withParentIndex(0),
//                isDisplayed()
//            )
//        )
//        textView16.check(matches(withText("隔週土曜日")))
    }
}
