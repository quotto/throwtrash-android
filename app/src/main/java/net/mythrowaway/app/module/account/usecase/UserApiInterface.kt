package net.mythrowaway.app.module.account.usecase

interface UserApiInterface {
  suspend fun signin(idToken: String): String
  suspend fun deleteAccount(idToken: String, userId: String)
}