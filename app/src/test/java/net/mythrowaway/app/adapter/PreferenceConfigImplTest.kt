package net.mythrowaway.app.adapter

import net.mythrowaway.app.adapter.repository.PreferenceConfigImpl
import net.mythrowaway.app.domain.AlarmConfig
import net.mythrowaway.app.stub.TestSharedPreferencesImpl
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PreferenceConfigImplTest {
    private val testPreferences =
        TestSharedPreferencesImpl()
    private val instance =
            PreferenceConfigImpl(testPreferences)

    @Before
    fun initPreference() {
        testPreferences.removeAll()
    }

    @Test
    fun getAlarmConfig_NoneConfig() {
        val alarmConfig: AlarmConfig = instance.getAlarmConfig()
        Assert.assertFalse(alarmConfig.enabled)
        Assert.assertEquals(7, alarmConfig.hourOfDay)
        Assert.assertEquals(0, alarmConfig.minute)
        Assert.assertFalse(alarmConfig.notifyEveryday)
    }

    @Test
    fun getAlarmConfig_ConfigExist() {
        testPreferences.edit().apply {
            putString(
                "KEY_ALARM_CONFIG",
                """
                        {"enabled":true,"hourOfDay": 12, "minute": 33, "notifyEveryday": true}
                """.trimIndent()
            )
            commit()
        }

        val alarmConfig: AlarmConfig = instance.getAlarmConfig()
        Assert.assertTrue(alarmConfig.enabled)
        Assert.assertEquals(12, alarmConfig.hourOfDay)
        Assert.assertEquals(33, alarmConfig.minute)
        Assert.assertTrue(alarmConfig.notifyEveryday)
    }

    @Test
    fun saveAlarmConfig_NoneConfig() {
        val alarmConfig: AlarmConfig = instance.getAlarmConfig()
        instance.saveAlarmConfig(alarmConfig)

        Assert.assertEquals("""
            {"enabled":false,"hourOfDay":7,"minute":0,"notifyEveryday":false}
        """.trimIndent(),testPreferences.getString("KEY_ALARM_CONFIG",null))
    }

    @Test
    fun saveAlarmConfig_ExistConfig() {
        testPreferences.edit().apply {
            putString(
                "KEY_ALARM_CONFIG",
                """
                        {"enabled":false,"hourOfDay": 12, "minute": 33, "notifyEveryday": false}
                """.trimIndent()
            )
            commit()
        }

        val alarmConfig: AlarmConfig = instance.getAlarmConfig()
        alarmConfig.enabled = true
        alarmConfig.hourOfDay = 11
        alarmConfig.minute = 59
        alarmConfig.notifyEveryday = true
        instance.saveAlarmConfig(alarmConfig)

        Assert.assertEquals("""
            {"enabled":true,"hourOfDay":11,"minute":59,"notifyEveryday":true}
        """.trimIndent(),testPreferences.getString("KEY_ALARM_CONFIG",null))

    }
}