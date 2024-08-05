package net.mythrowaway.app.usecase.dto

import net.mythrowaway.app.domain.TrashType

class TrashDTO (
  private val _id: String,
  private val _type: TrashType,
  private val _displayName: String,
  private val _scheduleDTOs: List<ScheduleDTO>,
  private val _excludeDayOfMonthDTOs: List<ExcludeDayDTO>
){
  val id: String
    get() = _id
  val type: TrashType
    get() = _type
  val displayName: String
    get() = _displayName
  val scheduleDTOs: List<ScheduleDTO>
    get() = _scheduleDTOs

  val excludeDayOfMonthDTOs: List<ExcludeDayDTO>
    get() = _excludeDayOfMonthDTOs
}