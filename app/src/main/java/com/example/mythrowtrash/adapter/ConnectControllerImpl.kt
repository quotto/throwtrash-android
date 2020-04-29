package com.example.mythrowtrash.adapter

import com.example.mythrowtrash.usecase.ConnectUseCase
import com.example.mythrowtrash.usecase.TrashManager

class ConnectControllerImpl(private val view: IConnectView): IConnectController {
    private val useCase = ConnectUseCase(
        presenter = ConnectPresenterImpl(view),
        trashManager = DIContainer.resolve(TrashManager::class.java)!!
    )
    override fun changeEnabledStatus() {
        useCase.checkEnabledConnect()
    }
}