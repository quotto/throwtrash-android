package net.mythrowaway.app.domain.trash.infra

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import net.mythrowaway.app.domain.trash.infra.data.TrashJsonData
import net.mythrowaway.app.domain.trash.infra.data.mapper.TrashJsonDataListMapper
import net.mythrowaway.app.domain.trash.infra.data.mapper.TrashJsonDataMapper
import net.mythrowaway.app.domain.trash.entity.Trash
import net.mythrowaway.app.domain.trash.entity.TrashList
import net.mythrowaway.app.domain.trash.usecase.TrashRepositoryInterface
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class PreferenceTrashRepositoryImpl @Inject constructor(private val context: Context):
    TrashRepositoryInterface {
    private val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    companion object {
        const val KEY_TRASH_DATA = "KEY_TRASH_DATA"
        const val DEFAULT_KEY_TRASH_DATA = "[]"
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

    override fun getAllTrash(): TrashList {
        val currentData:String = preference.getString(
            KEY_TRASH_DATA,
            DEFAULT_KEY_TRASH_DATA
        ) ?: DEFAULT_KEY_TRASH_DATA

        Log.i(this.javaClass.simpleName,"Get All Trash Schedule -> $currentData")
        val trashJsonDataList:List<TrashJsonData> = TrashJsonDataListMapper.fromJson(currentData)
        return TrashList(trashJsonDataList.map { TrashJsonDataMapper.toTrash(it) })
    }

    override fun importScheduleList(trashList: TrashList) {
        save(KEY_TRASH_DATA, TrashJsonDataListMapper.toJson(trashList.trashList.map { TrashJsonDataMapper.toData(it) }))
    }
}
