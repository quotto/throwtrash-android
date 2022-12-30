package net.mythrowaway.app.adapter.presenter

import net.mythrowaway.app.adapter.CalendarViewInterface
import net.mythrowaway.app.service.CalendarManagerImpl
import net.mythrowaway.app.usecase.CalendarPresenterInterface
import net.mythrowaway.app.viewmodel.CalendarViewModel
import javax.inject.Inject

class CalendarPresenterImpl @Inject constructor(
    private val calendarManager: CalendarManagerImpl
): CalendarPresenterInterface {

    private lateinit var view: CalendarViewInterface

    override fun setView(view: CalendarViewInterface) {
        this.view = view
    }
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