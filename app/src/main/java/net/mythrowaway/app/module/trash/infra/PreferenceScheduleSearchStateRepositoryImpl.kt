package net.mythrowaway.app.module.trash.infra

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import net.mythrowaway.app.module.trash.usecase.ScheduleSearchStateRepositoryInterface
import javax.inject.Inject

class PreferenceScheduleSearchStateRepositoryImpl @Inject constructor(
  private val context: Context
) : ScheduleSearchStateRepositoryInterface {
  private val preference: SharedPreferences by lazy {
    PreferenceManager.getDefaultSharedPreferences(context)
  }

  override fun shouldShowStartupDialog(): Boolean {
    return !preference.getBoolean(KEY_STARTUP_DIALOG_SUPPRESSED, false)
  }

  override fun suppressStartupDialog() {
    preference.edit().putBoolean(KEY_STARTUP_DIALOG_SUPPRESSED, true).apply()
  }

  override fun saveImportMessage(message: String) {
    preference.edit().putString(KEY_IMPORT_MESSAGE, message).apply()
  }

  override fun consumeImportMessage(): String? {
    val message = preference.getString(KEY_IMPORT_MESSAGE, null)
    preference.edit().remove(KEY_IMPORT_MESSAGE).apply()
    return message
  }

  companion object {
    private const val KEY_STARTUP_DIALOG_SUPPRESSED = "KEY_SCHEDULE_SEARCH_STARTUP_DIALOG_SUPPRESSED"
    private const val KEY_IMPORT_MESSAGE = "KEY_SCHEDULE_SEARCH_IMPORT_MESSAGE"
  }
}
