package net.mythrowaway.app.module.info.dto

enum class GoogleSignInResult {
  SIGNUP,   // 新規登録の場合
  SIGNIN,   // 既存アカウントでのサインインの場合
  FAILURE   // 失敗の場合
}