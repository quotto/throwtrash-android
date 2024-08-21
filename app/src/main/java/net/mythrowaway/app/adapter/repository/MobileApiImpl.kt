package net.mythrowaway.app.adapter.repository

import android.util.Log
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import net.mythrowaway.app.service.TrashDataConverter
import net.mythrowaway.app.domain.LatestTrashData
import net.mythrowaway.app.domain.RegisteredData
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.usecase.MobileApiInterface
import net.mythrowaway.app.usecase.dto.StartAccountLinkResponse

class UpdateParams {
    @JsonProperty("id")
    var id: String = ""
    @JsonProperty("description")
    var description: String = ""
    @JsonProperty("platform")
    var platform: String = ""
    @JsonProperty("timestamp")
    var currentTimestamp: Long = 0
}
class RegisterParams {
    @JsonProperty("description")
    var description: String = ""
    @JsonProperty("platform")
    var platform: String = ""
}

class UpdateResult(val statusCode: Int, val timestamp: Long) {
}

class MobileApiImpl (
    private val mEndpoint: String,
): MobileApiInterface, TrashDataConverter() {

    override fun sync(id: String): Pair<ArrayList<TrashData>, Long>? {
        Log.d(this.javaClass.simpleName, "sync: user_id=$id(@$mEndpoint)")

        val (_, response, result) = "$mEndpoint/sync?user_id=$id".httpGet().responseJson()
        return when (result) {
            is Result.Success -> {
                when (response.statusCode) {
                    200 -> {
                        Log.d(this.javaClass.simpleName, "sync result -> ${response.body()}")
                        val obj = result.get().obj()
                        val schedule: String = obj.get("description") as String
                        val timestamp: Long = obj.get("timestamp") as Long
                        Pair(jsonToTrashList(schedule), timestamp)
                    }
                    else -> {
                        Log.e(this.javaClass.simpleName, response.responseMessage)
                        null
                    }
                }
            }
            is Result.Failure -> {
                Log.e(this.javaClass.simpleName, result.getException().stackTraceToString())
                null
            }
        }
    }

    override fun update(id: String, scheduleList: ArrayList<TrashData>, currentTimestamp: Long): UpdateResult {
        Log.d(this.javaClass.simpleName,"update -> id=$id(@$mEndpoint)")
        val updateParams = UpdateParams().apply {
            this.id = id
            this.description = trashListToJson(scheduleList)
            this.platform = "android"
            this.currentTimestamp = currentTimestamp
        }
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)

        val (_,response,result) = Fuel.post("$mEndpoint/update").jsonBody(mapper.writeValueAsString(updateParams)).responseJson()
        return when(result) {
            is Result.Success -> {
                when (response.statusCode) {
                    200 -> {
                        Log.d(this.javaClass.simpleName, "update result -> ${result.get()}")
                        return UpdateResult(response.statusCode,result.get().obj().get("timestamp") as Long)
                    }
                    400 -> {
                        Log.e(this.javaClass.simpleName, response.responseMessage)
                        return UpdateResult(response.statusCode, result.get().obj().get("timestamp") as Long)
                    }
                    else -> {
                        Log.e(this.javaClass.simpleName, response.responseMessage)
                        return UpdateResult(response.statusCode, -1)
                    }
                }
            }
            is Result.Failure -> {
                Log.e(this.javaClass.simpleName, result.getException().stackTraceToString())
                return UpdateResult(response.statusCode, -1)
            }
        }
    }

    override fun register(scheduleList: ArrayList<TrashData>): RegisteredData? {
        Log.d(this.javaClass.simpleName, "register -> $mEndpoint")
        val registerParams = RegisterParams().apply {
            val mapper = ObjectMapper()
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
            description = mapper.writeValueAsString(scheduleList)
            platform = "android"
        }
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        val (_,response,result) = Fuel.post("$mEndpoint/register").jsonBody(mapper.writeValueAsString(registerParams)).responseJson()
        return when(result) {
            is Result.Success -> {
                when(response.statusCode) {
                    200 -> {
                        Log.d(this.javaClass.simpleName, "register response -> ${response.body()}")
                        RegisteredData().apply {
                            id = result.get().obj().get("id") as String
                            timestamp = result.get().obj().get("timestamp") as Long
                        }
                    }
                    else -> {
                        Log.e(this.javaClass.simpleName, response.responseMessage)
                        null
                    }
                }
            }
            is Result.Failure -> {
                Log.e(this.javaClass.simpleName, result.getException().stackTraceToString())
                null
            }
        }
    }

    override fun publishActivationCode(id: String): String {
        Log.d(this.javaClass.simpleName,"publish activation code -> user_id=$id(@$mEndpoint)")
        val (_,response,result) = "$mEndpoint/publish_activation_code?user_id=$id".httpGet().responseJson()
        return when(result) {
            is Result.Success -> {
                when (response.statusCode) {
                    200 -> {
                        Log.d(
                            this.javaClass.simpleName,
                            "publish activation code response -> ${response.body()}"
                        )
                        result.get().obj().get("code") as String
                    }
                    else -> {
                        Log.e(this.javaClass.simpleName, response.responseMessage)
                        throw Exception("Failed to publish activation code")
                    }
                }
            }
            is Result.Failure -> {
                Log.e(this.javaClass.simpleName, result.getException().stackTraceToString())
                throw Exception("Failed to publish activation code")
            }
        }
    }

    override fun activate(code:String, userId: String): LatestTrashData? {
        Log.d(this.javaClass.simpleName,"activate -> code=$code, user_id=$userId(@$mEndpoint)")
        val (_,response,result) = "$mEndpoint/activate?code=$code&user_id=$userId".httpGet().responseJson()
        return when(result) {
            is Result.Success -> {
                when (response.statusCode) {
                    200 -> {
                        Log.d(
                            this.javaClass.simpleName,
                            "activate code response -> ${response.body()}"
                        )
                        LatestTrashData().apply {
                            scheduleList =
                                jsonToTrashList(result.get().obj().get("description") as String)
                            timestamp = result.get().obj().get("timestamp") as Long
                        }
                    }
                    else -> {
                        Log.e(this.javaClass.simpleName, response.responseMessage)
                        null
                    }
                }
            }
            is Result.Failure -> {
                Log.e(this.javaClass.simpleName, result.getException().stackTraceToString())
                null
            }
        }
    }

    private fun doAccountLink(id: String, type: String): StartAccountLinkResponse {
        Log.d(javaClass.simpleName, "Start account link -> \"${mEndpoint}/start_link?id=${id}&platform=${type}\"")
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

    override fun accountLink(id: String): StartAccountLinkResponse {
        return doAccountLink(id,"android")
    }

    override fun accountLinkAsWeb(id: String): StartAccountLinkResponse {
        return doAccountLink(id, "web")
    }
}