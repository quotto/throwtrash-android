package net.mythrowaway.app.module.alarm.dto

class AlarmConfigDTO(
  val enabled: Boolean,
  val hour: Int,
  val minute: Int,
  val notifyEveryday: Boolean
) {
}