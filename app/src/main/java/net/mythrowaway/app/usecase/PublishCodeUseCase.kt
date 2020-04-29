package net.mythrowaway.app.usecase

import android.util.Log

class PublishCodeUseCase(private val apiAdapter: IAPIAdapter,
                         private val config: IConfigRepository,
                         private val presenter: IPublishCodePresenter) {
    fun publishActivationCode() {
        config.getUserId()?.let {id->
            apiAdapter.publishActivationCode(id)?.let {code->
                Log.i(this.javaClass.simpleName, "Publish Activation Code -> id:$id, code: $code")
                presenter.showActivationCode(code)
                return
            }
            Log.e(this.javaClass.simpleName, "Failed Publish Activation Code -> id:$id")
            presenter.showPublishCodeError()
        }
    }
}