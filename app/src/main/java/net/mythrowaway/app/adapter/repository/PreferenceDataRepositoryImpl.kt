package net.mythrowaway.app.adapter.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import net.mythrowaway.app.service.TrashDataConverter
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.usecase.DataRepositoryInterface
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class PreferenceDataRepositoryImpl @Inject constructor(private val context: Context): DataRepositoryInterface, TrashDataConverter() {
    private val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    companion object {
        const val KEY_TRASH_DATA = "KEY_TRASH_DATA"
        const val DEFAULT_KEY_TRASH_DATA = "[]"
    }

    override fun saveTrashData(trashData: TrashData) {
        trashData.id = Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis.toString()
        val currentData:String = preference.getString(
            KEY_TRASH_DATA,
            DEFAULT_KEY_TRASH_DATA
        ) ?: DEFAULT_KEY_TRASH_DATA
        val trashList:ArrayList<TrashData>  = jsonToTrashList(currentData)
        trashList.add(trashData)
        Log.i(this.javaClass.simpleName,"Save trash data $trashList")
        save(KEY_TRASH_DATA,trashListToJson(trashList))
    }

    override fun updateTrashData(trashData: TrashData) {
        Log.i(this.javaClass.simpleName,"Update trash data -> $trashData")
        val currentData:String = preference.getString(
            KEY_TRASH_DATA,
            DEFAULT_KEY_TRASH_DATA
        ) ?: DEFAULT_KEY_TRASH_DATA
        val trashList:ArrayList<TrashData> = jsonToTrashList(currentData)
        trashList.forEachIndexed {_, data->
            if(data.id == trashData.id) {
                data.id = trashData.id
                data.schedules = trashData.schedules
                data.type = trashData.type
                data.trash_val = trashData.trash_val
                data.excludes = trashData.excludes
                return@forEachIndexed
            }
        }
        save(KEY_TRASH_DATA,trashListToJson(trashList))
    }

    override fun deleteTrashData(id: String) {
        Log.i(this.javaClass.simpleName,"Delete trash data -> id=$id")
        val currentData:String = preference.getString(
            KEY_TRASH_DATA,
            DEFAULT_KEY_TRASH_DATA
        ) ?: DEFAULT_KEY_TRASH_DATA
        val trashList:ArrayList<TrashData> = jsonToTrashList(currentData)
        val deleteData = trashList.filter{trashData ->
            trashData.id == id
        }
        deleteData.forEach {
            trashList.remove(it)
        }
        save(KEY_TRASH_DATA,trashListToJson(trashList))
    }

    private fun save(key:String, stringData: String) {
        Log.d(this.javaClass.simpleName,"Save Data -> $key=$stringData")
        preference.edit().apply {
            putString(key, stringData)
            apply()
        }
    }

    override fun getAllTrashSchedule(): ArrayList<TrashData> {
        val currentData:String = preference.getString(
            KEY_TRASH_DATA,
            DEFAULT_KEY_TRASH_DATA
        ) ?: DEFAULT_KEY_TRASH_DATA

        Log.i(this.javaClass.simpleName,"Get All Trash Schedule -> $currentData")
        return jsonToTrashList(currentData)
    }

    override fun getTrashData(id: String): TrashData? {
        val allTrashData:ArrayList<TrashData> = getAllTrashSchedule()
        allTrashData.forEach { trashData ->
            if(trashData.id == id) {
                Log.d(this.javaClass.simpleName,"Get trash data -> id=$id, $trashData")
                return trashData
            }
        }
        Log.e(this.javaClass.simpleName,"Not found trash data -> id=$id")
        return null
    }

    override fun importScheduleList(scheduleList: ArrayList<TrashData>) {
        save(KEY_TRASH_DATA, trashListToJson(scheduleList))
    }
}
