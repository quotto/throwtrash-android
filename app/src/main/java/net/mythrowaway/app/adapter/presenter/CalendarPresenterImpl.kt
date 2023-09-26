package net.mythrowaway.app.adapter.presenter

import net.mythrowaway.app.adapter.CalendarViewInterface
import net.mythrowaway.app.domain.TrashData
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
    override fun setCalendar(year:Int, month:Int, trashList: Array<ArrayList<TrashData>>, dateList: ArrayList<Int>) {
        val viewModel = CalendarViewModel()
        viewModel.year = year
        viewModel.month = month
        viewModel.dateList = dateList

        // trashList内の重複を削除する
        // 重複の判定はTrashData.typeとTrashData.trash_valの組み合わせで行う
        val trashListDistinct = trashList.map{
                dayOfTrashList->dayOfTrashList.distinctBy{trashData->
                    trashData.type.toString() + trashData.trash_val
                } as ArrayList<TrashData>
        }.toTypedArray()
        viewModel.trashList = trashListDistinct

        val yearSub = year - calendarManager.getYear()
        val monthSub = month - calendarManager.getMonth()
        val position = yearSub * 12 + monthSub
        viewModel.position = position
        view.update(viewModel)
    }
}