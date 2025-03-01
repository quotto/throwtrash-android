package net.mythrowaway.app.module.info.presentation.view

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.mythrowaway.app.R
import net.mythrowaway.app.module.info.dto.SigninResult
import net.mythrowaway.app.module.info.infra.AuthManager
import net.mythrowaway.app.module.info.usecase.UserApiInterface
import net.mythrowaway.app.module.info.usecase.UserRepositoryInterface
import net.mythrowaway.app.module.trash.service.TrashService

@Composable
fun GoogleSignInButton(
  authManager: AuthManager,
  userApi: UserApiInterface,
  userRepository: UserRepositoryInterface,
  trashService: TrashService,
  onSignInSuccess: (FirebaseUser) -> Unit,
  onSignInFailure: (Exception) -> Unit
) {
  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current

  Button(
    onClick = {
      coroutineScope.launch {
        authManager.signInWithGoogle().let {
          withContext(Dispatchers.IO) {
            when (it) {
              SigninResult.SIGNUP -> {
                onSignInSuccess(authManager.getCurrentUser()!!)
              }
              SigninResult.SIGNIN -> {
                try {
                  val userId = userApi.signin(authManager.getIdToken()!!)
                  userRepository.saveUserId(userId)
                  trashService.reset()
                  onSignInSuccess(authManager.getCurrentUser()!!)
                } catch (e: Exception) {
                  Log.d("GoogleSignInButton", "Sign in with Google failed")
                  Log.d("GoogleSignInButton", e.stackTraceToString())
                  onSignInFailure(Exception("Sign in with Google failed"))
                }
              }
              SigninResult.FAILURE -> {
                onSignInFailure(Exception("Sign in with Google failed"))
              }
            }
          }
        }
      }
    },
    colors = ButtonDefaults.buttonColors(Color.White),
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp)
      .height(48.dp)
  ) {
    Image(
      painter = painterResource(id = R.drawable.ic_google), // Google のロゴ画像
      contentDescription = "Google Sign In",
      modifier = Modifier.size(24.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(text = "Sign in with Google", color = androidx.compose.ui.graphics.Color.Black)
  }
}