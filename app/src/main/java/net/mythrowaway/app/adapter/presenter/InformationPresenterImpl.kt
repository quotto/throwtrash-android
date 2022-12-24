package net.mythrowaway.app.adapter.presenter

import net.mythrowaway.app.adapter.InformationViewInterface
import net.mythrowaway.app.usecase.InformationPresenterInterface
import net.mythrowaway.app.viewmodel.InformationViewModel
import javax.inject.Inject

class InformationPresenterImpl @Inject constructor(): InformationPresenterInterface {
    private lateinit var view: InformationViewInterface
    private lateinit var viewModel: InformationViewModel
    override fun showUserInfo(accountId: String) {
        val viewModel: InformationViewModel = InformationViewModel()
        viewModel.userId = accountId
        view.showUserInfo(viewModel)
    }

    override fun setView(view: InformationViewInterface) {
        this.view = view
    }

    fun setViewModel(viewModel: InformationViewModel) {
        this.viewModel = viewModel
    }
}