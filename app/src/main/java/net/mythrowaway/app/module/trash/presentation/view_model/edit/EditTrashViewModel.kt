package net.mythrowaway.app.module.trash.presentation.view_model.edit

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
import net.mythrowaway.app.module.trash.entity.trash.TrashType
import net.mythrowaway.app.module.trash.usecase.EditUseCase
import net.mythrowaway.app.module.trash.presentation.view_model.edit.data.ExcludeDayOfMonthViewData
import net.mythrowaway.app.module.trash.presentation.view_model.edit.data.IntervalWeeklyScheduleViewData
import net.mythrowaway.app.module.trash.presentation.view_model.edit.data.MonthlyScheduleViewData
import net.mythrowaway.app.module.trash.presentation.view_model.edit.data.OrdinalWeeklyScheduleViewData
import net.mythrowaway.app.module.trash.presentation.view_model.edit.data.ScheduleType
import net.mythrowaway.app.module.trash.presentation.view_model.edit.data.ScheduleViewData
import net.mythrowaway.app.module.trash.presentation.view_model.edit.data.TrashTypeViewData
import net.mythrowaway.app.module.trash.presentation.view_model.edit.data.WeeklyScheduleViewData
import net.mythrowaway.app.module.trash.presentation.view_model.edit.mapper.ExcludeDayOfMonthMapper
import net.mythrowaway.app.module.trash.presentation.view_model.edit.mapper.ScheduleMapper
import net.mythrowaway.app.module.trash.presentation.view_model.edit.mapper.TrashTypeMapper
import net.mythrowaway.app.module.trash.usecase.MaxScheduleException
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

enum class InputTrashNameError {
  EMPTY,
  TOO_LONG,
  INVALID_CHAR,
  NONE
}

class EditTrashViewModel(private val _usecase: EditUseCase): ViewModel() {
  private var _id: String = ""
  private var _trashType: MutableState<TrashTypeViewData> = mutableStateOf(
    TrashTypeViewData(
    TrashType.BURN.toString(), TrashType.BURN.getTrashText(), "")
  )
  private val _enabledRegisterButton: MutableState<Boolean> = mutableStateOf(true)
  private val _inputTrashNameError: MutableState<InputTrashNameError> = mutableStateOf(InputTrashNameError.NONE)
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
  val inputTrashNameError: State<InputTrashNameError> get() = _inputTrashNameError
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
    val trashDTO = _usecase.createNewTrash()
    _id = trashDTO.id
    _trashType.value = TrashTypeMapper.toViewData(trashDTO)
    _scheduleViewDataList.value = trashDTO.scheduleDTOList.map { ScheduleMapper.toViewData(it) }.toMutableList()
    _excludeDayOfMonthViewDataList.value = trashDTO.excludeDayOfMonthDTOList.map { ExcludeDayOfMonthMapper.toViewData(it) }.toMutableList()

    viewModelScope.launch {
      _scheduleMessage.emit(ScheduleMessage.Add(0))
    }
  }

  suspend fun setTrash(trashId: String) {
    _loadStatus.value = LoadStatus.INIT
    withContext(Dispatchers.IO) {
      val trashDTO = _usecase.getTrashById(trashId)
      if (trashDTO == null) {
        _loadStatus.value = LoadStatus.ERROR
      }
      _id = trashDTO!!.id
      _trashType.value = TrashTypeMapper.toViewData(trashDTO)
      _scheduleViewDataList.value = trashDTO.scheduleDTOList.map { ScheduleMapper.toViewData(it) }.toMutableList()
      _excludeDayOfMonthViewDataList.value = trashDTO.excludeDayOfMonthDTOList.map { ExcludeDayOfMonthMapper.toViewData(it) }.toMutableList()
      _loadStatus.value = LoadStatus.SUCCESS
    }
  }

  suspend fun saveTrash() {
    withContext(Dispatchers.IO) {
      launch {
        _enabledRegisterButton.value = false
        try {
          _usecase.saveTrash(
            _id,
            TrashType.fromString(_trashType.value.type),
            _trashType.value.inputName,
            _scheduleViewDataList.value.map { ScheduleMapper.toDTO(it) },
            _excludeDayOfMonthViewDataList.value.map { ExcludeDayOfMonthMapper.toDTO(it) }
          )
          _savedStatus.value = SavedStatus.SUCCESS
        } catch(e: MaxScheduleException) {
          _savedStatus.value = SavedStatus.ERROR_MAX_SCHEDULE
          _enabledRegisterButton.value = true
        } catch(e: Exception) {
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
      if(newTrashTypeViewData.inputName.isEmpty()){
        _enabledRegisterButton.value = false
        _inputTrashNameError.value = InputTrashNameError.EMPTY
      } else if(newTrashTypeViewData.inputName.length > 10) {
        _enabledRegisterButton.value = false
        _inputTrashNameError.value = InputTrashNameError.TOO_LONG
      } else if(
        Regex("^[A-Za-z0-9Ａ-Ｚａ-ｚ０-９ぁ-んァ-ヶー一-龠\\s]+$").find(newTrashTypeViewData.inputName)?.value == null
      ) {
        _enabledRegisterButton.value = false
        _inputTrashNameError.value = InputTrashNameError.INVALID_CHAR
      } else {
        _enabledRegisterButton.value = true
        _inputTrashNameError.value = InputTrashNameError.NONE
      }
    }
  }

  fun addSchedule() {
        val newList = _scheduleViewDataList.value.toMutableList()
        newList.add(WeeklyScheduleViewData(0))
        _scheduleViewDataList.value = newList
        setComponentEnabled()
      }

  fun removeSchedule(position : Int) {
        val newList = _scheduleViewDataList.value.toMutableList()
        newList.removeAt(position)
        _scheduleViewDataList.value = newList
        setComponentEnabled()
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
    val newExcludeDayOfMonthViewDataList = _excludeDayOfMonthViewDataList.value.toMutableList()
    newExcludeDayOfMonthViewDataList.add(ExcludeDayOfMonthViewData(0, 0))
    _excludeDayOfMonthViewDataList.value = newExcludeDayOfMonthViewDataList
    setComponentEnabled()
  }

  fun removeExcludeDayOfMonth(position: Int) {
    val newExcludeDayOfMonthViewDataList = _excludeDayOfMonthViewDataList.value.toMutableList()
    newExcludeDayOfMonthViewDataList.removeAt(position)
    _excludeDayOfMonthViewDataList.value = newExcludeDayOfMonthViewDataList
    setComponentEnabled()
  }

  fun updateExcludeDayOfMonth(position: Int, month: Int, dayOfMonth: Int) {
    val newExcludeDayOfMonthViewDataList = _excludeDayOfMonthViewDataList.value.toMutableList()
    newExcludeDayOfMonthViewDataList[position] = ExcludeDayOfMonthViewData(month, dayOfMonth)
    _excludeDayOfMonthViewDataList.value = newExcludeDayOfMonthViewDataList
  }

  fun resetLoadStatus() {
    _loadStatus.value = LoadStatus.INIT
  }

  private fun setComponentEnabled() {
    _enabledAppendButton.value = _scheduleViewDataList.value.size < 3
    _enabledRemoveButton.value = _scheduleViewDataList.value.size > 1
    _enabledAddExcludeDayButton.value = _excludeDayOfMonthViewDataList.value.size < 10
  }
}

sealed class ScheduleMessage {
  data class Add(val position: Int): ScheduleMessage()
}