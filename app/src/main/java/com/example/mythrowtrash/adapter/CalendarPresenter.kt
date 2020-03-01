package com.example.mythrowtrash.adapter

import com.example.mythrowtrash.usecase.ICalendarManager
import com.example.mythrowtrash.usecase.ICalendarPresenter

class CalendarViewModel {
    var year:Int = 0 // 対象カレンダーの年
    var month: Int = 0 // 対象カレンダーの月
    lateinit var dateList: ArrayList<Int>  // 日付リスト
    lateinit var trashList: Array<ArrayList<String>> // ゴミ出しリスト
    var position: Int = 0 // 現在年月からの経過月数を表すインデックス
}
class CalendarPresenter(private val view: ICalendarView, private val calendarManager: ICalendarManager): ICalendarPresenter{
    /**
     * 初期表示用
     * ViewModelに年月とカレンダー情報をセットしてViewに渡す
     * 画面操作が行われた場合はnext/backメソッドを利用する
     */
    override fun setCalendar(year:Int, month:Int, trashList: Array<ArrayList<String>>, dateList: ArrayList<Int>) {
        val viewModel = CalendarViewModel()
        viewModel.year = year
        viewModel.month = month
        viewModel.dateList = dateList
        viewModel.trashList = trashList

        val yearSub = year - calendarManager.getYear()
        val monthSub = month - calendarManager.getMonth()
        val position = yearSub * 12 + monthSub
        viewModel.position = position
        view.update(viewModel)
    }
}