package net.mythrowaway.app.adapter.controller

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mythrowaway.app.adapter.DIContainer
import net.mythrowaway.app.adapter.IPublishCodeView
import net.mythrowaway.app.adapter.presenter.PublishCodePresenterImpl
import net.mythrowaway.app.usecase.IAPIAdapter
import net.mythrowaway.app.usecase.IConfigRepository
import net.mythrowaway.app.usecase.PublishCodeUseCase

class PublishCodeControllerImpl(view: IPublishCodeView):
    IPublishCodeController {
    private val useCase = PublishCodeUseCase(
        presenter = PublishCodePresenterImpl(
            view
        ),
        apiAdapter = DIContainer.resolve(
            IAPIAdapter::class.java
        )!!,
        config = DIContainer.resolve(
            IConfigRepository::class.java
        )!!
    )
    override suspend fun publishActivationCode() {
        withContext(Dispatchers.IO) {
            useCase.publishActivationCode()
        }
    }
}