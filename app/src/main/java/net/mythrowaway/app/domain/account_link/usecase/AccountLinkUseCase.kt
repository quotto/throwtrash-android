package net.mythrowaway.app.domain.account_link.usecase

import android.util.Log
import net.mythrowaway.app.domain.account_link.entity.FinishAccountLinkRequestInfo
import net.mythrowaway.app.domain.info.usecase.UserRepositoryInterface
import net.mythrowaway.app.domain.trash.usecase.AccountLinkRepositoryInterface
import net.mythrowaway.app.domain.trash.usecase.MobileApiInterface
import javax.inject.Inject

class AccountLinkUseCase @Inject constructor(
    private val adapter: MobileApiInterface,
    private val accountLinkRepository: AccountLinkRepositoryInterface,
    private val userRepository: UserRepositoryInterface
) {
    fun startAccountLinkWithAlexaApp(): String {
        val userId = userRepository.getUserId()
        if (userId === null) {
            throw Exception("User ID is null")
        }
        val startAccountLinkResponse = adapter.accountLink(userId)

        val redirectUriPattern = Regex("^https://.+&redirect_uri=(https://[^&]+)")
        redirectUriPattern.matchEntire(startAccountLinkResponse.url)?.also {
            accountLinkRepository.saveAccountLinkRequestInfo(
                FinishAccountLinkRequestInfo(
                token = startAccountLinkResponse.token,
                redirectUri = it.groupValues[1]
            )
            )
        } ?: throw Exception("Failed to extract redirect_uri")
        return startAccountLinkResponse.url
    }

    fun startAccountLinkWithLWA(): String {
        val userId = userRepository.getUserId()
        if (userId === null) {
            throw Exception("User ID is null")
        }
        val startAccountLinkResponse = adapter.accountLinkAsWeb(userId)
        val redirectUriPattern = Regex("^https://.+&redirect_uri=(https://[^&]+)")
        redirectUriPattern.matchEntire(startAccountLinkResponse.url)?.also {
            Log.d(javaClass.simpleName, "redirect_uri: ${it.groupValues[1]}, token: ${startAccountLinkResponse.token}")
            accountLinkRepository.saveAccountLinkRequestInfo(
                FinishAccountLinkRequestInfo(
                token = startAccountLinkResponse.token,
                redirectUri = it.groupValues[1]
            )
            )
        } ?: throw Exception("Failed to extract redirect_uri")
        return startAccountLinkResponse.url
    }

    fun getAccountLinkRequest(): FinishAccountLinkRequestInfo {
        val userId = userRepository.getUserId()
        if (userId === null) {
            throw Exception("User ID is null")
        }
        return accountLinkRepository.getAccountLinkRequestInfo()
    }
}