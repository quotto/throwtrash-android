package net.mythrowaway.app.module.alarm.usecase

import android.util.Log
import net.mythrowaway.app.module.alarm.entity.AlarmConfig
import net.mythrowaway.app.module.trash.entity.trash.TrashType
import net.mythrowaway.app.module.alarm.dto.AlarmConfigDTO
import net.mythrowaway.app.module.alarm.dto.AlarmTrashDTO
import net.mythrowaway.app.module.trash.service.TrashService
import net.mythrowaway.app.module.trash.dto.TrashDTO
import javax.inject.Inject

class AlarmUseCase @Inject constructor(
  private val config: AlarmRepositoryInterface,
  private val trashService: TrashService,
) {
  fun getAlarmConfig(): AlarmConfigDTO {
    val alarmConfig = config.getAlarmConfig() ?: return AlarmConfigDTO(false, 0, 0, false)
    return AlarmConfigDTO(alarmConfig.enabled, alarmConfig.hourOfDay, alarmConfig.minute, alarmConfig.notifyEveryday)
  }

  fun saveAlarmConfig(alarmConfigDTO: AlarmConfigDTO, alarmManager: AlarmManager) {
    val alarmConfig = AlarmConfig(
      _enabled = alarmConfigDTO.enabled,
      _hourOfDay = alarmConfigDTO.hour,
      _minute = alarmConfigDTO.minute,
      _notifyEveryday = alarmConfigDTO.notifyEveryday
    )
    config.saveAlarmConfig(alarmConfig)
    if (alarmConfig.enabled) {
      alarmManager.setAlarm(alarmConfig.hourOfDay, alarmConfig.minute)
    } else {
      alarmManager.cancelAlarm()
    }
  }

  fun alarm(year: Int, month: Int, date: Int, alarmManager: AlarmManager) {
    val trashList: List<TrashDTO> = trashService.findTrashInDay(year,month,date)
    alarmManager.showAlarmMessage(trashList.map {
      AlarmTrashDTO(if(it.type === TrashType.OTHER) it.displayName else it.type.getTrashText())
    })

    val alarmConfig = config.getAlarmConfig()
    if(alarmConfig?.enabled == true) {
      alarmManager.setAlarm(alarmConfig.hourOfDay, alarmConfig.minute)
    } else {
      Log.w(this.javaClass.simpleName, "AlarmConfig is not set")
    }
  }
}