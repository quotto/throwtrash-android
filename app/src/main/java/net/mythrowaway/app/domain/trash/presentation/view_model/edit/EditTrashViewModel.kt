package net.mythrowaway.app.domain.trash.presentation.view_model.edit

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.mythrowaway.app.domain.trash.entity.trash.TrashType
import net.mythrowaway.app.domain.trash.usecase.EditUseCase
import net.mythrowaway.app.domain.trash.dto.TrashDTO
import net.mythrowaway.app.domain.trash.presentation.view_model.edit.data.ExcludeDayOfMonthViewData
import net.mythrowaway.app.domain.trash.presentation.view_model.edit.data.IntervalWeeklyScheduleViewData
import net.mythrowaway.app.domain.trash.presentation.view_model.edit.data.MonthlyScheduleViewData
import net.mythrowaway.app.domain.trash.presentation.view_model.edit.data.OrdinalWeeklyScheduleViewData
import net.mythrowaway.app.domain.trash.presentation.view_model.edit.data.ScheduleType
import net.mythrowaway.app.domain.trash.presentation.view_model.edit.data.ScheduleViewData
import net.mythrowaway.app.domain.trash.presentation.view_model.edit.data.TrashTypeViewData
import net.mythrowaway.app.domain.trash.presentation.view_model.edit.data.WeeklyScheduleViewData
import net.mythrowaway.app.domain.trash.presentation.view_model.edit.mapper.ExcludeDayOfMonthMapper
import net.mythrowaway.app.domain.trash.presentation.view_model.edit.mapper.ScheduleMapper
import net.mythrowaway.app.domain.trash.presentation.view_model.edit.mapper.TrashTypeMapper
import java.time.LocalDate
import javax.inject.Inject

enum class SavedStatus {
  SUCCESS,
  ERROR_MAX_SCHEDULE,
  ERROR,
  INIT,
}

enum class LoadStatus {
  SUCCESS,
  ERROR,
  INIT,
}

class EditTrashViewModel(private val _usecase: EditUseCase): ViewModel() {
  private var _id: String = ""
  private var _trashType: MutableState<TrashTypeViewData> = mutableStateOf(
    TrashTypeViewData(
    TrashType.BURN.toString(), TrashType.BURN.getTrashText(), "")
  )
  private val _enabledRegisterButton: MutableState<Boolean> = mutableStateOf(true)
  private val _displayTrashNameErrorMessage: MutableState<String> = mutableStateOf("")
  private val _enabledRemoveButton: MutableState<Boolean> = mutableStateOf(false)
  private val _enabledAppendButton: MutableState<Boolean> = mutableStateOf(true)
  private val _scheduleViewDataList: MutableState<List<ScheduleViewData>> = mutableStateOf(listOf())
  private val _excludeDayOfMonthViewDataList: MutableState<List<ExcludeDayOfMonthViewData>> = mutableStateOf(listOf())
  private val _enabledAddExcludeDayButton: MutableState<Boolean> = mutableStateOf(true)
  private val _savedStatus: MutableState<SavedStatus> = mutableStateOf(SavedStatus.INIT)
  private val _loadStatus: MutableState<LoadStatus> = mutableStateOf(LoadStatus.INIT)
  private val _scheduleMessage: MutableSharedFlow<ScheduleMessage?> = MutableSharedFlow(replay = 1)


  val trashType: State<TrashTypeViewData> get() = _trashType
  val enabledRegisterButton: State<Boolean> get() = _enabledRegisterButton
  val displayTrashNameErrorMessage: State<String> get() = _displayTrashNameErrorMessage
  val enabledRemoveButton: State<Boolean> get() = _enabledRemoveButton
  val enabledAppendButton: State<Boolean> get() = _enabledAppendButton
  val scheduleViewDataList: State<List<ScheduleViewData>> get() = _scheduleViewDataList
  val excludeDayOfMonthViewDataList: State<List<ExcludeDayOfMonthViewData>> get() = _excludeDayOfMonthViewDataList
  val enabledAddExcludeDayButton: State<Boolean> get() = _enabledAddExcludeDayButton
  val savedStatus: State<SavedStatus> get() = _savedStatus
  val loadStatus: State<LoadStatus> get() = _loadStatus

  class Factory @Inject constructor(private val _usecase: EditUseCase): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return EditTrashViewModel(_usecase) as T
    }
  }

  init {
    val trashDTO = _usecase.createNewTrashDTO()
    _id = trashDTO.id
    _trashType.value = TrashTypeMapper.toViewData(trashDTO)
    _scheduleViewDataList.value = trashDTO.scheduleViewData.map { ScheduleMapper.toViewData(it) }.toMutableList()
    _excludeDayOfMonthViewDataList.value = trashDTO.excludeDayOfMonthDTOs.map { ExcludeDayOfMonthMapper.toViewData(it) }.toMutableList()

    viewModelScope.launch {
      _scheduleMessage.emit(ScheduleMessage.Add(0))
    }
  }

  suspend fun setTrash(trashId: String) {
    _loadStatus.value = LoadStatus.INIT
    withContext(Dispatchers.IO) {
      val trashDTO = _usecase.getTrashData(trashId)
      if (trashDTO == null) {
        _loadStatus.value = LoadStatus.ERROR
      }
      _id = trashDTO!!.id
      _trashType.value = TrashTypeMapper.toViewData(trashDTO)
      _scheduleViewDataList.value = trashDTO.scheduleViewData.map { ScheduleMapper.toViewData(it) }.toMutableList()
      _excludeDayOfMonthViewDataList.value = trashDTO.excludeDayOfMonthDTOs.map { ExcludeDayOfMonthMapper.toViewData(it) }.toMutableList()
      _loadStatus.value = LoadStatus.SUCCESS
    }
  }

  suspend fun saveTrash() {
    withContext(Dispatchers.IO) {
      launch {
        _enabledRegisterButton.value = false
        val result = _usecase.saveTrash(
          _id,
          TrashType.fromString(_trashType.value.type),
          _trashType.value.inputName,
          _scheduleViewDataList.value.map { ScheduleMapper.toDTO(it) },
          _excludeDayOfMonthViewDataList.value.map { ExcludeDayOfMonthMapper.toDTO(it) }
        )
        try {
          when (result) {
            EditUseCase.SaveResult.SUCCESS -> {
              _savedStatus.value = SavedStatus.SUCCESS
            }

            EditUseCase.SaveResult.ERROR_MAX_SCHEDULE -> {
              _savedStatus.value = SavedStatus.ERROR_MAX_SCHEDULE
              _enabledRegisterButton.value = true
            }
          }
        } catch (e: Exception) {
          _savedStatus.value = SavedStatus.ERROR
          _enabledRegisterButton.value = true
        }
      }
    }
  }

  fun changeTrashType(trashType: String) {
    val newTrashType = TrashType.fromString(trashType)
    _enabledRegisterButton.value = newTrashType != TrashType.OTHER
    _trashType.value = TrashTypeViewData(newTrashType.toString(), newTrashType.getTrashText(), "")
  }

  fun changeInputTrashName(changedName: String) {
    val newTrashTypeViewData = _trashType.value.copy(_inputName = changedName)
    _trashType.value = newTrashTypeViewData
    if (newTrashTypeViewData.type == TrashType.OTHER.toString()) {
      val result: EditUseCase.ResultCode = _usecase.validateOtherTrashText(newTrashTypeViewData.inputName)
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
            TrashType.fromString(_trashType.value.type),
            _trashType.value.inputName,
            _scheduleViewDataList.value.map { ScheduleMapper.toDTO(it) },
            _excludeDayOfMonthViewDataList.value.map { ExcludeDayOfMonthMapper.toDTO(it) }
          )
        )
        _scheduleViewDataList.value = newTrashDTO.scheduleViewData.map { ScheduleMapper.toViewData(it) }.toMutableList()
        _enabledAppendButton.value = _usecase.canAddSchedule(newTrashDTO)
        _enabledRemoveButton.value = _usecase.canRemoveSchedule(newTrashDTO)
      }

  fun removeSchedule(position : Int) {
        val newTrashDTO = _usecase.removeSchedule(
          TrashDTO(
            _id,
            TrashType.fromString(_trashType.value.type),
            _trashType.value.inputName,
            _scheduleViewDataList.value.map { ScheduleMapper.toDTO(it) },
            _excludeDayOfMonthViewDataList.value.map { ExcludeDayOfMonthMapper.toDTO(it) }
          ),
          position
        )
        _scheduleViewDataList.value = newTrashDTO.scheduleViewData.map { ScheduleMapper.toViewData(it) }.toMutableList()
        _enabledAppendButton.value = _usecase.canAddSchedule(newTrashDTO)
        _enabledRemoveButton.value = _usecase.canRemoveSchedule(newTrashDTO)
      }

  fun changeScheduleType(position: Int, scheduleType: String) {
    Log.d(this.javaClass.simpleName, "changeScheduleType: $position, $scheduleType")
    val newScheduleViewDataList = _scheduleViewDataList.value.toMutableList()
    when(scheduleType) {
      ScheduleType.WEEKLY.value -> {
        newScheduleViewDataList[position] = WeeklyScheduleViewData(0)
      }
      ScheduleType.MONTHLY.value -> {
        newScheduleViewDataList[position] = MonthlyScheduleViewData(0)
      }
      ScheduleType.ORDINAL_WEEKLY.value -> {
        newScheduleViewDataList[position] = OrdinalWeeklyScheduleViewData(_ordinal = 0,_dayOfWeek = 0)
      }
      ScheduleType.INTERVAL_WEEKLY.value -> {
        newScheduleViewDataList[position] = IntervalWeeklyScheduleViewData(_start = LocalDate.now().toString(),_dayOfWeek = 0,_interval = 0)
      }
      else -> {
        Log.d(this.javaClass.simpleName, "changeScheduleType: unknown schedule type")
      }
    }
    _scheduleViewDataList.value = newScheduleViewDataList
  }

  fun changeScheduleValue(position: Int, value: ScheduleViewData) {
    val newScheduleViewDataList = _scheduleViewDataList.value.toMutableList()
    when(value) {
      is WeeklyScheduleViewData -> {
        newScheduleViewDataList[position] = WeeklyScheduleViewData(value.dayOfWeek)
      }
      is MonthlyScheduleViewData -> {
        newScheduleViewDataList[position] = MonthlyScheduleViewData(value.dayOfMonth)
      }
      is OrdinalWeeklyScheduleViewData -> {
        newScheduleViewDataList[position] = OrdinalWeeklyScheduleViewData(_ordinal = value.ordinal, _dayOfWeek = value.dayOfWeek)
      }
      is IntervalWeeklyScheduleViewData -> {
        newScheduleViewDataList[position] = IntervalWeeklyScheduleViewData(_start = value.startDate, _dayOfWeek = value.dayOfWeek, _interval = value.interval)
      }
    }
    _scheduleViewDataList.value = newScheduleViewDataList
  }

  fun appendExcludeDayOfMonth() {
    val newExcludeDayDTOList = _usecase.addExcludeDay(
      _excludeDayOfMonthViewDataList.value.map { ExcludeDayOfMonthMapper.toDTO(it) }, 1, 1
    )
    _excludeDayOfMonthViewDataList.value = newExcludeDayDTOList.map { ExcludeDayOfMonthMapper.toViewData(it) }.toMutableList()
    _enabledAddExcludeDayButton.value = _usecase.canAddExcludeDay(newExcludeDayDTOList)
  }

  fun removeExcludeDayOfMonth(position: Int) {
    val newExcludeDayDTOList = _usecase.removeExcludeDay(
      _excludeDayOfMonthViewDataList.value.map { ExcludeDayOfMonthMapper.toDTO(it) }, position
    )
    _excludeDayOfMonthViewDataList.value = newExcludeDayDTOList.map { ExcludeDayOfMonthMapper.toViewData(it) }.toMutableList()
    _enabledAddExcludeDayButton.value = _usecase.canAddExcludeDay(newExcludeDayDTOList)
  }

  fun updateExcludeDayOfMonth(position: Int, month: Int, dayOfMonth: Int) {
    val newExcludeDayOfMonthViewDataList = _excludeDayOfMonthViewDataList.value.toMutableList()
    newExcludeDayOfMonthViewDataList[position] = ExcludeDayOfMonthViewData(month, dayOfMonth)
    _excludeDayOfMonthViewDataList.value = newExcludeDayOfMonthViewDataList
  }

  fun resetLoadStatus() {
    _loadStatus.value = LoadStatus.INIT
  }
}

sealed class ScheduleMessage {
  data class Add(val position: Int): ScheduleMessage()
}