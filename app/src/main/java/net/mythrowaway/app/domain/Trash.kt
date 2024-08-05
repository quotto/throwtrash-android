package net.mythrowaway.app.domain

import java.time.LocalDate

class Trash(
  private val _id: String,
  private val _type: TrashType,
  private val _displayName: String,
  schedules: List<Schedule>,
  private val _excludeDayOfMonth: ExcludeDayOfMonthList
) {

  private val _schedules: MutableList<Schedule> = schedules.toMutableList()

  init {
    if (_id.isEmpty()) {
      throw IllegalArgumentException("idが設定されていません")
    }
    if (_type == TrashType.OTHER && _displayName.isEmpty()) {
      throw IllegalArgumentException("その他のゴミの場合はdisplayNameが必須です")
    }
    if(_schedules.isEmpty()) {
      throw IllegalArgumentException("スケジュールが設定されていません")
    }

    if(_schedules.size > 3) {
      throw IllegalArgumentException("スケジュールの数が3を超えています")
    }
  }

  val id: String get() = this._id
  val type: TrashType get() = this._type

  val schedules: List<Schedule> get() = this._schedules
  val excludeDayOfMonth: ExcludeDayOfMonthList get() = this._excludeDayOfMonth
  val displayName: String get() {
    return when (this._type) {
      TrashType.OTHER -> this._displayName
      else -> this._type.getTrashText()
    }
  }

  fun isTrashDay(target: LocalDate): Boolean {
    if (!_excludeDayOfMonth.isExcluded(target)) {
      return _schedules.any {
        it.isTrashDay(target)
      }
    }
    return false
  }

  fun isEqualOfType(trash: Trash): Boolean {
    if(this._type == TrashType.OTHER && trash._type == TrashType.OTHER) {
      return this._displayName == trash._displayName
    }
    return this._type == trash._type
  }

  fun equals(trash: Trash): Boolean {
    return this._id == trash._id
  }

  fun addSchedule(schedule: Schedule) {
    if(!canAddSchedule()) {
      throw IllegalArgumentException("スケジュールの数が上限3に達しています")
    }
    _schedules.add(schedule)
  }

  fun removeScheduleAt(position: Int) {
    if(!canRemoveSchedule()) {
      throw IllegalArgumentException("スケジュールの数は0にできません")
    }
    _schedules.removeAt(position)
  }

  fun canAddSchedule(): Boolean {
    return _schedules.size < 3
  }

  fun canRemoveSchedule(): Boolean {
    return _schedules.size > 1
  }
}