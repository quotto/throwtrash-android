package net.mythrowaway.app.domain.account_link.presentation.view

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.mythrowaway.app.domain.account_link.presentation.view_model.AccountLinkStatus
import net.mythrowaway.app.domain.account_link.presentation.view_model.AccountLinkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartAccountLinkScreen(
  viewModel: AccountLinkViewModel
) {
  val uiState by viewModel.uiState.collectAsState()
  val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val hostState = remember { SnackbarHostState() }

  val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
    Log.d("AccountLinkScreen", "Result: ${result.data}")
  }

  LaunchedEffect(uiState.accountLinkStatus) {
    when (uiState.accountLinkStatus) {
      AccountLinkStatus.Success-> {
        hostState.showSnackbar("アレクサと連携しました", duration = SnackbarDuration.Long)
      }
      AccountLinkStatus.Error -> {
        hostState.showSnackbar("アレクサとの連携に失敗しました", duration = SnackbarDuration.Long)
      }
      AccountLinkStatus.Start -> {
        // アレクサアプリを起動
        launcher.launch(Intent(Intent.ACTION_VIEW, Uri.parse(uiState.accountLinkUrl)))
        viewModel.resetAccountLinkStatus()
      }
      else -> {}
    }
  }
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("アレクサ連携") },
        navigationIcon = {
          IconButton(
            onClick = {
              dispatcher?.onBackPressed()
            }
          ) {
            Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
          }
        }
      )
    },
    snackbarHost = {
      SnackbarHost(
        hostState = hostState,
        snackbar = {data ->
          Snackbar(
            modifier = Modifier.padding(16.dp),
            snackbarData = data,
            containerColor = if (uiState.accountLinkStatus == AccountLinkStatus.Success) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
            contentColor = if (uiState.accountLinkStatus == AccountLinkStatus.Success) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
          )
        }
      )
    }
  ) {paddingValues ->
    Column(
      modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(imageVector = Icons.Filled.Check, contentDescription = "Check")
        Spacer(modifier = Modifier.padding(4.dp))
        Text("アプリで設定したゴミ出しスケジュールをアレクサに連携することができます。")
      }
      Row(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(imageVector = Icons.Filled.Check, contentDescription = "Check")
        Spacer(modifier = Modifier.padding(4.dp))
        Text("下のボタンをタップするとアレクサアプリまたはブラウザが起動します。")
      }
      Row(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(imageVector = Icons.Filled.Check, contentDescription = "Check")
        Spacer(modifier = Modifier.padding(4.dp))
        Text("アレクサでご利用のAmazonアカウントを使ってログインし、連携を完了してください。")
      }
      Spacer(modifier = Modifier.padding(16.dp))
      Button(
        onClick = {
          scope.launch {
            viewModel.startAccountLink(context)
          }
        }
      ) {
        Text("アレクサと連携する")
      }
    }
    if(uiState.accountLinkStatus == AccountLinkStatus.WaitForStart) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator(
          color = MaterialTheme.colorScheme.secondary,
          trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
      }
    }
  }
}