package net.mythrowaway.app.adapter.presenter

import net.mythrowaway.app.adapter.IConnectView
import net.mythrowaway.app.usecase.ConnectUseCase
import net.mythrowaway.app.usecase.IConnectPresenter
import javax.inject.Inject

class ConnectPresenterImpl @Inject constructor(): IConnectPresenter {
    private val viewModel = ConnectViewModel()
    private lateinit var view: IConnectView
    override fun changeEnabledStatus(status: ConnectUseCase.ConnectStatus) {
        when(status) {
            ConnectUseCase.ConnectStatus.ENABLED -> {
                viewModel.apply {
                    enabledShare = true
                    enabledActivate = true
                    enabledAlexa = true
                }
            }
            ConnectUseCase.ConnectStatus.DISABLED_SHARE -> {
                viewModel.apply {
                    enabledShare = false
                    enabledActivate = true
                    enabledAlexa = false
                }
            }
        }
        view.setEnabledStatus(viewModel)
    }

    override fun setView(view: IConnectView) {
        this.view = view
    }
}

class ConnectViewModel {
    var enabledShare = true
    var enabledActivate = true
    var enabledAlexa = true
}