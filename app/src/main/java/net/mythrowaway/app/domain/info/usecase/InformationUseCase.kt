package net.mythrowaway.app.domain.info.usecase

import android.util.Log
import javax.inject.Inject

class InformationUseCase @Inject constructor(
    private val userRepository: UserRepositoryInterface,
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
}