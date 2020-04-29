package net.mythrowaway.app.adapter.controller

import net.mythrowaway.app.adapter.presenter.ConnectPresenterImpl
import net.mythrowaway.app.adapter.DIContainer
import net.mythrowaway.app.adapter.IConnectView
import net.mythrowaway.app.usecase.ConnectUseCase
import net.mythrowaway.app.usecase.TrashManager

class ConnectControllerImpl(private val view: IConnectView):
    IConnectController {
    private val useCase = ConnectUseCase(
        presenter = ConnectPresenterImpl(
            view
        ),
        trashManager = DIContainer.resolve(
            TrashManager::class.java
        )!!
    )
    override fun changeEnabledStatus() {
        useCase.checkEnabledConnect()
    }
}