package net.mythrowaway.app.domain.info.usecase

interface UserRepositoryInterface {
  fun saveUserId(id: String)
  fun getUserId(): String?
}
