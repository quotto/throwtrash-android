package net.mythrowaway.app.adapter.controller

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mythrowaway.app.usecase.AccountLinkUseCase
import javax.inject.Inject

class AccountLinkControllerImpl@Inject constructor(private val useCase: AccountLinkUseCase): AccountLinkControllerInterface {
    override suspend fun accountLinkWithApp() {
        withContext(Dispatchers.IO) {
            useCase.startAccountLinkWithAlexaApp()
        }
    }

    override suspend fun accountLinkWithLWA() {
        withContext(Dispatchers.IO) {
            useCase.startAccountLinkWithLWA()
        }
    }
}