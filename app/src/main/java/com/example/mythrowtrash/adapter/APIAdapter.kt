package com.example.mythrowtrash.adapter

import com.example.mythrowtrash.domain.RegisteredData
import com.example.mythrowtrash.domain.TrashData
import com.example.mythrowtrash.usecase.IAPIAdapter
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson

class APIAdapter: IAPIAdapter,TrashDataConverter() {
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
        println("[MyApp] sync: id=$id")
        val (_,response,result) = "$mEndpoint/sync?id=$id".httpGet().responseJson()
        return when(response.statusCode) {
            200 -> {
                val obj = result.get().obj()
                val schedule: String = obj.get("description") as String
                val timestamp: Long = obj.get("timestamp") as Long
                Pair(jsonToTrashList(schedule),timestamp)
            }
            else -> null
        }
    }

    override fun update(id: String, scheduleList: ArrayList<TrashData>): Long? {
        println("[MyApp] update: id=$id")
        val updateParams:UpdateParams = UpdateParams().apply {
            this.id = id
            this.description = trashListToJson(scheduleList)
            this.platform = "android"
        }
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)

        val (_,response,result) = Fuel.post("$mEndpoint/update").jsonBody(mapper.writeValueAsString(updateParams)).responseJson()
        return when(response.statusCode) {
            200 -> result.get().obj().get("timestamp") as Long
            else -> null
        }
    }

    override fun register(scheduleList: ArrayList<TrashData>): Pair<String, Long>? {
        println("[MyApp] register")
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
            200 -> Pair(result.get().obj().get("id") as String, result.get().obj().get("timestamp") as Long)
            else -> null
        }
    }

    override fun publishActivationCode(id: String): String? {
        println("[MyApp] request publishing code")
        val (_,response,result) = "$mEndpoint/publish_activation_code?id=$id".httpGet().responseJson()
        return when(response.statusCode) {
            200 -> result.get().obj().get("code") as String
            else -> null
        }
    }

    override fun activate(code:String): RegisteredData? {
        val (_,response,result) = "$mEndpoint/activate?code=$code".httpGet().responseJson()
        return when(response.statusCode) {
            200 -> {
                RegisteredData().apply {
                    id = result.get().obj().get("id") as String
                    scheduleList = jsonToTrashList(result.get().obj().get("description") as String)
                    timestamp = result.get().obj().get("timestamp") as Long
                    println("[MyApp] activated: ${result.get().obj()}")
                }
            }
            else -> null
        }
    }

    override fun getAccountLinkUrl(id: String): String? {
        val (_,response,result) = "$mBackendEndpoint/start_link?platform=android&id=$id".httpGet().responseString()
        return when(response.statusCode) {
            200->{
                return result.get()
            }
            else -> null
        }
    }
}