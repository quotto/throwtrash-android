package net.mythrowaway.app.usecase

import androidx.preference.PreferenceManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.capture
import net.mythrowaway.app.module.alarm.dto.AlarmConfigDTO
import net.mythrowaway.app.module.alarm.dto.AlarmTrashDTO
import net.mythrowaway.app.module.alarm.entity.AlarmConfig
import net.mythrowaway.app.module.alarm.infra.PreferenceAlarmRepositoryImpl
import net.mythrowaway.app.module.alarm.usecase.AlarmManager
import net.mythrowaway.app.module.alarm.usecase.AlarmUseCase
import net.mythrowaway.app.module.trash.entity.trash.ExcludeDayOfMonth
import net.mythrowaway.app.module.trash.entity.trash.ExcludeDayOfMonthList
import net.mythrowaway.app.module.trash.entity.trash.IntervalWeeklySchedule
import net.mythrowaway.app.module.trash.entity.trash.MonthlySchedule
import net.mythrowaway.app.module.trash.entity.trash.OrdinalWeeklySchedule
import net.mythrowaway.app.module.trash.entity.trash.Trash
import net.mythrowaway.app.module.trash.entity.trash.TrashType
import net.mythrowaway.app.module.trash.entity.trash.WeeklySchedule
import net.mythrowaway.app.module.trash.infra.PreferenceSyncRepositoryImpl
import net.mythrowaway.app.module.trash.infra.PreferenceTrashRepositoryImpl
import net.mythrowaway.app.module.trash.service.TrashService
import net.mythrowaway.app.module.trash.usecase.ResetTrashUseCase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.time.DayOfWeek
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class AlarmUseCaseTest {
  @Mock private lateinit var alarmManager: AlarmManager
  @Captor private lateinit var captorTrashList: ArgumentCaptor<List<AlarmTrashDTO>>

  private val alarmRepository = PreferenceAlarmRepositoryImpl(
    InstrumentationRegistry.getInstrumentation().context
  )

  private val trashRepository = PreferenceTrashRepositoryImpl(
    InstrumentationRegistry.getInstrumentation().context
  )
  private val syncRepository = PreferenceSyncRepositoryImpl(
  InstrumentationRegistry.getInstrumentation().context
  )

  private lateinit var useCase: AlarmUseCase

  private val preferences = PreferenceManager.getDefaultSharedPreferences(
    InstrumentationRegistry.getInstrumentation().context
  )

  @Before
  fun before(){
    MockitoAnnotations.openMocks(this)
    Mockito.reset(alarmManager)
    preferences.edit().clear().commit()

    useCase = AlarmUseCase(
      config = alarmRepository,
      trashService = TrashService(
        trashRepository = trashRepository,
        syncRepository = syncRepository,
        resetTrashUseCase = ResetTrashUseCase(
          trashRepository = trashRepository,
          syncRepository = syncRepository
        )
      )
    )
  }

  @Test
  fun get_default_config_when_alarm_config_is_not_set() {
    // AlarmConfigが設定されていない場合はデフォルト値を返す
    val alarmConfig = useCase.getAlarmConfig()
    assertEquals(false, alarmConfig.enabled)
    assertEquals(0, alarmConfig.hour)
    assertEquals(0, alarmConfig.minute)
    assertEquals(false, alarmConfig.notifyEveryday)
  }

  @Test
  fun get_alarm_config_when_alarm_config_is_set() {
    // AlarmConfigが設定されている場合はその値を返す
    alarmRepository.saveAlarmConfig(
      AlarmConfig(
        _enabled = true,
        _hourOfDay = 12,
        _minute = 30,
        _notifyEveryday = true
      )
    )
    val alarmConfig = useCase.getAlarmConfig()
    assertEquals(true, alarmConfig.enabled)
    assertEquals(12, alarmConfig.hour)
    assertEquals(30, alarmConfig.minute)
    assertEquals(true, alarmConfig.notifyEveryday)
  }

  @Test
  fun create_alarm_config_and_set_alarm_when_alarm_config_is_not_set() {
    // AlarmConfigが設定されていない場合はデフォルト値を保存する
    useCase.saveAlarmConfig(
      AlarmConfigDTO(
        enabled = true,
        hour = 12,
        minute = 30,
        notifyEveryday = true
      ),
      alarmManager
    )
    val alarmConfig = alarmRepository.getAlarmConfig()
    assertEquals(true, alarmConfig?.enabled)
    assertEquals(12, alarmConfig?.hourOfDay)
    assertEquals(30, alarmConfig?.minute)
    assertEquals(true, alarmConfig?.notifyEveryday)

    Mockito.verify(alarmManager, Mockito.times(1)).setAlarm(12, 30)
  }

  @Test
  fun overwrite_alarm_config_when_alarm_config_is_set() {
    // AlarmConfigが設定されている場合は上書きする
    alarmRepository.saveAlarmConfig(
      AlarmConfig(
        _enabled = true,
        _hourOfDay = 12,
        _minute = 30,
        _notifyEveryday = true
      )
    )
    useCase.saveAlarmConfig(
      AlarmConfigDTO(
        enabled = false,
        hour = 10,
        minute = 20,
        notifyEveryday = false
      ),
      alarmManager
    )

    val alarmConfig = alarmRepository.getAlarmConfig()
    assertEquals(false, alarmConfig?.enabled)
    assertEquals(10, alarmConfig?.hourOfDay)
    assertEquals(20, alarmConfig?.minute)
    assertEquals(false, alarmConfig?.notifyEveryday)
  }

  @Test
  fun cancel_alarm_when_alarm_config_is_disabled() {
    // AlarmConfigが無効の場合はアラームをキャンセルする
    alarmRepository.saveAlarmConfig(
      AlarmConfig(
        _enabled = true,
        _hourOfDay = 12,
        _minute = 30,
        _notifyEveryday = true
      )
    )
    useCase.saveAlarmConfig(
      AlarmConfigDTO(
        enabled = false,
        hour = 10,
        minute = 20,
        notifyEveryday = false
      ),
      alarmManager
    )

    val alarmConfig = alarmRepository.getAlarmConfig()
    assertEquals(false, alarmConfig?.enabled)
    assertEquals(10, alarmConfig?.hourOfDay)
    assertEquals(20, alarmConfig?.minute)
    assertEquals(false, alarmConfig?.notifyEveryday)

    Mockito.verify(alarmManager, Mockito.times(1)).cancelAlarm()
  }

  @Test
  fun show_single_trash_and_set_next_alarm_when_has_one_matched_trash_and_alarm_is_enabled() {
    alarmRepository.saveAlarmConfig(
      AlarmConfig(
        _enabled = true,
        _hourOfDay = 12,
        _minute = 30,
        _notifyEveryday = true
      )
    )
    // 1件のゴミ出し予定がある場合は通知する
    trashRepository.saveTrash(
      Trash(
        _id = "1",
        _type = TrashType.BURN,
        schedules = listOf(
          WeeklySchedule(_dayOfWeek = DayOfWeek.FRIDAY),
        ),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
      ),
    )
    trashRepository.saveTrash(
      Trash(
        _id = "2",
        _type = TrashType.BURN,
        schedules = listOf(
          WeeklySchedule(_dayOfWeek = DayOfWeek.SATURDAY),
        ),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
      ),
    )

    useCase.alarm(2024, 9, 6, alarmManager)

    Mockito.verify(alarmManager, Mockito.times(1)).showAlarmMessage(capture(captorTrashList))
    assertEquals(1, captorTrashList.value.size)
    assertEquals("もえるゴミ", captorTrashList.value[0].displayName)

    Mockito.verify(alarmManager, Mockito.times(1)).setAlarm(12, 30)
  }

  @Test
  fun show_nothing_and_set_next_alarm_when_has_no_matched_trash_and_alarm_is_enabled() {
    alarmRepository.saveAlarmConfig(
      AlarmConfig(
        _enabled = true,
        _hourOfDay = 12,
        _minute = 30,
        _notifyEveryday = true
      )
    )
    // 1件のゴミ出し予定がある場合は通知する
    trashRepository.saveTrash(
      Trash(
        _id = "1",
        _type = TrashType.BURN,
        schedules = listOf(
          WeeklySchedule(_dayOfWeek = DayOfWeek.FRIDAY),
        ),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
      ),
    )
    trashRepository.saveTrash(
      Trash(
        _id = "2",
        _type = TrashType.BURN,
        schedules = listOf(
          WeeklySchedule(_dayOfWeek = DayOfWeek.FRIDAY),
        ),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
      ),
    )

    useCase.alarm(2024, 9, 7, alarmManager)

    Mockito.verify(alarmManager, Mockito.times(1)).showAlarmMessage(capture(captorTrashList))
    assertEquals(0, captorTrashList.value.size)

    Mockito.verify(alarmManager, Mockito.times(1)).setAlarm(12, 30)
  }

  @Test
  fun show_multiple_trash_and_not_set_next_alarm_when_has_multiple_matched_trash_and_alarm_is_enabled() {
    alarmRepository.saveAlarmConfig(
      AlarmConfig(
        _enabled = false,
        _hourOfDay = 12,
        _minute = 30,
        _notifyEveryday = true
      )
    )
    trashRepository.saveTrash(
      Trash(
        _id = "1",
        _type = TrashType.PLASTIC,
        schedules = listOf(
          WeeklySchedule(_dayOfWeek = DayOfWeek.FRIDAY),
        ),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
      ),
    )
    trashRepository.saveTrash(
      Trash(
        _id = "2",
        _type = TrashType.OTHER,
        schedules = listOf(
          MonthlySchedule(_day = 6),
          OrdinalWeeklySchedule(_dayOfWeek = DayOfWeek.SUNDAY, _ordinalOfWeek = 1),
        ),
        _displayName = "生ゴミ",
        _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
      ),
    )
    trashRepository.saveTrash(
      Trash(
        _id = "3",
        _type = TrashType.UNBURN,
        schedules = listOf(
          OrdinalWeeklySchedule(_dayOfWeek = DayOfWeek.FRIDAY, _ordinalOfWeek = 1),
        ),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
      ),
    )
    trashRepository.saveTrash(
      Trash(
        _id = "4",
        _type = TrashType.PAPER,
        schedules = listOf(
          IntervalWeeklySchedule(_dayOfWeek = DayOfWeek.FRIDAY, _interval = 2, _start = LocalDate.of(2024, 8, 23)),
        ),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
      ),
    )
    trashRepository.saveTrash(
      Trash(
        _id = "5",
        _type = TrashType.BURN,
        schedules = listOf(
          WeeklySchedule(_dayOfWeek = DayOfWeek.FRIDAY),
        ),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf(
          ExcludeDayOfMonth(_month = 9, _dayOfMonth = 6)
        ))
      ),
    )

    useCase.alarm(2024, 9, 6, alarmManager)

    Mockito.verify(alarmManager, Mockito.times(1)).showAlarmMessage(capture(captorTrashList))
    assertEquals(4, captorTrashList.value.size)
    assertEquals("プラスチック", captorTrashList.value[0].displayName)
    assertEquals("生ゴミ", captorTrashList.value[1].displayName)
    assertEquals("もえないゴミ", captorTrashList.value[2].displayName)
    assertEquals("古紙", captorTrashList.value[3].displayName)

    Mockito.verify(alarmManager, Mockito.times(0)).setAlarm(any(), any())
  }

  @Test
  fun not_set_next_alarm_when_alarm_is_not_exist() {
    // AlarmConfigが存在しない場合は次回アラームを設定しない
    useCase.alarm(2024, 9, 6, alarmManager)

    Mockito.verify(alarmManager, Mockito.times(0)).setAlarm(any(), any())
  }

  @Test
  fun show_empty_message_when_matched_trash_is_not_exist() {
    trashRepository.saveTrash(
      Trash(
        _id = "1",
        _type = TrashType.BURN,
        schedules = listOf(
          WeeklySchedule(_dayOfWeek = DayOfWeek.SUNDAY),
        ),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
      ),
    )
    alarmRepository.saveAlarmConfig(
      AlarmConfig(
        _enabled = true,
        _hourOfDay = 12,
        _minute = 30,
        _notifyEveryday = true
      )
    )

    useCase.alarm(2024, 9, 6, alarmManager)

    Mockito.verify(alarmManager, Mockito.times(1)).showAlarmMessage(capture(captorTrashList))
    assertEquals(0, captorTrashList.value.size)
  }
}