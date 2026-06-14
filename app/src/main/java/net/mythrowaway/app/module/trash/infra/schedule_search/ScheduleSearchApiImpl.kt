package net.mythrowaway.app.module.trash.infra.schedule_search

import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import net.mythrowaway.app.module.trash.usecase.ScheduleSearchApiInterface
import net.mythrowaway.app.module.trash.usecase.ScheduleSearchRequest
import org.json.JSONObject
import java.io.InterruptedIOException
import java.net.SocketException
import java.net.SocketTimeoutException

class ScheduleSearchApiImpl(
  private val endpoint: String,
  private val apiKey: String,
  private val maxRetryCount: Int = 2,
  private val retryDelayMillis: Long = 1_000,
  private val sleep: (Long) -> Unit = Thread::sleep
) : ScheduleSearchApiInterface {
  override fun search(request: ScheduleSearchRequest): ScheduleSearchResponse {
    if (endpoint.isBlank() || apiKey.isBlank()) {
      Log.e(this.javaClass.simpleName, "Schedule search API is not configured.")
      return ScheduleSearchResponse(trashes = emptyList(), errorType = ScheduleSearchErrorType.UNKNOWN)
    }
    val body = mapOf(
      "address" to request.address,
      "postal_code" to request.postalCode
    ).filterValues { !it.isNullOrBlank() }
    val mapper = ObjectMapper()
    val requestBody = mapper.writeValueAsString(body)
    var lastException: Exception? = null

    repeat(maxRetryCount + 1) { attempt ->
      val (_, response, result) = Fuel.post("${endpoint.trimEnd('/')}/search")
        .header("x-api-key" to apiKey)
        .header("Content-Type" to "application/json")
        .timeout(300_000)
        .timeoutRead(300_000)
        .jsonBody(requestBody)
        .responseJson()

      when (result) {
        is Result.Success -> {
          return if (response.statusCode in 200..299) {
            parseResponse(result.get().obj())
          } else {
            Log.e(this.javaClass.simpleName, "Schedule search failed: ${response.statusCode}")
            parseResponse(result.get().obj())
          }
        }
        is Result.Failure -> {
          val exception = result.getException()
          val responseBody = response.data.toString(Charsets.UTF_8)
          if (responseBody.isNotBlank()) {
            return parseResponse(JSONObject(responseBody))
          }
          lastException = exception
          if (attempt < maxRetryCount && exception.isRetryableNetworkError()) {
            Log.w(this.javaClass.simpleName, "Schedule search connection failed. Retry ${attempt + 1}/$maxRetryCount.")
            sleep(retryDelayMillis * (attempt + 1))
          } else {
            Log.e(this.javaClass.simpleName, exception.stackTraceToString())
            throw exception
          }
        }
      }
    }
    throw lastException ?: IllegalStateException("Schedule search failed.")
  }

  private fun parseResponse(obj: JSONObject): ScheduleSearchResponse {
    val trashesJson = obj.optJSONArray("trashes")
    val trashes = (0 until (trashesJson?.length() ?: 0)).map { trashIndex ->
      val trashJson = trashesJson!!.getJSONObject(trashIndex)
      val schedulesJson = trashJson.optJSONArray("schedule")
      ScheduleSearchTrashItem(
        type = trashJson.getString("type"),
        trashName = trashJson.optString("trash_name").ifBlank { null },
        schedule = (0 until (schedulesJson?.length() ?: 0)).map { scheduleIndex ->
          val scheduleJson = schedulesJson!!.getJSONObject(scheduleIndex)
          ScheduleSearchScheduleItem(
            type = scheduleJson.getString("type"),
            value = scheduleJson.get("value")
          )
        }
      )
    }
    return ScheduleSearchResponse(
      trashes = trashes,
      message = obj.optString("message").ifBlank { null },
      errorType = if (obj.has("error_type")) {
        ScheduleSearchErrorType.from(obj.optString("error_type"))
      } else {
        ScheduleSearchErrorType.UNKNOWN
      }
    )
  }

  private fun Throwable.isRetryableNetworkError(): Boolean {
    var current: Throwable? = this
    while (current != null) {
      if (
        current is SocketException ||
        current is SocketTimeoutException ||
        current is InterruptedIOException
      ) {
        return true
      }
      current = current.cause
    }
    return false
  }
}
