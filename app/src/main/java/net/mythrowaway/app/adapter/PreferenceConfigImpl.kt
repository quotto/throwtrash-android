package net.mythrowaway.app.adapter

import android.content.SharedPreferences
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import net.mythrowaway.app.domain.AlarmConfig
import net.mythrowaway.app.usecase.CalendarUseCase
import net.mythrowaway.app.usecase.IConfigRepository
import java.util.*

class PreferenceConfigImpl(private val preference: SharedPreferences): IConfigRepository {
    companion object {
        private const val KEY_ALARM_CONFIG = "KEY_ALARM_CONFIG"
        private const val KEY_USER_ID = "KEY_USER_ID"
        private const val KEY_TIMESTAMP = "KEY_TIMESTAMP"
        private const val KEY_SYNC_STATE = "KEY_SYNC_STATE"
    }

    private inline fun <reified T>jsonToConfig(stringData: String): T {
        val mapper = ObjectMapper()
        return mapper.readValue(stringData, T::class.java)
    }

    override fun setUserId(id: String) {
        preference.edit().apply {
            putString(KEY_USER_ID,id)
            apply()
        }
    }

    override fun setTimestamp(timestamp: Long)  {
        preference.edit().apply {
            putLong(KEY_TIMESTAMP,timestamp)
            apply()
        }
    }

    override fun getUserId(): String? {
        return preference.getString(KEY_USER_ID,null)
    }

    override fun getTimeStamp(): Long {
        return preference.getLong(KEY_TIMESTAMP, 0)
    }

    override fun getSyncState(): Int {
        return preference.getInt(KEY_SYNC_STATE, CalendarUseCase.SYNC_NO)
    }

    override fun setSyncState(state: Int) {
        preference.edit().apply {
            putInt(KEY_SYNC_STATE, state)
            apply()
        }
    }

    override fun updateLocalTimestamp() {
        preference.edit().apply {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            putLong(KEY_TIMESTAMP, calendar.timeInMillis)
            apply()
        }
    }

    private fun <T>configToJson(config: T): String {
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        return mapper.writeValueAsString(config)
    }

    override fun saveAlarmConfig(alarmConfig: AlarmConfig) {
        preference.edit().apply {
            putString(KEY_ALARM_CONFIG, configToJson(alarmConfig))
            apply()
        }
    }

    override fun getAlarmConfig(): AlarmConfig {
        preference.getString(KEY_ALARM_CONFIG,null)?.let { config ->
            return jsonToConfig(config)
        }
        return AlarmConfig()
    }
}