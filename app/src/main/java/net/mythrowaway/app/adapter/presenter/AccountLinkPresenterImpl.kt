package net.mythrowaway.app.adapter.presenter

import net.mythrowaway.app.viewmodel.AccountLinkViewModel
import net.mythrowaway.app.adapter.IAccountLinkView
import net.mythrowaway.app.domain.AccountLinkInfo
import net.mythrowaway.app.usecase.IAccountLinkPresenter



class AccountLinkPresenterImpl(private val view: IAccountLinkView) : IAccountLinkPresenter {
    override suspend fun passAccountLinkInfo(accountLinkInfo: AccountLinkInfo) {
        val stateValue = accountLinkInfo.linkUrl.substring(accountLinkInfo.linkUrl.indexOf("state=")+6)
        val viewModel = AccountLinkViewModel().apply {
            sessionId = accountLinkInfo.sessionId
            sessionValue = accountLinkInfo.sessionValue
            url = accountLinkInfo.linkUrl
            state = stateValue
        }
        view.startAccountLink(viewModel)
    }

    override suspend fun handleError() {
        view.showError()
    }
}