package net.mythrowaway.app.module.info.usecase

import android.util.Log
import com.google.firebase.auth.FirebaseUser
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
        return try {
            val result = authManager.signInWithGoogle()
            result.fold(
                onSuccess = { status ->
                    when (status) {
                        SignInStatus.SIGNUP -> {
                            Result.success(authManager.getCurrentUser()!!)
                        }
                        SignInStatus.SIGNIN -> {
                            try {
                                val userId = userApi.signin(authManager.getIdToken()!!)
                                userRepository.saveUserId(userId)
                                trashService.reset()
                                Result.success(authManager.getCurrentUser()!!)
                            } catch (e: Exception) {
                                Log.d(javaClass.simpleName, "Sign in with Google failed")
                                Log.d(javaClass.simpleName, e.stackTraceToString())
                                Result.failure(Exception("Sign in with Google failed"))
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

    suspend fun signOut(): Result<Unit> {
        return try {
            if (authManager.signOut()) {
                trashService.reset()
                userRepository.deleteUserId()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Sign out failed"))
            }
        } catch (e: Exception) {
            Log.d(javaClass.simpleName, "Sign out failed")
            Log.d(javaClass.simpleName, e.stackTraceToString())
            Result.failure(e)
        }
    }

    suspend fun deleteAccount(): Result<Unit> {
        return try {
            if (authManager.deleteAccount()) {
                userRepository.deleteUserId()
                trashService.reset()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Delete account failed"))
            }
        } catch (e: Exception) {
            Log.d(javaClass.simpleName, "Delete account failed")
            Log.d(javaClass.simpleName, e.stackTraceToString())
            Result.failure(e)
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return authManager.getCurrentUser()
    }
}