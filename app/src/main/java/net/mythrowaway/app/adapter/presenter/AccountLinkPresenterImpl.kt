package net.mythrowaway.app.adapter.presenter

import net.mythrowaway.app.viewmodel.AccountLinkViewModel
import net.mythrowaway.app.adapter.IAccountLinkView
import net.mythrowaway.app.domain.AccountLinkInfo
import net.mythrowaway.app.usecase.IAccountLinkPresenter
import net.mythrowaway.app.viewmodel.ACCOUNT_LINK_TYPE
import javax.inject.Inject


class AccountLinkPresenterImpl @Inject constructor() : IAccountLinkPresenter {
    private lateinit var view: IAccountLinkView
    private lateinit var viewModel: AccountLinkViewModel

    override fun setView(view: IAccountLinkView) {
        this.view = view
    }

    override suspend fun startAccountLink(accountLinkInfo: AccountLinkInfo) {
        updateViewModel(accountLinkInfo)
        when(viewModel.type) {
            ACCOUNT_LINK_TYPE.WEB -> {
                view.startAccountLinkWithLWA()
            }
            ACCOUNT_LINK_TYPE.APP -> {
                view.startAccountLinkWithAlexaApp()
            }
        }
    }

    override suspend fun handleError() {
        view.showError()
    }

    fun setViewModel(viewModel: AccountLinkViewModel) {
        this.viewModel = viewModel
    }

    private fun updateViewModel(accountLinkInfo: AccountLinkInfo) {
        val stateValue = accountLinkInfo.linkUrl.substring(accountLinkInfo.linkUrl.indexOf("state=")+6)
        this.viewModel.token = accountLinkInfo.token
        this.viewModel.url = accountLinkInfo.linkUrl
        this.viewModel.state = stateValue
    }
}