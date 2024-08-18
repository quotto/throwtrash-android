package net.mythrowaway.app.adapter.presenter

import net.mythrowaway.app.adapter.ActivateViewInterface
import net.mythrowaway.app.usecase.ActivateUseCase
import net.mythrowaway.app.usecase.ActivatePresenterInterface
import javax.inject.Inject

class ActivatePresenterImpl @Inject constructor(): ActivatePresenterInterface {
    private lateinit var view: ActivateViewInterface

    override fun notify(resultCode: ActivateUseCase.ActivationResult) {
        when(resultCode) {
            ActivateUseCase.ActivationResult.ACTIVATE_SUCCESS ->
                view.success()
            ActivateUseCase.ActivationResult.ACTIVATE_ERROR ->
                view.failed()
//            ActivateUseCase.ActivationResult.VALID_CODE ->
//                view.validCode()
//            ActivateUseCase.ActivationResult.INVALID_CODE ->
//                view.invalidCodeError()
        }
    }

    override fun setView(view: ActivateViewInterface) {
        this.view = view
    }
}