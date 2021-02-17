package net.mythrowaway.app.usecase

import javax.inject.Inject

class AccountLinkUseCase @Inject constructor(
    private val adapter: IAPIAdapter,
    private val config: IConfigRepository,
    private val presenter: IAccountLinkPresenter
) {
    suspend fun getUrl() {
        val accountLinkInfo = config.getUserId().let {
            adapter.accountLink(it!!)
        }
        if(accountLinkInfo != null) {
            presenter.passAccountLinkInfo(accountLinkInfo)
        } else {
            presenter.handleError()
        }
    }
}