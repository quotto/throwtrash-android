package net.mythrowaway.app.edit

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
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

@LargeTest
@RunWith(AndroidJUnit4::class)
class TrashListScreenTest {

    @get:Rule
    val mActivityScenarioRule = ActivityScenarioRule(CalendarActivity::class.java)

    @get:Rule
    val editActivityRule = createAndroidComposeRule(EditActivity::class.java)

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
    private val editMenuButton = onView(
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
    private val listMenuButton = onView(
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

    /*
    複数のゴミ出しスケジュールを登録するシナリオ
    - 4種類のスケジュールを登録する
    - 一覧画面にゴミの名前とスケジュールが正しく表示されていること。
    - 2件目を削除した場合一覧にはそれ以外のデータが正しく表示されること。
     */
    @Test
    fun valid_delete_second_trash_when_registered_four_trash() {
        menuButton.perform(click())
        editMenuButton.perform(click())

        // 1つめ: もえるゴミを毎週日曜日で登録
        editActivityRule.onNodeWithTag("RegisterButton").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("登録が完了しました").isDisplayed()
        }

        Espresso.pressBack()

        menuButton.perform(click())
        editMenuButton.perform(click())

        // 2つ目: その他-あいうえおかきくけこ を毎月3日、第3木曜日、3週ごとの金曜日で登録
        editActivityRule.onNodeWithTag("TrashType").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("自分で入力").isDisplayed()
        }
        editActivityRule.onNodeWithText("自分で入力").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithTag("TrashNameInput").isDisplayed()
        }
        editActivityRule.onNodeWithTag("TrashNameInput").performTextInput("あいうえおかきくけこ")

        editActivityRule.onNodeWithText("毎月").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithTag("DayOfMonthlySchedule").isDisplayed()
        }
        editActivityRule.onNodeWithTag("DayOfMonthlySchedule").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("毎月 3 日").isDisplayed()
        }
        editActivityRule.onNodeWithText("毎月 3 日").performClick()

        editActivityRule.onNodeWithTag("AddScheduleButton").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithTag("WeekdayOfWeeklySchedule").isDisplayed()
        }
        editActivityRule.onAllNodesWithText("毎週(第○曜日)")[1].performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithTag("WeekdayOfOrdinalWeeklySchedule").isDisplayed()
        }
        editActivityRule.onNodeWithTag("OrderOfOrdinalWeeklySchedule").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("第3").isDisplayed()
        }
        editActivityRule.onNodeWithText("第3").performClick()
        editActivityRule.onNodeWithTag("WeekdayOfOrdinalWeeklySchedule").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("木曜日").isDisplayed()
        }
        editActivityRule.onNodeWithText("木曜日").performClick()

        editActivityRule.onNodeWithTag("AddScheduleButton").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithTag("WeekdayOfWeeklySchedule").isDisplayed()
        }
        editActivityRule.onAllNodesWithText("隔週")[2].performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithTag("WeekdayOfIntervalWeeklySchedule").isDisplayed()
        }
        editActivityRule.onNodeWithTag("WeekdayOfIntervalWeeklySchedule").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("金曜日").isDisplayed()
        }
        editActivityRule.onNodeWithText("金曜日").performClick()
        editActivityRule.onNodeWithTag("IntervalOfIntervalWeeklySchedule").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("3 週ごと").isDisplayed()
        }
        editActivityRule.onNodeWithText("3 週ごと").performClick()

        editActivityRule.onNodeWithTag("RegisterButton").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("登録が完了しました").isDisplayed()
        }

        Espresso.pressBack()

        menuButton.perform(click())
        editMenuButton.perform(click())

        // 3つめ: ペットボトルを毎週月曜日で登録
        editActivityRule.onNodeWithTag("TrashType").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("ペットボトル").isDisplayed()
        }
        editActivityRule.onNodeWithText("ペットボトル").performClick()
        editActivityRule.onNodeWithTag("WeekdayOfWeeklySchedule").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("毎週 月曜日").isDisplayed()
        }
        editActivityRule.onNodeWithText("毎週 月曜日").performClick()
        editActivityRule.onNodeWithTag("RegisterButton").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("登録が完了しました").isDisplayed()
        }

        Espresso.pressBack()

        menuButton.perform(click())
        editMenuButton.perform(click())

        // 4つめ: 古紙を毎週日曜日で登録
        editActivityRule.onNodeWithTag("TrashType").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("古紙").isDisplayed()
        }
        editActivityRule.onNodeWithText("古紙").performClick()
        editActivityRule.onNodeWithTag("RegisterButton").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("登録が完了しました").isDisplayed()
        }

        Espresso.pressBack()

        menuButton.perform(click())
        listMenuButton.perform(click())

        // 登録したゴミの一覧が正しく表示されていることを確認
        val trashes = editActivityRule.onAllNodesWithTag(
            "TrashTypeAndScheduleInTrashRow",
            useUnmergedTree = true
        )
        trashes[0].onChildAt(0).assertTextEquals("もえるゴミ")
        trashes[0].onChildAt(1).assertTextEquals("毎週日曜日")
        trashes[1].onChildAt(0).assertTextEquals("あいうえおかきくけこ")
        trashes[1].onChildAt(1).assertTextEquals("毎月3日,第3木曜日,3週間ごとの金曜日")
        trashes[2].onChildAt(0).assertTextEquals("ペットボトル")
        trashes[2].onChildAt(1).assertTextEquals("毎週月曜日")
        trashes[3].onChildAt(0).assertTextEquals("古紙")
        trashes[3].onChildAt(1).assertTextEquals("毎週日曜日")

        // 2件目を削除
        editActivityRule.onAllNodesWithTag("DeleteTrashButton")[1].performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("ゴミ出し予定を削除しました").isDisplayed()
        }

        // 2件目が削除されたことを確認
        val trashesAfterDelete = editActivityRule.onAllNodesWithTag(
            "TrashTypeAndScheduleInTrashRow",
            useUnmergedTree = true
        )
        trashesAfterDelete[0].onChildAt(0).assertTextEquals("もえるゴミ")
        trashesAfterDelete[0].onChildAt(1).assertTextEquals("毎週日曜日")
        trashesAfterDelete[1].onChildAt(0).assertTextEquals("ペットボトル")
        trashesAfterDelete[1].onChildAt(1).assertTextEquals("毎週月曜日")
        trashesAfterDelete[2].onChildAt(0).assertTextEquals("古紙")
        trashesAfterDelete[2].onChildAt(1).assertTextEquals("毎週日曜日")
    }
}
