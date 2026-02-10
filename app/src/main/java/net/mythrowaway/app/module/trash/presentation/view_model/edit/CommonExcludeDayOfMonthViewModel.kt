package net.mythrowaway.app.module.trash.presentation.view_model.edit

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.mythrowaway.app.module.trash.presentation.view_model.edit.data.ExcludeDayOfMonthViewData
import net.mythrowaway.app.module.trash.presentation.view_model.edit.mapper.ExcludeDayOfMonthMapper
import net.mythrowaway.app.module.trash.usecase.CommonExcludeDayOfMonthUseCase
import javax.inject.Inject

class CommonExcludeDayOfMonthViewModel(
  private val useCase: CommonExcludeDayOfMonthUseCase
) : ViewModel() {
  private val _excludeDayOfMonthViewDataList: MutableState<List<ExcludeDayOfMonthViewData>> = mutableStateOf(listOf())
  private val _enabledAddExcludeDayButton: MutableState<Boolean> = mutableStateOf(true)

  val excludeDayOfMonthViewDataList: State<List<ExcludeDayOfMonthViewData>> get() = _excludeDayOfMonthViewDataList
  val enabledAddExcludeDayButton: State<Boolean> get() = _enabledAddExcludeDayButton

  class Factory @Inject constructor(
    private val useCase: CommonExcludeDayOfMonthUseCase
  ) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return CommonExcludeDayOfMonthViewModel(useCase) as T
    }
  }

  init {
    load()
  }

  fun load() {
    viewModelScope.launch {
      val excludeList = withContext(Dispatchers.IO) {
        useCase.getCommonExcludeDays()
      }
      _excludeDayOfMonthViewDataList.value = excludeList.map { ExcludeDayOfMonthMapper.toViewData(it) }
      setComponentEnabled()
    }
  }

  suspend fun save() {
    val targetList = _excludeDayOfMonthViewDataList.value.map { ExcludeDayOfMonthMapper.toDTO(it) }
    withContext(Dispatchers.IO) {
      useCase.saveCommonExcludeDays(targetList)
    }
  }

  fun appendExcludeDayOfMonth() {
    if (!_enabledAddExcludeDayButton.value) {
      return
    }
    val newExcludeDayOfMonthViewDataList = _excludeDayOfMonthViewDataList.value.toMutableList()
    newExcludeDayOfMonthViewDataList.add(ExcludeDayOfMonthViewData(0, 0))
    _excludeDayOfMonthViewDataList.value = newExcludeDayOfMonthViewDataList
    setComponentEnabled()
  }

  fun removeExcludeDayOfMonth(position: Int) {
    val newExcludeDayOfMonthViewDataList = _excludeDayOfMonthViewDataList.value.toMutableList()
    if (position < 0 || position >= newExcludeDayOfMonthViewDataList.size) {
      return
    }
    newExcludeDayOfMonthViewDataList.removeAt(position)
    _excludeDayOfMonthViewDataList.value = newExcludeDayOfMonthViewDataList
    setComponentEnabled()
  }

  fun updateExcludeDayOfMonth(position: Int, month: Int, dayOfMonth: Int) {
    val newExcludeDayOfMonthViewDataList = _excludeDayOfMonthViewDataList.value.toMutableList()
    if (position < 0 || position >= newExcludeDayOfMonthViewDataList.size) {
      return
    }
    newExcludeDayOfMonthViewDataList[position] = ExcludeDayOfMonthViewData(month, dayOfMonth)
    _excludeDayOfMonthViewDataList.value = newExcludeDayOfMonthViewDataList
  }

  private fun setComponentEnabled() {
    _enabledAddExcludeDayButton.value = _excludeDayOfMonthViewDataList.value.size < 10
  }
}
