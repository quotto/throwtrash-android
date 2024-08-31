package net.mythrowaway.app.domain.account_link.presentation.view_model

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.mythrowaway.app.domain.account_link.usecase.AccountLinkUseCase
import javax.inject.Inject

enum class AccountLinkType {
    WEB,
    APP
}

private const val ALEXA_PACKAGE_NAME = "com.amazon.dee.app"

private const val REQUIRED_MINIMUM_VERSION_CODE = 866607211

class AccountLinkViewModel(private val _accountLinkUseCase: AccountLinkUseCase): ViewModel() {
    private val _uiState: MutableStateFlow<AccountLinkUiState> = MutableStateFlow(AccountLinkUiState())
    val uiState = _uiState.asStateFlow()

    // TODO 削除予定
    var token = ""
    var url = ""
    // urlから切り出したstate
    var state = ""
    lateinit var type: AccountLinkType

    suspend fun startAccountLink(context: Context) {
        _uiState.value = _uiState.value.copy(accountLinkStatus = AccountLinkStatus.WaitForStart)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (isAlexaAppSupportAppToApp(context)) {
                    val accountLinkUrl = _accountLinkUseCase.startAccountLinkWithAlexaApp()
                    _uiState.value = _uiState.value.copy(accountLinkUrl = accountLinkUrl)
                } else {
                    val accountLinkInfo = _accountLinkUseCase.startAccountLinkWithLWA()
                    _uiState.value = _uiState.value.copy(accountLinkUrl = accountLinkInfo)
                }
                _uiState.value = _uiState.value.copy(accountLinkStatus = AccountLinkStatus.Start)
            }
        }
    }

    suspend fun finishAccountLink(apiUrl: String,code: String, state: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val accountLinkRequestInfo =_accountLinkUseCase.getAccountLinkRequest()
                _uiState.value = _uiState.value.copy(
                    enableSkillUrl = "${apiUrl}/enable_skill?code=${code}&state=${state}&token=${accountLinkRequestInfo.token}&redirect_uri=${accountLinkRequestInfo.redirectUri}",
                    finishAccountLink = true
                )
            }
        }
    }

    fun resetAccountLinkStatus() {
        _uiState.value = uiState.value.copy(accountLinkStatus = AccountLinkStatus.None)
    }

    class Factory @Inject constructor(private val _accountLinkUseCase: AccountLinkUseCase): ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AccountLinkViewModel(_accountLinkUseCase) as T
        }
    }
    private fun isAlexaAppSupportAppToApp(context: Context): Boolean {
        return try {
            val packageManager: PackageManager = context.packageManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val packageInfo = packageManager.getPackageInfo(ALEXA_PACKAGE_NAME, PackageManager.PackageInfoFlags.of(0))
                packageInfo.longVersionCode > REQUIRED_MINIMUM_VERSION_CODE
            } else {
                val packageInfo = packageManager.getPackageInfo(ALEXA_PACKAGE_NAME, 0)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode > REQUIRED_MINIMUM_VERSION_CODE
                } else {
                    packageInfo != null
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.w(javaClass.simpleName, "Alexa app is not found")
            // Alexaアプリがインストールされていない場合
            false
        }
    }
}

data class AccountLinkUiState(
    val isProgress : Boolean = false,
    val accountLinkStatus: AccountLinkStatus = AccountLinkStatus.None,
    val accountLinkUrl: String = "",
    val enableSkillUrl: String = "",
    val finishAccountLink: Boolean = false
)

sealed class AccountLinkStatus {
    object Success: AccountLinkStatus()
    object Error: AccountLinkStatus()
    object Start: AccountLinkStatus()
    object WaitForStart: AccountLinkStatus()
    object None: AccountLinkStatus()
}
