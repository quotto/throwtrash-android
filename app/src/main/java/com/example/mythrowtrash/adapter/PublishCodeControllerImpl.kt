package com.example.mythrowtrash.adapter

import com.example.mythrowtrash.usecase.PublishCodeUseCase
import com.example.mythrowtrash.usecase.IAPIAdapter
import com.example.mythrowtrash.usecase.IConfigRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PublishCodeControllerImpl(view: IPublishCodeView):IPublishCodeController {
    private val useCase = PublishCodeUseCase(
        presenter = PublishCodePresenterImpl(view),
        apiAdapter = DIContainer.resolve(IAPIAdapter::class.java)!!,
        config = DIContainer.resolve(IConfigRepository::class.java)!!
    )
    override suspend fun publishActivationCode() {
        withContext(Dispatchers.IO) {
            useCase.publishActivationCode()
        }
    }
}