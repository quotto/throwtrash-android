package net.mythrowaway.app.usecase

import android.util.Log
import javax.inject.Inject

class PublishCodeUseCase @Inject constructor(
  private val apiAdapter: MobileApiInterface,
  private val config: ConfigRepositoryInterface,
  private val presenter: PublishCodePresenterInterface) {

    fun publishActivationCode() {
        config.getUserId()?.let {id->
            apiAdapter.publishActivationCode(id)?.let {code->
                Log.i(this.javaClass.simpleName, "Publish Activation Code -> id:$id, code: $code")
                presenter.showActivationCode(code)
                return
            }
            Log.e(this.javaClass.simpleName, "Failed Publish Activation Code -> id:$id")
            presenter.showPublishCodeError()
            return
        }
        Log.e(this.javaClass.simpleName, "ID Not exist in Configuration")
    }
}