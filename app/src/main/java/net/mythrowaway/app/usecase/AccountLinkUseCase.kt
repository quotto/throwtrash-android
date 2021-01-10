package net.mythrowaway.app.usecase

class AccountLinkUseCase(
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