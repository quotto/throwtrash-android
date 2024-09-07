package net.mythrowaway.app.domain.trash.dto

import net.mythrowaway.app.domain.trash.entity.trash.TrashType

class TrashDTO (
  private val _id: String,
  private val _type: TrashType,
  private val _displayName: String,
  private val _scheduleDTOList: List<ScheduleDTO>,
  private val _excludeDayOfMonthDTOList: List<ExcludeDayOfMonthDTO>
){
  val id: String
    get() = _id
  val type: TrashType
    get() = _type
  val displayName: String
    get() = _displayName
  val scheduleDTOList: List<ScheduleDTO>
    get() = _scheduleDTOList

  val excludeDayOfMonthDTOList: List<ExcludeDayOfMonthDTO>
    get() = _excludeDayOfMonthDTOList
}