package net.mythrowaway.app.usecase

import android.util.Log
import net.mythrowaway.app.domain.account_link.FinishAccountLinkRequestInfo
import javax.inject.Inject

class AccountLinkUseCase @Inject constructor(
    private val adapter: MobileApiInterface,
    private val config: ConfigRepositoryInterface,
//    private val presenter: AccountLinkPresenterInterface
) {
    fun startAccountLinkWithAlexaApp(): String {
        val userId = config.getUserId()
        if (userId === null) {
            throw Exception("User ID is null")
        }
        val startAccountLinkResponse = adapter.accountLink(userId)

        val redirectUriPattern = Regex("^https://.+&redirect_uri=(https://[^&]+)")
        redirectUriPattern.matchEntire(startAccountLinkResponse.url)?.also {
            config.saveAccountLinkRequestInfo(
                FinishAccountLinkRequestInfo(
                token = startAccountLinkResponse.token,
                redirectUri = it.groupValues[1]
            )
            )
        } ?: throw Exception("Failed to extract redirect_uri")
        return startAccountLinkResponse.url
//        presenter.handleError()
    }

    fun startAccountLinkWithLWA(): String {
        val userId = config.getUserId()
        if (userId === null) {
            throw Exception("User ID is null")
//                presenter.startAccountLink(accountLinkInfo)
//                return
//            }
        }
        val startAccountLinkResponse = adapter.accountLinkAsWeb(userId)
        val redirectUriPattern = Regex("^https://.+&redirect_uri=(https://[^&]+)")
        redirectUriPattern.matchEntire(startAccountLinkResponse.url)?.also {
            Log.d(javaClass.simpleName, "redirect_uri: ${it.groupValues[1]}, token: ${startAccountLinkResponse.token}")
            config.saveAccountLinkRequestInfo(
                FinishAccountLinkRequestInfo(
                token = startAccountLinkResponse.token,
                redirectUri = it.groupValues[1]
            )
            )
        } ?: throw Exception("Failed to extract redirect_uri")
        return startAccountLinkResponse.url
//        presenter.handleError()
    }

    fun getAccountLinkRequest(): FinishAccountLinkRequestInfo {
        val userId = config.getUserId()
        if (userId === null) {
            throw Exception("User ID is null")
        }
        return config.getAccountLinkRequestInfo()
    }
}