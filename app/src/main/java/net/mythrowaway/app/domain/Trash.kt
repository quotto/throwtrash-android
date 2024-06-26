package net.mythrowaway.app.domain

import java.time.LocalDate

class Trash(
  private val _id: String,
  private val _type: TrashType,
  private val _displayName: String,
  private val _schedules: List<Schedule>,
  private val _excludeDayOfMonth: List<ExcludeDayOfMonth>
) {

  init {
    if (_type == TrashType.OTHER && _displayName.isEmpty()) {
      throw IllegalArgumentException("その他のゴミの場合はdisplayNameが必須です")
    }
  }

  val type: TrashType get() = this._type
  val displayName: String get() {
    return when (this._type) {
      TrashType.OTHER -> this._displayName
      else -> this._type.getTrashText()
    }
  }

  fun isTrashDay(target: LocalDate): Boolean {
    if (_excludeDayOfMonth.none {
        it.isExcluded(target)
      }) {
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
}