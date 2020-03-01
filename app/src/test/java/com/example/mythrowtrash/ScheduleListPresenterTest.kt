package com.example.mythrowtrash

import com.example.mythrowtrash.adapter.IScheduleListView
import com.example.mythrowtrash.adapter.ScheduleListPresenter
import com.example.mythrowtrash.adapter.ScheduleViewModel
import com.example.mythrowtrash.domain.TrashData
import com.example.mythrowtrash.domain.TrashSchedule
import com.example.mythrowtrash.usecase.IPersistentRepository
import com.example.mythrowtrash.usecase.TrashManager
import org.junit.Assert
import org.junit.Test

class ScheduleListPresenterTest {
    inner class TestView: IScheduleListView {
        lateinit var viewModel: ArrayList<ScheduleViewModel>
        override fun update(viewModel: ArrayList<ScheduleViewModel>) {
            this.viewModel = viewModel
        }
    }
    // TrashManagerに渡すだけ（このテストでメソッドは利用しない）
    inner class TestPersist: IPersistentRepository {
        override fun saveTrashData(trashData: TrashData) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun updateTrashData(trashData: TrashData) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun deleteTrashData(id: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getAllTrashSchedule(): ArrayList<TrashData> {
            return arrayListOf()
        }

        override fun incrementCount(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getTrashData(id: Int): TrashData? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    private val testView = TestView()
    private val presenter = ScheduleListPresenter(TrashManager(TestPersist()),testView)

    @Test
    fun showSchedule() {
        val trash1 = TrashData().apply {
            id = 1
            type = "burn"
            schedules = arrayListOf(TrashSchedule().apply{
                type = "weekday"
                value = "0"
            }, TrashSchedule().apply{
                type = "biweek"
                value = "6-1"
            })
        }
        val trash2 = TrashData().apply {
            id = 2
            type = "other"
            trash_val = "家電"
            schedules = arrayListOf(TrashSchedule().apply{
                type = "evweek"
                value = hashMapOf<String,String>()
                (value as HashMap<String, String>)["start"]  = "2020-02-01"
                (value as HashMap<String, String>)["weekday"] = "3"
            },TrashSchedule().apply {
                type = "month"
                value = "11"
            })
        }
        presenter.showScheduleList(arrayListOf(trash1,trash2))
        Assert.assertEquals(2,testView.viewModel.size)
        Assert.assertEquals(1,testView.viewModel[0].id)
        Assert.assertEquals("もえるゴミ",testView.viewModel[0].trashName)
        Assert.assertEquals("毎週日曜日",testView.viewModel[0].scheduleList[0])
        Assert.assertEquals("第1土曜日",testView.viewModel[0].scheduleList[1])
        Assert.assertEquals(2,testView.viewModel[1].id)
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