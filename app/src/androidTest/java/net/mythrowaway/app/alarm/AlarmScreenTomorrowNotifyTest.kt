package net.mythrowaway.app.alarm

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.preference.PreferenceManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import net.mythrowaway.app.module.alarm.dto.AlarmTrashDTO
import net.mythrowaway.app.module.alarm.infra.PreferenceAlarmRepositoryImpl
import net.mythrowaway.app.module.alarm.presentation.view.AlarmScreen
import net.mythrowaway.app.module.alarm.presentation.view_model.AlarmViewModel
import net.mythrowaway.app.module.alarm.usecase.AlarmManager
import net.mythrowaway.app.module.alarm.usecase.AlarmUseCase
import net.mythrowaway.app.module.trash.infra.PreferenceSyncRepositoryImpl
import net.mythrowaway.app.module.trash.infra.PreferenceTrashRepositoryImpl
import net.mythrowaway.app.module.trash.service.TrashService
import net.mythrowaway.app.ui.theme.AppTheme
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlarmScreenTomorrowNotifyTest {
  @get:Rule
  val composeRule = createComposeRule()

  private val context = InstrumentationRegistry.getInstrumentation().targetContext
  private val alarmRepository = PreferenceAlarmRepositoryImpl(context)
  private val trashService = TrashService(
    PreferenceTrashRepositoryImpl(context),
    PreferenceSyncRepositoryImpl(context)
  )
  private val alarmUseCase = AlarmUseCase(alarmRepository, trashService)

  @Before
  fun setUp() {
    PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit()
  }

  @Test
  fun tomorrow_toggle_is_disabled_when_notify_is_off() {
    val alarmViewModel = AlarmViewModel(alarmUseCase, NoOpAlarmManager())
    composeRule.setContent {
      AppTheme {
        AlarmScreen(
          alarmViewModel = alarmViewModel
        )
      }
    }

    composeRule.onNodeWithText("翌日のゴミ出しを通知する").assertIsDisplayed()
    composeRule.onNodeWithTag("alarm_notify_tomorrow_switch").assertIsOff().assertIsNotEnabled()
  }

  @Test
  fun save_tomorrow_toggle_when_notify_is_enabled() {
    val alarmViewModel = AlarmViewModel(alarmUseCase, NoOpAlarmManager())
    composeRule.setContent {
      AppTheme {
        AlarmScreen(
          alarmViewModel = alarmViewModel
        )
      }
    }

    composeRule.onNodeWithTag("alarm_notify_switch").performClick()
    composeRule.onNodeWithTag("alarm_notify_tomorrow_switch").assertIsEnabled()
    composeRule.onNodeWithTag("alarm_notify_tomorrow_switch").performClick()
    composeRule.onNodeWithTag("alarm_notify_tomorrow_switch").assertIsOn()
    composeRule.onNodeWithText("設定").performClick()

    composeRule.waitForIdle()
    assertTrue(alarmRepository.getAlarmConfig()?.notifyTomorrow == true)
  }

  private class NoOpAlarmManager : AlarmManager {
    override fun showAlarmMessage(notifyTrashList: List<AlarmTrashDTO>) {
    }

    override fun setAlarm(hourOfDay: Int, minute: Int) {
    }

    override fun cancelAlarm() {
    }
  }
}
