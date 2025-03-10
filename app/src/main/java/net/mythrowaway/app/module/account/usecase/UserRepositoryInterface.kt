package net.mythrowaway.app.module.account.usecase

interface UserRepositoryInterface {
  fun saveUserId(id: String)
  fun getUserId(): String?
  fun deleteUserId()
}