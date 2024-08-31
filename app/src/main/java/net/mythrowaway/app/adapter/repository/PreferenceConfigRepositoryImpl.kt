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
}