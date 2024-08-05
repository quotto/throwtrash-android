package net.mythrowaway.app.domain

import java.time.LocalDate

class ExcludeDayOfMonthList(private val _members: MutableList<ExcludeDayOfMonth>) {

  val members: List<ExcludeDayOfMonth> get() = _members
  init {
    if(_members.size > MAX_SIZE) {
      throw IllegalArgumentException("除外日の数が3を超えています")
    }
  }

  fun add(excludeDayOfMonth: ExcludeDayOfMonth) {
    if(canAdd()) {
      _members.add(excludeDayOfMonth)
    } else {
      throw IllegalArgumentException("除外日の数が上限に達しています")
    }
  }

  fun removeAt(position: Int) {
    if(canRemove()) {
      _members.removeAt(position)
    } else {
      throw IllegalArgumentException("除外日が設定されていません")
    }
  }

  fun canAdd(): Boolean {
    return _members.size < MAX_SIZE
  }

  fun canRemove(): Boolean {
    return _members.isNotEmpty()
  }

  fun isExcluded(date: LocalDate): Boolean {
    return _members.any {
      it.isExcluded(date)
    }
  }

  companion object {
    const val MAX_SIZE = 10
  }

}