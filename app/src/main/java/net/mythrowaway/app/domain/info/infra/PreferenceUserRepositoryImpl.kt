package net.mythrowaway.app.domain.info.infra

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import net.mythrowaway.app.domain.info.usecase.UserRepositoryInterface
import javax.inject.Inject

class PreferenceUserRepositoryImpl @Inject constructor(private val context: Context):
    UserRepositoryInterface {
    private val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    companion object {
        private const val KEY_USER_ID = "KEY_USER_ID"
    }

    override fun setUserId(id: String) {
        preference.edit().apply {
            Log.i(this.javaClass.simpleName, "Set user id -> $KEY_USER_ID=$id")
            putString(KEY_USER_ID, id)
            apply()
        }
    }

    override fun getUserId(): String? {
        return preference.getString(KEY_USER_ID, null)
    }
}