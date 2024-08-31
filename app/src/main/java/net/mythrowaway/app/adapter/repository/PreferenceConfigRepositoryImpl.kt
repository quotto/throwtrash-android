package net.mythrowaway.app.adapter.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import net.mythrowaway.app.domain.account_link.FinishAccountLinkRequestInfo
import net.mythrowaway.app.usecase.ConfigRepositoryInterface
import java.util.*
import javax.inject.Inject

class PreferenceConfigRepositoryImpl @Inject constructor(private val context: Context): ConfigRepositoryInterface {
    private val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    companion object {
        private const val KEY_CONFIG_VERSION = "KEY_CONFIG_VERSION"
        private const val KEY_LAST_USED_TIME = "KEY_LAST_USED_TIME"
        private const val KEY_CONTINUOUS_DATE = "KEY_CONTINUOUS_DATE"
        private const val KEY_REVIEWED = "KEY_REVIEWED"
    }

    private inline fun <reified T>jsonToConfig(stringData: String): T {
        val mapper = ObjectMapper()
        return mapper.readValue(stringData, T::class.java)
    }

    override fun getConfigVersion():Int {
        return preference.getInt(KEY_CONFIG_VERSION,0)
    }

    override fun updateConfigVersion(version: Int) {
        preference.edit().apply {
            putInt(KEY_CONFIG_VERSION, version)
            apply()
        }
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
}