package net.mythrowaway.app.module.account.service

import net.mythrowaway.app.module.account.usecase.AccountUseCase
import net.mythrowaway.app.module.account.usecase.AuthManagerInterface
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor(
    private val usecase: AccountUseCase
) {
    suspend fun getIdToken(forceRefresh: Boolean = true): Result<String> {
        return usecase.getIdToken(forceRefresh)
    }
}