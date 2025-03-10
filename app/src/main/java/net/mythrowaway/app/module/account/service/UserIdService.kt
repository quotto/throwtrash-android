package net.mythrowaway.app.module.account.service

import net.mythrowaway.app.module.account.usecase.AccountUseCase
import javax.inject.Inject

class UserIdService @Inject constructor(private val useCase: AccountUseCase){
  fun registerUserId(id: String) {
    useCase.saveUserId(id)
  }

  fun getUserId(): String? {
    return useCase.getUserId()
  }
}