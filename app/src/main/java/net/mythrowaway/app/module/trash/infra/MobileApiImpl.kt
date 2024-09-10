package net.mythrowaway.app.module.trash.infra

import android.util.Log
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import net.mythrowaway.app.module.trash.infra.model.TrashListApiModelMapper
import net.mythrowaway.app.module.trash.entity.trash.TrashList
import net.mythrowaway.app.module.trash.entity.sync.RegisteredInfo
import net.mythrowaway.app.module.trash.entity.sync.RemoteTrash
import net.mythrowaway.app.module.trash.usecase.MobileApiInterface

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

class UpdateResult(val statusCode: Int, val timestamp: Long)

class MobileApiImpl (
    private val mEndpoint: String,
): MobileApiInterface {

    override fun getRemoteTrash(userId: String): RemoteTrash {
        Log.d(this.javaClass.simpleName, "sync: user_id=$userId(@$mEndpoint)")

        val (_, response, result) = "$mEndpoint/sync?user_id=$userId".httpGet().responseJson()
        return when (result) {
            is Result.Success -> {
                when (response.statusCode) {
                    200 -> {
                        Log.d(this.javaClass.simpleName, "sync result -> ${response.body()}")
                        val obj = result.get().obj()
                        val description: String = obj.get("description") as String
                        val timestamp: Long = obj.get("timestamp") as Long
                        RemoteTrash(
                            _trashList =
                                TrashListApiModelMapper.toTrashList(
                                    TrashListApiModelMapper.fromJson(description)
                                ),
                            _timestamp = timestamp
                        )
                    }
                    else -> {
                        Log.e(this.javaClass.simpleName, response.responseMessage)
                        throw Exception("Failed to get remote trash: ${response.responseMessage}")
                    }
                }
            }
            is Result.Failure -> {
                Log.e(this.javaClass.simpleName, result.getException().stackTraceToString())
                throw result.getException()
            }
        }
    }

    override fun update(userId: String, trashList: TrashList, currentTimestamp: Long): UpdateResult {
        Log.d(this.javaClass.simpleName,"update -> id=$userId(@$mEndpoint)")
        val updateParams = UpdateParams().apply {
            this.id = userId
            this.description =
                TrashListApiModelMapper.toJson(
                    TrashListApiModelMapper.toTrashApiModelList(trashList)
                )
            this.platform = "android"
            this.currentTimestamp = currentTimestamp
        }
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)

        val (_,response,result) = Fuel.post("$mEndpoint/update").jsonBody(mapper.writeValueAsString(updateParams)).responseJson()
        when(result) {
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

    override fun register(trashList: TrashList): RegisteredInfo {
        Log.d(this.javaClass.simpleName, "register -> $mEndpoint")
        val registerParams = RegisterParams().apply {
            description = TrashListApiModelMapper.toJson(TrashListApiModelMapper.toTrashApiModelList(trashList))
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
                        RegisteredInfo(
                            _userId = result.get().obj().get("id") as String,
                            _latestTrashListUpdateTimestamp = result.get().obj().get("timestamp") as Long
                        )
                    }
                    else -> {
                        Log.e(this.javaClass.simpleName, response.responseMessage)
                        throw Exception("Failed to register, ${response.responseMessage}")
                    }
                }
            }
            is Result.Failure -> {
                Log.e(this.javaClass.simpleName, result.getException().stackTraceToString())
                throw Exception("Failed to register, ${result.getException().message}")
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

    override fun activate(code:String, userId: String): RemoteTrash {
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
                        RemoteTrash(
                            _trashList =
                                TrashListApiModelMapper.toTrashList(
                                    TrashListApiModelMapper.fromJson(result.get().obj().get("description") as String)
                                ),
                            _timestamp = result.get().obj().get("timestamp") as Long
                        )
                    }
                    else -> {
                        Log.e(this.javaClass.simpleName, response.responseMessage)
                        throw Exception("Failed to activate: ${response.responseMessage}")
                    }
                }
            }
            is Result.Failure -> {
                Log.e(this.javaClass.simpleName, result.getException().stackTraceToString())
                throw Exception("Failed to activate: ${result.getException().message}")
            }
        }
    }

}