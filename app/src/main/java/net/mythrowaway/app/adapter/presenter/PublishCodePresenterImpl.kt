package net.mythrowaway.app.adapter.presenter

import net.mythrowaway.app.adapter.PublishCodeViewInterface
import net.mythrowaway.app.usecase.PublishCodePresenterInterface
import javax.inject.Inject


class PublishCodePresenterImpl @Inject constructor():
    PublishCodePresenterInterface {
    private lateinit var view: PublishCodeViewInterface
    override fun setView(view: PublishCodeViewInterface) {
        this.view = view
    }

    override fun showActivationCode(activationCode: String) {
        view.showActivationCode(activationCode)
    }

    override fun showPublishCodeError() {
        view.showError()
    }
}