package net.mythrowaway.app.view.alarm

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import net.mythrowaway.app.viewmodel.AlarmSavedStatus
import net.mythrowaway.app.viewmodel.AlarmViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(
  alarmViewModel: AlarmViewModel,
) {
  val alarmUiState by alarmViewModel.uiState.collectAsState()
  val hostState = remember { SnackbarHostState() }
  val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "通知設定",
            style = MaterialTheme.typography.titleSmall,
          )
        },
        navigationIcon = {
          IconButton(
            modifier = Modifier.size(24.dp),
            onClick = {
              dispatcher?.onBackPressed()
            }
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
            )
          }
        }
      )
    },
    snackbarHost = {
      SnackbarHost(
        hostState,
        snackbar = { data ->
          Snackbar(
            snackbarData = data,
            containerColor =
            if (alarmUiState.alarmSavedStatus === AlarmSavedStatus.Success) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
            contentColor =
            if (alarmUiState.alarmSavedStatus === AlarmSavedStatus.Success) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
          )
        }
      )
    }
  ) { paddingValues ->
    LaunchedEffect(alarmUiState.alarmSavedStatus) {
      when (alarmUiState.alarmSavedStatus) {
        AlarmSavedStatus.Success -> {
          hostState.showSnackbar("通知設定を保存しました", duration = SnackbarDuration.Short)
        }

        AlarmSavedStatus.Failure -> {
          hostState.showSnackbar("通知設定の保存に失敗しました", duration = SnackbarDuration.Short)
        }

        else -> {}
      }
    }
    Column(
      modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize(),
    ) {
      Row(
        modifier = Modifier.padding(start = 8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = "通知を受け取る",
          style = MaterialTheme.typography.bodyMedium,
        )
        Switch(
          modifier = Modifier.padding(start = 8.dp),
          checked = alarmUiState.notifyChecked,
          onCheckedChange = { checked ->
            alarmViewModel.toggleNotify(checked)
          },
        )
      }
      Column(
        modifier = Modifier.padding(top = 16.dp)
      ) {
        Text(
          modifier = Modifier.padding(start = 8.dp),
          text = "毎日",
          style = MaterialTheme.typography.labelSmall,
        )
        Box(
          modifier = Modifier
            .clickable(
              enabled = alarmUiState.notifyChecked,
              onClick = {
                alarmViewModel.openTimePicker()
              }
            )
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(8.dp),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = alarmUiState.timeText,
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
          )
        }
      }
      Row(
        modifier = Modifier.padding(start = 8.dp, top = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
      ) {
        Text(
          text = "ゴミ出しが無い日も通知する",
          style = MaterialTheme.typography.bodyMedium,
        )
        Checkbox(
          enabled = alarmUiState.notifyChecked,
          checked = alarmUiState.notifyEverydayChecked,
          onCheckedChange = { checked ->
            alarmViewModel.changeNotifyEveryday(checked)
          }
        )
      }
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        Button(
          modifier = Modifier.padding(top = 16.dp),
          onClick = {
            alarmViewModel.saveAlarm()
          },
        ) {
          Text("設定")
        }
      }
    }
  }
  if (alarmUiState.timePickerOpened) {
    AlarmTimePickerDialog(
      hour = alarmUiState.hour,
      minute = alarmUiState.minute,
      onConfirm = { hour: Int, minute: Int ->
        alarmViewModel.changeTime(hour, minute)
        alarmViewModel.closeTimePicker()
      },
      onDismiss = {
        alarmViewModel.closeTimePicker()
      }
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmTimePickerDialog(
  hour: Int,
  minute: Int,
  onConfirm: (hour: Int, minute: Int) -> Unit,
  onDismiss: () -> Unit,
) {
  val timePickerState = rememberTimePickerState(
    initialHour = hour,
    initialMinute = minute,
    is24Hour = true,
  )


  Dialog(
    onDismissRequest = {
      onDismiss()
    },
  ) {
    Column(
      modifier = Modifier.padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      TimePicker(
        state = timePickerState,
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
      ) {
        Button(
          onClick = {
            onConfirm(timePickerState.hour, timePickerState.minute)
          }
        ) {
          Text("設定")
        }
        Button(
          onClick = {
            onDismiss()
          }
        ) {
          Text("キャンセル")
        }
      }
    }
  }
}