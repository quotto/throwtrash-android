package net.mythrowaway.app.module.trash.infra

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import net.mythrowaway.app.module.trash.infra.data.ExcludeDayOfMonthJsonData
import net.mythrowaway.app.module.trash.infra.data.TrashJsonData
import net.mythrowaway.app.module.trash.infra.data.TrashScheduleJsonData
import net.mythrowaway.app.module.trash.infra.data.mapper.TrashScheduleJsonDataMapper
import net.mythrowaway.app.module.trash.infra.data.mapper.TrashJsonDataMapper
import net.mythrowaway.app.module.trash.entity.trash.ExcludeDayOfMonth
import net.mythrowaway.app.module.trash.entity.trash.ExcludeDayOfMonthList
import net.mythrowaway.app.module.trash.entity.trash.Trash
import net.mythrowaway.app.module.trash.entity.trash.TrashList
import net.mythrowaway.app.module.trash.usecase.TrashRepositoryInterface
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
        const val DEFAULT_KEY_TRASH_DATA = "{\"trashData\":[],\"globalExcludes\":[]}"
    }

    override fun saveTrash(trash: Trash) {
        val trashDTO = TrashJsonDataMapper.toData(trash)
        val currentData:String = preference.getString(
            KEY_TRASH_DATA,
            DEFAULT_KEY_TRASH_DATA
        ) ?: DEFAULT_KEY_TRASH_DATA
        val currentSchedule = TrashScheduleJsonDataMapper.fromJson(currentData)
        val trashJsonDataList:ArrayList<TrashJsonData>  = currentSchedule.trashData.filter {
            it.id != trashDTO.id
        }.toCollection(ArrayList())
        trashJsonDataList.add(trashDTO)
        val updateSchedule = TrashScheduleJsonData(
            _trashData = trashJsonDataList,
            _globalExcludes = currentSchedule.globalExcludes
        )
        Log.d(this.javaClass.simpleName,"Save trash -> ${TrashScheduleJsonDataMapper.toJson(updateSchedule)}")
        save(KEY_TRASH_DATA, TrashScheduleJsonDataMapper.toJson(updateSchedule))
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
        val updateSchedule = TrashScheduleJsonData(
            _trashData = newTrashList.map { TrashJsonDataMapper.toData(it) },
            _globalExcludes = trashList.globalExcludeDayOfMonthList.members.map { exclude ->
                ExcludeDayOfMonthJsonData(
                    _month = exclude.month,
                    _date = exclude.dayOfMonth
                )
            }
        )
        save(KEY_TRASH_DATA, TrashScheduleJsonDataMapper.toJson(updateSchedule))
    }

    override fun getAllTrash(): TrashList {
        val currentData:String = preference.getString(
            KEY_TRASH_DATA,
            DEFAULT_KEY_TRASH_DATA
        ) ?: DEFAULT_KEY_TRASH_DATA

        Log.i(this.javaClass.simpleName,"Get All Trash Schedule -> $currentData")
        val trashSchedule = TrashScheduleJsonDataMapper.fromJson(currentData)
        val globalExcludes = trashSchedule.globalExcludes.map { exclude ->
            ExcludeDayOfMonth(exclude.month, exclude.date)
        }
        return TrashList(
            trashSchedule.trashData.map { TrashJsonDataMapper.toTrash(it) },
            ExcludeDayOfMonthList(globalExcludes.toMutableList())
        )
    }

    override fun replaceTrashList(trashList: TrashList) {
        val updateSchedule = TrashScheduleJsonData(
            _trashData = trashList.trashList.map { TrashJsonDataMapper.toData(it) },
            _globalExcludes = trashList.globalExcludeDayOfMonthList.members.map { exclude ->
                ExcludeDayOfMonthJsonData(
                    _month = exclude.month,
                    _date = exclude.dayOfMonth
                )
            }
        )
        save(KEY_TRASH_DATA, TrashScheduleJsonDataMapper.toJson(updateSchedule))
    }
    private fun save(key:String, stringData: String) {
        Log.d(this.javaClass.simpleName,"Save Data -> $key=$stringData")
        preference.edit().apply {
            putString(key, stringData)
            apply()
        }
    }

}
