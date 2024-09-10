package net.mythrowaway.app.module.info.usecase

interface UserRepositoryInterface {
  fun saveUserId(id: String)
  fun getUserId(): String?
}
