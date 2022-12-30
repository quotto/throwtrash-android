package net.mythrowaway.app.adapter.presenter

import net.mythrowaway.app.adapter.ConnectViewInterface
import net.mythrowaway.app.usecase.ConnectUseCase
import net.mythrowaway.app.usecase.ConnectPresenterInterface
import net.mythrowaway.app.viewmodel.ConnectViewModel
import javax.inject.Inject

class ConnectPresenterImplInterface @Inject constructor(): ConnectPresenterInterface {
    private val viewModel = ConnectViewModel()
    private lateinit var view: ConnectViewInterface
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

    override fun setView(view: ConnectViewInterface) {
        this.view = view
    }
}