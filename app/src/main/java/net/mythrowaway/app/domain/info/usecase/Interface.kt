package net.mythrowaway.app.domain.info.usecase

interface UserRepositoryInterface {
  fun setUserId(id: String)
  fun getUserId(): String?
}
