package net.mythrowaway.app.module.account_link.presentation.view

import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import net.mythrowaway.app.R
import net.mythrowaway.app.module.account_link.presentation.view_model.AccountLinkViewModel

@Composable
fun FinishAccountLinkScreen(
  code: String,
  state: String,
  viewModel: AccountLinkViewModel,
) {
  val uiState by viewModel.uiState.collectAsState()
  val apiUrl = stringResource(id = R.string.url_api)
  LaunchedEffect(Unit) {
    viewModel.finishAccountLink(
      apiUrl = apiUrl,
      code = code,
      state = state
    )
  }
  if(uiState.finishAccountLink) {
    Log.d("EnableSkillScreen", "url: ${uiState.enableSkillUrl}")
    // WebViewを表示する
    AndroidView(
      modifier = Modifier.fillMaxSize(1f),
      factory = {
        val webView = WebView(it)
        webView.setBackgroundColor(android.graphics.Color.WHITE)
        webView
      },
      update = { webView ->
        webView.apply {
          webViewClient = WebViewClient()
        }
        webView.loadUrl(uiState.enableSkillUrl)
      })
  } else {
    Box(
      modifier = Modifier.fillMaxSize(1f)
        .background(color = androidx.compose.ui.graphics.Color.White),
      contentAlignment = Alignment.Center,
    ) {
      Text("Loading...")
    }
  }
}
