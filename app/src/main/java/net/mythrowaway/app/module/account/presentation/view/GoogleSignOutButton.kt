package net.mythrowaway.app.module.account.presentation.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.mythrowaway.app.module.account.presentation.view_model.AccountViewModel

@Composable
fun GoogleSignOutButton(
  viewModel: AccountViewModel,
  onSignOutSuccess: () -> Unit,
  onSignOutFailure: () -> Unit
) {
  val scope = rememberCoroutineScope()

  Button(
    onClick = {
      scope.launch {
        viewModel.signOut(
          onSuccess = onSignOutSuccess,
          onFailure = onSignOutFailure
        )
      }
    },
    modifier = Modifier
      .fillMaxWidth()
      .padding(8.dp)
  ) {
    Text("ログアウト")
  }
}