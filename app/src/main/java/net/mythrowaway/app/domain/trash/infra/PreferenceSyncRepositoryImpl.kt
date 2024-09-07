package net.mythrowaway.app.domain.trash.infra

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import net.mythrowaway.app.domain.trash.entity.sync.SyncState
import net.mythrowaway.app.domain.trash.usecase.SyncRepositoryInterface
import javax.inject.Inject

class PreferenceSyncRepositoryImpl @Inject constructor(private val context: Context):
  SyncRepositoryInterface {
  private val preference: SharedPreferences by lazy {
    PreferenceManager.getDefaultSharedPreferences(context)
  }

  companion object {
    private const val KEY_TIMESTAMP = "KEY_TIMESTAMP"
    private const val KEY_SYNC_STATE = "KEY_SYNC_STATE"
  }
  override fun setTimestamp(timestamp: Long)  {
    preference.edit().apply {
      Log.i(this.javaClass.simpleName, "Set timestamp -> $KEY_TIMESTAMP = $timestamp")
      putLong(KEY_TIMESTAMP,timestamp)
      apply()
    }
  }

  override fun getTimeStamp(): Long {
    return preference.getLong(KEY_TIMESTAMP, 0)
  }

  override fun getSyncState(): SyncState {
    return SyncState.from(preference.getInt(KEY_SYNC_STATE, SyncState.NotInit.value))
  }

  override fun setSyncWait() {
    preference.edit().apply {
      Log.i(this.javaClass.simpleName, "Set sync state -> $KEY_SYNC_STATE=${SyncState.Wait.value}")
      putInt(KEY_SYNC_STATE, SyncState.Wait.value)
      apply()
    }
  }

  override fun setSyncComplete() {
    preference.edit().apply {
      Log.i(this.javaClass.simpleName, "Set sync state -> $KEY_SYNC_STATE=${SyncState.Synced.value}")
      putInt(KEY_SYNC_STATE, SyncState.Synced.value)
      apply()
    }
  }
}