package net.mythrowaway.app.usecase

import android.util.Log
import javax.inject.Inject

class InformationUseCase @Inject constructor(
    private val config: IConfigRepository,
    private val presenter: IInformationPresenter,
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