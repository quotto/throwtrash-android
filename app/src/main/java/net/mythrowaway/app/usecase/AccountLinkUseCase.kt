package net.mythrowaway.app.usecase

import javax.inject.Inject

class AccountLinkUseCase @Inject constructor(
    private val adapter: IAPIAdapter,
    private val config: IConfigRepository,
    private val presenter: IAccountLinkPresenter
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