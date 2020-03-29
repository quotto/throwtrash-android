package com.example.mythrowtrash.adapter

import com.example.mythrowtrash.usecase.IPublishCodePresenter


class PublishCodePresenterImpl(private val view:IPublishCodeView): IPublishCodePresenter {
    override fun showActivationCode(activationCode: String) {
        view.showActivationCode(activationCode)
    }

    override fun showPublishCodeError() {
        view.showError()
    }
}