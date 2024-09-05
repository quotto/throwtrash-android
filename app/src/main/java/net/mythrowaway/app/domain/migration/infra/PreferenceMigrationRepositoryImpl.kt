package net.mythrowaway.app.domain.migration.infra

import android.content.Context
import androidx.preference.PreferenceManager
import net.mythrowaway.app.domain.migration.usecase.MigrationRepositoryInterface
import javax.inject.Inject

class PreferenceMigrationRepositoryImpl @Inject constructor(private val context: Context):
  MigrationRepositoryInterface {
    private val preference by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

  override fun getStringValue(key: String, defaultValue: String): String {
    return preference.getString(key, defaultValue) ?: defaultValue
  }

  override fun getIntValue(key: String, defaultValue: Int): Int {
    return preference.getInt(key, defaultValue)
  }

  override fun getLongValue(key: String, defaultValue: Long): Long {
    return preference.getLong(key, defaultValue)
  }

  override fun getBooleanValue(key: String, defaultValue: Boolean): Boolean {
    return preference.getBoolean(key, defaultValue)
  }

}