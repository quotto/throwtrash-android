package net.mythrowaway.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import net.mythrowaway.app.usecase.CalendarSyncResult
import net.mythrowaway.app.usecase.CalendarUseCase
import javax.inject.Inject

class CalendarViewModel(
  private val calendarUseCase: CalendarUseCase
): ViewModel() {
  private val _message: MutableSharedFlow<CalendarViewModelMessage> = MutableSharedFlow(replay = 0)
  val message: SharedFlow<CalendarViewModelMessage> get() = _message

  class Factory @Inject constructor(
    private val calendarUseCase: CalendarUseCase
  ) {
    fun create(): CalendarViewModel {
      return CalendarViewModel(calendarUseCase)
    }
  }
  suspend fun refresh() {
    viewModelScope.launch {
      val result = calendarUseCase.syncData()
      when (result) {
        CalendarSyncResult.FAILED -> {
          _message.emit(CalendarViewModelMessage.Failed)
        }
        CalendarSyncResult.PULL_SUCCESS -> {
          _message.emit(CalendarViewModelMessage.PullUpdate)
        }
        CalendarSyncResult.PENDING, CalendarSyncResult.PUSH_SUCCESS -> {
          _message.emit(CalendarViewModelMessage.Update)
        }
        CalendarSyncResult.NONE -> {
          Log.d(Class::class.java.simpleName, "No update")
        }
      }
    }
  }
}
sealed class CalendarViewModelMessage {
  object Update: CalendarViewModelMessage()
  object PullUpdate: CalendarViewModelMessage()
  object Failed: CalendarViewModelMessage()
}
