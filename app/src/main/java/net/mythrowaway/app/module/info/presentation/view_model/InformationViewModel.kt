package net.mythrowaway.app.module.info.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.mythrowaway.app.module.info.usecase.InformationUseCase
import javax.inject.Inject

class InformationViewModel(private val _informationUsecase: InformationUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(InformationUiState())
    val uiState = _uiState.asStateFlow()

    class Factory @Inject constructor(private val _informationUsecase: InformationUseCase): ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return InformationViewModel(_informationUsecase) as T
        }
    }

    init {
        // Load the current user on initialization
        _uiState.value = InformationUiState(
            currentUser = _informationUsecase.getCurrentUser()
        )
    }

    suspend fun loadInformation() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val userId = _informationUsecase.getUserId()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    userId = userId ?: "",
                    currentUser = _informationUsecase.getCurrentUser()
                )
            }
        }
    }

    fun signInWithGoogle(
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                _informationUsecase.signInWithGoogle()
            }
            result.fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentUser = user
                    )
                    onSuccess(user)
                    loadInformation()
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onFailure(exception as Exception)
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
            val result = withContext(Dispatchers.IO) {
                _informationUsecase.signOut()
            }
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentUser = _informationUsecase.getCurrentUser()
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
            val result = withContext(Dispatchers.IO) {
                _informationUsecase.deleteAccount()
            }
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentUser = _informationUsecase.getCurrentUser()
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

data class InformationUiState(
    val isLoading: Boolean = false,
    val userId: String = "",
    val currentUser: FirebaseUser? = null
)