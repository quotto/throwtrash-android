package net.mythrowaway.app.adapter.controller

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mythrowaway.app.usecase.*
import javax.inject.Inject

class ActivateControllerImpl @Inject constructor(private val useCase: ActivateUseCase):
    ActivateControllerInterface {
    override suspend fun activate(code: String) {
        withContext(Dispatchers.IO) {
            useCase.activate(code)
        }
    }

    override fun checkCode(code: String) {
//        useCase.checkCode(code)
    }
}