package net.mythrowaway.app.domain.alarm.usecase

import net.mythrowaway.app.domain.alarm.entity.AlarmConfig
import net.mythrowaway.app.domain.alarm.dto.AlarmTrashDTO

interface AlarmRepositoryInterface {
  fun getAlarmConfig(): AlarmConfig?
  fun saveAlarmConfig(alarmConfig: AlarmConfig)
}

interface AlarmManager {
  fun showAlarmMessage(notifyTrashList: List<AlarmTrashDTO>)
  fun setAlarm(hourOfDay: Int, minute: Int)

  fun cancelAlarm()
}
