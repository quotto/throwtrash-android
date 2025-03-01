package net.mythrowaway.app.module.info.presentation.view

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.mythrowaway.app.module.info.infra.AuthManager
import net.mythrowaway.app.module.info.usecase.UserRepositoryInterface
import net.mythrowaway.app.module.trash.service.TrashService

@Composable
fun GoogleSignOutButton(
  authManager: AuthManager,
  userRepository: UserRepositoryInterface,
  trashService: TrashService,
  onSignOutSuccess: () -> Unit,
  onSignOutFailure: () -> Unit
) {
  val coroutineScope = rememberCoroutineScope()

  Button(
    onClick = {
      coroutineScope.launch {
        if (authManager.signOut()) {
          try {
            trashService.reset()
            userRepository.deleteUserId()
            onSignOutSuccess()
          } catch(e: Exception) {
            Log.d("GoogleSignOutButton", "Sign out with Google failed")
            Log.d("GoogleSignOutButton", e.stackTraceToString())
            onSignOutFailure()
          }
        } else {
          onSignOutFailure()
        }
      }
    },
    colors = ButtonDefaults.buttonColors(Color.White),
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp)
      .height(48.dp)
  ) {
    Text(text = "サインアウト", color = androidx.compose.ui.graphics.Color.Black)
  }
}