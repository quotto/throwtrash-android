package net.mythrowaway.app.module.inquiry.presentation.view

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InquiryScreen() {
  val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("問い合わせ") },
        navigationIcon = {
          IconButton(onClick = {
            dispatcher?.onBackPressed()
          }) {
            Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
          }
        }
      )
    }
  ) { paddingValues ->
    AndroidView(
      modifier = Modifier.padding(paddingValues),
      factory = { context ->
                WebView(context)
      },
      update = { webView ->
        webView.apply {
          webViewClient = WebViewClient()
        }
        webView.settings.javaScriptEnabled = true

        webView.loadUrl("https://docs.google.com/forms/d/e/1FAIpQLScQiZNzcYKgto1mQYAmxmo49RTuAnvtmkk3BQ02MsVlE4OmHg/viewform")

      }
    )
  }
}