package net.mythrowaway.app.usecase

import android.util.Log
import net.mythrowaway.app.service.TrashManager
import javax.inject.Inject

class ConnectUseCase @Inject constructor(
    private val presenter: IConnectPresenter,
    private val trashManager: TrashManager
){
    fun checkEnabledConnect() {
        if(trashManager.getScheduleCount() > 0) {
            // すべて利用可能
            Log.d(this.javaClass.simpleName,"Exist Trash Schedule -> ${trashManager.getScheduleCount()}")
            presenter.changeEnabledStatus(ConnectStatus.ENABLED)
        } else {
            // 共有機能が利用不可能
            Log.d(this.javaClass.simpleName,"Not Exist Trash Schedule")
            presenter.changeEnabledStatus(ConnectStatus.DISABLED_SHARE)
        }
    }

    enum class ConnectStatus {
        ENABLED,
        DISABLED_SHARE
    }
}