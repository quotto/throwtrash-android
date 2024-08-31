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
import net.mythrowaway.app.domain.trash.presentation.view.calendar.CalendarActivity
import org.hamcrest.core.IsInstanceOf
import org.junit.After
import org.junit.Before

@LargeTest
@RunWith(AndroidJUnit4::class)
class CalendarActivityTest5 {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(CalendarActivity::class.java)

    private var mIdlingResource: CountingIdlingResource? = null

//    private val registerButton = onView(
//        allOf(
//            withId(R.id.registerButton), withText("登録"),
//            childAtPosition(
//                allOf(
//                    withId(R.id.buttonContainer),
//                    childAtPosition(
//                        withId(R.id.mainScheduleContainer),
//                        3
//                    )
//                ),
//                0
//            ),
//            isDisplayed()
//        )
//    )

    private val menuButton = onView(
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

    private val addMenu = onView(
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

//   private val trashTypeSpinner =  onView(
//        allOf(
//            withId(R.id.trashTypeList),
//            childAtPosition(
//                allOf(
//                    withId(R.id.trashTypeContainer),
//                    childAtPosition(
//                        withId(R.id.mainScheduleContainer),
//                        2
//                    )
//                ),
//                2
//            ),
//            isDisplayed()
//        )
//    )

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
    - 4つ以上のゴミを登録した場合に最初の3つが日付セルに表示されること
    - 4つ以上のゴミがある場合は日付せるの4行目が「...+1」になること
     */
    @Test
    fun calendarActivityTest3() {
        // 1つ目: もえるゴミの登録
        menuButton.perform(click())

        addMenu.perform(click())

//        registerButton.perform(click())

        // 2つ目: その他（テスト）の登録
        menuButton.perform(click())
        addMenu.perform(click())
//        trashTypeSpinner.perform(click())

        val trashTypeTextView = onData(anything())
            .inAdapterView(
                childAtPosition(
                    withClassName(`is`("android.widget.PopupWindow\$PopupBackgroundView")),
                    0
                )
            )
            .atPosition(9)
        trashTypeTextView.perform(click())

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
//
//        registerButton.perform(click())

        // 3つ目: もえないゴミの登録
        menuButton.perform(click())
        addMenu.perform(click())

//        trashTypeSpinner.perform(click())
        val trashTypeTextView3 = onData(anything())
            .inAdapterView(
                childAtPosition(
                    withClassName(`is`("android.widget.PopupWindow\$PopupBackgroundView")),
                    0
                )
            )
            .atPosition(1)
        trashTypeTextView3.perform(click())

//        registerButton.perform(click())

        // 4つ目: プラスチックの登録
        menuButton.perform(click())
        addMenu.perform(click())

//        trashTypeSpinner.perform(click())

        val trashTypeTextView4 = onData(anything())
            .inAdapterView(
                childAtPosition(
                    withClassName(`is`("android.widget.PopupWindow\$PopupBackgroundView")),
                    0
                )
            )
            .atPosition(2)
        trashTypeTextView4.perform(click())

//        registerButton.perform(click())

        val trashTextLinearLayout = allOf(
                withId(R.id.trashTextListLayout),
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
            )
        val firstTrashTextOnSunday = onView(
            allOf(
                withId(R.id.trashText),
                childAtPosition(
                    childAtPosition(
                        trashTextLinearLayout,
                        0
                    ),
            0
                ),
                isDisplayed()
            ),
        )
        firstTrashTextOnSunday.check(matches(withText("もえるゴミ")))

        val secondTrashTextOnSunday = onView(
            allOf(
                withId(R.id.trashText),
                childAtPosition(
                    childAtPosition(
                        trashTextLinearLayout,
                    1
                    ),
                    0
                ),
                isDisplayed()
            ),
        )
        secondTrashTextOnSunday.check(matches(withText("テスト")))

        val thirdTrashTextOnSunday = onView(
            allOf(
                withId(R.id.trashText),
                childAtPosition(
                    childAtPosition(
                        trashTextLinearLayout,
                        2
                    ),
                    0
                ),
                isDisplayed()
            ),
        )
        thirdTrashTextOnSunday.check(matches(withText("もえないゴミ")))


        val forthTrashTextOnSunday = onView(
            allOf(
                childAtPosition(
                    trashTextLinearLayout,
                    3
                ),
                isDisplayed()
            ),
        )
        forthTrashTextOnSunday.check(matches(withText("...+ 1")))

        // ダイアログを開く
        firstTrashTextOnSunday.perform(click())
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
        dialogTrashText.check(matches(withText("もえるゴミ\nテスト\nもえないゴミ\nプラスチック")))

        Espresso.pressBack()

    }
}
