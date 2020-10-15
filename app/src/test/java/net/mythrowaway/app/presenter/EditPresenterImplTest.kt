package net.mythrowaway.app.presenter

import net.mythrowaway.app.adapter.IEditView
import net.mythrowaway.app.adapter.presenter.EditPresenterImpl
import net.mythrowaway.app.adapter.presenter.EditItem
import net.mythrowaway.app.adapter.presenter.EditScheduleItem
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.domain.TrashSchedule
import net.mythrowaway.app.usecase.ICalendarManager
import net.mythrowaway.app.usecase.IPersistentRepository
import net.mythrowaway.app.usecase.TrashManager
import org.junit.Assert
import org.junit.Test
import java.util.*
import kotlin.collections.ArrayList

class EditPresenterImplTest {
    private val testView = TestVIew()
    private val calendarManager =
        TestCalendarManager()
    private val instance =
        EditPresenterImpl(
            calendarManager,
            TrashManager(TestPersistent()),
            testView
        )
    @Test
    fun loadTrashData_Weekday() {
        val schedule = TrashSchedule()
        schedule.type = "weekday"
        schedule.value = "5"
        val trashData = TrashData()
        trashData.id = "999"
        trashData.schedules = arrayListOf(schedule)
        trashData.type = "resource"

        instance.loadTrashData(trashData)
        Assert.assertEquals(trashData.id,testView.viewModel.id)
        Assert.assertEquals(trashData.type,testView.viewModel.type)
        Assert.assertEquals("weekday",testView.viewModel.scheduleItem[0].type)
        Assert.assertEquals("5",testView.viewModel.scheduleItem[0].weekdayValue)
    }

    @Test
    fun loadTrashData_Month() {
        val schedule = TrashSchedule()
        schedule.type = "month"
        schedule.value = "10"
        val trashData = TrashData()
        trashData.id = "999"
        trashData.schedules = arrayListOf(schedule)
        trashData.type = "other"
        trashData.trash_val = "生ゴミ"

        instance.loadTrashData(trashData)
        Assert.assertEquals(trashData.id,testView.viewModel.id)
        Assert.assertEquals(trashData.type,testView.viewModel.type)
        Assert.assertEquals(trashData.trash_val,testView.viewModel.trashVal)
        Assert.assertEquals("month",testView.viewModel.scheduleItem[0].type)
        Assert.assertEquals("10",testView.viewModel.scheduleItem[0].monthValue)
    }

    @Test
    fun loadTrashData_NumOfWeek() {
        val schedule = TrashSchedule()
        schedule.type = "biweek"
        schedule.value = "2-3"
        val trashData = TrashData()
        trashData.id = "999"
        trashData.schedules = arrayListOf(schedule)
        trashData.type = "other"
        trashData.trash_val = "生ゴミ"

        instance.loadTrashData(trashData)
        Assert.assertEquals(trashData.id,testView.viewModel.id)
        Assert.assertEquals(trashData.type,testView.viewModel.type)
        Assert.assertEquals(trashData.trash_val,testView.viewModel.trashVal)
        Assert.assertEquals("biweek",testView.viewModel.scheduleItem[0].type)
        Assert.assertEquals("2",testView.viewModel.scheduleItem[0].numOfWeekWeekdayValue)
        Assert.assertEquals("3",testView.viewModel.scheduleItem[0].numOfWeekNumberValue)
    }

    @Test
    fun loadTrashData_Evweek() {
        val schedule1 = TrashSchedule()
        schedule1.type = "evweek"
        schedule1.value = hashMapOf("start" to "2020-1-5","weekday" to  "0", "interval" to 2) //偶数週
        val schedule2 = TrashSchedule()
        schedule2.type = "evweek"
        schedule2.value = hashMapOf("start" to "2020-1-15","weekday" to  "4", "interval" to 3) //奇数週
        val trashData = TrashData()
        trashData.id = "999"
        trashData.schedules = arrayListOf(schedule1,schedule2)
        trashData.type = "other"
        trashData.trash_val = "生ゴミ"

        calendarManager.retTodayStringDate = "2020-01-22"

        instance.loadTrashData(trashData)
        Assert.assertEquals(trashData.id,testView.viewModel.id)
        Assert.assertEquals(trashData.type,testView.viewModel.type)
        Assert.assertEquals(trashData.trash_val,testView.viewModel.trashVal)
        Assert.assertEquals("evweek",testView.viewModel.scheduleItem[0].type)
        Assert.assertEquals("0",testView.viewModel.scheduleItem[0].evweekWeekdayValue)
        Assert.assertEquals("2020/01/05",testView.viewModel.scheduleItem[0].evweekStartValue)
        Assert.assertEquals(2,testView.viewModel.scheduleItem[0].evweekIntervalValue)

        Assert.assertEquals("evweek",testView.viewModel.scheduleItem[1].type)
        Assert.assertEquals("4",testView.viewModel.scheduleItem[1].evweekWeekdayValue)
        Assert.assertEquals("2020/01/19",testView.viewModel.scheduleItem[1].evweekStartValue)
        Assert.assertEquals(3,testView.viewModel.scheduleItem[1].evweekIntervalValue)
    }

    @Test
    fun showError_EmptyText() {

    }
}

class TestVIew: IEditView {
    var viewModel = EditItem()
    override fun showOtherTextError(resultCode: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setTrashData(item: EditItem) {
        viewModel = item
    }

    override fun showErrorMaxSchedule() {
        TODO("Not yet implemented")
    }

    override fun addTrashSchedule(nextAdd: Boolean, deleteEnabled: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteTrashSchedule(delete_index: Int, nextAdd: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun complete() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class TestPersistent: IPersistentRepository {
    override fun saveTrashData(trashData: TrashData) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun importScheduleList(scheduleList: ArrayList<TrashData>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateTrashData(trashData: TrashData) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteTrashData(id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllTrashSchedule(): ArrayList<TrashData> {
        return arrayListOf()
    }

    override fun getTrashData(id: String): TrashData? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class TestCalendarManager: ICalendarManager {
    override fun getYear(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMonth(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addYM(year: Int, month: Int, addMonth: Int): Pair<Int, Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun subYM(year: Int, month: Int, subMonth: Int): Pair<Int, Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun compareYM(param1: Pair<Int, Int>, param2: Pair<Int, Int>): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTodayStringDate(cal: Calendar): String {
        return retTodayStringDate
    }

    var retTodayStringDate:String = ""
}