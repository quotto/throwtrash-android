package net.mythrowaway.app.module.trash.presentation.view.share

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.mythrowaway.app.module.trash.presentation.view_model.share.ActivateStatus
import net.mythrowaway.app.module.trash.presentation.view_model.share.ActivateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivateScreen(
  viewModel: ActivateViewModel
) {
  val uiState by viewModel.viewState.collectAsState()
  val hostState = remember { SnackbarHostState() }
  val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

  LaunchedEffect(uiState.activateStatus) {
    when(uiState.activateStatus) {
      ActivateStatus.Success -> {
        hostState.showSnackbar(
          "スケジュールの取り込みに成功しました",
          duration = SnackbarDuration.Long
        )
        dispatcher?.onBackPressed()
      }

      ActivateStatus.Error -> {
        hostState.showSnackbar(
          "スケジュールの取り込みに失敗しました",
          duration = SnackbarDuration.Long
        )
      }
      else -> {}
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text("スケジュールの取り込み")
        },
        navigationIcon = {
          IconButton(
            onClick = {
              dispatcher?.onBackPressed()
            },
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back"
            )
          }
        }
      )
    },
    snackbarHost = {
      SnackbarHost(
        hostState = hostState,
        snackbar = { data ->
          Snackbar(
            snackbarData = data,
            modifier = Modifier.padding(8.dp),
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
          )
        }
      )
    }
  ) {innerPadding ->
    Column(
      modifier = Modifier
        .padding(innerPadding)
        .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
      TextField(
        value = uiState.code,
        onValueChange = {
          viewModel.inputCode(it)
        },
        placeholder = { Text("認証コードを入力してください") },
        colors = TextFieldDefaults.colors(
          unfocusedContainerColor = MaterialTheme.colorScheme.background,
          focusedContainerColor = MaterialTheme.colorScheme.background,
        ),
        maxLines = 1,
        textStyle = MaterialTheme.typography.displayMedium.copy(
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.primary
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
      )
      Spacer(modifier = Modifier.height(16.dp))
      Button(
        onClick = {
          viewModel.activate()
        },
        enabled = (
                uiState.code.length == 10
                        && !uiState.isProgress
                        && uiState.activateStatus != ActivateStatus.Success
                ),
        modifier = Modifier.padding(8.dp),
      ) {
        Text("取り込む")
      }
    }
  }
}