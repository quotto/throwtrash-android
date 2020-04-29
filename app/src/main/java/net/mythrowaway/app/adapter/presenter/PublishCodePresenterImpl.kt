package net.mythrowaway.app.adapter.presenter

import net.mythrowaway.app.adapter.IPublishCodeView
import net.mythrowaway.app.usecase.IPublishCodePresenter


class PublishCodePresenterImpl(private val view: IPublishCodeView):
    IPublishCodePresenter {
    override fun showActivationCode(activationCode: String) {
        view.showActivationCode(activationCode)
    }

    override fun showPublishCodeError() {
        view.showError()
    }
}