package net.mythrowaway.app.module.migration.infra

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import net.mythrowaway.app.module.migration.usecase.VersionRepositoryInterface
import javax.inject.Inject

class PreferenceVersionRepositoryImpl @Inject constructor(private val context: Context):
    VersionRepositoryInterface {
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