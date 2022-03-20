package net.mythrowaway.app.adapter.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import net.mythrowaway.app.domain.AlarmConfig
import net.mythrowaway.app.usecase.CalendarUseCase
import net.mythrowaway.app.usecase.IConfigRepository
import java.util.*
import javax.inject.Inject

class PreferenceConfigImpl @Inject constructor(private val context: Context): IConfigRepository {
    private val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    companion object {
        private const val KEY_ALARM_CONFIG = "KEY_ALARM_CONFIG"
        private const val KEY_USER_ID = "KEY_USER_ID"
        private const val KEY_TIMESTAMP = "KEY_TIMESTAMP"
        private const val KEY_SYNC_STATE = "KEY_SYNC_STATE"
        private const val KEY_CONFIG_VERSION = "KEY_CONFIG_VERSION"
        private const val CONFIG_VERSION:Int = 1
        private const val KEY_ACCOUNT_LINK_SESSION = "KEY_ACCOUNT_LINK_SESSION"
        private const val KEY_LAST_USED_TIME = "KEY_LAST_USED_TIME"
        private const val KEY_CONTINUOUS_DATE = "KEY_CONTINUOUS_DATE"
        private const val KEY_REVIEWED = "KEY_REVIEWED"
    }

    private inline fun <reified T>jsonToConfig(stringData: String): T {
        val mapper = ObjectMapper()
        return mapper.readValue(stringData, T::class.java)
    }

    override fun setUserId(id: String) {
        preference.edit().apply {
            Log.i(this.javaClass.simpleName, "Set user id -> $KEY_USER_ID=$id")
            putString(KEY_USER_ID,id)
            apply()
        }
    }

    override fun setTimestamp(timestamp: Long)  {
        preference.edit().apply {
            Log.i(this.javaClass.simpleName, "Set timestamp -> $KEY_TIMESTAMP = $timestamp")
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
            Log.i(this.javaClass.simpleName, "Set sync state -> $KEY_SYNC_STATE=$state")
            putInt(KEY_SYNC_STATE, state)
            apply()
        }
    }

    override fun updateLocalTimestamp() {
        preference.edit().apply {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            Log.i(this.javaClass.simpleName, "Update Local Timestamp -> $KEY_TIMESTAMP=${calendar.timeInMillis}")
            putLong(KEY_TIMESTAMP, calendar.timeInMillis)
            apply()
        }
    }

    override fun getConfigVersion():Int {
        return preference.getInt(KEY_CONFIG_VERSION,0)
    }

    override fun updateConfigVersion() {
        preference.edit().apply {
            putInt(KEY_CONFIG_VERSION, CONFIG_VERSION)
            apply()
        }
    }

    override fun saveAccountLinkSession(sessionId: String, sessionValue: String) {
        preference.edit().apply {
            putString(KEY_ACCOUNT_LINK_SESSION,"${sessionId}=${sessionValue}")
            apply()
        }
    }

    override fun getAccountLinkSession(): String {
        return preference.getString(KEY_ACCOUNT_LINK_SESSION,"") ?: ""
    }

    override fun updateLastUsedTime() {
        preference.edit {
            putLong(KEY_LAST_USED_TIME, Calendar.getInstance().timeInMillis)
        }
    }

    override fun getLastUsedTime(): Long {
        return preference.getLong(KEY_LAST_USED_TIME, Calendar.getInstance().timeInMillis)
    }

    override fun updateContinuousDate(continuousData: Int) {
        preference.edit {
            putInt(KEY_CONTINUOUS_DATE, continuousData)
        }
    }

    override fun getContinuousDate(): Int {
        return preference.getInt(KEY_CONTINUOUS_DATE, 0)
    }

    override fun getReviewed(): Boolean {
        return preference.getBoolean(KEY_REVIEWED, false)
    }

    override fun writeReviewed() {
        preference.edit {
            putBoolean(KEY_REVIEWED, true)
        }
    }

    private fun <T>configToJson(config: T): String {
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        return mapper.writeValueAsString(config)
    }

    override fun saveAlarmConfig(alarmConfig: AlarmConfig) {
        preference.edit().apply {
            Log.i(this.javaClass.simpleName, "Save Alarm Config -> $KEY_ALARM_CONFIG=${configToJson(alarmConfig)}")
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