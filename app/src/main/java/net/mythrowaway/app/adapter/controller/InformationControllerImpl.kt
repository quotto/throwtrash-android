package net.mythrowaway.app.adapter.controller

import net.mythrowaway.app.usecase.InformationUseCase
import javax.inject.Inject

class InformationControllerImpl @Inject constructor(
    private val useCase: InformationUseCase
) : InformationControllerInterface {
    override fun loadInformation() {
        useCase.showUserInformation()
    }
}