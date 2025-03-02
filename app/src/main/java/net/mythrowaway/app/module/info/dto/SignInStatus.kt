package net.mythrowaway.app.module.info.dto

/**
 * Googleサインインの成功時の状態を表すEnum
 * SIGNINは既存アカウントでのサインイン
 * SIGNUPは新規アカウント登録
 */
enum class SignInStatus {
  SIGNIN,  // 既存アカウントでのサインイン
  SIGNUP   // 新規アカウント登録
}