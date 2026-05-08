package net.mythrowaway.app.module.trash.presentation.view_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.mythrowaway.app.module.trash.usecase.ScheduleSearchImportUseCase
import net.mythrowaway.app.module.trash.usecase.ScheduleSearchStateRepositoryInterface
import javax.inject.Inject

enum class ScheduleSearchImportRequestStatus {
  INIT,
  STARTED
}

class ScheduleSearchImportViewModel(
  private val importUseCase: ScheduleSearchImportUseCase,
  private val stateRepository: ScheduleSearchStateRepositoryInterface,
  private val notifier: ScheduleSearchImportNotifier
) : ViewModel() {
  private val _requestStatus: MutableState<ScheduleSearchImportRequestStatus> =
    mutableStateOf(ScheduleSearchImportRequestStatus.INIT)
  val requestStatus: State<ScheduleSearchImportRequestStatus> = _requestStatus

  fun shouldShowStartupDialog(): Boolean {
    return stateRepository.shouldShowStartupDialog()
  }

  fun suppressStartupDialog() {
    stateRepository.suppressStartupDialog()
  }

  fun startImport(input: String) {
    _requestStatus.value = ScheduleSearchImportRequestStatus.STARTED
    viewModelScope.launch {
      withContext(Dispatchers.IO) {
        val result = importUseCase.import(input)
        notifier.notifyImportResult(result.status, result.message)
      }
    }
  }

  fun resetRequestStatus() {
    _requestStatus.value = ScheduleSearchImportRequestStatus.INIT
  }

  fun consumeImportMessage(): String? {
    return stateRepository.consumeImportMessage()
  }

  class Factory @Inject constructor(
    private val importUseCase: ScheduleSearchImportUseCase,
    private val stateRepository: ScheduleSearchStateRepositoryInterface,
    private val notifier: ScheduleSearchImportNotifier
  ) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return ScheduleSearchImportViewModel(importUseCase, stateRepository, notifier) as T
    }
  }
}
