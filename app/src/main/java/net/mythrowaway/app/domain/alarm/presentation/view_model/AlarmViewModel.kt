package net.mythrowaway.app.domain.alarm.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import net.mythrowaway.app.domain.alarm.usecase.AlarmManager
import net.mythrowaway.app.domain.alarm.usecase.AlarmUseCase
import net.mythrowaway.app.domain.alarm.usecase.dto.AlarmConfigDTO

class AlarmViewModel(
  private val _alarmUseCase: AlarmUseCase,
  private val _alarmManager: AlarmManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(AlarmUiState())
    val uiState: StateFlow<AlarmUiState> = _uiState.asStateFlow()


    class Factory(
      private val _usecase: AlarmUseCase,
      private val _alarmManager: AlarmManager
    ): ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AlarmViewModel(_usecase,_alarmManager) as T
        }
    }
    init {
        val alarm: AlarmConfigDTO = _alarmUseCase.getAlarmConfig()
        _uiState.value = AlarmUiState(
            notifyChecked = alarm.enabled,
            hour = alarm.hour,
            minute = alarm.minute,
            notifyEverydayChecked = alarm.notifyEveryday
        )
    }

    fun openTimePicker() {
        _uiState.update { state ->
            state.copy(timePickerOpened = true)
        }
    }

    fun closeTimePicker() {
        _uiState.update { state ->
            state.copy(timePickerOpened = false)
        }
    }

    fun changeTime(hour: Int, minute: Int) {
        _uiState.update { state ->
            state.copy(hour = hour, minute = minute, timeText = toTimeText(hour, minute))
        }
    }

    fun toggleNotify(value: Boolean) {
        _uiState.update { state ->
            state.copy(notifyChecked = value)
        }
    }

    fun changeNotifyEveryday(value: Boolean) {
        _uiState.update { state ->
            state.copy(notifyEverydayChecked = value)
        }
    }

    fun saveAlarm() {
        val state = _uiState.value
        try {
            _alarmUseCase.saveAlarmConfig(
                AlarmConfigDTO(
                    enabled = state.notifyChecked,
                    hour = state.hour,
                    minute = state.minute,
                    notifyEveryday = state.notifyEverydayChecked
                ),
                _alarmManager
            )
            _uiState.update { state.copy(alarmSavedStatus = AlarmSavedStatus.Success) }
        } catch (e: Exception) {
            _uiState.update { state.copy(alarmSavedStatus = AlarmSavedStatus.Failure) }
        }
    }
}

private fun toTimeText(hour: Int, minute: Int): String {
    return "%1$02d:%2$02d".format(hour, minute)
}

data class AlarmUiState(
  val notifyChecked: Boolean = false,
  val timePickerOpened: Boolean = false,
  val hour: Int = 7,
  val minute: Int = 0,
  val timeText: String = toTimeText(hour, minute),
  val notifyEverydayChecked: Boolean = false,
  val alarmSavedStatus: AlarmSavedStatus = AlarmSavedStatus.None
)

sealed class AlarmSavedStatus {
    object Success : AlarmSavedStatus()
    object Failure : AlarmSavedStatus()
    object None : AlarmSavedStatus()
}