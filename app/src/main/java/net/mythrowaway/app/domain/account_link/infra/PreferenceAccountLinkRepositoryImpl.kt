package net.mythrowaway.app.domain.account_link.infra

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import net.mythrowaway.app.domain.account_link.entity.FinishAccountLinkRequestInfo
import net.mythrowaway.app.domain.account_link.usecase.AccountLinkRepositoryInterface
import javax.inject.Inject

class PreferenceAccountLinkRepositoryImpl @Inject constructor(private val context: Context):
  AccountLinkRepositoryInterface {
  private val preference: SharedPreferences by lazy {
    PreferenceManager.getDefaultSharedPreferences(context)
  }
  companion object {
    private const val KEY_ACCOUNT_LINK_TOKEN = "KEY_ACCOUNT_LINK_TOKEN"
    private const val KEY_ACCOUNT_LINK_REDIRECT_URI = "KEY_ACCOUNT_LINK_URL"
  }
  override fun saveAccountLinkRequestInfo(finishAccountLinkRequestInfo: FinishAccountLinkRequestInfo) {
    preference.edit().apply {
      putString(KEY_ACCOUNT_LINK_REDIRECT_URI, finishAccountLinkRequestInfo.redirectUri)
      putString(KEY_ACCOUNT_LINK_TOKEN, finishAccountLinkRequestInfo.token)
      apply()
    }
  }

  override fun getAccountLinkRequestInfo(): FinishAccountLinkRequestInfo {
    return FinishAccountLinkRequestInfo(
      redirectUri = preference.getString(KEY_ACCOUNT_LINK_REDIRECT_URI,"") ?: "",
      token = preference.getString(KEY_ACCOUNT_LINK_TOKEN,"") ?: ""
    )
  }
}