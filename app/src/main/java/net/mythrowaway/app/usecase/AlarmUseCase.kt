package net.mythrowaway.app.usecase

import android.util.Log
import net.mythrowaway.app.domain.AlarmConfig
import net.mythrowaway.app.domain.TrashList
import net.mythrowaway.app.domain.TrashType
import net.mythrowaway.app.usecase.dto.AlarmConfigDTO
import net.mythrowaway.app.usecase.dto.AlarmTrashDTO
import net.mythrowaway.app.view.AlarmManager
import java.time.LocalDate
import javax.inject.Inject

class AlarmUseCase @Inject constructor(
  private val config: AlarmRepositoryInterface,
  private val repository: TrashRepositoryInterface,
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
    val trashList: TrashList = repository.getAllTrash()
    val targetDate = LocalDate.of(year, month, date)
    alarmManager.showAlarmMessage(trashList.trashList.filter { trash ->
      trash.isTrashDay(targetDate)
    }.map {
      AlarmTrashDTO(if(it.type === TrashType.OTHER) it.displayName else it.type.getTrashText())
    })

    config.getAlarmConfig()?.let {alarmConfig ->
      alarmManager.setAlarm(alarmConfig.hourOfDay, alarmConfig.minute)
    } ?: Log.w(this.javaClass.simpleName, "AlarmConfig is not set")
  }
}