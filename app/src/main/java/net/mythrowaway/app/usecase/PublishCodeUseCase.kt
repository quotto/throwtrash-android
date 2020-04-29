package net.mythrowaway.app.usecase

class PublishCodeUseCase(private val apiAdapter: IAPIAdapter,
                         private val config: IConfigRepository,
                         private val presenter: IPublishCodePresenter) {
    fun publishActivationCode() {
        config.getUserId()?.let {id->
            apiAdapter.publishActivationCode(id)?.let {code->
                presenter.showActivationCode(code)
                return
            }
            presenter.showPublishCodeError()
        }
    }
}