package net.mythrowaway.app.module.info.infra

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import net.mythrowaway.app.module.info.usecase.UserRepositoryInterface
import javax.inject.Inject

class PreferenceUserRepositoryImpl @Inject constructor(private val context: Context):
    UserRepositoryInterface {
    private val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    companion object {
        private const val KEY_USER_ID = "KEY_USER_ID"
    }

    override fun saveUserId(id: String) {
        preference.edit().apply {
            Log.i(this.javaClass.simpleName, "Set user id -> $KEY_USER_ID=$id")
            putString(KEY_USER_ID, id)
            apply()
        }
    }

    override fun getUserId(): String? {
        return preference.getString(KEY_USER_ID, null)
    }

    override fun deleteUserId() {
        preference.edit().apply {
            Log.i(this.javaClass.simpleName, "Delete user id(key: $KEY_USER_ID)")
            remove(KEY_USER_ID)
            apply()
        }
    }
}