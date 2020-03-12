package com.example.mythrowtrash.usecase

import com.example.mythrowtrash.domain.AlarmConfig
import com.example.mythrowtrash.domain.TrashData
import kotlin.collections.ArrayList

interface IPersistentRepository {
    fun saveTrashData(trashData: TrashData)
    fun updateTrashData(trashData: TrashData)
    fun deleteTrashData(id: String)
    fun getAllTrashSchedule(): ArrayList<TrashData>
    fun getTrashData(id: String): TrashData?
}

interface IConfigRepository {
    fun getAlarmConfig(): AlarmConfig
    fun saveAlarmConfig(alarmConfig: AlarmConfig)
}