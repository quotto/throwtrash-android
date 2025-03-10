package net.mythrowaway.app.module.account.presentation.view

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.mythrowaway.app.module.account.presentation.view_model.AccountViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AccountScreen(
  viewModel: AccountViewModel
) {
  val uiState by viewModel.uiState.collectAsState()
  val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
  val clipboardManager = LocalClipboardManager.current
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  LaunchedEffect(Unit) {
    viewModel.loadInformation()
  }

  Scaffold (
    topBar = {
       TopAppBar(
         title = {
           Text( text = "ユーザー情報" )
         },
         navigationIcon = {
           IconButton(
             onClick = {
                dispatcher?.onBackPressed()
             }
           ) {
             Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
           }
         }
       )
    }
  ){ paddingValues ->
      Column(
        modifier = Modifier.padding(paddingValues)
      ) {
        Box(
          modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
          contentAlignment = Alignment.CenterStart
        ) {
          Text(
            "ユーザーID:",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium
          )
        }
        Box(
          modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth()
            .combinedClickable(
              enabled = true,
              onLongClickLabel = "コピーしました",
              onClick = {},
              onLongClick = {
                clipboardManager.setText(
                  AnnotatedString(uiState.userId)
                )
                Toast.makeText(context, "コピーしました", Toast.LENGTH_SHORT).show()
              }
            )
            .background(color = MaterialTheme.colorScheme.primaryContainer),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            modifier = Modifier.padding(16.dp),
            text = uiState.userId,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
          )
        }
        Box(
          modifier = Modifier.fillMaxWidth(),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "長押しでコピーできます",
            style= MaterialTheme.typography.bodySmall,
          )
        }
        Box(
          modifier = Modifier.fillMaxWidth(),
          contentAlignment = Alignment.Center
        ) {
          HorizontalDivider(
            modifier = Modifier
              .fillMaxWidth()
              .height(1.dp)
              .padding(vertical = 16.dp),
            color = Color.Gray
          )
        }
        Box(
          modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp),
          contentAlignment = Alignment.CenterStart
        ) {
          Text(
            "ログイン情報:",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium
          )
        }
        if(uiState.currentUser != null && uiState.currentUser!!.isAnonymous) {
          Column(
            modifier = Modifier.padding(16.dp)
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
              contentAlignment = Alignment.TopStart
            ) {
              Text(
                text = "ログインしていません。機種変更でデータを引き継ぐ/取り込む場合はログインしてください。",
                style = MaterialTheme.typography.bodyMedium
              )
            }
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
              contentAlignment = Alignment.Center
            ) {
              GoogleSignInButton(
                viewModel = viewModel,
                onSignInSuccess = {
                  Toast.makeText(context, "ログインしました", Toast.LENGTH_SHORT)
                    .show()
                },
                onSignInFailure = {
                  Toast.makeText(context, "ログインに失敗しました", Toast.LENGTH_SHORT)
                    .show()
                }
              )
            }
          }
        } else {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(start=32.dp),
          ) {
            Text(
              text = "${uiState.currentUser?.email}",
              style = MaterialTheme.typography.titleMedium
            )
          }
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(start=16.dp, end=16.dp, top=16.dp),
              contentAlignment = Alignment.Center
          ) {
            GoogleSignOutButton(
              viewModel = viewModel,
              onSignOutSuccess = {
                Toast.makeText(context, "ログアウトしました", Toast.LENGTH_SHORT).show()
              },
              onSignOutFailure = {
                Toast.makeText(context, "ログアウトに失敗しました", Toast.LENGTH_SHORT).show()
              }
            )
          }
        }
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(start=16.dp, end=16.dp),
          contentAlignment = Alignment.Center
        ) {
          DeleteAccountButton(
            viewModel = viewModel,
            onDeleteSuccess = {
              Toast.makeText(context, "データを削除しました", Toast.LENGTH_SHORT).show()
            },
            onDeleteFailure = {
              Toast.makeText(context, "データの削除に失敗しました", Toast.LENGTH_SHORT).show()
            }
          )
        }
      }
    if(uiState.isLoading) {
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