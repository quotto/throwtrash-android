package net.mythrowaway.app.usecase

import android.util.Log
import javax.inject.Inject

class InformationUseCase @Inject constructor(
  private val config: ConfigRepositoryInterface,
  private val presenter: InformationPresenterInterface,
) {
    /**
     * ユーザーの情報を表示する
     */
    fun showUserInformation() {
        val userId: String = config.getUserId()?.let{userId-> userId} ?: run {""};
        Log.d(javaClass.simpleName, "get user id: ${userId}")
        presenter.showUserInfo(userId);
    }
}