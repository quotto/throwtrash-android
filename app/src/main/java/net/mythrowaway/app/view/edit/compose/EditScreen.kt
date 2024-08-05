package net.mythrowaway.app.view.edit.compose

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import net.mythrowaway.app.R
import net.mythrowaway.app.usecase.dto.IntervalWeeklyScheduleDTO
import net.mythrowaway.app.usecase.dto.MonthlyScheduleDTO
import net.mythrowaway.app.usecase.dto.OrdinalWeeklyScheduleDTO
import net.mythrowaway.app.usecase.dto.ScheduleDTO
import net.mythrowaway.app.usecase.dto.WeeklyScheduleDTO
import net.mythrowaway.app.viewmodel.edit.EditTrashViewModel
import java.time.Instant
import java.time.ZonedDateTime

@Composable
fun EditScreen(
  editTrashViewModel: EditTrashViewModel,
  modifier : Modifier = Modifier,
  onClickToExcludeDayOfMonth: () -> Unit
) {
  var scheduleIdList by rememberSaveable { mutableStateOf(listOf("weekly")) }
  var scheduleList by rememberSaveable { mutableStateOf(listOf<ScheduleDTO>(WeeklyScheduleDTO(_dayOfWeek = 0))) }
  Column(
    modifier = modifier.fillMaxSize(),
  ) {
    Column(
      modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()
        .weight(1f)
    ) {
      TrashTypeInput(
        selectedTrashTypePosition = editTrashViewModel.selectedTrashTypePosition.value,
        displayTrashName = editTrashViewModel.displayTrashName.value,
        displayTrashNameErrorMessage = editTrashViewModel.displayTrashNameErrorMessage.value,
        onItemSelected = { selectedTrashTypePosition: Int, trashId: String ->
          editTrashViewModel.changeTrashType(selectedTrashTypePosition, trashId)
        },
        onDisplayTrashNameChanged = { displayTrashName: String ->
          editTrashViewModel.changeDisplayTrashName(displayTrashName)
        },
        modifier = Modifier.fillMaxWidth(),
      )
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState()),
      ) {
        editTrashViewModel.scheduleDTOList.value.forEachIndexed { index, schedule ->
          ScheduleInput(
            selectedScheduleId = scheduleIdList[index],
            scheduleDTO = schedule,
            enabledRemoveButton = editTrashViewModel.enabledRemoveButton.value,
            modifier = Modifier
              .fillMaxWidth()
              .background(
                if (index % 2 == 0) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primaryContainer
              ),
            onChangeScheduleType = { scheduleId: String ->
              editTrashViewModel.changeScheduleType(index, scheduleId)
              val newScheduleIdList = scheduleIdList.toMutableList()
              newScheduleIdList[index] = scheduleId
              scheduleIdList = newScheduleIdList
            },
            onChangeScheduleValue = { value: ScheduleDTO ->
              editTrashViewModel.changeScheduleValue(index, value)
            },
            onDeleteSchedule = {
              val newScheduleIdList = scheduleIdList.toMutableList()
              newScheduleIdList.removeAt(index)

              val newScheduleList = scheduleList.toMutableList()
              newScheduleList.removeAt(index)

              scheduleIdList = newScheduleIdList
              scheduleList = newScheduleList
            }
          )
        }
        if(editTrashViewModel.enabledAppendButton.value) {
          IconButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
              val newScheduleIdList = scheduleIdList.toMutableList()
              newScheduleIdList.add("weekly")
              scheduleIdList = newScheduleIdList
              editTrashViewModel.addSchedule()
            },
          ) {
            Icon(
              modifier = Modifier
                .width(24.dp)
                .height(24.dp),
              imageVector = Icons.Outlined.AddCircle,
              contentDescription = "Add Schedule",
              tint = MaterialTheme.colorScheme.secondary,
            )
          }
        }
      }
    }
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      FilledTonalButton (
        onClick = {
          onClickToExcludeDayOfMonth()
        },
      ) {
        Text(
          text = "除外日の追加",
          style = MaterialTheme.typography.labelSmall,
        )
      }
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
      ) {
        TextButton(
          modifier = Modifier
            .padding(16.dp)
            .width(96.dp)
            .background(
              if(editTrashViewModel.enabledRegisterButton.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inverseOnSurface
            ),
          onClick = {
            Log.d("Edit", "Save")
          },
          enabled = editTrashViewModel.enabledRegisterButton.value
        ) {
          Text(
            text = "登録",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary
          )
        }
        TextButton(
          modifier = Modifier
            .padding(16.dp)
            .width(96.dp)
            .background(MaterialTheme.colorScheme.error),
          onClick = {
            Log.d("Edit", "Cancel")
          },
        ) {
          Text(
            text = "キャンセル",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onError
          )
        }
      }
    }
  }
}

@Composable
fun TrashTypeInput(
  selectedTrashTypePosition: Int,
  modifier: Modifier = Modifier,
  displayTrashName: String = "",
  displayTrashNameErrorMessage: String = "",
  onItemSelected: (Int, String) -> Unit,
  onDisplayTrashNameChanged: (String) -> Unit
) {
  val trashTextList = stringArrayResource(R.array.list_trash_select)
  val trashIdList = stringArrayResource(R.array.list_trash_id_select)
  var expanded by remember { mutableStateOf(false) }

  CustomDropDown(
    modifier = modifier,
    items = trashTextList.toList(),
    selectedText = trashTextList[selectedTrashTypePosition],
    expanded = expanded,
    dropDownColor = MaterialTheme.colorScheme.secondaryContainer,
    onItemSelected = { selectedIndex: Int ->
      onItemSelected(selectedIndex, trashIdList[selectedIndex])
    },
    onExpandedChange = { expanded = !expanded },
    onDismissRequest = { expanded = false }
  )
  if (trashIdList[selectedTrashTypePosition] == "other") {
    Column {
      TextField(
        modifier = Modifier.padding(top = 8.dp),
        label = { Text(text = "ゴミの名前を入力") },
        value = displayTrashName,
        onValueChange = {
          onDisplayTrashNameChanged(it)
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.titleSmall,
        colors = TextFieldDefaults.colors(
          unfocusedContainerColor = MaterialTheme.colorScheme.background,
          focusedContainerColor = MaterialTheme.colorScheme.background,
        ),
      )
      if(displayTrashNameErrorMessage.isNotEmpty()) {
        Text(
          text = displayTrashNameErrorMessage,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.error,
        )
      }
    }
  }
}

@Composable
fun ScheduleInput(
  selectedScheduleId: String,
  scheduleDTO: ScheduleDTO,
  modifier: Modifier = Modifier,
  enabledRemoveButton: Boolean = true,
  onChangeScheduleType: (String) -> Unit,
  onChangeScheduleValue: (value: ScheduleDTO) -> Unit,
  onDeleteSchedule: () -> Unit,
){
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Row(
      modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
    ) {
      ScheduleTypeToggleButton(
        modifier = Modifier.weight(0.1f),
        id = "weekly",
        label = "毎週",
        selectedId = selectedScheduleId,
        onSelectedChange = {
          onChangeScheduleType(it)
        }
      )
      ScheduleTypeToggleButton(
        modifier = Modifier.weight(0.1f),
        id = "monthly",
        label = "毎月",
        selectedId = selectedScheduleId,
        onSelectedChange = {
          onChangeScheduleType(it)
        }
      )
      ScheduleTypeToggleButton(
        modifier = Modifier.weight(0.2f),
        id = "ordinalWeekly",
        label = "毎週(第○曜日)",
        selectedId = selectedScheduleId,
        onSelectedChange = {
          onChangeScheduleType(it)
        }
      )
      ScheduleTypeToggleButton(
        modifier = Modifier.weight(0.1f),
        id = "intervalWeekly",
        label = "隔週",
        selectedId = selectedScheduleId,
        onSelectedChange = {
          onChangeScheduleType(it)
        }
      )
    }
    Row(
      modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier.weight(1f)
      ) {
        when (selectedScheduleId) {
          "weekly" -> WeeklySchedule(
            selectedIndex = (scheduleDTO as WeeklyScheduleDTO).dayOfWeek,
            onChangeScheduleValue = onChangeScheduleValue
          )
          "monthly" -> MonthlySchedule(
            monthIndex = (scheduleDTO as MonthlyScheduleDTO).dayOfMonth,
            onChangeScheduleValue = onChangeScheduleValue
          )
          "ordinalWeekly" -> {
            val ordinalWeeklyScheduleDTO = scheduleDTO as OrdinalWeeklyScheduleDTO
            OrdinalWeeklySchedule(
              weekdayIndex = ordinalWeeklyScheduleDTO.dayOfWeek,
              orderIndex = ordinalWeeklyScheduleDTO.ordinal,
              onChangeScheduleValue = onChangeScheduleValue
            )
          }
          "intervalWeekly" -> {
            val intervalWeeklyScheduleDTO = scheduleDTO as IntervalWeeklyScheduleDTO
            val systemZonedOffset = ZonedDateTime.now().offset
            val startDateMillis = ZonedDateTime.parse("${intervalWeeklyScheduleDTO.start}T00:00:00${systemZonedOffset.id}").toInstant().toEpochMilli()

            IntervalWeeklySchedule(
              intervalIndex = intervalWeeklyScheduleDTO.interval,
              dayOfWeekIndex = intervalWeeklyScheduleDTO.dayOfWeek,
              startDate = startDateMillis,
              onChangeScheduleValue = onChangeScheduleValue
            )
          }

        }
      }
      if(enabledRemoveButton) {
        IconButton(
          onClick = {
            onDeleteSchedule()
          },
          colors = IconButtonColors(
            contentColor = MaterialTheme.colorScheme.error,
            disabledContentColor = MaterialTheme.colorScheme.inverseOnSurface,
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
          )
        ) {
          Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = "Delete",
          )
        }
      }
    }
  }
}

@Composable
fun ScheduleTypeToggleButton(
  modifier: Modifier = Modifier,
  id: String,
  label: String,
  selectedId: String,
  onSelectedChange: (String) -> Unit
) {
  val selected = id == selectedId
  Box(
    modifier = modifier
      .toggleable(
        value = selected,
        enabled = !selected,
        onValueChange = { checked ->
          if (checked) {
            onSelectedChange(id)
          }
        },
        role = Role.RadioButton,
      )
      .background(if (selected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.inverseOnSurface)
      .clip(RectangleShape)
      .border(0.2.dp, MaterialTheme.colorScheme.background),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      modifier = Modifier.padding(8.dp),
      text = label,
      style = MaterialTheme.typography.bodySmall,
      maxLines = 1,
      color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
    )
  }
}

@Composable
fun WeeklySchedule(
  selectedIndex: Int,
  modifier: Modifier = Modifier,
  onChangeScheduleValue: (value: ScheduleDTO) -> Unit
) {
  val weekdays = stringArrayResource(R.array.list_weekday_select).map{"毎週 $it"}
  var expanded by remember { mutableStateOf(false) }
  Box(
    modifier = modifier,
  ) {
    CustomDropDown(
      modifier = Modifier.wrapContentWidth(),
      items = weekdays,
      selectedText = weekdays[selectedIndex],
      expanded = expanded,
      onItemSelected = { selectedIndex: Int ->
        onChangeScheduleValue(WeeklyScheduleDTO(_dayOfWeek = selectedIndex))
      },
      onExpandedChange = { expanded = !expanded},
      onDismissRequest = { expanded = false }
    )
  }
}

@Composable
fun MonthlySchedule(
  monthIndex: Int,
  modifier: Modifier = Modifier,
  onChangeScheduleValue: (value: ScheduleDTO) -> Unit
) {
  val days = (1..31).map{"毎月 $it 日"}
  var expanded by remember { mutableStateOf(false) }

  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center
  ) {
    CustomDropDown(
      modifier = Modifier.wrapContentWidth(),
      items = days,
      selectedText = days[monthIndex],
      expanded = expanded,
      onItemSelected = { selectedIndex: Int ->
        onChangeScheduleValue(MonthlyScheduleDTO(_dayOfMonth = selectedIndex))
      },
      onExpandedChange = { expanded = !expanded},
      onDismissRequest = { expanded = false }
    )
  }
}

@Composable
fun OrdinalWeeklySchedule(
  weekdayIndex: Int,
  orderIndex: Int,
  modifier: Modifier = Modifier,
  onChangeScheduleValue: (value: ScheduleDTO) -> Unit
) {
  val weekdays = stringArrayResource(R.array.list_weekday_select)
  val orders = (1..5).map{"第${it}"}
  var weekdaysExpanded by remember { mutableStateOf(false) }
  var ordersExpanded by remember { mutableStateOf(false) }
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.Center,
  ) {
    CustomDropDown(
      modifier = Modifier.wrapContentWidth(),
      items = orders,
      selectedText = orders[orderIndex],
      expanded = ordersExpanded,
      onItemSelected = { selectedIndex: Int ->
        onChangeScheduleValue(OrdinalWeeklyScheduleDTO(_dayOfWeek = weekdayIndex, _ordinal = selectedIndex))
      },
      onExpandedChange = { ordersExpanded = !ordersExpanded},
      onDismissRequest = { ordersExpanded = false }
    )
    CustomDropDown(
      modifier = Modifier
        .padding(start = 8.dp)
        .wrapContentWidth(),
      items = weekdays.toList(),
      selectedText = weekdays[weekdayIndex],
      expanded = weekdaysExpanded,
      onItemSelected = { selectedIndex: Int ->
        onChangeScheduleValue(OrdinalWeeklyScheduleDTO(_dayOfWeek = selectedIndex, _ordinal = orderIndex))
      },
      onExpandedChange = { weekdaysExpanded = !weekdaysExpanded},
      onDismissRequest = { weekdaysExpanded = false }
    )
  }
}

@Composable
fun IntervalWeeklySchedule(
  intervalIndex: Int,
  dayOfWeekIndex: Int,
  startDate: Long,
  modifier: Modifier = Modifier,
  onChangeScheduleValue: (value: ScheduleDTO) -> Unit
) {
  val intervals = (2..4).map{"$it 週ごと"}
  val weekdays = stringArrayResource(R.array.list_weekday_select)
  var intervalExpanded by remember { mutableStateOf(false) }
  var weekdayExpanded by remember { mutableStateOf(false) }
  var showDatePicker by remember { mutableStateOf(false) }
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.Start
  ) {
    Row(
      horizontalArrangement = Arrangement.Center
    ) {
      CustomDropDown(
        modifier = Modifier.width(calculateTextWidth(text = intervals[0], style = MaterialTheme.typography.titleSmall)),
        items = intervals,
        selectedText = intervals[intervalIndex],
        expanded = intervalExpanded,
        onItemSelected = { selectedIndex: Int ->
          onChangeScheduleValue( IntervalWeeklyScheduleDTO(_dayOfWeek = dayOfWeekIndex, _interval = selectedIndex, _start = startDate.toDateString()))
        },
        onExpandedChange = { intervalExpanded = !intervalExpanded},
        onDismissRequest = { intervalExpanded = false }
      )
      CustomDropDown(
        modifier = Modifier
          .padding(start = 8.dp)
          .width(
            calculateTextWidth(
              text = weekdays[0],
              style = MaterialTheme.typography.titleSmall
            )
          ),
        items = weekdays.toList(),
        selectedText = weekdays[dayOfWeekIndex],
        expanded = weekdayExpanded,
        onItemSelected = { selectedIndex: Int ->
          onChangeScheduleValue(IntervalWeeklyScheduleDTO(_dayOfWeek = selectedIndex, _interval = intervalIndex, _start = startDate.toDateString()))
        },
        onExpandedChange = { weekdayExpanded = !weekdayExpanded},
        onDismissRequest = { weekdayExpanded = false }
      )
    }
    if(showDatePicker) {
      IntervalStartDateDialog(
        initialMillis = startDate,
        onDismissRequest = { showDatePicker = false }
      )
    }
    Box(
      modifier = Modifier
    ) {
      OutlinedTextField(
        modifier = Modifier
          .padding(top = 8.dp)
          .clickable(enabled = true) {
            showDatePicker = true
          },
        colors = TextFieldDefaults.colors(
          disabledTextColor = TextFieldDefaults.colors().focusedTextColor,
          disabledTrailingIconColor = TextFieldDefaults.colors().focusedTextColor,
          disabledLabelColor = TextFieldDefaults.colors().focusedTextColor,
          focusedContainerColor = MaterialTheme.colorScheme.background,
          unfocusedContainerColor = MaterialTheme.colorScheme.background,
          disabledContainerColor = MaterialTheme.colorScheme.background,
        ),
        enabled = false,
        value = startDate.toDateString(),
        onValueChange = {
          onChangeScheduleValue(IntervalWeeklyScheduleDTO(_dayOfWeek = dayOfWeekIndex, _interval = intervalIndex, _start = startDate.toDateString()))
        },
        readOnly = true,
        label = { Text(text = "直近のゴミ出し日") },
        singleLine = true,
        trailingIcon = {
          Icon(
            imageVector = Icons.Filled.Edit,
            contentDescription = "edit icon"
          )
        },
      )
    }
  }
}

// Millisを文字列フォーマットに変換する拡張関数
fun Long.toDateString(): String {
  return Instant.ofEpochMilli(this).toString().split('T')[0]
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntervalStartDateDialog(
  initialMillis: Long = System.currentTimeMillis(),
  onDismissRequest: () -> Unit,
  onSelectRequest: (Long) -> Unit = {}
) {
  val datePickerState = rememberDatePickerState(
    initialSelectedDateMillis = initialMillis
  )
  DatePickerDialog(
    onDismissRequest = { onDismissRequest() },
    dismissButton = {
      TextButton(
        onClick = { onDismissRequest() }
      ) {
        Text("Cancel")
      }
    },
    confirmButton = {
      TextButton(
        onClick = {
          if(datePickerState.selectedDateMillis != null) {
            onSelectRequest(datePickerState.selectedDateMillis!!)
            onDismissRequest()
          }
        }
      ) {
        Text("OK")
      }
    },
  ) {
    DatePicker(
      state = datePickerState,
    )
  }
}


