package net.mythrowaway.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.mythrowaway.app.usecase.InformationUseCase
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

    suspend fun loadInformation() {
        _uiState.value = InformationUiState(isLoading = true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val userId = _informationUsecase.showUserInformation()
                _uiState.value = InformationUiState(isLoading = false, userId = userId)
            }
        }
    }
}

data class InformationUiState(
    val isLoading: Boolean = false,
    val userId: String = ""
)