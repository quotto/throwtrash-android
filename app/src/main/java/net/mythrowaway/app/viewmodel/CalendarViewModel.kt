package net.mythrowaway.app.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.mythrowaway.app.domain.TrashData

/**
 * @param year 対象カレンダーの年
 * @param month 対象カレンダーの月
 * @param dateList 日付リスト
 * @param trashList ゴミ出しリスト
 * @param position 現在年月からの経過月数を表すインデックス
 */
class CalendarViewModel(
  var year:Int = 0,
  var month:Int = 0,
  var dateList: ArrayList<Int> = arrayListOf(),
  var trashList: Array<ArrayList<TrashData>> = arrayOf(arrayListOf()),
  var position: Int = 0
) {
}

class CalendarItemViewModel: ViewModel() {
    val cardItem: MutableLiveData<CalendarViewModel> by lazy {
        MutableLiveData<CalendarViewModel>()
    }
}