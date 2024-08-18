package net.mythrowaway.app.viewmodel.share

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.mythrowaway.app.usecase.ActivateUseCase
import javax.inject.Inject

class ActivateViewModel(private val _activateUseCase: ActivateUseCase): ViewModel() {
  private val _viewState: MutableStateFlow<ActivateViewState> = MutableStateFlow(ActivateViewState())
  val viewState: StateFlow<ActivateViewState> = _viewState

  fun inputCode(code: String) {
    _viewState.value = _viewState.value.copy(code = code)
  }
  fun activate() {
    _viewState.value = _viewState.value.copy(isProgress = true)
    viewModelScope.launch {
      withContext(Dispatchers.IO) {
        try {
          val result = _activateUseCase.activate(_viewState.value.code)
          if (result == ActivateUseCase.ActivationResult.ACTIVATE_SUCCESS) {
            _viewState.value = _viewState.value.copy(
              isProgress = false,
              activateStatus = ActivateStatus.Success
            )
          } else {
            _viewState.value = _viewState.value.copy(
              isProgress = false,
              activateStatus = ActivateStatus.Error
            )
          }
        } catch (e: Exception) {
          FirebaseCrashlytics.getInstance().recordException(e)
          _viewState.value =
            _viewState.value.copy(
              isProgress = false,
              activateStatus = ActivateStatus.Error
            )
        }
      }
    }
  }

  class Factory @Inject constructor(private val _activateUseCase: ActivateUseCase): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return ActivateViewModel(_activateUseCase) as T
    }
  }
}

data class ActivateViewState(
  val code: String = "",
  val isProgress: Boolean = false,
  val activateStatus: ActivateStatus = ActivateStatus.None
)

sealed class ActivateStatus {
  object None: ActivateStatus()
  object Success: ActivateStatus()
  object Error: ActivateStatus()
}