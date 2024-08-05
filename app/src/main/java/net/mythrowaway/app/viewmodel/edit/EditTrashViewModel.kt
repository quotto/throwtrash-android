package net.mythrowaway.app.viewmodel.edit

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.mythrowaway.app.domain.TrashType
import net.mythrowaway.app.usecase.EditUseCase
import net.mythrowaway.app.usecase.dto.ExcludeDayDTO
import net.mythrowaway.app.usecase.dto.IntervalWeeklyScheduleDTO
import net.mythrowaway.app.usecase.dto.MonthlyScheduleDTO
import net.mythrowaway.app.usecase.dto.OrdinalWeeklyScheduleDTO
import net.mythrowaway.app.usecase.dto.ScheduleDTO
import net.mythrowaway.app.usecase.dto.TrashDTO
import net.mythrowaway.app.usecase.dto.WeeklyScheduleDTO
import java.time.LocalDate
import javax.inject.Inject

class EditTrashViewModel(private val _usecase: EditUseCase): ViewModel() {
  private var _id: String = ""
  private var _trashType: TrashType = TrashType.BURN
  private val _selectedTrashTypePosition: MutableState<Int> = mutableIntStateOf(0)
  private var _displayTrashName: MutableState<String> = mutableStateOf("")
  private val _enabledRegisterButton: MutableState<Boolean> = mutableStateOf(true)
  private val _displayTrashNameErrorMessage: MutableState<String> = mutableStateOf("")
  private val _enabledRemoveButton: MutableState<Boolean> = mutableStateOf(false)
  private val _enabledAppendButton: MutableState<Boolean> = mutableStateOf(true)
  private val _visibleDisplayNameInput: MutableStateFlow<Boolean> = MutableStateFlow(false)
  private val _scheduleDTOList: MutableState<MutableList<ScheduleDTO>> = mutableStateOf(arrayListOf())
  private val _inputValidationMessage: MutableStateFlow<String> = MutableStateFlow("")
  private val _registerMessage: MutableStateFlow<RegisterMessage?> = MutableStateFlow(null)
  private val _scheduleMessage: MutableSharedFlow<ScheduleMessage?> = MutableSharedFlow(replay = 1)
  private val _excludeDayDTOList: MutableState<MutableList<ExcludeDayDTO>> = mutableStateOf(arrayListOf())
  private val _enabledAddExcludeDayButton: MutableState<Boolean> = mutableStateOf(true)
  private val _excludeDayMessage: MutableSharedFlow<ExcludeDayMessage?> = MutableSharedFlow(replay = 1)


  val enabledRegisterButton: State<Boolean> get() = _enabledRegisterButton
  val displayTrashName: State<String> get() = _displayTrashName
  val displayTrashNameErrorMessage: State<String> get() = _displayTrashNameErrorMessage
  val enabledRemoveButton: State<Boolean> get() = _enabledRemoveButton
  val enabledAppendButton: State<Boolean> get() = _enabledAppendButton
  val scheduleDTOList: State<List<ScheduleDTO>> get() = _scheduleDTOList
  val inputValidationMessage: StateFlow<String> get() = _inputValidationMessage
  val registerMessage: StateFlow<RegisterMessage?> get() = _registerMessage
  val scheduleMessage: SharedFlow<ScheduleMessage?> get() = _scheduleMessage

  val visibleDisplayNameInput: StateFlow<Boolean> get() = _visibleDisplayNameInput

  val excludeDayDTOList: State<List<ExcludeDayDTO>> get() = _excludeDayDTOList
  val enabledAddExcludeDayButton: State<Boolean> get() = _enabledAddExcludeDayButton
  val trashTypeName: String get() = _trashType.getTrashText()
  val selectedTrashTypePosition: State<Int> get() = _selectedTrashTypePosition

  class Factory @Inject constructor(private val _usecase: EditUseCase): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return EditTrashViewModel(_usecase) as T
    }
  }

  init {
    val trashDTO = _usecase.createNewTrashDTO()
    _id = trashDTO.id
    _trashType = trashDTO.type
    _scheduleDTOList.value.addAll(trashDTO.scheduleDTOs)

    viewModelScope.launch {
      _scheduleMessage.emit(ScheduleMessage.Add(0))
    }
  }

  fun setTrashType(trashType: String) {
    _trashType = TrashType.fromString(trashType)
    _visibleDisplayNameInput.value = _trashType == TrashType.OTHER
  }

  suspend fun registerTrash() {
    withContext(Dispatchers.IO) {
      launch {
        _enabledRegisterButton.value = false
        val registeredDisplayName = if (_trashType == TrashType.OTHER) _displayTrashName.value else ""
        val result = _usecase.saveTrash(
          _id,
          _trashType,
          registeredDisplayName,
          _scheduleDTOList.value,
          _excludeDayDTOList.value
        )
        try {
          when (result) {
            EditUseCase.SaveResult.SUCCESS -> {
              _registerMessage.emit(RegisterMessage.Success("登録に成功しました"))
            }

            EditUseCase.SaveResult.ERROR_MAX_SCHEDULE -> {
              _registerMessage.emit(RegisterMessage.Failure("登録可能なゴミ出しスケジュールの上限を超えています"))
            }
          }
        } catch (e: Exception) {
          _registerMessage.emit(RegisterMessage.Failure("登録に失敗しました"))
        } finally {
          _enabledRegisterButton.value = true
        }
      }
    }
  }

  fun changeTrashType(selectedPosition: Int, trashType: String) {
    _trashType = TrashType.fromString(trashType)
    if (_trashType == TrashType.OTHER) {
      _displayTrashName.value = ""
      _enabledRegisterButton.value = false
    } else {
      _enabledRegisterButton.value = true
    }
    _selectedTrashTypePosition.value = selectedPosition
  }

  fun changeDisplayTrashName(changedName: String) {
    _displayTrashName.value = changedName
    if (_trashType == TrashType.OTHER) {
      val result: EditUseCase.ResultCode = _usecase.validateOtherTrashText(_displayTrashName.value)
      Log.d(this.javaClass.simpleName, "validateDisplayName: $result")
      when (result) {
        EditUseCase.ResultCode.SUCCESS -> {
          _enabledRegisterButton.value = true
          _displayTrashNameErrorMessage.value = ""
        }

        EditUseCase.ResultCode.INVALID_OTHER_TEXT_EMPTY -> {
          _enabledRegisterButton.value = false
          _displayTrashNameErrorMessage.value = "空の名前は設定できません"
        }

        EditUseCase.ResultCode.INVALID_OTHER_TEXT_CHARACTER -> {
          _enabledRegisterButton.value = false
          _displayTrashNameErrorMessage.value = "使用できない文字が含まれています"
        }

        EditUseCase.ResultCode.INVALID_OTHER_TEXT_OVER -> {
          _displayTrashNameErrorMessage.value = "ゴミの名前は10文字以内で設定してください"
        }
      }
    }
  }

  fun addSchedule() {
        val newTrashDTO = _usecase.appendNewSchedule(
          TrashDTO(
            _id,
            _trashType,
            _displayTrashName.value,
            _scheduleDTOList.value,
            _excludeDayDTOList.value
          )
        )
        _scheduleDTOList.value = newTrashDTO.scheduleDTOs.toMutableList()
        _enabledAppendButton.value = _usecase.canAddSchedule(newTrashDTO)
        _enabledRemoveButton.value = _usecase.canRemoveSchedule(newTrashDTO)
      }

  fun removeSchedule(position : Int) {
        val newTrashDTO = _usecase.removeSchedule(
          TrashDTO(
            _id,
            _trashType,
            _displayTrashName.value,
            _scheduleDTOList.value,
            _excludeDayDTOList.value
          ),
          position
        )
        _scheduleDTOList.value = newTrashDTO.scheduleDTOs.toMutableList()
        _enabledAppendButton.value = _usecase.canAddSchedule(newTrashDTO)
        _enabledRemoveButton.value = _usecase.canRemoveSchedule(newTrashDTO)
      }

  fun changeScheduleType(position: Int, scheduleType: String) {
    Log.d(this.javaClass.simpleName, "changeScheduleType: $position, $scheduleType")
    val newScheduleDTOList = _scheduleDTOList.value.toMutableList()
    when(scheduleType) {
      "weekly" -> {
        newScheduleDTOList[position] = WeeklyScheduleDTO(0)
      }
      "monthly" -> {
        newScheduleDTOList[position] = MonthlyScheduleDTO(0)
      }
      "ordinalWeekly" -> {
        newScheduleDTOList[position] = OrdinalWeeklyScheduleDTO(0,0)
      }
      "intervalWeekly" -> {
        newScheduleDTOList[position] = IntervalWeeklyScheduleDTO(LocalDate.now().toString(),0,0)
      }
      else -> {
        Log.d(this.javaClass.simpleName, "changeScheduleType: unknown schedule type")
      }
    }
    _scheduleDTOList.value = newScheduleDTOList
  }

  fun changeScheduleValue(position: Int, value: ScheduleDTO) {
    val newScheduleDTOList = _scheduleDTOList.value.toMutableList()
    when(value) {
      is WeeklyScheduleDTO -> {
        newScheduleDTOList[position] = WeeklyScheduleDTO(value.dayOfWeek)
      }
      is MonthlyScheduleDTO -> {
        newScheduleDTOList[position] = MonthlyScheduleDTO(value.dayOfMonth)
      }
      is OrdinalWeeklyScheduleDTO -> {
        newScheduleDTOList[position] = OrdinalWeeklyScheduleDTO(value.ordinal, value.dayOfWeek)
      }
      is IntervalWeeklyScheduleDTO -> {
        newScheduleDTOList[position] = IntervalWeeklyScheduleDTO(value.start, value.dayOfWeek, value.interval)
      }
    }
    _scheduleDTOList.value = newScheduleDTOList
  }

  fun updateWeeklyScheduleValue(position: Int, dayOfWeek: Int) {
    Log.d(this.javaClass.simpleName, "updateWeeklyScheduleValue: $position, $dayOfWeek")
    _scheduleDTOList.value[position] = WeeklyScheduleDTO(dayOfWeek)
  }

  fun updateMonthlyScheduleValue(position: Int, dayOfMonth: Int) {
    Log.d(this.javaClass.simpleName, "updateMonthlyScheduleValue: $position, $dayOfMonth")
    _scheduleDTOList.value[position] = MonthlyScheduleDTO(dayOfMonth)
  }

  fun updateOrdinalWeeklyScheduleValue(position: Int, orderOfWeek: Int, dayOfWeek: Int) {
    Log.d(this.javaClass.simpleName, "updateOrdinalWeeklyScheduleValue: $position, $orderOfWeek, $dayOfWeek")
    _scheduleDTOList.value[position] = OrdinalWeeklyScheduleDTO(orderOfWeek,dayOfWeek)
  }

  fun updateIntervalWeeklyScheduleValue(position: Int, start: String, dayOfWeek: Int, interval: Int ) {
    Log.d(this.javaClass.simpleName, "updateIntervalWeeklyScheduleValue: $position, $start, $dayOfWeek, $interval")
    _scheduleDTOList.value[position] = IntervalWeeklyScheduleDTO(start,dayOfWeek, interval)
  }

  fun appendExcludeDayOfMonth() {
    val newExcludeDayDTOList = _usecase.addExcludeDay(_excludeDayDTOList.value, 1, 1)
    _excludeDayDTOList.value = newExcludeDayDTOList.toMutableList()
  }

  fun removeExcludeDayOfMonth(position: Int) {
    val newExcludeDayDTOList = _usecase.removeExcludeDay(_excludeDayDTOList.value, position)
    _excludeDayDTOList.value = newExcludeDayDTOList.toMutableList()
    _enabledAddExcludeDayButton.value = _usecase.canAddExcludeDay(newExcludeDayDTOList)
  }

  fun updateExcludeDayOfMonth(position: Int, month: Int, dayOfMonth: Int) {
    val newExcludeDayDTOList = _excludeDayDTOList.value.toMutableList()
    newExcludeDayDTOList[position] = ExcludeDayDTO(month, dayOfMonth)
    _excludeDayDTOList.value = newExcludeDayDTOList
  }
}

sealed class ScheduleMessage {
  data class Add(val position: Int): ScheduleMessage()
  data class Remove(val position: Int): ScheduleMessage()

  data class Update(val position: Int): ScheduleMessage()
}

sealed class RegisterMessage {
  data class Success(val message: String): RegisterMessage()
  data class Failure(val message: String): RegisterMessage()
}

sealed class ExcludeDayMessage {
  object Add : ExcludeDayMessage()
  data class Remove(val position: Int): ExcludeDayMessage()
  data class Update(val position: Int, val month: Int, val dayOfMonth: Int): ExcludeDayMessage()
}