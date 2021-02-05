package net.mythrowaway.app.adapter.presenter

import net.mythrowaway.app.adapter.ICalendarView
import net.mythrowaway.app.usecase.ICalendarManager
import net.mythrowaway.app.usecase.ICalendarPresenter
import net.mythrowaway.app.viewmodel.CalendarViewModel

class CalendarPresenterImpl(
    private val view: ICalendarView,
    private val calendarManager: ICalendarManager): ICalendarPresenter {
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
        viewModel.trashList = trashList.map{ArrayList(it.distinct())}.toTypedArray()

        val yearSub = year - calendarManager.getYear()
        val monthSub = month - calendarManager.getMonth()
        val position = yearSub * 12 + monthSub
        viewModel.position = position
        view.update(viewModel)
    }
}