package net.mythrowaway.app.view.share

import android.content.ClipData
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.mythrowaway.app.viewmodel.share.PublishCodeViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PublishCodeScreen(
  viewModel: PublishCodeViewModel
) {
  val uiState by viewModel.viewState.collectAsState()
  val hostState = remember { SnackbarHostState() }
  val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

  LaunchedEffect(uiState.isError) {
    if(uiState.isError) {
      hostState.showSnackbar("認証コードの発行に失敗しました", duration = SnackbarDuration.Long)
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("スケジュールの共有") },
        navigationIcon = {
          IconButton(
            onClick = {
              dispatcher?.onBackPressed()
            },
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = MaterialTheme.colorScheme.onSurface)
          }
        }
      )
    },
    snackbarHost = {
      SnackbarHost(
        hostState = hostState,
        modifier = Modifier.padding(16.dp),
        snackbar = { data ->
          Snackbar(
            snackbarData = data,
            modifier = Modifier
              .padding(8.dp),
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.error
          )
        }
      )
    }
  ) {innerPadding ->
    Column(
      modifier = Modifier
        .padding(innerPadding)
        .fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Top
    ) {
      Box(
        modifier = Modifier
          .padding(8.dp)
          .fillMaxWidth()
          .background(color = MaterialTheme.colorScheme.primaryContainer)
          .border(
            width = 0.5.dp,
            color = MaterialTheme.colorScheme.onSurface,
          )
          .combinedClickable(
            onClick = { },
            enabled = true,
            onLongClickLabel = "コピーしました",
            onLongClick = {
              // クリックボードにテキストをコピーする
              ClipData.newPlainText("publishCode", "1234567890")
            }
          )
        ,
        contentAlignment = Alignment.Center
      ) {
        Text(
          modifier = Modifier.padding(16.dp),
          text = uiState.code,
          color = MaterialTheme.colorScheme.primary,
          style = MaterialTheme.typography.displaySmall
        )
      }
      Button(
        modifier = Modifier
          .padding(top = 16.dp),
        enabled = !uiState.isProgress,
        onClick = {
          viewModel.publishCode()
        }
      ) {
        Text("認証コードを発行する")
      }
      Column(
        modifier = Modifier
          .padding(16.dp)
          .fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .padding(top = 16.dp),
          horizontalArrangement = Arrangement.Start,
        ) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Back",
            tint = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.padding(4.dp))
          Text(
            text = "認証コードを発行すると10桁のコードが表示されます。",
            style = MaterialTheme.typography.bodyMedium
          )
        }
        Row(
          modifier = Modifier
            .padding(top = 16.dp),
          horizontalArrangement = Arrangement.Start,
        ) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Back",
            tint = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.padding(4.dp))
          Text(
            text = "他のスマートフォンで認証コードを入力するとゴミ出し予定が共有されます。",
            style = MaterialTheme.typography.bodyMedium
          )
        }
        Row(
          modifier = Modifier
            .padding(top = 16.dp),
          horizontalArrangement = Arrangement.Start,
        ) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Back",
            tint = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.padding(4.dp))
          Text(
            text = "認証コードの有効期限は10分です。",
            style = MaterialTheme.typography.bodyMedium
          )
        }
      }
    } }
}