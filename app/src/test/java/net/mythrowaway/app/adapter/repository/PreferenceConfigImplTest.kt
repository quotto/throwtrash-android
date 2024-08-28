package net.mythrowaway.app.adapter.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import net.mythrowaway.app.domain.AlarmConfig
import net.mythrowaway.app.stub.StubSharedPreferencesImpl
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class PreferenceConfigImplTest {

  @Mock
  private lateinit var mockContext: Context

  @InjectMocks
  private lateinit var instance: PreferenceConfigRepositoryImpl

  private val stubSharedPreference =
    StubSharedPreferencesImpl()

  private lateinit var mockedStatic: MockedStatic<PreferenceManager>

  @BeforeEach
  fun beforeClass() {
    MockitoAnnotations.openMocks(this)
    mockedStatic = Mockito.mockStatic(PreferenceManager::class.java)
    mockedStatic.`when`<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(mockContext) }
      .thenReturn(stubSharedPreference)
    stubSharedPreference.removeAll()
  }

  @AfterEach
  fun afterEach() {
    mockedStatic.close()
  }
  @Test
  fun getAlarmConfig_NoneConfig() {
    val alarmConfig: AlarmConfig? = instance.getAlarmConfig()
    assertNull(alarmConfig)
  }

  @Test
  fun getAlarmConfig_ConfigExist() {
    stubSharedPreference.edit().apply {
      putString(
        "KEY_ALARM_CONFIG",
        """
                        {"enabled":true,"hourOfDay": 12, "minute": 33, "notifyEveryday": true}
                """.trimIndent()
      )
      commit()
    }

    val alarmConfig: AlarmConfig? = instance.getAlarmConfig()
    assertNotNull(alarmConfig)
    assertTrue(alarmConfig!!.enabled)
    assertEquals(12, alarmConfig.hourOfDay)
    assertEquals(33, alarmConfig.minute)
    assertTrue(alarmConfig.notifyEveryday)
  }

  @Test
  fun saveAlarmConfig_NoneConfig() {
    instance.saveAlarmConfig(AlarmConfig())

    assertEquals("""
            {"enabled":false,"hourOfDay":7,"minute":0,"notifyEveryday":false}
        """.trimIndent(),stubSharedPreference.getString("KEY_ALARM_CONFIG",null))
  }

  @Test
  fun saveAlarmConfig_ExistConfig() {
    stubSharedPreference.edit().apply {
      putString(
        "KEY_ALARM_CONFIG",
        """
                        {"enabled":false,"hourOfDay": 12, "minute": 33, "notifyEveryday": false}
                """.trimIndent()
      )
      commit()
    }

    val alarmConfig: AlarmConfig? = instance.getAlarmConfig()
    alarmConfig!!.enabled = true
    alarmConfig.hourOfDay = 11
    alarmConfig.minute = 59
    alarmConfig.notifyEveryday = true
    instance.saveAlarmConfig(alarmConfig)

    assertEquals("""
            {"enabled":true,"hourOfDay":11,"minute":59,"notifyEveryday":true}
        """.trimIndent(),stubSharedPreference.getString("KEY_ALARM_CONFIG",null))
  }
}