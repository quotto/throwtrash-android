package net.mythrowaway.app.module.account_link.usecase

import android.util.Log
import net.mythrowaway.app.module.account_link.entity.FinishAccountLinkRequestInfo
import net.mythrowaway.app.module.info.service.UserIdService
import javax.inject.Inject

class AccountLinkUseCase @Inject constructor(
    private val api: AccountLinkApiInterface,
    private val accountLinkRepository: AccountLinkRepositoryInterface,
    private val userIdService: UserIdService
) {
    fun startAccountLinkWithAlexaApp(): String {
        val userId = userIdService.getUserId()
        if (userId === null) {
            throw UserIdNotFoundException("User ID is null")
        }
        val startAccountLinkResponse = api.accountLink(userId)

        val redirectUriPattern = Regex("^https://.+&redirect_uri=(https://[^&]+)")
        redirectUriPattern.matchEntire(startAccountLinkResponse.url)?.also {
            accountLinkRepository.saveAccountLinkRequestInfo(
                FinishAccountLinkRequestInfo(
                token = startAccountLinkResponse.token,
                redirectUri = it.groupValues[1]
            )
            )
        } ?: throw InvalidRedirectUriException("Failed to extract redirect_uri")
        return startAccountLinkResponse.url
    }

    fun startAccountLinkWithLWA(): String {
        val userId = userIdService.getUserId()
        if (userId === null) {
            throw UserIdNotFoundException("User ID is null")
        }
        val startAccountLinkResponse = api.accountLinkAsWeb(userId)
        val redirectUriPattern = Regex("^https://.+&redirect_uri=(https://[^&]+)")
        redirectUriPattern.matchEntire(startAccountLinkResponse.url)?.also {
            Log.d(javaClass.simpleName, "redirect_uri: ${it.groupValues[1]}, token: ${startAccountLinkResponse.token}")
            accountLinkRepository.saveAccountLinkRequestInfo(
                FinishAccountLinkRequestInfo(
                token = startAccountLinkResponse.token,
                redirectUri = it.groupValues[1]
            )
            )
        } ?: throw InvalidRedirectUriException("Failed to extract redirect_uri")
        return startAccountLinkResponse.url
    }

    fun getAccountLinkRequest(): FinishAccountLinkRequestInfo {
        val userId = userIdService.getUserId()
        if (userId === null) {
            throw UserIdNotFoundException("User ID is null")
        }
        return accountLinkRepository.getAccountLinkRequestInfo()?: throw Exception("Account link request info not found")
    }
}