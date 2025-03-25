package net.mythrowaway.app.module.account.infra
import android.content.Context
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import net.mythrowaway.app.R
import net.mythrowaway.app.module.account.dto.SignInStatus
import net.mythrowaway.app.module.account.usecase.AuthManagerInterface
import javax.inject.Inject

class FirebaseAuthManager @Inject constructor(
): AuthManagerInterface {
  private val auth: FirebaseAuth = FirebaseAuth.getInstance()

  // Firebase にログイン (アプリ起動時に実行)
  override suspend fun initializeAuth(): Result<FirebaseUser> {
    val currentUser = auth.currentUser
    Log.d(javaClass.simpleName, "Current user: $currentUser")
    return if (currentUser != null) {
      checkAndSignInAnonymously(currentUser)
    } else {
      signInAnonymously()
    }
  }

  override fun getCurrentUser(): Result<FirebaseUser?> {
    return Result.success(auth.currentUser)
  }

  // Google アカウントがリンク済みかチェックし、未リンクなら匿名ログイン
  private suspend fun checkAndSignInAnonymously(user: FirebaseUser): Result<FirebaseUser> {
    val providers = user.providerData.map { it.providerId }
    return if (providers.contains(GoogleAuthProvider.PROVIDER_ID)) {
      // Google アカウントがリンク済みならそのまま返す
      Log.i(javaClass.simpleName, "Already linked with Google account: ${user.uid}")
      Result.success(user)
    } else {
      // 未リンクなら匿名ログイン
      signInAnonymously()
    }
  }

  // 匿名ログインを同期的に処理（suspend）
  private suspend fun signInAnonymously(): Result<FirebaseUser> {
    return try {
      val result = auth.signInAnonymously().await()
      val user = result.user
      if (user != null) {
        Log.i(javaClass.simpleName, "Sign in as anonymous user: ${user.uid}")
        Result.success(user)
      } else {
        Log.e(javaClass.simpleName, "Failed to sign in anonymously: user is null")
        Result.failure(Exception("Failed to sign in anonymously: user is null"))
      }
    } catch (e: Exception) {
      Log.e(javaClass.simpleName, "Failed to sign in anonymously.")
      Log.e(javaClass.simpleName, e.stackTraceToString())
      Result.failure(e)
    }
  }

  // IDトークンを取得する（suspend）
  override suspend fun getIdToken(forceRefresh: Boolean): Result<String> {
    return try {
      val currentUser = auth.currentUser
      val user = if (currentUser != null) {
        Result.success(currentUser)
      } else {
        return Result.failure(Exception("Failed to get ID token: user is null"))
      }

      user.fold(
        onSuccess = { firebaseUser ->
          val tokenResult = firebaseUser.getIdToken(forceRefresh).await()
          val token = tokenResult.token
          if (token != null) {
            Log.d(javaClass.simpleName, "ID token retrieved successfully")
            Result.success(token)
          } else {
            Result.failure(Exception("ID token is null"))
          }
        },
        onFailure = {
          Log.e(javaClass.simpleName, "Failed to get user for ID token")
          Result.failure(it)
        }
      )
    } catch (e: Exception) {
      Log.e(javaClass.simpleName, "Failed to get ID token.")
      Log.e(javaClass.simpleName, e.stackTraceToString())
      Result.failure(e)
    }
  }

  override suspend fun signInWithGoogle(context: Context): Result<SignInStatus> {
    val serverClientId = context.getString(R.string.default_web_client_id)
    val credentialManager = CredentialManager.create(context)

    val currentUser = auth.currentUser
    if (currentUser != null) {
      try {
        var credential: Credential
        try {
          val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(serverClientId)
            .setFilterByAuthorizedAccounts(false) // すべてのアカウントを対象
            .build()

          val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

          credential = credentialManager.getCredential(context, request).credential

        } catch (e: NoCredentialException) {
          Log.w(javaClass.simpleName, "No Google account found.")
          val signInWithGoogleOption: GetSignInWithGoogleOption = GetSignInWithGoogleOption.Builder(
            serverClientId).build()
          val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()
          credential = credentialManager.getCredential(context, request).credential
        }

        val providers = currentUser.providerData.map { it.providerId }
        if (providers.contains(GoogleAuthProvider.PROVIDER_ID)) {
          // 🔹 既に Google アカウントがリンク済みなら、何もしない
          Log.w(javaClass.simpleName, "Already linked with Google account: ${currentUser.uid}")
          return Result.success(SignInStatus.SIGNIN)
        }
        val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
        val googleIdToken = googleCredential.idToken
        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)

        try {
          // 🔹 Google アカウントをリンク
          currentUser.linkWithCredential(firebaseCredential).await()
          Log.i(javaClass.simpleName, "Successfully linked with Google account: ${currentUser.uid}")
          return Result.success(SignInStatus.SIGNUP)

        } catch (e: FirebaseAuthUserCollisionException) {
          Log.w(javaClass.simpleName, "Collision with another account, try to sign in.")
          // 🔹 既に別のアカウントにリンク済みのエラー (ID が重複)
          auth.signInWithCredential(firebaseCredential).await()
          Log.w(javaClass.simpleName, "Successfully signed in with Google account: ${auth.currentUser?.uid}")
          return Result.success(SignInStatus.SIGNIN)
        }
      } catch (e: Exception) {
        Log.e(javaClass.simpleName, "Failed to sign in with Google.")
        Log.e(javaClass.simpleName, e.stackTraceToString())
        return Result.failure(e)
      }
    }
    return Result.failure(Exception("Failed to sign in with Google"))
  }

  override suspend fun signOut(): Result<Unit> {
    return try {
      auth.signOut()
      signInAnonymously().fold(
        onSuccess = { Result.success(Unit) },
        onFailure = { Result.failure(it) }
      )
    } catch (e: Exception) {
      Log.e(javaClass.simpleName, "Failed to sign out.")
      Log.e(javaClass.simpleName, e.stackTraceToString())
      Result.failure(e)
    }
  }
}