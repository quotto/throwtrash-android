package net.mythrowaway.app.usecase

import javax.inject.Inject

class AccountLinkUseCase @Inject constructor(
    private val adapter: MobileApiInterface,
    private val config: ConfigRepositoryInterface,
    private val presenter: AccountLinkPresenterInterface
) {
    suspend fun startAccountLinkWithAlexaApp() {
        config.getUserId()?.let {userId ->
            adapter.accountLink(userId)?.let {accountLinkInfo ->
                presenter.startAccountLink(accountLinkInfo)
                return
            }
        }
        presenter.handleError()
    }

    suspend fun startAccountLinkWithLWA() {
        config.getUserId()?.let { userId ->
            adapter.accountLinkAsWeb(userId)?.let { accountLinkInfo ->
                presenter.startAccountLink(accountLinkInfo)
                return
            }
        }
        presenter.handleError()
    }
}