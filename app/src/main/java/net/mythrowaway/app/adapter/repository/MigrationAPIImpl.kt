package net.mythrowaway.app.adapter.repository

import android.util.Log
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result

interface IMigrationApi {
  fun updateTrashScheduleTimestamp(userId: String): Long
}
class MigrationApiImpl(private val mEndpoint: String): IMigrationApi {
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