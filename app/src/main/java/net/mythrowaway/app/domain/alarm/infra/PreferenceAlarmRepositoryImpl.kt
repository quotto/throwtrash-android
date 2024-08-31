package net.mythrowaway.app.domain.alarm.infra

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import net.mythrowaway.app.domain.trash.infra.data.AlarmConfigJsonData
import net.mythrowaway.app.domain.alarm.entity.AlarmConfig
import net.mythrowaway.app.usecase.AlarmRepositoryInterface
import javax.inject.Inject

class PreferenceAlarmRepositoryImpl @Inject constructor(private val context: Context):
    AlarmRepositoryInterface {
    private val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    companion object {
        private const val KEY_ALARM_CONFIG = "KEY_ALARM_CONFIG"
    }

    private inline fun <reified T>jsonToConfig(stringData: String): T {
        val mapper = ObjectMapper()
        return mapper.readValue(stringData, T::class.java)
    }

    private fun <T>configToJson(config: T): String {
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        return mapper.writeValueAsString(config)
    }

    override fun saveAlarmConfig(alarmConfig: AlarmConfig) {
        preference.edit().apply {
            Log.i(this.javaClass.simpleName, "Save Alarm Config -> $KEY_ALARM_CONFIG=${configToJson(alarmConfig)}")
            putString(
                KEY_ALARM_CONFIG, configToJson(
                AlarmConfigJsonData(
                    _enabled = alarmConfig.enabled,
                    _hourOfDay = alarmConfig.hourOfDay,
                    _minute = alarmConfig.minute,
                    _notifyEveryday = alarmConfig.notifyEveryday
                )
            ))
            apply()
        }
    }

    override fun getAlarmConfig(): AlarmConfig? {
        preference.getString(KEY_ALARM_CONFIG,null)?.let { config ->
            val jsonConfig = jsonToConfig<AlarmConfigJsonData>(config)
            return AlarmConfig(
                _enabled = jsonConfig.enabled,
                _hourOfDay = jsonConfig.hourOfDay,
                _minute = jsonConfig.minute,
                _notifyEveryday = jsonConfig.notifyEveryday
            )
        }
        return null
    }
}