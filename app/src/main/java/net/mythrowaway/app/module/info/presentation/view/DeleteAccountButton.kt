package net.mythrowaway.app.module.info.presentation.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.mythrowaway.app.module.info.presentation.view_model.InformationViewModel

@Composable
fun DeleteAccountButton(
  viewModel: InformationViewModel,
  onDeleteSuccess: () -> Unit,
  onDeleteFailure: () -> Unit
) {
  val showDialog = remember { mutableStateOf(false) }

  // ダイアログを表示する
  if (showDialog.value) {
    val isAnonymous = viewModel.uiState.value.currentUser?.isAnonymous ?: true
    val dialogMessage = if (isAnonymous) {
      "すべての登録データが削除されます。よろしいですか？"
    } else {
      "すべての登録データが削除され、サインアウトされます。よろしいですか？"
    }

    AlertDialog(
      onDismissRequest = { showDialog.value = false },
      title = { Text("確認") },
      text = { Text(dialogMessage) },
      confirmButton = {
        TextButton(
          onClick = {
            showDialog.value = false
            viewModel.deleteAccount(onDeleteSuccess, onDeleteFailure)
          }
        ) {
          Text("はい")
        }
      },
      dismissButton = {
        TextButton(
          onClick = { showDialog.value = false }
        ) {
          Text("いいえ")
        }
      }
    )
  }

  Button(
    onClick = { showDialog.value = true },
    colors = ButtonDefaults.buttonColors(
      containerColor = MaterialTheme.colorScheme.errorContainer
    ),
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp)
      .height(48.dp)
  ) {
    Text(text = "データ削除", color = MaterialTheme.colorScheme.error)
  }
}