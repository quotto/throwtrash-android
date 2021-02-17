package net.mythrowaway.app.adapter.controller

import net.mythrowaway.app.usecase.ConnectUseCase
import javax.inject.Inject

class ConnectControllerImpl @Inject constructor(private val useCase: ConnectUseCase):
    IConnectController {
    override fun changeEnabledStatus() {
        useCase.checkEnabledConnect()
    }
}