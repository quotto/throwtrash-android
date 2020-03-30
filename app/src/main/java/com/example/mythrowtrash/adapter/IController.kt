package com.example.mythrowtrash.adapter


interface IEditController {
    fun checkOtherText(text: String, view: IEditView)
    fun addTrashSchedule()
    fun saveTrashData(viewModel: EditViewModel)
    fun deleteSchedule(removed_index:Int)
    fun loadTrashData(id: String?)
}

interface ICalendarController {
    suspend fun syncData()
    suspend fun generateCalendarFromPositionAsync(position: Int)
}

interface IAlarmController {
    fun loadAlarmConfig()
    fun saveAlarmConfig(viewModel: AlarmViewModel)
    fun alarmToday(year: Int, month: Int, date: Int)
}

interface IPublishCodeController {
    suspend fun publishActivationCode()
}

interface IActivateController {
    suspend fun activate(code: String)
}