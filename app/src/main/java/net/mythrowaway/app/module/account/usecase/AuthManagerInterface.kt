package net.mythrowaway.app.module.account.usecase

import android.content.Context
import com.google.firebase.auth.FirebaseUser
import net.mythrowaway.app.module.account.dto.SignInStatus

interface AuthManagerInterface {
  suspend fun initializeAuth(): Result<FirebaseUser>
  fun getCurrentUser(): Result<FirebaseUser?>
  suspend fun getIdToken(forceRefresh: Boolean = true): Result<String>
  suspend fun signInWithGoogle(context: Context): Result<SignInStatus>
  suspend fun signOut(): Result<Unit>
}