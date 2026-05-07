package net.mythrowaway.app.module.trash.presentation.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import net.mythrowaway.app.R

@Composable
fun ScheduleSearchImportDialog(
  onExecute: (String) -> Unit,
  onCancel: () -> Unit,
  onDismiss: () -> Unit
) {
  var input by remember { mutableStateOf("") }
  var showNotice by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(stringResource(id = R.string.title_schedule_search_import_dialog)) },
    text = {
      Column {
        OutlinedTextField(
          modifier = Modifier
            .fillMaxWidth()
            .testTag(stringResource(id = R.string.testTag_schedule_search_input)),
          value = input,
          onValueChange = { value -> input = value.take(50) },
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
          label = { Text(stringResource(id = R.string.label_schedule_search_input)) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          modifier = Modifier
            .clickable { showNotice = true }
            .testTag(stringResource(id = R.string.testTag_schedule_search_notice_link)),
          text = stringResource(id = R.string.text_schedule_search_notice_link),
          textDecoration = TextDecoration.Underline
        )
      }
    },
    confirmButton = {
      Button(
        modifier = Modifier.testTag(stringResource(id = R.string.testTag_schedule_search_execute_button)),
        enabled = input.isNotBlank(),
        onClick = { onExecute(input) }
      ) {
        Text(stringResource(id = R.string.label_schedule_search_execute_button))
      }
    },
    dismissButton = {
      TextButton(
        modifier = Modifier.testTag(stringResource(id = R.string.testTag_schedule_search_cancel_button)),
        onClick = onCancel
      ) {
        Text(stringResource(id = R.string.label_close_button))
      }
    }
  )

  if (showNotice) {
    AlertDialog(
      onDismissRequest = { showNotice = false },
      title = { Text(stringResource(id = R.string.title_schedule_search_notice_dialog)) },
      text = { Text(stringResource(id = R.string.text_schedule_search_notice)) },
      confirmButton = {
        TextButton(onClick = { showNotice = false }) {
          Text(stringResource(id = R.string.label_close_button))
        }
      }
    )
  }
}
