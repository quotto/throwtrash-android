package com.example.mythrowtrash.usecase

import com.example.mythrowtrash.domain.AlarmConfig
import com.example.mythrowtrash.domain.TrashData
import kotlin.collections.ArrayList

interface IPersistentRepository {
    fun saveTrashData(trashData: TrashData)
    fun updateTrashData(trashData: TrashData)
    fun deleteTrashData(id: Int)
    fun getAllTrashSchedule(): ArrayList<TrashData>
    fun incrementCount(): Int
    fun getTrashData(id: Int): TrashData?
}

interface IConfigRepository {
    fun getAlarmConfig(): AlarmConfig
    fun saveAlarmConfig(alarmConfig: AlarmConfig)
}