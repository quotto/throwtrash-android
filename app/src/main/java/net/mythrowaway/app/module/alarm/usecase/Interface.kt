package net.mythrowaway.app.module.alarm.usecase

import net.mythrowaway.app.module.alarm.entity.AlarmConfig
import net.mythrowaway.app.module.alarm.dto.AlarmTrashDTO

interface AlarmRepositoryInterface {
  fun getAlarmConfig(): AlarmConfig?
  fun saveAlarmConfig(alarmConfig: AlarmConfig)
}

interface AlarmManager {
  fun showAlarmMessage(notifyTrashList: List<AlarmTrashDTO>, notifyTomorrow: Boolean)
  fun setAlarm(hourOfDay: Int, minute: Int)

  fun cancelAlarm()
}
