package net.mythrowaway.app.calendar


import android.widget.ScrollView
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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
import org.hamcrest.core.IsInstanceOf
import org.junit.After
import org.junit.Before

@LargeTest
@RunWith(AndroidJUnit4::class)
class CalendarActivityTest3 {

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
    複数のゴミを登録するシナリオ
    - 複数のゴミを登録した場合にカレンダー画面のゴミ表記が複数行となること
    - もえるゴミ:毎週日曜日、その他-テスト:毎週日曜日を登録する
    - ゴミの表記は1行目:もえるゴミ,2行目:テストとなること
    - カレンダー画面から日付タップで起動されるダイアログのゴミ表記はもえるゴミ\nテストであること
    - 編集画面に登録したゴミが表示されること
     */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun calendarActivityTest3() {
        menuButton.perform(click())
        editMenuButton.perform(click())

        editActivityRule.onNodeWithText("登録").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("登録が完了しました").isDisplayed()
        }

        Espresso.pressBack()

        menuButton.perform(click())
        editMenuButton.perform(click())

        editActivityRule.onNodeWithTag("TrashType").performClick()
        // ドロップダウンが開くまで待機
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("自分で入力").isDisplayed()
        }
        editActivityRule.onNodeWithText("自分で入力").performClick()
        editActivityRule.onNodeWithTag("TrashNameInput").performTextInput("テスト")
        editActivityRule.onNodeWithText("登録").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("登録が完了しました").isDisplayed()
        }

        Espresso.pressBack()
        Thread.sleep(2000)

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
        val firstTrashTextOnSaturday = onView(
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
        firstTrashTextOnSaturday.check(matches(withText("もえるゴミ")))

        val secondTrashTextOnSaturday = onView(
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
        secondTrashTextOnSaturday.check(matches(withText("テスト")))

        // ダイアログを開く
        firstTrashTextOnSaturday.perform(click())
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

        menuButton.perform(click())

        val listMenuButton = onView(
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
        listMenuButton.perform(click())

        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("もえるゴミ").isDisplayed() &&
                    editActivityRule.onNodeWithText("テスト").isDisplayed()
        }
        editActivityRule.waitUntilNodeCount(
            matcher = hasText("毎週日曜日"),
            count =2
        )
    }
}
