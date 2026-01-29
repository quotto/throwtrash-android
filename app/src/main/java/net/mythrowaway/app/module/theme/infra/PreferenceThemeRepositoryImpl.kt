package net.mythrowaway.app.module.theme.infra

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import net.mythrowaway.app.module.theme.usecase.ThemeRepositoryInterface
import javax.inject.Inject

class PreferenceThemeRepositoryImpl @Inject constructor(
  private val context: Context,
) : ThemeRepositoryInterface {
  private val preference: SharedPreferences by lazy {
    PreferenceManager.getDefaultSharedPreferences(context)
  }

  override fun isDarkModeEnabled(): Boolean {
    return preference.getBoolean(KEY_DARK_MODE, false)
  }

  override fun saveDarkModeEnabled(enabled: Boolean) {
    preference.edit().apply {
      putBoolean(KEY_DARK_MODE, enabled)
      apply()
    }
  }

  companion object {
    private const val KEY_DARK_MODE = "KEY_DARK_MODE"
  }
}
