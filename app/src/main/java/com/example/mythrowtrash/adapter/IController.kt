package com.example.mythrowtrash.adapter


interface IEditController {
    fun checkOtherText(text: String, view: IEditView)
    fun addTrashSchedule()
    fun saveTrashData(viewModel: EditViewModel)
    fun deleteSchedule(removed_index:Int)
    fun loadTrashData(id: Int?)
}

interface ICalendarController {
    suspend fun generateCalendarFromPositionAsync(position: Int)
}