package net.mythrowaway.app.module.account.infra

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.responseJson
import net.mythrowaway.app.module.account.usecase.UserApiInterface
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserApiImpl @Inject constructor(
  private val mEndpoint: String
): UserApiInterface {
  override suspend fun signin(idToken: String): String {
    val endpoint = "$mEndpoint/signin"
    val headers = mapOf(
      "Authorization" to idToken
    )
    Log.d(this.javaClass.simpleName,"request: $endpoint, headers: $headers")
    val (_,response, result) = Fuel.post(endpoint)
      .header(headers)
      .responseJson()
    if (response.statusCode != 200) {
      Log.e(this.javaClass.simpleName,result.get().content)
      Log.e(this.javaClass.simpleName,"${response.statusCode}")
      Log.e(this.javaClass.simpleName,response.responseMessage)
      Log.e(this.javaClass.simpleName,response.data.toString())
      throw Exception("Failed to signin")

    }
    val json = result.get().obj()
    if (!json.has("userId")) {
      throw Exception("Failed to signin: userId not found")
    }
    return json.getString("userId")
  }

  override suspend fun deleteAccount(idToken: String, userId: String) {
    val endpoint = "$mEndpoint/delete"
    val headers = mapOf(
      "Authorization" to idToken,
      "X-TRASH-USERID" to userId
    )
    Log.d(this.javaClass.simpleName,"request: $endpoint, headers: $headers")
    val (_, response, result) = Fuel.delete(endpoint)
      .header(headers)
      .responseJson()

    if (response.statusCode != 204) {
      Log.e(this.javaClass.simpleName, "Unexpected status code: ${response.statusCode}")
      Log.e(this.javaClass.simpleName, response.responseMessage)
      Log.e(this.javaClass.simpleName, response.data.toString())
      throw Exception("Failed to delete account")
    }
    Log.d(this.javaClass.simpleName, "Account deleted successfully")
  }
}