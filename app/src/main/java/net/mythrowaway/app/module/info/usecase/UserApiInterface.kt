package net.mythrowaway.app.module.info.usecase

interface UserApiInterface {
  suspend fun signin(idToken: String): String
  suspend fun deleteAccount(idToken: String, userId: String)
}