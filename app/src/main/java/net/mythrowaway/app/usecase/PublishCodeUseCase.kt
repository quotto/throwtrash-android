package net.mythrowaway.app.usecase

import android.util.Log
import javax.inject.Inject

class PublishCodeUseCase @Inject constructor(
  private val apiAdapter: MobileApiInterface,
  private val config: ConfigRepositoryInterface,
 ) {

    fun publishActivationCode(): String {
      val userId = config.getUserId()
      if(userId.isNullOrEmpty()) {
        Log.e(this.javaClass.simpleName, "userId is empty")
        throw IllegalStateException("userId is empty")
      }
      return apiAdapter.publishActivationCode(userId)
    }
}