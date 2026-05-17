package net.mythrowaway.app.module.other.presentation.view

import androidx.annotation.DrawableRes
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import net.mythrowaway.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherScreen(
  onOpenTerms: () -> Unit,
  onOpenPrivacyPolicy: () -> Unit,
  onOpenLicense: () -> Unit,
) {
  val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("その他") },
        navigationIcon = {
          IconButton(onClick = { dispatcher?.onBackPressed() }) {
            Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
          }
        }
      )
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      OtherMenuItem(
        text = "利用規約",
        iconRes = R.drawable.ic_description_24,
        onClick = onOpenTerms,
      )
      OtherMenuItem(
        text = "プライバシーポリシー",
        iconRes = R.drawable.ic_local_police_24,
        onClick = onOpenPrivacyPolicy,
      )
      OtherMenuItem(
        text = "ライセンス",
        iconRes = R.drawable.ic_license_24,
        onClick = onOpenLicense,
      )
    }
  }
}

@Composable
private fun OtherMenuItem(
  text: String,
  @DrawableRes iconRes: Int,
  onClick: () -> Unit,
) {
  Column {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)
        .padding(horizontal = 24.dp, vertical = 18.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        modifier = Modifier.size(24.dp),
        painter = painterResource(id = iconRes),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Spacer(modifier = Modifier.width(16.dp))
      Text(
        modifier = Modifier.weight(1f),
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
      )
    }
    HorizontalDivider()
  }
}
