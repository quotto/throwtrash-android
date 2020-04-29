package com.example.mythrowtrash.usecase

class ConnectUseCase(
    private val presenter: IConnectPresenter,
    private val trashManager: TrashManager
){
    fun checkEnabledConnect() {
        if(trashManager.getScheduleCount() > 0) {
            // すべて利用可能
            presenter.changeEnabledStatus(ConnectStatus.ENABLED)
        } else {
            // 共有機能が利用不可能
            presenter.changeEnabledStatus(ConnectStatus.DISABLED_SHARE)
        }
    }

    enum class ConnectStatus {
        ENABLED,
        DISABLED_SHARE
    }
}