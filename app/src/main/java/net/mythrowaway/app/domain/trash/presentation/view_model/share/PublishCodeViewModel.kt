package net.mythrowaway.app.domain.trash.presentation.view_model.share

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.mythrowaway.app.domain.trash.usecase.PublishCodeUseCase
import javax.inject.Inject

class PublishCodeViewModel(private val _publishCodeUseCase: PublishCodeUseCase): ViewModel() {
  private val _viewState: MutableStateFlow<PublishCodeViewState> = MutableStateFlow(
    PublishCodeViewState()
  )
  val viewState: StateFlow<PublishCodeViewState> = _viewState

  fun publishCode() {
    _viewState.value = _viewState.value.copy(isProgress = true)
    viewModelScope.launch {
      withContext(Dispatchers.IO) {
        try {
          val code = _publishCodeUseCase.publishActivationCode()
          _viewState.value = _viewState.value.copy(
            code = code,
            isProgress = false,
            isError = false
          )
        } catch (e: Exception) {
          FirebaseCrashlytics.getInstance().recordException(e)
          _viewState.value =
            _viewState.value.copy(isProgress = false, isError = true)
        }
      }
    }
  }

  class Factory @Inject constructor(private val _publishCodeUseCase: PublishCodeUseCase): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return PublishCodeViewModel(_publishCodeUseCase) as T
    }
  }
}

data class PublishCodeViewState(
  val code: String = "",
  val isProgress: Boolean = false,
  val isError : Boolean = false,
)
