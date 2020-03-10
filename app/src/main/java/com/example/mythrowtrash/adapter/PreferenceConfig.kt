package com.example.mythrowtrash.adapter

import android.content.SharedPreferences
import com.example.mythrowtrash.domain.AlarmConfig
import com.example.mythrowtrash.usecase.IConfigRepository
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper

class PreferenceConfigImpl(private val preference: SharedPreferences): IConfigRepository {
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
        preference.edit().apply() {
            putString(KEY_ALARM_CONFIG, configToJson(alarmConfig))
            commit()
        }
    }

    override fun getAlarmConfig(): AlarmConfig {
        preference.getString(KEY_ALARM_CONFIG,null)?.let {config ->
            val alarmConfig:AlarmConfig = jsonToConfig<AlarmConfig>(config)
            return alarmConfig
        }
        return AlarmConfig()
    }
}