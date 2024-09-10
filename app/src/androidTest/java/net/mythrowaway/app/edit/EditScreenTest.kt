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
import androidx.test.platform.app.InstrumentationRegistry
import net.mythrowaway.app.R
import net.mythrowaway.app.module.trash.presentation.view.edit.EditActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@LargeTest
@RunWith(AndroidJUnit4::class)
class EditScreenTest {
    @get:Rule
    val editActivityRule = createAndroidComposeRule(EditActivity::class.java)

    private val resource = InstrumentationRegistry.getInstrumentation().targetContext.resources

    /*
    2件のスケジュールを設定して2件目を削除するシナリオ
    1件目が画面上に残り、追加ボタンが表示された状態になること
     */
    @Test
    fun second_schedule_is_exist_after_delete_first_schedule() {
        editActivityRule.onNodeWithText(resource.getString(R.string.text_interval_weekday_toggle_button)).performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_weekday_of_interval_weekly_dropdown)).isDisplayed()
        }
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_schedule_button)).performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_weekday_of_weekly_dropdown)).isDisplayed()
        }
        editActivityRule.onAllNodesWithTag(resource.getString(R.string.testTag_delete_schedule_button))[0].performClick()
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_weekday_of_weekly_dropdown)).assertExists()
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_weekday_of_interval_weekly_dropdown)).assertDoesNotExist()
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_schedule_button)).assertIsDisplayed()
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
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_schedule_button)).performClick()
        editActivityRule.waitUntilNodeCount (
            hasTestTag(resource.getString(R.string.testTag_weekday_of_weekly_dropdown)),
            2,
            1000
        )
        editActivityRule.onAllNodesWithText(resource.getString(R.string.text_interval_weekday_toggle_button))[1].performClick()

        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_schedule_button)).performClick()
        editActivityRule.waitUntilNodeCount (
            hasTestTag(resource.getString(R.string.testTag_weekday_of_weekly_dropdown)),
            2,
            1000
        )

        editActivityRule.onAllNodesWithText(resource.getString(R.string.text_monthly_toggle_button))[2].performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_day_of_month_of_monthly_dropdown)).isDisplayed()
        }

        editActivityRule.onNodeWithTag(resource.getString(R.string.message_complete_save_trash)).assertIsNotDisplayed()

        editActivityRule.onAllNodesWithTag(resource.getString(R.string.testTag_delete_schedule_button))[1].performClick()

        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_add_schedule_button)).assertIsDisplayed()

        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_weekday_of_weekly_dropdown)).assertExists()
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_weekday_of_interval_weekly_dropdown)).assertDoesNotExist()
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_day_of_month_of_monthly_dropdown)).assertExists()
    }

    /*
     *  隔週スケジュールの開始日の初期値は今日の日付であること
     */
    @Test
    fun initial_start_of_interval_weekly_schedule_is_today() {
        editActivityRule.onNodeWithText(resource.getString(R.string.text_interval_weekday_toggle_button)).performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_weekday_of_interval_weekly_dropdown)).isDisplayed()
        }
        editActivityRule.onNodeWithText(LocalDate.now().toString()).assertExists()
    }

    /*
     *  その他のゴミの名称入力テスト
     *  - ブランクの場合は登録ボタンが押下できない、エラーメッセージが表示されること
     */
    @Test
    fun is_invalid_if_trash_name_is_blank() {
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_trash_type_dropdown)).performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("自分で入力").isDisplayed()
        }
        editActivityRule.onNodeWithText("自分で入力").performClick()
        // 初期状態はゴミの名称がブランクのため登録ボタンは押下できない
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_register_trash_button)).assertIsNotEnabled()
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_trash_name_input)).performTextInput("a")
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_register_trash_button)).assertIsEnabled()

        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_trash_name_input)).performTextClearance()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText(resource.getString(R.string.message_invalid_input_trash_name_empty)).isDisplayed()
        }
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_register_trash_button)).assertIsNotEnabled()
    }

    /*
     *  その他のゴミの名称入力テスト
     *  - 記号が入力されている場合は登録ボタンが押下できない、エラーメッセージが表示されること
     */
    @Test
    fun is_invalid_if_trash_name_has_symbol() {
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_trash_type_dropdown)).performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("自分で入力").isDisplayed()
        }
        editActivityRule.onNodeWithText("自分で入力").performClick()
        // 初期状態はゴミの名称がブランクのため登録ボタンは押下できない
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_trash_name_input)).performTextInput("a!")
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText(resource.getString(R.string.message_invalid_input_trash_name_invalid_char)).isDisplayed()
        }
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_register_trash_button)).assertIsNotEnabled()
    }

  /*
    *  その他のゴミの名称入力テスト
    *  - 10文字以上の場合は登録ボタンが押下できない、エラーメッセージが表示されること
  */
    @Test
    fun is_invalid_if_trash_name_is_more_than_10_characters() {
      editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_trash_type_dropdown)).performClick()
      editActivityRule.waitUntil {
        editActivityRule.onNodeWithText("自分で入力").isDisplayed()
      }
      editActivityRule.onNodeWithText("自分で入力").performClick()
      editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_trash_name_input)).performTextInput("12345678901")
      editActivityRule.waitUntil {
        editActivityRule.onNodeWithText(resource.getString(R.string.message_invalid_input_trash_name_too_long)).isDisplayed()
      }
      editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_register_trash_button)).assertIsNotEnabled()

      // 10文字以下の場合は登録ボタンが押下できる
      editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_trash_name_input)).performTextClearance()
      editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_trash_name_input)).performTextInput("1234567890")
      editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_register_trash_button)).assertIsEnabled()

      // 全角の場合も文字数でカウントされること
      editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_trash_name_input)).performTextClearance()
      editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_trash_name_input)).performTextInput("あいうえおかきくけこさ")
      editActivityRule.waitUntil {
        editActivityRule.onNodeWithText(resource.getString(R.string.message_invalid_input_trash_name_too_long)).isDisplayed()
      }
      editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_register_trash_button)).assertIsNotEnabled()

      editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_trash_name_input)).performTextClearance()
      editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_trash_name_input)).performTextInput("あいうえおかきくけこ")
      editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_register_trash_button)).assertIsEnabled()
  }

}
