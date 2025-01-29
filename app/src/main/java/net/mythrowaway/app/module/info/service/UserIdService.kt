package net.mythrowaway.app.module.info.service

import net.mythrowaway.app.module.info.usecase.InformationUseCase
import javax.inject.Inject

class UserIdService @Inject constructor(private val useCase: InformationUseCase){
  fun registerUserId(id: String) {
    useCase.saveUserId(id)
  }

  fun getUserId(): String? {
    return useCase.getUserId()
  }
}