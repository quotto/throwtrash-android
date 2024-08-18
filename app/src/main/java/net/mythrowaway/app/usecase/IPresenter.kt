package net.mythrowaway.app.usecase

import net.mythrowaway.app.adapter.*
import net.mythrowaway.app.domain.AccountLinkInfo
import net.mythrowaway.app.domain.AlarmConfig
import net.mythrowaway.app.domain.TrashData

interface PublishCodePresenterInterface {
    fun showActivationCode(activationCode: String)
    fun showPublishCodeError()
    fun setView(view: PublishCodeViewInterface)
}

interface ActivatePresenterInterface {
    fun notify(resultCode: ActivateUseCase.ActivationResult)
    fun setView(view: ActivateViewInterface)
}

interface ConnectPresenterInterface {
    fun changeEnabledStatus(status: ConnectUseCase.ConnectStatus)
    fun setView(view: ConnectViewInterface)
}

interface AccountLinkPresenterInterface {
    suspend fun startAccountLink(accountLinkInfo: AccountLinkInfo)
    suspend fun handleError()
    fun setView(view: AccountLinkViewInterface)
}

interface InformationPresenterInterface {
    fun showUserInfo(accountId: String)
    fun setView(view: InformationViewInterface)
}