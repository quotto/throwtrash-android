package net.mythrowaway.app.module.migration.infra

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result

interface MigrationApiInterface {
  fun signUp(userId: String, idToken: String): Long
  fun updateTrashScheduleTimestamp(userId: String): Long
}
class MigrationApiImplInterface(private val mEndpoint: String,): MigrationApiInterface {
  override fun signUp(userId: String, idToken: String): Long {
    Log.d(this.javaClass.simpleName, "migration: sign up, user_id=$userId(@$mEndpoint")
    val (_, response, result) = Fuel.post("$mEndpoint/migration/signup", parameters = listOf(Pair("user_id", userId))).header(map = mapOf(
      "Authorization" to idToken)).responseJson()
    return when(result) {
      is Result.Success -> {
        when(response.statusCode) {
          200 -> {
            val timestamp = result.get().obj().get("timestamp") as Long
            Log.d(this.javaClass.simpleName, "sign up timestamp -> $timestamp")
            timestamp
          } else -> {
            Log.e(this.javaClass.simpleName, response.responseMessage)
            -1
          }
        }
      }
      is Result.Failure -> {
        Log.e(this.javaClass.simpleName, response.responseMessage)
        -1
      }
    }
  }
  override fun updateTrashScheduleTimestamp(userId: String): Long {
    Log.d(this.javaClass.simpleName, "migration: update trash schedule timestamp, user_id=$userId(@$mEndpoint")
    val (_, response, result) = "$mEndpoint/migration/v2?user_id=$userId".httpGet().responseJson()
    return when(result) {
      is Result.Success -> {
        when(response.statusCode) {
          200 -> {
            val timestamp = result.get().obj().get("timestamp") as Long
            Log.d(this.javaClass.simpleName, "updated timestamp -> $timestamp")
            timestamp
          } else -> {
            Log.e(this.javaClass.simpleName, response.responseMessage)
            -1
          }
        }
      }
      is Result.Failure -> {
        Log.e(this.javaClass.simpleName, response.responseMessage)
        -1
      }
    }
  }
}