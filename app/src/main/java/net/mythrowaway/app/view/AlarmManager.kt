package net.mythrowaway.app.view

import net.mythrowaway.app.usecase.dto.AlarmTrashDTO

interface AlarmManager {
    fun showAlarmMessage(notifyTrashList: List<AlarmTrashDTO>)
    fun setAlarm(hourOfDay: Int, minute: Int)

    fun cancelAlarm()
}