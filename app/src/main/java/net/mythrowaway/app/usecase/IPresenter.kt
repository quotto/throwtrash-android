package net.mythrowaway.app.usecase

import net.mythrowaway.app.adapter.*

interface InformationPresenterInterface {
    fun showUserInfo(accountId: String)
    fun setView(view: InformationViewInterface)
}