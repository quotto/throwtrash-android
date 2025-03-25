package net.mythrowaway.app.module.account.presentation.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.mythrowaway.app.module.account.usecase.AccountUseCase
import javax.inject.Inject

class AccountViewModel(private val _accountUsecase: AccountUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState = _uiState.asStateFlow()

    class Factory @Inject constructor(private val _accountUsecase: AccountUseCase): ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AccountViewModel(_accountUsecase) as T
        }
    }

    init {
        // Load the current user on initialization
        _uiState.value = AccountUiState(
            currentUser = _accountUsecase.getCurrentUser()
        )
    }

    suspend fun loadInformation() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val userId = _accountUsecase.getUserId()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    userId = userId ?: "",
                    currentUser = _accountUsecase.getCurrentUser()
                )
            }
        }
    }

    fun signInWithGoogle(
        context: Context,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val result = _accountUsecase.signInWithGoogle(context)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentUser = user
                    )
                    onSuccess()
                    loadInformation()
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onFailure()
                }
            )
        }
    }

    fun signOut(
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val result = _accountUsecase.signOut()
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentUser = _accountUsecase.getCurrentUser()
                    )
                    onSuccess()
                    loadInformation()
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onFailure()
                }
            )
        }
    }

    fun deleteAccount(
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val result = _accountUsecase.deleteAccount()
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentUser = _accountUsecase.getCurrentUser()
                    )
                    onSuccess()
                    loadInformation()
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onFailure()
                }
            )
        }
    }
}

data class AccountUiState(
    val isLoading: Boolean = false,
    val userId: String = "",
    val currentUser: FirebaseUser? = null
)