package net.mythrowaway.app.module.account_link.infra

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import net.mythrowaway.app.module.account_link.usecase.AccountLinkApiInterface
import net.mythrowaway.app.module.account_link.dto.StartAccountLinkResponse
import net.mythrowaway.app.module.info.infra.AuthManager
import javax.inject.Inject

class AccountLinkApi @Inject constructor (
  private val mEndpoint: String,
  private val authManager: AuthManager
): AccountLinkApiInterface {
  override suspend fun accountLink(id: String): StartAccountLinkResponse {
    return doAccountLink(id,"android")
  }

  override suspend fun accountLinkAsWeb(id: String): StartAccountLinkResponse {
    return doAccountLink(id, "web")
  }
  private suspend fun doAccountLink(id: String, type: String): StartAccountLinkResponse {
    Log.d(javaClass.simpleName, "Start account link -> \"${mEndpoint}/start_link?id=${id}&platform=${type}\"")
    val idToken = authManager.getIdToken()
      ?: throw Exception("Failed to get ID token: id token is null.")
    Fuel.get("${mEndpoint}/start_link?user_id=${id}&platform=${type}").header(
      mapOf(
        "Authorization" to idToken,
        "X-TRASH-USERID" to id,
      )
    )
    val (_, response, result) = "${mEndpoint}/start_link?user_id=${id}&platform=${type}".httpGet().responseJson()
    return when (result) {
      is Result.Success -> {
        when (response.statusCode) {
          200 -> {
            Log.d(
              this.javaClass.simpleName,
              "response:\n${result.get().obj()}"
            )
            val url = result.get().obj().getString("url")
            val token = result.get().obj().get("token") as String
            StartAccountLinkResponse(url = url, token = token)
          }
          else -> {
            throw Exception("Failed to start account link: ${response.responseMessage}")
          }
        }
      }
      is Result.Failure -> {
        throw Exception(result.getException().stackTraceToString())
      }
    }

  }
}
