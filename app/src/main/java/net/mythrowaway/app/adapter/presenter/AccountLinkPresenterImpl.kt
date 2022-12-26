package net.mythrowaway.app.adapter.presenter

import net.mythrowaway.app.viewmodel.AccountLinkViewModel
import net.mythrowaway.app.adapter.AccountLinkViewInterface
import net.mythrowaway.app.domain.AccountLinkInfo
import net.mythrowaway.app.usecase.AccountLinkPresenterInterface
import net.mythrowaway.app.viewmodel.AccountLinkType
import javax.inject.Inject


class AccountLinkPresenterImpl @Inject constructor() : AccountLinkPresenterInterface {
    private lateinit var view: AccountLinkViewInterface
    private lateinit var viewModel: AccountLinkViewModel

    override fun setView(view: AccountLinkViewInterface) {
        this.view = view
    }

    override suspend fun startAccountLink(accountLinkInfo: AccountLinkInfo) {
        updateViewModel(accountLinkInfo)
        when(viewModel.type) {
            AccountLinkType.WEB -> {
                view.startAccountLinkWithLWA()
            }
            AccountLinkType.APP -> {
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