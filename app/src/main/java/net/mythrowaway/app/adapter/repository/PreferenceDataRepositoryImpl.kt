package net.mythrowaway.app.adapter.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import net.mythrowaway.app.adapter.repository.data.TrashJsonData
import net.mythrowaway.app.adapter.repository.data.mapper.TrashJsonDataListMapper
import net.mythrowaway.app.adapter.repository.data.mapper.TrashJsonDataMapper
import net.mythrowaway.app.domain.Trash
import net.mythrowaway.app.service.TrashDataConverter
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.domain.TrashList
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

    override fun saveTrash(trash: Trash) {
        val trashDTO = TrashJsonDataMapper.toData(trash)
        val currentData:String = preference.getString(
            KEY_TRASH_DATA,
            DEFAULT_KEY_TRASH_DATA
        ) ?: DEFAULT_KEY_TRASH_DATA
        val trashJsonDataList:ArrayList<TrashJsonData>  = TrashJsonDataListMapper.fromJson(currentData).filter {
            it.id != trashDTO.id
        }.toCollection(ArrayList())
        trashJsonDataList.add(trashDTO)
        Log.d(this.javaClass.simpleName,"Save trash -> ${TrashJsonDataListMapper.toJson(trashJsonDataList)}")
        save(KEY_TRASH_DATA, TrashJsonDataListMapper.toJson(trashJsonDataList))
    }

    override fun findTrashById(id: String): Trash? {
        val allTrash: TrashList = getAllTrash()
        try {
            return allTrash.trashList.first { trash ->
                trash.id == id
            }
        } catch(e: NoSuchElementException) {
            Log.e(this.javaClass.simpleName,"Not found trash -> id=$id")
            return null
        }
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

    override fun deleteTrash(trash: Trash) {
        val trashList: TrashList = getAllTrash()
        val newTrashList = trashList.trashList.filter { t ->
            t.id != trash.id
        }
        save(KEY_TRASH_DATA, TrashJsonDataListMapper.toJson(newTrashList.map { TrashJsonDataMapper.toData(it) }))
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

    override fun getAllTrash(): TrashList {
        val currentData:String = preference.getString(
            KEY_TRASH_DATA,
            DEFAULT_KEY_TRASH_DATA
        ) ?: DEFAULT_KEY_TRASH_DATA

        Log.i(this.javaClass.simpleName,"Get All Trash Schedule -> $currentData")
        val trashJsonDataList:List<TrashJsonData> = TrashJsonDataListMapper.fromJson(currentData)
        return TrashList(trashJsonDataList.map { TrashJsonDataMapper.toTrash(it) })
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

    override fun importScheduleList(trashList: TrashList) {
        save(KEY_TRASH_DATA, TrashJsonDataListMapper.toJson(trashList.trashList.map { TrashJsonDataMapper.toData(it) }))
    }
}
