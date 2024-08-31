package net.mythrowaway.app.domain.info.usecase

import android.util.Log
import net.mythrowaway.app.usecase.UserRepositoryInterface
import javax.inject.Inject

class InformationUseCase @Inject constructor(
    private val userRepository: UserRepositoryInterface,
) {
    /**
     * ユーザーの情報を表示する
     */
    fun showUserInformation(): String {
        val userId: String = userRepository.getUserId() ?: run {""}
        Log.d(javaClass.simpleName, "get user id: $userId")
        return userId
    }
}