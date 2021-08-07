package net.mythrowaway.app.adapter.controller

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mythrowaway.app.usecase.AccountLinkUseCase
import javax.inject.Inject

class AccountLinkControllerImpl @Inject constructor(private val useCase: AccountLinkUseCase): IAccountLinkController {
    override suspend fun accountLink() {
        withContext(Dispatchers.IO) {
            useCase.getUrl()
        }
    }
}