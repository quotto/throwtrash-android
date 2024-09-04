package net.mythrowaway.app.domain.trash.entity.trash

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

interface Schedule {
  fun isTrashDay(target: LocalDate): Boolean
}

class WeeklySchedule(private val _dayOfWeek: DayOfWeek) : Schedule {
  val dayOfWeek: DayOfWeek get() = _dayOfWeek

  override fun isTrashDay(target: LocalDate): Boolean {
    return target.dayOfWeek == _dayOfWeek

  }
}

class MonthlySchedule(private val _day: Int) : Schedule {
  val day: Int get() = _day

  init {
    if(_day < 1 || _day > 31) throw IllegalArgumentException("日の指定に誤りがあります")
  }
  override fun isTrashDay(target: LocalDate): Boolean {
    return target.dayOfMonth == this._day
  }
}

class IntervalWeeklySchedule(
  private val _start: LocalDate,
  private var _dayOfWeek: DayOfWeek,
  private var _interval: Int
) : Schedule {
  val start: LocalDate get() = _start
  val dayOfWeek: DayOfWeek get() = _dayOfWeek
  val interval: Int get() = _interval

  init {
    if(_interval < 2 || _interval > 4) throw IllegalArgumentException("インターバルの指定に誤りがあります")
  }
  override fun isTrashDay(target: LocalDate): Boolean {
    if(target.dayOfWeek != _dayOfWeek) return false

    // startの日付からその週の日曜日の日付を求める
    val startSunday = _start.with(
      TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)
    )

    // targetの日付がどの週の日曜日を含むかを求める
    val targetSunday = target.with(
      TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)
    )

    // 日曜日を起点とした週数の差を求める
    val weeksBetween = ChronoUnit.WEEKS.between(startSunday, targetSunday).toInt()

    // 週数の差がintervalの倍数かどうかをチェックする
    return weeksBetween % _interval == 0
  }
}

class OrdinalWeeklySchedule(
  private val _ordinalOfWeek: Int,
  private val _dayOfWeek: DayOfWeek
) : Schedule {
  val ordinalOfWeek: Int get() = _ordinalOfWeek
  val dayOfWeek: DayOfWeek get() = _dayOfWeek

  init {
    if(_ordinalOfWeek < 1 || _ordinalOfWeek > 5) throw IllegalArgumentException("週の指定に誤りがあります")
  }
  override fun isTrashDay(target: LocalDate): Boolean {
    val expectedDayOfMonth = target.with(
      TemporalAdjusters.dayOfWeekInMonth(this._ordinalOfWeek,this._dayOfWeek)
    )

    return expectedDayOfMonth.compareTo(target) == 0
  }
}