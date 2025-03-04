package net.mythrowaway.app.module.info.usecase

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mythrowaway.app.module.info.dto.SignInStatus
import net.mythrowaway.app.module.info.infra.AuthManager
import net.mythrowaway.app.module.trash.service.TrashService
import javax.inject.Inject

class InformationUseCase @Inject constructor(
  private val userRepository: UserRepositoryInterface,
  private val userApi: UserApiInterface,
  private val authManager: AuthManager,
  private val trashService: TrashService
) {
  fun saveUserId(id: String) {
    userRepository.saveUserId(id)
  }
  /**
   * ユーザーの情報を表示する
   */
  fun getUserId(): String? {
    val userId: String? = userRepository.getUserId()
    Log.d(javaClass.simpleName, "get user id: $userId")
    return userId
  }

  suspend fun signInWithGoogle(): Result<FirebaseUser> {
    return withContext(Dispatchers.IO) {
      try {
        authManager.signInWithGoogle().fold(
          onSuccess = { status ->
            when (status) {
              SignInStatus.SIGNUP -> {
                authManager.getCurrentUser().fold(
                  onSuccess = { firebaseUser ->
                    if (firebaseUser == null) {
                      return@withContext Result.failure(Exception("Firebase user is null"))
                    }
                    return@withContext Result.success(firebaseUser)
                  },
                  onFailure = { e ->
                    Log.d(javaClass.simpleName, "Failed to get current user")
                    Log.d(javaClass.simpleName, e.stackTraceToString())
                    return@withContext Result.failure(e)
                  }
                )
              }
              SignInStatus.SIGNIN -> {
                try {
                  val idTokenResult = authManager.getIdToken()
                  idTokenResult.fold(
                    onSuccess = { idToken ->
                      val userId = userApi.signin(idToken)
                      userRepository.saveUserId(userId)
                      trashService.reset()
                      authManager.getCurrentUser().fold(
                        onSuccess = { firebaseUser ->
                          if(firebaseUser == null) {
                            return@withContext Result.failure(Exception("Firebase user is null"))
                          }
                          return@withContext Result.success(firebaseUser)
                        },
                        onFailure = { e ->
                          Log.d(javaClass.simpleName, "Failed to get current user")
                          Log.d(javaClass.simpleName, e.stackTraceToString())
                          return@withContext Result.failure(e)
                        }
                      )
                    },
                    onFailure = { e ->
                      Log.d(javaClass.simpleName, "Failed to get ID token")
                      Log.d(javaClass.simpleName, e.stackTraceToString())
                      Result.failure(e)
                    }
                  )
                } catch (e: Exception) {
                  Log.d(javaClass.simpleName, "Sign in with Google failed")
                  Log.d(javaClass.simpleName, e.stackTraceToString())
                  Result.failure(e)
                }
              }
            }
          },
          onFailure = { e ->
            Log.d(javaClass.simpleName, "Sign in with Google failed")
            Log.d(javaClass.simpleName, e.stackTraceToString())
            Result.failure(e)
          }
        )
      } catch (e: Exception) {
        Log.d(javaClass.simpleName, "Sign in with Google failed with exception")
        Log.d(javaClass.simpleName, e.stackTraceToString())
        Result.failure(e)
      }
    }
  }

  suspend fun signOut(): Result<Unit> {
    return withContext(Dispatchers.IO) {
      try {
        authManager.signOut().fold(
          onSuccess = {
            trashService.reset()
            userRepository.deleteUserId()
            Result.success(Unit)
          },
          onFailure = { e ->
            Log.d(javaClass.simpleName, "Sign out failed")
            Log.d(javaClass.simpleName, e.stackTraceToString())
            Result.failure(e)
          }
        )
      } catch (e: Exception) {
        Log.d(javaClass.simpleName, "Sign out failed with exception")
        Log.d(javaClass.simpleName, e.stackTraceToString())
        Result.failure(e)
      }
    }
  }

  suspend fun deleteAccount(): Result<Unit> {
    return withContext(Dispatchers.IO) {
      try {
        val idTokenResult = authManager.getIdToken()
        idTokenResult.fold(
          onSuccess = { idToken ->
            val userId = userRepository.getUserId()
              ?: return@fold Result.failure(Exception("User ID not found"))

            try {
              userApi.deleteAccount(idToken, userId)
              userRepository.deleteUserId()
              trashService.reset()
              Result.success(Unit)
            } catch (e: Exception) {
              Log.d(javaClass.simpleName, "Delete account API call failed")
              Log.d(javaClass.simpleName, e.stackTraceToString())
              Result.failure(e)
            }
          },
          onFailure = { e ->
            Log.d(javaClass.simpleName, "Failed to get ID token for account deletion")
            Log.d(javaClass.simpleName, e.stackTraceToString())
            Result.failure(e)
          }
        )
      } catch (e: Exception) {
        Log.d(javaClass.simpleName, "Delete account failed with exception")
        Log.d(javaClass.simpleName, e.stackTraceToString())
        Result.failure(e)
      }
    }
  }

  fun getCurrentUser(): FirebaseUser? {
    return authManager.getCurrentUser().getOrNull()
  }
}