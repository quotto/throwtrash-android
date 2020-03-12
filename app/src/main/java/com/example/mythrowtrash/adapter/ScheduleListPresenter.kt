package com.example.mythrowtrash.adapter

import com.example.mythrowtrash.domain.TrashData
import com.example.mythrowtrash.usecase.IScheduleListPresenter
import com.example.mythrowtrash.usecase.TrashManager

class ScheduleViewModel {
    var id: String = ""
    var trashName: String = ""
    var scheduleList: ArrayList<String> = arrayListOf()
}

class ScheduleListPresenter(private val trashManager: TrashManager,private val view: IScheduleListView): IScheduleListPresenter {
    private val WeekdayMap: Map<String,String> = mapOf(
        "0" to "日",
        "1" to "月",
        "2" to "火",
        "3" to "水",
        "4" to "木",
        "5" to "金",
        "6" to "土"
    )
    override fun showScheduleList(scheduleList: ArrayList<TrashData>) {
        val viewModel: ArrayList<ScheduleViewModel> = arrayListOf()
        scheduleList.forEach {trashData ->
            val scheduleViewModel = ScheduleViewModel()
            scheduleViewModel.id = trashData.id
            scheduleViewModel.trashName = trashManager.getTrashName(trashData.type, trashData.trash_val)
            trashData.schedules.forEach {trashSchedule ->
                when(trashSchedule.type) {
                    "weekday" ->
                        scheduleViewModel.scheduleList.add("毎週${WeekdayMap[trashSchedule.value]}曜日")
                    "month" ->
                        scheduleViewModel.scheduleList.add("毎月${trashSchedule.value}日")
                    "biweek" -> {
                        val v = (trashSchedule.value as String).split("-")
                        scheduleViewModel.scheduleList.add("第${v[1]}${WeekdayMap[v[0]]}曜日")
                    }
                    "evweek" ->
                        scheduleViewModel.scheduleList.add("隔週${WeekdayMap[(trashSchedule.value as HashMap<String,String>)["weekday"]]}曜日")
                }
            }
            viewModel.add(scheduleViewModel)
        }
        view.update(viewModel)
    }
}