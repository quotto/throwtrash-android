package net.mythrowaway.app.adapter.presenter

import net.mythrowaway.app.adapter.IActivateView
import net.mythrowaway.app.usecase.ActivateUseCase
import net.mythrowaway.app.usecase.IActivatePresenter

class ActivatePresenterImpl(private val view: IActivateView): IActivatePresenter {
    override fun notify(resultCode: ActivateUseCase.ActivationResult) {
        when(resultCode) {
            ActivateUseCase.ActivationResult.ACTIVATE_SUCCESS ->
                view.success()
            ActivateUseCase.ActivationResult.ACTIVATE_ERROR ->
                view.failed()
            ActivateUseCase.ActivationResult.VALID_CODE ->
                view.validCode()
            ActivateUseCase.ActivationResult.INVALID_CODE ->
                view.invalidCodeError()
        }
    }
}