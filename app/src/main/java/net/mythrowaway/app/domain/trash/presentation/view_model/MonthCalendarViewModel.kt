package net.mythrowaway.app.domain.trash.presentation.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mythrowaway.app.domain.trash.dto.MonthCalendarDTO
import net.mythrowaway.app.domain.trash.usecase.CalendarUseCase
import java.time.LocalDate
import javax.inject.Inject
import kotlin.properties.Delegates

class MonthCalendarViewModel(
  position: Int,
  private val calendarUseCase: CalendarUseCase
): ViewModel(){

  private var year by Delegates.notNull<Int>();
  private var month by Delegates.notNull<Int>();
  private val _trashCalendar: MutableLiveData<MonthCalendarDTO> by lazy {
    MutableLiveData<MonthCalendarDTO>()
  }
  val trashCalendar: LiveData<MonthCalendarDTO> get() = _trashCalendar

  class Factory @Inject constructor(
    private val calendarUseCase: CalendarUseCase
  ) {
    fun create(position: Int): MonthCalendarViewModel {
      return MonthCalendarViewModel(position, calendarUseCase)
    }
  }

  init {
    Log.d(this.javaClass.simpleName, "init calendar $position")
    val date = LocalDate.now().plusMonths(position.toLong())
    this.year = date.year
    this.month = date.monthValue
  }
  suspend fun updateCalendar() {
    withContext(Dispatchers.IO) {
      calendarUseCase.getTrashCalendarOfMonth(year, month)
    }.let {
      _trashCalendar.value = it
    }
  }
}

