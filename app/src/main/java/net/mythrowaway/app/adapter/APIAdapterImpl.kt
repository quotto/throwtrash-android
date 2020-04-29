package net.mythrowaway.app.adapter

import android.util.Log
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import net.mythrowaway.app.domain.RegisteredData
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.usecase.IAPIAdapter

class APIAdapterImpl: IAPIAdapter, TrashDataConverter() {
    private val mEndpoint = "https://test-mobile.mythrowaway.net/test"
    private val mBackendEndpoint = "https://backend.mythrowaway.net/dev"
    inner class UpdateParams {
        @JsonProperty("id")
        var id: String = ""
        @JsonProperty("description")
        var description: String = ""
        @JsonProperty("platform")
        var platform: String = ""
    }
    inner class RegisterParams {
        @JsonProperty("description")
        var description: String = ""
        @JsonProperty("platform")
        var platform: String = ""
    }
    override fun sync(id: String): Pair<ArrayList<TrashData>, Long>? {
        Log.d(this.javaClass.simpleName,"sync: id=$id")
        val (_,response,result) = "$mEndpoint/sync?id=$id".httpGet().responseJson()
        return when(response.statusCode) {
            200 -> {
                Log.d(this.javaClass.simpleName,"sync result -> ${response.body()}")
                val obj = result.get().obj()
                val schedule: String = obj.get("description") as String
                val timestamp: Long = obj.get("timestamp") as Long
                Pair(jsonToTrashList(schedule),timestamp)
            }
            else -> {
                Log.e(this.javaClass.simpleName,"${response.responseMessage}")
                null
            }
        }
    }

    override fun update(id: String, scheduleList: ArrayList<TrashData>): Long? {
        Log.e(this.javaClass.simpleName,"update -> id: $id")
        val updateParams = UpdateParams().apply {
            this.id = id
            this.description = trashListToJson(scheduleList)
            this.platform = "android"
        }
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)

        val (_,response,result) = Fuel.post("$mEndpoint/update").jsonBody(mapper.writeValueAsString(updateParams)).responseJson()
        return when(response.statusCode) {
            200 -> {
                Log.d(this.javaClass.simpleName,"update result -> ${response.body()}")
                result.get().obj().get("timestamp") as Long
            }
            else -> {
                Log.e(this.javaClass.simpleName,"${response.responseMessage}")
                null
            }
        }
    }

    override fun register(scheduleList: ArrayList<TrashData>): Pair<String, Long>? {
        Log.d(this.javaClass.simpleName, "register -> $scheduleList")
        val registerParams = RegisterParams().apply {
            val mapper = ObjectMapper()
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
            description = mapper.writeValueAsString(scheduleList)
            platform = "android"
        }
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        val (_,response,result) = Fuel.post("$mEndpoint/register").jsonBody(mapper.writeValueAsString(registerParams)).responseJson()
        return when(response.statusCode) {
            200 -> {
                Log.d(this.javaClass.simpleName,"register response -> ${response.body()}")
                Pair(result.get().obj().get("id") as String, result.get().obj().get("timestamp") as Long)
            }
            else -> {
                Log.e(this.javaClass.simpleName,"${response.responseMessage}")
                null
            }
        }
    }

    override fun publishActivationCode(id: String): String? {
        Log.d(this.javaClass.simpleName,"publish activation code -> id:$id")
        val (_,response,result) = "$mEndpoint/publish_activation_code?id=$id".httpGet().responseJson()
        return when(response.statusCode) {
            200 -> {
                Log.d(this.javaClass.simpleName,"publish activation code response -> ${response.body()}")
                result.get().obj().get("code") as String
            }
            else -> {
                Log.e(this.javaClass.simpleName,"${response.responseMessage}")
                null
            }
        }
    }

    override fun activate(code:String): RegisteredData? {
        Log.d(this.javaClass.simpleName,"activate -> code:$code")
        val (_,response,result) = "$mEndpoint/activate?code=$code".httpGet().responseJson()
        return when(response.statusCode) {
            200 -> {
                Log.d(this.javaClass.simpleName,"activate code response -> ${response.body()}")
                RegisteredData().apply {
                    id = result.get().obj().get("id") as String
                    scheduleList = jsonToTrashList(result.get().obj().get("description") as String)
                    timestamp = result.get().obj().get("timestamp") as Long
                }
            }
            else -> {
                Log.e(this.javaClass.simpleName,"${response.responseMessage}")
                null
            }
        }
    }
}