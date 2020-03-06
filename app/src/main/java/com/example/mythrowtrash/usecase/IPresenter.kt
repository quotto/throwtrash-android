package com.example.mythrowtrash.usecase

import com.example.mythrowtrash.domain.TrashData

interface IEditPresenter {
    fun complete(trashData: TrashData)
    fun showOtherTextError(resultCode: Int)
    fun addTrashSchedule(scheduleCount:Int)
    fun deleteTrashSchedule(delete_index: Int, scheduleCount:Int)
    fun loadTrashData(trashData: TrashData)
}

interface ICalendarPresenter {
    fun setCalendar(year:Int, month:Int, trashList:Array<ArrayList<String>>, dateList:ArrayList<Int>)
}

interface IScheduleListPresenter {
    fun showScheduleList(scheduleList: ArrayList<TrashData>)
}