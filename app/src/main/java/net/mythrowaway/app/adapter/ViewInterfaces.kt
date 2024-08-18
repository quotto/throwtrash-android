package net.mythrowaway.app.adapter

import net.mythrowaway.app.viewmodel.*

interface PublishCodeViewInterface {
    fun showActivationCode(code: String)
    fun showError()
}

interface ActivateViewInterface {
    fun success()
    fun failed()
    fun invalidCodeError()
    fun validCode()
}

interface ConnectViewInterface {
    fun setEnabledStatus(viewModel: ConnectViewModel)
}

interface AccountLinkViewInterface {
    suspend fun startAccountLinkWithAlexaApp()
    fun startAccountLinkWithLWA()
    suspend fun showError()
}
interface InformationViewInterface {
    fun showUserInfo(viewModel: InformationViewModel)
}