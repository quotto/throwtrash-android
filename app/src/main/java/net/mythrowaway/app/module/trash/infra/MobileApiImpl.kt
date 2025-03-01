package net.mythrowaway.app.module.trash.infra

import android.util.Log
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import net.mythrowaway.app.module.info.infra.AuthManager
import net.mythrowaway.app.module.trash.infra.model.TrashListApiModelMapper
import net.mythrowaway.app.module.trash.entity.trash.TrashList
import net.mythrowaway.app.module.trash.entity.sync.RegisteredInfo
import net.mythrowaway.app.module.trash.entity.sync.RemoteTrash
import net.mythrowaway.app.module.trash.infra.model.RegisterParams
import net.mythrowaway.app.module.trash.infra.model.UpdateParams
import net.mythrowaway.app.module.trash.usecase.MobileApiInterface
import javax.inject.Inject

class UpdateResult(val statusCode: Int, val timestamp: Long)

class MobileApiImpl @Inject constructor (
    private val mEndpoint: String,
    private val mAuthManager: AuthManager
): MobileApiInterface {

    override suspend fun getRemoteTrash(userId: String): RemoteTrash {
        Log.d(this.javaClass.simpleName, "sync: user_id=$userId(@$mEndpoint)")

        val (_, response, result) = Fuel.get("$mEndpoint/sync?user_id=$userId").header(
            getAuthorizationHeader(userId)
        ).responseJson()
        return when (result) {
            is Result.Success -> {
                when (response.statusCode) {
                    200 -> {
                        Log.d(this.javaClass.simpleName, "sync result -> ${result.get().obj()}")
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

    override suspend fun update(userId: String, trashList: TrashList, currentTimestamp: Long): UpdateResult {
        val endPoint = "$mEndpoint/update"
        Log.d(this.javaClass.simpleName, "update -> user_id=$userId(@$endPoint)")
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

        Log.d(this.javaClass.simpleName, "update params -> ${mapper.writeValueAsString(updateParams)}")
        val (_, response, result) = Fuel.post("$mEndpoint/update").header(
            getAuthorizationHeader(userId)
        ).jsonBody(mapper.writeValueAsString(updateParams)).responseJson()
        when(result) {
            is Result.Success -> {
                when (response.statusCode) {
                    200 -> {
                        Log.d(this.javaClass.simpleName, "update result -> ${result.get().obj()}")
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

    override suspend fun register(): RegisteredInfo {
        Log.d(this.javaClass.simpleName, "register -> $mEndpoint/register")
        val registerParams = RegisterParams().apply {
            platform = "android"
        }
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        val token = mAuthManager.getIdToken()
        if (token == null) {
            Log.e(this.javaClass.simpleName, "Failed to get ID token")
            throw Exception("Failed to get ID token")
        }
        val (_, response, result) = Fuel.post("$mEndpoint/register").header(
            getAuthorizationHeader(null)
        ).jsonBody(mapper.writeValueAsString(registerParams)).responseJson()
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

    override suspend fun publishActivationCode(id: String): String {
        Log.d(this.javaClass.simpleName,"publish activation code -> user_id=$id(@$mEndpoint)")
        val (_,response,result) = Fuel.get("$mEndpoint/publish_activation_code?user_id=$id").header(
            getAuthorizationHeader(id)
        ).responseJson()
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

    override suspend fun activate(code:String, userId: String): RemoteTrash {
        Log.d(this.javaClass.simpleName,"activate -> code=$code, user_id=$userId(@$mEndpoint)")
        val (_,response,result) = Fuel.get("$mEndpoint/activate?code=$code&user_id=$userId").header(
            getAuthorizationHeader(userId)
        ).responseJson()
        return when(result) {
            is Result.Success -> {
                when (response.statusCode) {
                    200 -> {
                        Log.d(
                            this.javaClass.simpleName,
                            "activate code response -> ${result.get().obj().get("description")}"
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

    private suspend fun getAuthorizationHeader(userId: String?): Map<String, String> {
        val token = mAuthManager.getIdToken()
        if (token == null) {
            Log.e(this.javaClass.simpleName, "Failed to get ID token")
            throw Exception("Failed to get ID token")
        }
        val headers =  hashMapOf(
            "Content-Type" to "application/json",
            "Authorization" to "$token",
        )
        if (userId != null) {
            headers["X-TRASH-USERID"] = userId
        }
        Log.d(this.javaClass.simpleName, "headers -> $headers")
        return headers.toMap()
    }

}