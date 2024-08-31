package net.mythrowaway.app.domain.trash.usecase.dto

import net.mythrowaway.app.domain.trash.entity.TrashType

class TrashDTO (
  private val _id: String,
  private val _type: TrashType,
  private val _displayName: String,
  private val _scheduleViewData: List<ScheduleDTO>,
  private val _excludeDayOfMonthDTOs: List<ExcludeDayOfMonthDTO>
){
  val id: String
    get() = _id
  val type: TrashType
    get() = _type
  val displayName: String
    get() = _displayName
  val scheduleViewData: List<ScheduleDTO>
    get() = _scheduleViewData

  val excludeDayOfMonthDTOs: List<ExcludeDayOfMonthDTO>
    get() = _excludeDayOfMonthDTOs
}