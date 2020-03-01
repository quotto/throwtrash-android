package com.example.mythrowtrash

import com.example.mythrowtrash.adapter.EditPresenter
import com.example.mythrowtrash.adapter.EditViewModel
import com.example.mythrowtrash.adapter.EditViewModelSchedule
import com.example.mythrowtrash.adapter.IEditView
import com.example.mythrowtrash.domain.TrashData
import com.example.mythrowtrash.domain.TrashSchedule
import org.junit.Assert
import org.junit.Test
import java.util.*

class EditPresenterTest {
    private val testView = TestVIew()
    private val instance = EditPresenter(testView)
    @Test
    fun loadTrashData_Weekday() {
        val schedule = TrashSchedule()
        schedule.type = "weekday"
        schedule.value = "5"
        val trashData = TrashData()
        trashData.id = 999
        trashData.schedules = arrayListOf(schedule)
        trashData.type = "resource"

        instance.loadTrashData(trashData)
        Assert.assertEquals(trashData.id,testView.viewModel.id)
        Assert.assertEquals(trashData.type,testView.viewModel.type)
        Assert.assertEquals("weekday",testView.viewModel.schedule[0].type)
        Assert.assertEquals("5",testView.viewModel.schedule[0].weekdayValue)
    }

    @Test
    fun loadTrashData_Month() {
        val schedule = TrashSchedule()
        schedule.type = "month"
        schedule.value = "10"
        val trashData = TrashData()
        trashData.id = 999
        trashData.schedules = arrayListOf(schedule)
        trashData.type = "other"
        trashData.trash_val = "生ゴミ"

        instance.loadTrashData(trashData)
        Assert.assertEquals(trashData.id,testView.viewModel.id)
        Assert.assertEquals(trashData.type,testView.viewModel.type)
        Assert.assertEquals(trashData.trash_val,testView.viewModel.trashVal)
        Assert.assertEquals("month",testView.viewModel.schedule[0].type)
        Assert.assertEquals("10",testView.viewModel.schedule[0].monthValue)
    }

    @Test
    fun loadTrashData_NumOfWeek() {
        val schedule = TrashSchedule()
        schedule.type = "biweek"
        schedule.value = "2-3"
        val trashData = TrashData()
        trashData.id = 999
        trashData.schedules = arrayListOf(schedule)
        trashData.type = "other"
        trashData.trash_val = "生ゴミ"

        instance.loadTrashData(trashData)
        Assert.assertEquals(trashData.id,testView.viewModel.id)
        Assert.assertEquals(trashData.type,testView.viewModel.type)
        Assert.assertEquals(trashData.trash_val,testView.viewModel.trashVal)
        Assert.assertEquals("biweek",testView.viewModel.schedule[0].type)
        Assert.assertEquals("2",testView.viewModel.schedule[0].numOfWeekWeekdayValue)
        Assert.assertEquals("3",testView.viewModel.schedule[0].numOfWeekNumberValue)
    }

    @Test
    fun loadTrashData_Evweek() {
        val schedule1 = TrashSchedule()
        schedule1.type = "evweek"
        schedule1.value = hashMapOf("start" to "2020-01-05","weekday" to  "3") //偶数週
        val schedule2 = TrashSchedule()
        schedule2.type = "evweek"
        schedule2.value = hashMapOf("start" to "2020-01-12","weekday" to  "4") //奇数週
        val trashData = TrashData()
        trashData.id = 999
        trashData.schedules = arrayListOf(schedule1,schedule2)
        trashData.type = "other"
        trashData.trash_val = "生ゴミ"

        instance.loadTrashData(trashData)
        Assert.assertEquals(trashData.id,testView.viewModel.id)
        Assert.assertEquals(trashData.type,testView.viewModel.type)
        Assert.assertEquals(trashData.trash_val,testView.viewModel.trashVal)
        Assert.assertEquals("evweek",testView.viewModel.schedule[0].type)
        Assert.assertEquals("3",testView.viewModel.schedule[0].evweekWeekdayValue)

        var expectStart = if(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) % 2 == 0) {
            EditViewModelSchedule.EVWEEK_START_THIS_WEEK
        } else {
            EditViewModelSchedule.EVWEEK_START_NEXT_WEEK
        }
        Assert.assertEquals(expectStart,testView.viewModel.schedule[0].evweekStartValue)

        Assert.assertEquals("evweek",testView.viewModel.schedule[1].type)
        Assert.assertEquals("4",testView.viewModel.schedule[1].evweekWeekdayValue)
        expectStart = if(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) % 2 == 0) {
            EditViewModelSchedule.EVWEEK_START_NEXT_WEEK
        } else {
            EditViewModelSchedule.EVWEEK_START_THIS_WEEK
        }
        Assert.assertEquals(expectStart,testView.viewModel.schedule[1].evweekStartValue)
    }
}

class TestVIew: IEditView {
    var viewModel = EditViewModel()
    override fun showOtherTextError(resultCode: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addTrashSchedule(nextAdd: Boolean, deleteEnabled: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteTrashSchedule(delete_index: Int, nextAdd: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun complete(trashData: TrashData) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showTrashDtada(viewModel: EditViewModel) {
        this.viewModel = viewModel
    }
}