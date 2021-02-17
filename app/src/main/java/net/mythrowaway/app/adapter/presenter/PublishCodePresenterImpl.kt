package net.mythrowaway.app.adapter.presenter

import net.mythrowaway.app.adapter.IPublishCodeView
import net.mythrowaway.app.usecase.IPublishCodePresenter
import javax.inject.Inject


class PublishCodePresenterImpl @Inject constructor():
    IPublishCodePresenter {
    private lateinit var view: IPublishCodeView
    override fun setView(view: IPublishCodeView) {
        this.view = view
    }

    override fun showActivationCode(activationCode: String) {
        view.showActivationCode(activationCode)
    }

    override fun showPublishCodeError() {
        view.showError()
    }
}