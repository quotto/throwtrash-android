package net.mythrowaway.app.module.trash.presentation.view.edit

import android.util.Log
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.mythrowaway.app.R
import net.mythrowaway.app.module.trash.presentation.view_model.edit.EditTrashViewModel
import net.mythrowaway.app.module.trash.presentation.view_model.edit.InputTrashNameError
import net.mythrowaway.app.module.trash.presentation.view_model.edit.data.IntervalWeeklyScheduleViewData
import net.mythrowaway.app.module.trash.presentation.view_model.edit.data.MonthlyScheduleViewData
import net.mythrowaway.app.module.trash.presentation.view_model.edit.data.OrdinalWeeklyScheduleViewData
import net.mythrowaway.app.module.trash.presentation.view_model.edit.SavedStatus
import net.mythrowaway.app.module.trash.presentation.view_model.edit.data.ScheduleType
import net.mythrowaway.app.module.trash.presentation.view_model.edit.data.ScheduleViewData
import net.mythrowaway.app.module.trash.presentation.view_model.edit.data.WeeklyScheduleViewData
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@Composable
fun EditScreen(
  editTrashViewModel: EditTrashViewModel,
  modifier : Modifier = Modifier,
  onClickToExcludeDayOfMonth: () -> Unit,
  onSaveSuccess: () -> Unit = {}
) {
  val hostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()
  val context = LocalContext.current

  val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

  when (editTrashViewModel.savedStatus.value) {
    SavedStatus.SUCCESS -> {
      LaunchedEffect(Unit) {
        onSaveSuccess()
      }
    }

    SavedStatus.ERROR -> {
      LaunchedEffect(Unit) {
        Log.d("Edit", "Error")
        hostState.showSnackbar(
          context.getString(R.string.message_failed_save_trash),
          duration = SnackbarDuration.Long
        )
      }
    }

    SavedStatus.ERROR_MAX_SCHEDULE -> {
      LaunchedEffect(Unit) {
        Log.d("Edit", "Error Max Schedule")
        hostState.showSnackbar(
          context.getString(R.string.message_failed_save_trash_exceed_max_schedule_count),
          duration = SnackbarDuration.Long
        )
      }
    }
    else -> {
      Log.d("Edit", "Initial")
    }
  }

  Scaffold(
    snackbarHost = {SnackbarHost(
      hostState,
      snackbar = { data ->
        Snackbar(
          snackbarData = data,
          containerColor = if(editTrashViewModel.savedStatus.value == SavedStatus.SUCCESS) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
          contentColor = if(editTrashViewModel.savedStatus.value == SavedStatus.SUCCESS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
        )
      }
    )},
  ) {innerPadding ->
    Column(
      modifier = modifier
        .padding(innerPadding)
        .fillMaxSize(),
    ) {
      Column(
        modifier = Modifier
          .padding(8.dp)
          .fillMaxWidth()
          .weight(1f)
      ) {
        TrashTypeInput(
          selectedTrashTypeId = editTrashViewModel.trashType.value.type,
          displayTrashName = editTrashViewModel.trashType.value.inputName,
          inputTrashNameError = editTrashViewModel.inputTrashNameError.value,
          onItemSelected = { trashTypeId: String ->
            editTrashViewModel.changeTrashType(trashTypeId)
          },
          onDisplayTrashNameChanged = { displayTrashName: String ->
            editTrashViewModel.changeInputTrashName(displayTrashName)
          },
          modifier = Modifier.fillMaxWidth(),
        )
        Column(
          modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        ) {
          editTrashViewModel.scheduleViewDataList.value.forEachIndexed { index, schedule ->
            ScheduleInput(
              scheduleViewData = schedule,
              enabledRemoveButton = editTrashViewModel.enabledRemoveButton.value,
              modifier = Modifier
                .fillMaxWidth()
                .background(
                  if (index % 2 == 0) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primaryContainer
                ),
              onChangeScheduleType = { scheduleId: String ->
                editTrashViewModel.changeScheduleType(index, scheduleId)
              },
              onChangeScheduleValue = { value: ScheduleViewData ->
                editTrashViewModel.changeScheduleValue(index, value)
              },
              onDeleteSchedule = {
                editTrashViewModel.removeSchedule(index)
              }
            )
          }
          if (editTrashViewModel.enabledAppendButton.value) {
            IconButton(
              modifier = Modifier
                .fillMaxWidth()
                .testTag(stringResource(id = R.string.testTag_add_schedule_button)),
              onClick = {
                editTrashViewModel.addSchedule()
              },
            ) {
              Icon(
                modifier = Modifier
                  .width(24.dp)
                  .height(24.dp),
                imageVector = Icons.Outlined.AddCircle,
                contentDescription = stringResource(id = R.string.description_add_schedule_icon),
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
        FilledTonalButton(
          onClick = {
            onClickToExcludeDayOfMonth()
          },
          colors = ButtonDefaults.filledTonalButtonColors().copy(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
          ),
        ) {
          Text(
            text = stringResource(id = R.string.text_exclude_day_of_month_button),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.tertiary
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
                if (editTrashViewModel.enabledRegisterButton.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inverseOnSurface
              )
              .testTag(stringResource(R.string.testTag_register_trash_button)),
            onClick = {
              scope.launch {
                editTrashViewModel.saveTrash()
              }
            },
            enabled = editTrashViewModel.enabledRegisterButton.value
          ) {
            Text(
              text = stringResource(id = R.string.text_register_trash_button),
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
              dispatcher?.onBackPressed()
            },
          ) {
            Text(
              text = stringResource(id = R.string.text_cancel_register_trash_button),
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onError
            )
          }
        }
      }
    }
  }
}

@Composable
fun TrashTypeInput(
  selectedTrashTypeId: String,
  displayTrashName: String,
  modifier: Modifier = Modifier,
  inputTrashNameError: InputTrashNameError,
  onItemSelected: (String) -> Unit,
  onDisplayTrashNameChanged: (String) -> Unit
) {
  val trashTextList = stringArrayResource(R.array.list_trash_select)
  val trashIdList = stringArrayResource(R.array.list_trash_id_select)
  var expanded by remember { mutableStateOf(false) }

  CustomDropDown(
    modifier = modifier,
    items = trashTextList.toList(),
    selectedText = trashTextList[getTrashTypeIndex(selectedTrashTypeId)],
    expanded = expanded,
    dropDownColor = MaterialTheme.colorScheme.secondaryContainer,
    onItemSelected = { selectedIndex: Int ->
      onItemSelected(trashIdList[selectedIndex])
    },
    onExpandedChange = { expanded = !expanded },
    onDismissRequest = { expanded = false },
    testTag = stringResource(id = R.string.testTag_trash_type_dropdown)
  )
  if (selectedTrashTypeId == "other") {
    Column {
      TextField(
        modifier = Modifier
          .padding(top = 8.dp)
          .testTag(stringResource(id = R.string.testTag_trash_name_input)),
        label = { Text(text = stringResource(id = R.string.text_trash_name_input_placeholder)) },
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
        maxLines = 1,
      )
      if(inputTrashNameError != InputTrashNameError.NONE) {
        Text(
          text = when(inputTrashNameError) {
            InputTrashNameError.EMPTY -> stringResource(id = R.string.message_invalid_input_trash_name_empty)
            InputTrashNameError.TOO_LONG -> stringResource(id = R.string.message_invalid_input_trash_name_too_long)
            InputTrashNameError.INVALID_CHAR -> stringResource(id = R.string.message_invalid_input_trash_name_invalid_char)
            else -> ""
          },
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.error,
        )
      }
    }
  }
}

@Composable
fun ScheduleInput(
  scheduleViewData: ScheduleViewData,
  modifier: Modifier = Modifier,
  enabledRemoveButton: Boolean = true,
  onChangeScheduleType: (String) -> Unit,
  onChangeScheduleValue: (value: ScheduleViewData) -> Unit,
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
        id = ScheduleType.WEEKLY.value,
        label = stringResource(id = R.string.text_weekday_toggle_button),
        selectedId = scheduleViewData.scheduleType,
        onSelectedChange = {
          onChangeScheduleType(it)
        }
      )
      ScheduleTypeToggleButton(
        modifier = Modifier.weight(0.1f),
        id = ScheduleType.MONTHLY.value,
        label = stringResource(id = R.string.text_monthly_toggle_button),
        selectedId = scheduleViewData.scheduleType,
        onSelectedChange = {
          onChangeScheduleType(it)
        }
      )
      ScheduleTypeToggleButton(
        modifier = Modifier.weight(0.2f),
        id = ScheduleType.ORDINAL_WEEKLY.value,
        label = stringResource(id = R.string.text_ordinal_weekday_toggle_button),
        selectedId = scheduleViewData.scheduleType,
        onSelectedChange = {
          onChangeScheduleType(it)
        }
      )
      ScheduleTypeToggleButton(
        modifier = Modifier.weight(0.1f),
        id = ScheduleType.INTERVAL_WEEKLY.value,
        label = "隔週",
        selectedId = scheduleViewData.scheduleType,
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
        when (scheduleViewData.scheduleType) {
          ScheduleType.WEEKLY.value -> WeeklySchedule(
            selectedIndex = (scheduleViewData as WeeklyScheduleViewData).dayOfWeek,
            onChangeScheduleValue = onChangeScheduleValue
          )
          ScheduleType.MONTHLY.value -> MonthlySchedule(
            monthIndex = (scheduleViewData as MonthlyScheduleViewData).dayOfMonth,
            onChangeScheduleValue = onChangeScheduleValue
          )
          ScheduleType.ORDINAL_WEEKLY.value -> {
            val ordinalWeeklyScheduleViewData = scheduleViewData as OrdinalWeeklyScheduleViewData
            OrdinalWeeklySchedule(
              weekdayIndex = ordinalWeeklyScheduleViewData.dayOfWeek,
              orderIndex = ordinalWeeklyScheduleViewData.ordinal,
              onChangeScheduleValue = onChangeScheduleValue
            )
          }
          ScheduleType.INTERVAL_WEEKLY.value -> {
            val intervalWeeklyScheduleViewData = scheduleViewData as IntervalWeeklyScheduleViewData
            val systemZonedOffset = ZonedDateTime.now().offset
            val startDateMillis = ZonedDateTime.parse("${intervalWeeklyScheduleViewData.startDate}T00:00:00${systemZonedOffset.id}").toInstant().toEpochMilli()
            Log.d("EditScreen",startDateMillis.toString())

            IntervalWeeklySchedule(
              intervalIndex = intervalWeeklyScheduleViewData.interval,
              dayOfWeekIndex = intervalWeeklyScheduleViewData.dayOfWeek,
              startDateMillis = startDateMillis,
              onChangeScheduleValue = onChangeScheduleValue
            )
          }

        }
      }
      if(enabledRemoveButton) {
        IconButton(
          modifier = Modifier.testTag(stringResource(id = R.string.testTag_delete_schedule_button)),
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
  Log.d("ScheduleTypeToggleButton", "id: $id, selectedId: $selectedId, selected: $selected")
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
  onChangeScheduleValue: (value: ScheduleViewData) -> Unit
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
        onChangeScheduleValue(WeeklyScheduleViewData(_dayOfWeek = selectedIndex))
      },
      onExpandedChange = { expanded = !expanded},
      onDismissRequest = { expanded = false },
      testTag = stringResource(id = R.string.testTag_weekday_of_weekly_dropdown)
    )
  }
}

@Composable
fun MonthlySchedule(
  monthIndex: Int,
  modifier: Modifier = Modifier,
  onChangeScheduleValue: (value: ScheduleViewData) -> Unit
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
        onChangeScheduleValue(MonthlyScheduleViewData(_day = selectedIndex))
      },
      onExpandedChange = { expanded = !expanded},
      onDismissRequest = { expanded = false },
      testTag = stringResource(id = R.string.testTag_day_of_month_of_monthly_dropdown)
    )
  }
}

@Composable
fun OrdinalWeeklySchedule(
  weekdayIndex: Int,
  orderIndex: Int,
  modifier: Modifier = Modifier,
  onChangeScheduleValue: (value: ScheduleViewData) -> Unit
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
        onChangeScheduleValue(OrdinalWeeklyScheduleViewData(_dayOfWeek = weekdayIndex, _ordinal = selectedIndex))
      },
      onExpandedChange = { ordersExpanded = !ordersExpanded},
      onDismissRequest = { ordersExpanded = false },
      testTag = stringResource(id = R.string.testTag_order_of_ordinal_weekly_dropdown)
    )
    CustomDropDown(
      modifier = Modifier
        .padding(start = 8.dp)
        .wrapContentWidth(),
      items = weekdays.toList(),
      selectedText = weekdays[weekdayIndex],
      expanded = weekdaysExpanded,
      onItemSelected = { selectedIndex: Int ->
        onChangeScheduleValue(OrdinalWeeklyScheduleViewData(_dayOfWeek = selectedIndex, _ordinal = orderIndex))
      },
      onExpandedChange = { weekdaysExpanded = !weekdaysExpanded},
      onDismissRequest = { weekdaysExpanded = false },
      testTag = stringResource(id = R.string.testTag_weekday_of_ordinal_weekly_dropdown)
    )
  }
}

@Composable
fun IntervalWeeklySchedule(
  intervalIndex: Int,
  dayOfWeekIndex: Int,
  startDateMillis: Long,
  modifier: Modifier = Modifier,
  onChangeScheduleValue: (value: ScheduleViewData) -> Unit
) {
  val intervals = (2..4).map{"$it 週ごと"}
  val weekdays = stringArrayResource(R.array.list_weekday_select)
  var intervalExpanded by remember { mutableStateOf(false) }
  var weekdayExpanded by remember { mutableStateOf(false) }
  var showDatePicker by remember { mutableStateOf(false) }
  // DatePickerDialogのパラメータはUTC時刻で解釈されるため、表示される日付を端末のタイムゾーンに合わせるにはオフセットを加算する
  val offsetMillis = ZonedDateTime.now().offset.totalSeconds * 1000L
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
          onChangeScheduleValue( IntervalWeeklyScheduleViewData(_dayOfWeek = dayOfWeekIndex, _interval = selectedIndex, _start = startDateMillis.toStartDateString()))
        },
        onExpandedChange = { intervalExpanded = !intervalExpanded},
        onDismissRequest = { intervalExpanded = false },
        testTag = stringResource(id = R.string.testTag_interval_of_interval_weekly_dropdown)
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
          onChangeScheduleValue(
            IntervalWeeklyScheduleViewData(
              _dayOfWeek = selectedIndex,
              _interval = intervalIndex,
              _start = startDateMillis.toStartDateString()
            )
          )
        },
        onExpandedChange = { weekdayExpanded = !weekdayExpanded},
        onDismissRequest = { weekdayExpanded = false },
        testTag = stringResource(id = R.string.testTag_weekday_of_interval_weekly_dropdown)
      )
    }
    if(showDatePicker) {
      IntervalStartDateDialog(
        initialMillis = startDateMillis + offsetMillis,
        onDismissRequest = { showDatePicker = false },
        onSelectRequest = {
          Log.d("IntervalWeeklySchedule", "Selected date: $it")
          onChangeScheduleValue(
            IntervalWeeklyScheduleViewData(
              _dayOfWeek = dayOfWeekIndex,
              _interval = intervalIndex,
              // DatePickerDialogのパラメータにはオフセット分を加算しているため、ここで減算する
              _start = (it - offsetMillis).toStartDateString()
            )
          )
        }
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
        value = startDateMillis.toStartDateString(),
        onValueChange = {},
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

// Millisを日付文字列YYYY-MM-DDに変換する
fun Long.toStartDateString(): String {
  return ZonedDateTime.ofInstant(
      Instant.ofEpochMilli(this),
      ZoneId.systemDefault()
    ).toString().split("T")[0]
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

@Composable
fun getTrashTypeIndex(trashTypeId: String): Int {
  val trashIdList = stringArrayResource(R.array.list_trash_id_select)
  return trashIdList.indexOf(trashTypeId)
}