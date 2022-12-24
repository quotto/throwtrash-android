package net.mythrowaway.app.adapter.controller

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mythrowaway.app.usecase.PublishCodeUseCase
import javax.inject.Inject

class PublishActivationCodeControllerImpl @Inject constructor(private val useCase: PublishCodeUseCase):
    PublishActivationCodeControllerInterface {

    override suspend fun publishActivationCode() {
        withContext(Dispatchers.IO) {
            useCase.publishActivationCode()
        }
    }
}