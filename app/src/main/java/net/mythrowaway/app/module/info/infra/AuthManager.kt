package net.mythrowaway.app.module.info.infra
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
import net.mythrowaway.app.module.info.dto.GoogleSignInResult
import net.mythrowaway.app.module.info.dto.SignInStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor(
  private val context: Context
) {

  private val auth: FirebaseAuth = FirebaseAuth.getInstance()

  // Firebase にログイン (アプリ起動時に実行)
  suspend fun initializeAuth(): FirebaseUser? {
    val currentUser = auth.currentUser
    return if (currentUser != null) {
      checkAndSignInAnonymously(currentUser)
    } else {
      signInAnonymously()
    }
  }

  fun getCurrentUser(): FirebaseUser? {
    return auth.currentUser
  }

  // Google アカウントがリンク済みかチェックし、未リンクなら匿名ログイン
  private suspend fun checkAndSignInAnonymously(user: FirebaseUser): FirebaseUser? {
    val providers = user.providerData.map { it.providerId }
    return if (providers.contains(GoogleAuthProvider.PROVIDER_ID)) {
      // Google アカウントがリンク済みならそのまま返す
      Log.i(javaClass.simpleName, "Already linked with Google account: ${user.uid}")
      user
    } else {
      // 未リンクなら匿名ログイン
      signInAnonymously()
    }
  }

  // 匿名ログインを同期的に処理（suspend）
  private suspend fun signInAnonymously(): FirebaseUser? {
    return try {
      val result = auth.signInAnonymously().await()
      Log.i(javaClass.simpleName, "Sign in as anonymous user: ${result.user?.uid}");
      result.user
    } catch (e: Exception) {
      Log.e(javaClass.simpleName, "Failed to sign in anonymously.")
      Log.e(javaClass.simpleName, e.stackTraceToString());
      null
    }
  }

  // IDトークンを取得する（suspend）
  suspend fun getIdToken(forceRefresh: Boolean = true): String? {
    return try {
      val user = auth.currentUser ?: signInAnonymously() ?: return null
      val tokenResult = user.getIdToken(forceRefresh).await()
      Log.d(javaClass.simpleName, "ID token: ${tokenResult.token}")
      tokenResult.token
    } catch (e: Exception) {
      Log.e(javaClass.simpleName, "Failed to get ID token.")
      Log.e(javaClass.simpleName, e.stackTraceToString())
      null
    }
  }

  suspend fun signInWithGoogle(): Result<SignInStatus> {
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

  suspend fun signOut(): Boolean {
    try {
      auth.signOut()
      signInAnonymously()
      return true
    } catch (e: Exception) {
      Log.e(javaClass.simpleName, "Failed to sign out.")
      Log.e(javaClass.simpleName, e.stackTraceToString())
      return false
    }
  }

  private suspend fun getGoogleIdToken(): String? {
    val serverClientId = context.getString(R.string.default_web_client_id)
    val credentialManager = CredentialManager.create(context)

      val googleIdOption = GetGoogleIdOption.Builder()
        .setServerClientId(serverClientId)
        .setFilterByAuthorizedAccounts(false) // すべてのアカウントを対象
        .build()

      val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

      val credential = credentialManager.getCredential(context, request).credential

      val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
      val googleIdToken = googleCredential.idToken
      return googleIdToken
  }

  suspend fun deleteAccount(): Boolean {
    val user = auth.currentUser ?: return false
    try {
      user.providerData.forEach {
        when (it.providerId) {
          GoogleAuthProvider.PROVIDER_ID -> {
            Log.d(javaClass.simpleName, "Re-authenticate with Google account.")
            val googleCredential = GoogleAuthProvider.getCredential(getGoogleIdToken(), null)
            user.reauthenticate(googleCredential).await()
            return@forEach
          }
        }
      }
      user.delete().await()
      signInAnonymously()
      return true
    } catch (e: Exception) {
      Log.e(javaClass.simpleName, "Failed to delete account.")
      Log.e(javaClass.simpleName, e.stackTraceToString())
      return false
    }
  }
}