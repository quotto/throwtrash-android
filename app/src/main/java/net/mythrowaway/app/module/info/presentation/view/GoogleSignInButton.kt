package net.mythrowaway.app.module.info.presentation.view

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
import kotlinx.coroutines.launch
import net.mythrowaway.app.R
import net.mythrowaway.app.module.info.presentation.view_model.InformationViewModel

@Composable
fun GoogleSignInButton(
  viewModel: InformationViewModel,
  onSignInSuccess: () -> Unit,
  onSignInFailure: () -> Unit
) {
  Button(
    onClick = {
      viewModel.signInWithGoogle(onSignInSuccess, onSignInFailure)
    },
    colors = ButtonDefaults.buttonColors(Color.White),
    modifier = Modifier
      .fillMaxWidth()
      .height(48.dp)
  ) {
    Image(
      painter = painterResource(id = R.drawable.ic_google),
      contentDescription = "Google Sign In",
      modifier = Modifier.size(24.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(text = "Sign in with Google", color = androidx.compose.ui.graphics.Color.Black)
  }
}