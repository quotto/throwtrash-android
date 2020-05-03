package net.mythrowaway.app.adapter.controller

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mythrowaway.app.adapter.presenter.ActivatePresenterImpl
import net.mythrowaway.app.adapter.DIContainer
import net.mythrowaway.app.adapter.IActivateView
import net.mythrowaway.app.usecase.*

class ActivateControllerImpl(view: IActivateView):
    IActivateController {
    private val useCase = ActivateUseCase(
        adapter = DIContainer.resolve(
            IAPIAdapter::class.java
        )!!,
        config = DIContainer.resolve(
            IConfigRepository::class.java
        )!!,
        persist = DIContainer.resolve(
            IPersistentRepository::class.java
        )!!,
        presenter = ActivatePresenterImpl(
            view
        ),
        trashManager = DIContainer.resolve(
            TrashManager::class.java
        )!!
    )
    override suspend fun activate(code: String) {
        withContext(Dispatchers.IO) {
            useCase.activate(code)
        }
    }

    override fun checkCode(code: String) {
        useCase.checkCode(code)
    }
}