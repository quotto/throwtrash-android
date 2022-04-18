package net.mythrowaway.app.adapter.repository

import android.text.TextUtils
import android.util.Log
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import net.mythrowaway.app.adapter.TrashDataConverter
import net.mythrowaway.app.domain.AccountLinkInfo
import net.mythrowaway.app.domain.RegisteredData
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.usecase.IAPIAdapter
import javax.inject.Inject

class UpdateParams {
    @JsonProperty("id")
    var id: String = ""
    @JsonProperty("description")
    var description: String = ""
    @JsonProperty("platform")
    var platform: String = ""
}
class RegisterParams {
    @JsonProperty("description")
    var description: String = ""
    @JsonProperty("platform")
    var platform: String = ""
}

class APIAdapterImpl (
    private val mEndpoint: String,
    private val mAccountLinkEndpoint: String): IAPIAdapter, TrashDataConverter() {

    override fun sync(id: String): Pair<ArrayList<TrashData>, Long>? {
        Log.d(this.javaClass.simpleName, "sync: id=$id(@$mEndpoint)")

        val (_, response, result) = "$mEndpoint/sync?id=$id".httpGet().responseJson()
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

    override fun update(id: String, scheduleList: ArrayList<TrashData>): Long? {
        Log.d(this.javaClass.simpleName,"update -> id=$id(@$mEndpoint)")
        val updateParams = UpdateParams().apply {
            this.id = id
            this.description = trashListToJson(scheduleList)
            this.platform = "android"
        }
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)

        val (_,response,result) = Fuel.post("$mEndpoint/update").jsonBody(mapper.writeValueAsString(updateParams)).responseJson()
        return when(result) {
            is Result.Success -> {
                when (response.statusCode) {
                    200 -> {
                        Log.d(this.javaClass.simpleName, "update result -> ${response.body()}")
                        result.get().obj().get("timestamp") as Long
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

    override fun register(scheduleList: ArrayList<TrashData>): Pair<String, Long>? {
        Log.d(this.javaClass.simpleName, "register -> $scheduleList(@$mEndpoint)")
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
                        Pair(
                            result.get().obj().get("id") as String,
                            result.get().obj().get("timestamp") as Long
                        )
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

    override fun publishActivationCode(id: String): String? {
        Log.d(this.javaClass.simpleName,"publish activation code -> id=$id(@$mEndpoint)")
        val (_,response,result) = "$mEndpoint/publish_activation_code?id=$id".httpGet().responseJson()
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

    override fun activate(code:String): RegisteredData? {
        Log.d(this.javaClass.simpleName,"activate -> code=$code(@$mEndpoint)")
        val (_,response,result) = "$mEndpoint/activate?code=$code".httpGet().responseJson()
        return when(result) {
            is Result.Success -> {
                when (response.statusCode) {
                    200 -> {
                        Log.d(
                            this.javaClass.simpleName,
                            "activate code response -> ${response.body()}"
                        )
                        RegisteredData().apply {
                            id = result.get().obj().get("id") as String
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

    private fun doAccountLink(id: String, type: String): AccountLinkInfo? {
        val (_, response, result) = "${mAccountLinkEndpoint}/start_link?id=${id}&platform=${type}".httpGet().responseJson()
        return when (result) {
            is Result.Success -> {
                when (response.statusCode) {
                    200 -> {
                        val url = result.get().obj().getString("url")
                        Log.d(
                            this.javaClass.simpleName,
                            "return url -> $url"
                        )
                        Log.d(
                            this.javaClass.simpleName,
                            "response:\n${result.get().obj()}"
                        )
                        var cookieData: String? = null
                        response.headers["Set-Cookie"].forEach{
                            if(it.indexOf("throwaway-session") >= 0) {
                                cookieData = it;
                                return@forEach
                            }
                        }
                        cookieData?.let {
                            val cookieEnd = if(it.indexOf(";") >=0) { it.indexOf(";")} else {it.length}
                            val cookie = it.substring(
                                it.indexOf("=")+1,
                                cookieEnd
                            )
                            val cookieKey = it.substring(
                                0,it.indexOf("=")
                            )
                            AccountLinkInfo().apply{
                                sessionId = cookieKey
                                sessionValue = cookie
                                linkUrl = url
                            }
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

    override fun accountLink(id: String): AccountLinkInfo? {
        return doAccountLink(id,"android")
    }

    override fun accountLinkAsWeb(id: String): AccountLinkInfo? {
        return doAccountLink(id, "web")
    }
}