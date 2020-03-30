package com.example.mythrowtrash.adapter

import com.example.mythrowtrash.usecase.IActivatePresenter

class ActivatePresenterImpl(private val view: IActivateView): IActivatePresenter{
    override fun success() {
        view.success()
    }

    override fun failed() {
        view.failed()
    }
}