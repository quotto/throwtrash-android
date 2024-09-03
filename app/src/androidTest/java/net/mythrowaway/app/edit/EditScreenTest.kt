package net.mythrowaway.app.edit


import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import net.mythrowaway.app.domain.trash.presentation.view.edit.EditActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@LargeTest
@RunWith(AndroidJUnit4::class)
class EditScreenTest {
    @get:Rule
    val editActivityRule = createAndroidComposeRule(EditActivity::class.java)

    /*
    2件のスケジュールを設定して2件目を削除するシナリオ
    1件目が画面上に残り、追加ボタンが表示された状態になること
     */
    @Test
    fun second_schedule_is_exist_after_delete_first_schedule() {
        editActivityRule.onNodeWithText("隔週").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithTag("WeekdayOfIntervalWeeklySchedule").isDisplayed()
        }
        editActivityRule.onNodeWithTag("AddScheduleButton").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithTag("WeekdayOfWeeklySchedule").isDisplayed()
        }
        editActivityRule.onAllNodesWithTag("RemoveScheduleButton")[0].performClick()
        editActivityRule.onNodeWithTag("WeekdayOfWeeklySchedule").assertExists()
        editActivityRule.onNodeWithTag("WeekdayOfIntervalWeeklySchedule").assertDoesNotExist()
        editActivityRule.onNodeWithTag("AddScheduleButton").assertIsDisplayed()
    }

    /*
     3件のスケジュールを設定して2件目を削除するシナリオ
     - 3件のスケジュールがある場合は追加ボタンが表示されないこと。
     - 2件目を削除すると追加ボタンが表示されること。
     - 1件目と3件目が画面上に残ること。
     */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun add_schedule_button_is_not_displayed_if_has_3_schedules_and_is_displayed_after_delete_1_schedule() {
        editActivityRule.onNodeWithTag("AddScheduleButton").performClick()
        editActivityRule.waitUntilNodeCount (
            hasTestTag("WeekdayOfWeeklySchedule"),
            2,
            1000
        )
        editActivityRule.onAllNodesWithText("隔週")[1].performClick()

        editActivityRule.onNodeWithTag("AddScheduleButton").performClick()
        editActivityRule.waitUntilNodeCount (
            hasTestTag("WeekdayOfWeeklySchedule"),
            2,
            1000
        )

        editActivityRule.onAllNodesWithText("毎月")[2].performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithTag("DayOfMonthlySchedule").isDisplayed()
        }

        editActivityRule.onNodeWithTag("AddScheduleButton").assertIsNotDisplayed()

        editActivityRule.onAllNodesWithTag("RemoveScheduleButton")[1].performClick()

        editActivityRule.onNodeWithTag("AddScheduleButton").assertIsDisplayed()

        editActivityRule.onNodeWithTag("WeekdayOfWeeklySchedule").assertExists()
        editActivityRule.onNodeWithTag("WeekdayOfIntervalWeeklySchedule").assertDoesNotExist()
        editActivityRule.onNodeWithTag("DayOfMonthlySchedule").assertExists()
    }

    /*
     *  隔週スケジュールの開始日の初期値は今日の日付であること
     */
    @Test
    fun initial_start_of_interval_weekly_schedule_is_today() {
        editActivityRule.onNodeWithText("隔週").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithTag("WeekdayOfIntervalWeeklySchedule").isDisplayed()
        }
        editActivityRule.onNodeWithText(LocalDate.now().toString()).assertExists()
    }

    /*
     *  その他のゴミの名称入力テスト
     *  - ブランクの場合は登録ボタンが押下できない、エラーメッセージが表示されること
     */
    @Test
    fun is_invalid_if_trash_name_is_blank() {
        editActivityRule.onNodeWithTag("TrashType").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("自分で入力").isDisplayed()
        }
        editActivityRule.onNodeWithText("自分で入力").performClick()
        // 初期状態はゴミの名称がブランクのため登録ボタンは押下できない
        editActivityRule.onNodeWithTag("RegisterButton").assertIsNotEnabled()
        editActivityRule.onNodeWithTag("TrashNameInput").performTextInput("a")
        editActivityRule.onNodeWithTag("RegisterButton").assertIsEnabled()

        editActivityRule.onNodeWithTag("TrashNameInput").performTextClearance()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("空の名前は設定できません").isDisplayed()
        }
        editActivityRule.onNodeWithTag("RegisterButton").assertIsNotEnabled()
    }

    /*
     *  その他のゴミの名称入力テスト
     *  - 記号が入力されている場合は登録ボタンが押下できない、エラーメッセージが表示されること
     */
    @Test
    fun is_invalid_if_trash_name_has_symbol() {
        editActivityRule.onNodeWithTag("TrashType").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("自分で入力").isDisplayed()
        }
        editActivityRule.onNodeWithText("自分で入力").performClick()
        // 初期状態はゴミの名称がブランクのため登録ボタンは押下できない
        editActivityRule.onNodeWithTag("TrashNameInput").performTextInput("a!")
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("使用できない文字が含まれています").isDisplayed()
        }
        editActivityRule.onNodeWithTag("RegisterButton").assertIsNotEnabled()
    }

  /*
    *  その他のゴミの名称入力テスト
    *  - 10文字以上の場合は登録ボタンが押下できない、エラーメッセージが表示されること
  */
    @Test
    fun is_invalid_if_trash_name_is_more_than_10_characters() {
      editActivityRule.onNodeWithTag("TrashType").performClick()
      editActivityRule.waitUntil {
        editActivityRule.onNodeWithText("自分で入力").isDisplayed()
      }
      editActivityRule.onNodeWithText("自分で入力").performClick()
      editActivityRule.onNodeWithTag("TrashNameInput").performTextInput("12345678901")
      editActivityRule.waitUntil {
        editActivityRule.onNodeWithText("ゴミの名前は10文字以内で設定してください").isDisplayed()
      }
      editActivityRule.onNodeWithTag("RegisterButton").assertIsNotEnabled()

      // 10文字以下の場合は登録ボタンが押下できる
      editActivityRule.onNodeWithTag("TrashNameInput").performTextClearance()
      editActivityRule.onNodeWithTag("TrashNameInput").performTextInput("1234567890")
      editActivityRule.onNodeWithTag("RegisterButton").assertIsEnabled()

      // 全角の場合も文字数でカウントされること
      editActivityRule.onNodeWithTag("TrashNameInput").performTextClearance()
      editActivityRule.onNodeWithTag("TrashNameInput").performTextInput("あいうえおかきくけこさ")
      editActivityRule.waitUntil {
        editActivityRule.onNodeWithText("ゴミの名前は10文字以内で設定してください").isDisplayed()
      }
      editActivityRule.onNodeWithTag("RegisterButton").assertIsNotEnabled()

      editActivityRule.onNodeWithTag("TrashNameInput").performTextClearance()
      editActivityRule.onNodeWithTag("TrashNameInput").performTextInput("あいうえおかきくけこ")
      editActivityRule.onNodeWithTag("RegisterButton").assertIsEnabled()
  }

}
