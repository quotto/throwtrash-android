package net.mythrowaway.app.usecase

import android.util.Log
import javax.inject.Inject

class InformationUseCase @Inject constructor(
  private val config: ConfigRepositoryInterface,
) {
    /**
     * ユーザーの情報を表示する
     */
    fun showUserInformation(): String {
        val userId: String = config.getUserId()?.let{userId-> userId} ?: run {""};
        Log.d(javaClass.simpleName, "get user id: $userId")
        return userId
    }
}