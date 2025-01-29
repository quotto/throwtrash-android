package net.mythrowaway.app.module.trash.usecase

import android.util.Log
import net.mythrowaway.app.module.info.usecase.UserRepositoryInterface
import javax.inject.Inject

class PublishCodeUseCase @Inject constructor(
  private val apiAdapter: MobileApiInterface,
  private val userRepository: UserRepositoryInterface
 ) {

    fun publishActivationCode(): String {
      val userId = userRepository.getUserId()
      if(userId.isNullOrEmpty()) {
        Log.e(this.javaClass.simpleName, "userId is empty")
        throw IllegalStateException("userId is empty")
      }
      return apiAdapter.publishActivationCode(userId)
    }
}