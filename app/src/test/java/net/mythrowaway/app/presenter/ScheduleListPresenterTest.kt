package net.mythrowaway.app.presenter

import net.mythrowaway.app.adapter.IScheduleListView
import net.mythrowaway.app.adapter.presenter.ScheduleListPresenterImpl
import net.mythrowaway.app.adapter.presenter.ScheduleViewModel
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.domain.TrashSchedule
import net.mythrowaway.app.usecase.TrashManager
import net.mythrowaway.app.stub.TestPersistImpl
import org.junit.Assert
import org.junit.Test

class ScheduleListPresenterTest {
    inner class TestView: IScheduleListView {
        lateinit var viewModel: ArrayList<ScheduleViewModel>
        override fun update(viewModel: ArrayList<ScheduleViewModel>) {
            this.viewModel = viewModel
        }
    }

    private val testView = TestView()
    private val presenter =
        ScheduleListPresenterImpl(
            TrashManager(TestPersistImpl()), testView
        )

    @Test
    fun showSchedule() {
        val trash1 = TrashData().apply {
            id = "1"
            type = "burn"
            schedules = arrayListOf(
                TrashSchedule().apply{
                type = "weekday"
                value = "0"
            },  TrashSchedule().apply{
                type = "biweek"
                value = "6-1"
            })
        }
        val trash2 = TrashData().apply {
            id = "2"
            type = "other"
            trash_val = "家電"
            schedules = arrayListOf(
                TrashSchedule().apply{
                type = "evweek"
                value = hashMapOf<String,String>()
                (value as HashMap<String, String>)["start"]  = "2020-02-01"
                (value as HashMap<String, String>)["weekday"] = "3"
                (value as HashMap<String, Int>)["interval"] = 4
            }, TrashSchedule().apply {
                type = "month"
                value = "11"
            })
        }
        presenter.showScheduleList(arrayListOf(trash1,trash2))
        Assert.assertEquals(2,testView.viewModel.size)
        Assert.assertEquals("1",testView.viewModel[0].id)
        Assert.assertEquals("もえるゴミ",testView.viewModel[0].trashName)
        Assert.assertEquals("毎週日曜日",testView.viewModel[0].scheduleList[0])
        Assert.assertEquals("第1土曜日",testView.viewModel[0].scheduleList[1])
        Assert.assertEquals("2",testView.viewModel[1].id)
        Assert.assertEquals("家電",testView.viewModel[1].trashName)
        Assert.assertEquals("隔週水曜日",testView.viewModel[1].scheduleList[0])
        Assert.assertEquals("毎月11日",testView.viewModel[1].scheduleList[1])
    }

    @Test
    fun showSchedule_NoData() {
        presenter.showScheduleList(arrayListOf())
        Assert.assertEquals(0,testView.viewModel.size)
    }
}