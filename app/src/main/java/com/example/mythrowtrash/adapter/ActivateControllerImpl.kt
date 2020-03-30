package com.example.mythrowtrash.adapter

import com.example.mythrowtrash.usecase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ActivateControllerImpl(view: IActivateView): IActivateController {
    private val useCase = ActivateUseCase(
        adapter = DIContainer.resolve(IAPIAdapter::class.java)!!,
        config = DIContainer.resolve(IConfigRepository::class.java)!!,
        persist = DIContainer.resolve(IPersistentRepository::class.java)!!,
        presenter = ActivatePresenterImpl(view),
        trashManager = DIContainer.resolve(TrashManager::class.java)!!)
    override suspend fun activate(code: String) {
        withContext(Dispatchers.IO) {
            useCase.activate(code)
        }
    }
}