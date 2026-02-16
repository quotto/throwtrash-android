package net.mythrowaway.app.edit

import androidx.compose.ui.test.assertIsDisplayed
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
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import net.mythrowaway.app.R
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import net.mythrowaway.app.AndroidTestUtil.Companion.childAtPosition
import net.mythrowaway.app.module.trash.presentation.view.calendar.CalendarActivity
import net.mythrowaway.app.module.trash.presentation.view.edit.EditActivity

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
    private val drawerLayout = onView(withId(R.id.calendarActivityRoot))
    private val navigationView = onView(withId(R.id.main_nav_view))

    private val resource = InstrumentationRegistry.getInstrumentation().targetContext.resources

    /*
    複数のゴミ出しスケジュールを登録するシナリオ
    - 4種類のスケジュールを登録する
    - 一覧画面にゴミの名前とスケジュールが正しく表示されていること。
    - 2件目を削除した場合一覧にはそれ以外のデータが正しく表示されること。
     */
    @Test
    fun valid_delete_second_trash_when_registered_four_trash() {
        drawerLayout.perform(DrawerActions.open())
        navigationView.perform(NavigationViewActions.navigateTo(R.id.menuItemAdd))

        // 1つめ: もえるゴミを毎週日曜日で登録
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_register_trash_button)).performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText(resource.getString(R.string.message_complete_save_trash)).isDisplayed()
        }

        Espresso.pressBack()

        drawerLayout.perform(DrawerActions.open())
        navigationView.perform(NavigationViewActions.navigateTo(R.id.menuItemAdd))

        // 2つ目: その他-あいうえおかきくけこ を毎月3日、第3木曜日、3週ごとの金曜日で登録
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_trash_type_dropdown)).performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("自分で入力").isDisplayed()
        }
        editActivityRule.onNodeWithText("自分で入力").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_trash_name_input)).isDisplayed()
        }
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_trash_name_input)).performTextInput("あいうえおかきくけこ")

        editActivityRule.onNodeWithText(resource.getString(R.string.text_monthly_toggle_button)).performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_day_of_month_of_monthly_dropdown)).isDisplayed()
        }
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_day_of_month_of_monthly_dropdown)).performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("毎月 3 日").isDisplayed()
        }
        editActivityRule.onNodeWithText("毎月 3 日").performClick()

        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_schedule_button)).performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_weekday_of_weekly_dropdown)).isDisplayed()
        }
        editActivityRule.onAllNodesWithText(resource.getString(R.string.text_ordinal_weekday_toggle_button))[1].performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_weekday_of_ordinal_weekly_dropdown)).isDisplayed()
        }
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_order_of_ordinal_weekly_dropdown)).performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("第3").isDisplayed()
        }
        editActivityRule.onNodeWithText("第3").performClick()
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_weekday_of_ordinal_weekly_dropdown)).performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("木曜日").isDisplayed()
        }
        editActivityRule.onNodeWithText("木曜日").performClick()

        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_schedule_button)).performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_weekday_of_weekly_dropdown)).isDisplayed()
        }
        editActivityRule.onAllNodesWithText(resource.getString(R.string.text_interval_weekday_toggle_button))[2].performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_weekday_of_interval_weekly_dropdown)).isDisplayed()
        }
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_weekday_of_interval_weekly_dropdown)).performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("金曜日").isDisplayed()
        }
        editActivityRule.onNodeWithText("金曜日").performClick()
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_interval_of_interval_weekly_dropdown)).performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("3 週ごと").isDisplayed()
        }
        editActivityRule.onNodeWithText("3 週ごと").performClick()

        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_register_trash_button)).performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText(resource.getString(R.string.message_complete_save_trash)).isDisplayed()
        }

        Espresso.pressBack()

        drawerLayout.perform(DrawerActions.open())
        navigationView.perform(NavigationViewActions.navigateTo(R.id.menuItemAdd))

        // 3つめ: ペットボトルを毎週月曜日で登録
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_trash_type_dropdown)).performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("ペットボトル").isDisplayed()
        }
        editActivityRule.onNodeWithText("ペットボトル").performClick()
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_weekday_of_weekly_dropdown)).performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("毎週 月曜日").isDisplayed()
        }
        editActivityRule.onNodeWithText("毎週 月曜日").performClick()
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_register_trash_button)).performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText(resource.getString(R.string.message_complete_save_trash)).isDisplayed()
        }

        Espresso.pressBack()

        drawerLayout.perform(DrawerActions.open())
        navigationView.perform(NavigationViewActions.navigateTo(R.id.menuItemAdd))

        // 4つめ: 古紙を毎週日曜日で登録
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_trash_type_dropdown)).performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("古紙").isDisplayed()
        }
        editActivityRule.onNodeWithText("古紙").performClick()
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_register_trash_button)).performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText(resource.getString(R.string.message_complete_save_trash)).isDisplayed()
        }

        Espresso.pressBack()

        drawerLayout.perform(DrawerActions.open())
        navigationView.perform(NavigationViewActions.navigateTo(R.id.menuItemList))

        // 登録したゴミの一覧が正しく表示されていることを確認
        val trashes = editActivityRule.onAllNodesWithTag(
            resource.getString(R.string.testTag_trash_list_item),
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
            resource.getString(R.string.testTag_trash_list_item),
            useUnmergedTree = true
        )
        trashesAfterDelete[0].onChildAt(0).assertTextEquals("もえるゴミ")
        trashesAfterDelete[0].onChildAt(1).assertTextEquals("毎週日曜日")
        trashesAfterDelete[1].onChildAt(0).assertTextEquals("ペットボトル")
        trashesAfterDelete[1].onChildAt(1).assertTextEquals("毎週月曜日")
        trashesAfterDelete[2].onChildAt(0).assertTextEquals("古紙")
        trashesAfterDelete[2].onChildAt(1).assertTextEquals("毎週日曜日")
    }

    /*
    ゴミが1件も登録されていない場合の表示確認
    - 一覧にデータが無い旨のメッセージが表示されること。
     */
    @Test
    fun show_empty_message_when_trash_list_is_empty() {
        drawerLayout.perform(DrawerActions.open())
        navigationView.perform(NavigationViewActions.navigateTo(R.id.menuItemList))

        repeat(10) {
            val deleted = runCatching {
                editActivityRule.onAllNodesWithTag("DeleteTrashButton")[0].performClick()
                editActivityRule.waitUntil {
                    editActivityRule.onNodeWithText("ゴミ出し予定を削除しました").isDisplayed()
                }
                true
            }.getOrElse { false }
            if (!deleted) return@repeat
        }

        editActivityRule.onNodeWithText(
            resource.getString(R.string.text_trash_list_empty)
        ).assertIsDisplayed()
    }
}
