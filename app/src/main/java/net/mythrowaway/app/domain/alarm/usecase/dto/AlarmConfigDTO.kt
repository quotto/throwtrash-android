package net.mythrowaway.app.domain.alarm.usecase.dto

class AlarmConfigDTO(
  val enabled: Boolean,
  val hour: Int,
  val minute: Int,
  val notifyEveryday: Boolean
) {
}