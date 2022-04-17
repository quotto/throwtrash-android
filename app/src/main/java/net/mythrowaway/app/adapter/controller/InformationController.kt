package net.mythrowaway.app.adapter.controller

import net.mythrowaway.app.usecase.InformationUseCase
import javax.inject.Inject

class InformationController @Inject constructor(
    private val useCase: InformationUseCase
) : IInformationController {
    override fun loadInformation() {
        useCase.showUserInformation()
    }
}