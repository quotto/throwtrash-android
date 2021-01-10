package net.mythrowaway.app.adapter.controller

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mythrowaway.app.adapter.DIContainer
import net.mythrowaway.app.adapter.IAccountLinkView
import net.mythrowaway.app.adapter.presenter.AccountLinkPresenterImpl
import net.mythrowaway.app.usecase.AccountLinkUseCase
import net.mythrowaway.app.usecase.IAPIAdapter
import net.mythrowaway.app.usecase.IConfigRepository

class AccountLinkControllerImpl(view: IAccountLinkView): IAccountLinkController {
    private val presenter = AccountLinkPresenterImpl(view)
    private val useCase = AccountLinkUseCase(
        DIContainer.resolve(IAPIAdapter::class.java)!!,
        DIContainer.resolve(IConfigRepository::class.java)!!,
        presenter)
    override suspend fun accountLink() {
        withContext(Dispatchers.IO) {
            useCase.getUrl()
        }
    }
}