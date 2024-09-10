package net.mythrowaway.app.module.info.presentation.view

import android.widget.Toast
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import net.mythrowaway.app.module.info.presentation.view_model.InformationViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun InformationScreen(
  viewModel: InformationViewModel,
) {
  val uiState by viewModel.uiState.collectAsState()
  val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
  val clipboardManager = LocalClipboardManager.current
  val context = LocalContext.current
  LaunchedEffect(Unit) {
    viewModel.loadInformation()
  }
  Scaffold (
    topBar = {
       TopAppBar(
         title = {
           Text(text = "ユーザー情報")
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
    ){
      Box(
        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
        contentAlignment = Alignment.CenterStart
      ) {
        Text("ユーザーID:")
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
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.primary
        )
      }
      Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "長押しでコピーできます",
          color = MaterialTheme.colorScheme.primary
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
