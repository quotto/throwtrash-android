package net.mythrowaway.app.module.info.presentation.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
  val coroutineScope = rememberCoroutineScope()

  Button(
    onClick = {
      coroutineScope.launch {
        viewModel.deleteAccount(onDeleteSuccess, onDeleteFailure)
      }
    },
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