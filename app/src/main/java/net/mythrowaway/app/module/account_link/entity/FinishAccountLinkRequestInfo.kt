package net.mythrowaway.app.module.account_link.entity

class FinishAccountLinkRequestInfo(val token: String, val redirectUri: String) {
  init {
    if (token.isEmpty()) {
      throw IllegalArgumentException("Token is empty")
    }
    if (redirectUri.isEmpty()) {
      throw IllegalArgumentException("Redirect URI is empty")
    }
  }
}