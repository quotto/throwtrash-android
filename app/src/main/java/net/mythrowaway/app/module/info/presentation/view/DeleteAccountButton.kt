package net.mythrowaway.app.module.info.presentation.view

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.mythrowaway.app.module.info.infra.AuthManager
import net.mythrowaway.app.module.info.usecase.UserRepositoryInterface
import net.mythrowaway.app.module.trash.service.TrashService

@Composable
fun DeleteAccountButton(
  authManager: AuthManager,
  userRepository: UserRepositoryInterface,
  trashService: TrashService,
  onDeleteSuccess: () -> Unit,
  onDeleteFailure: () -> Unit
) {
  val coroutineScope = rememberCoroutineScope()

  Button(
    onClick = {
      coroutineScope.launch {
        if (authManager.deleteAccount()) {
          try {
            userRepository.deleteUserId()
            trashService.reset()
            onDeleteSuccess()
          } catch (e: Exception) {
            Log.d("DeleteAccountButton", "Failed to delete user id")
            Log.d("DeleteAccountButton", e.stackTraceToString())
            onDeleteFailure()
          }
        } else {
          onDeleteFailure()
        }
      }
    },
    colors = ButtonDefaults.buttonColors(
      containerColor = MaterialTheme.colorScheme.errorContainer
    ),
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp)
      .height(48.dp)
  ) {
    Text(text = "データ削除", color = MaterialTheme.colorScheme.error)
  }
}