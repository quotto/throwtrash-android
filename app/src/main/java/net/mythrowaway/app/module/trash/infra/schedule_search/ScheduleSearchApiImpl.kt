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

class ScheduleSearchApiImpl(
  private val endpoint: String,
  private val apiKey: String
) : ScheduleSearchApiInterface {
  override fun search(request: ScheduleSearchRequest): ScheduleSearchResponse {
    if (endpoint.isBlank() || apiKey.isBlank()) {
      Log.e(this.javaClass.simpleName, "Schedule search API is not configured. endpoint: $endpoint, apiKey: $apiKey")
      return ScheduleSearchResponse(trashes = emptyList(), message = "Schedule search API is not configured.")
    }
    val body = mapOf(
      "address" to request.address,
      "postal_code" to request.postalCode
    ).filterValues { !it.isNullOrBlank() }
    val mapper = ObjectMapper()
    val (_, response, result) = Fuel.post("${endpoint.trimEnd('/')}/search")
      .header("x-api-key" to apiKey)
      .header("Content-Type" to "application/json")
      .timeout(300_000)
      .timeoutRead(300_000)
      .jsonBody(mapper.writeValueAsString(body))
      .responseJson()

    return when (result) {
      is Result.Success -> {
        if (response.statusCode == 200) {
          parseResponse(result.get().obj())
        } else {
          Log.e(this.javaClass.simpleName, "Schedule search failed: ${response.statusCode}")
          throw IllegalStateException("Schedule search failed: ${response.statusCode}")
        }
      }
      is Result.Failure -> {
        Log.e(this.javaClass.simpleName, result.getException().stackTraceToString())
        throw result.getException()
      }
    }
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
      message = obj.optString("message").ifBlank { null }
    )
  }
}
