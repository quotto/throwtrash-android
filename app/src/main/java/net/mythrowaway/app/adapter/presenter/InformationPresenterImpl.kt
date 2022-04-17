package net.mythrowaway.app.adapter.presenter

import net.mythrowaway.app.adapter.IAlarmView
import net.mythrowaway.app.adapter.IInformationView
import net.mythrowaway.app.usecase.IInformationPresenter
import net.mythrowaway.app.viewmodel.InformationViewModel
import javax.inject.Inject

class InformationPresenterImpl @Inject constructor(): IInformationPresenter {
    private lateinit var view: IInformationView
    private lateinit var viewModel: InformationViewModel
    override fun showUserInfo(accountId: String) {
        val viewModel: InformationViewModel = InformationViewModel()
        viewModel.userId = accountId
        view.showUserInfo(viewModel)
    }

    override fun setView(view: IInformationView) {
        this.view = view
    }

    fun setViewModel(viewModel: InformationViewModel) {
        this.viewModel = viewModel
    }
}