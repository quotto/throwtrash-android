package net.mythrowaway.app.adapter.presenter

import net.mythrowaway.app.adapter.IActivateView
import net.mythrowaway.app.usecase.IActivatePresenter

class ActivatePresenterImpl(private val view: IActivateView): IActivatePresenter {
    override fun success() {
        view.success()
    }

    override fun failed() {
        view.failed()
    }
}