package net.mythrowaway.app.viewmodel

import androidx.lifecycle.ViewModel
import net.mythrowaway.app.domain.TrashType
import java.io.Serializable

class EditScheduleItem: Serializable {
    var type:String = ""
    var weekdayValue:String =""
    var monthValue: String = ""
    var numOfWeekWeekdayValue:String = ""
    var numOfWeekNumberValue:String = ""
    var evweekWeekdayValue:String = ""
    var evweekIntervalValue: Int = 2
    var evweekStartValue: String = ""
}
class EditItemViewModel {
    var id:String? = null
    var type:TrashType = TrashType.BURN
    var trashVal: String = ""
    var scheduleItem:ArrayList<EditScheduleItem> = ArrayList()
    var excludes: ArrayList<Pair<Int,Int>> = arrayListOf()
}

class EditScheduleItemViewModel: ViewModel() {
    var editScheduleItem: EditScheduleItem? = null
}