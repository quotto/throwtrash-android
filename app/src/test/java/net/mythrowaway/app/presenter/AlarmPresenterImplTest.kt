package net.mythrowaway.app.presenter

import net.mythrowaway.app.adapter.IAlarmView
import net.mythrowaway.app.adapter.presenter.AlarmPresenterImpl
import net.mythrowaway.app.adapter.presenter.AlarmViewModel
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.usecase.TrashManager
import net.mythrowaway.app.util.TestPersistImpl
import org.junit.Assert
import org.junit.Test

class AlarmPresenterImplTest {
    class TestAlarmView: IAlarmView {
        lateinit var trashList: List<String>
        override fun notify(trashList: List<String>) {
            this.trashList = trashList
        }

        override fun update(viewModel: AlarmViewModel) {
        }
    }
    private val testAlarmView =
        TestAlarmView()
    private val instance = AlarmPresenterImpl(
        trashManager = TrashManager(
            TestPersistImpl()
        ), view = testAlarmView
    )

    @Test
    fun notifyAlarm_UniqueData() {
        val trash1 = TrashData().apply {
            type = "burn"
        }
        val trash2 = TrashData().apply {
            type = "other"
            trash_val = "生ゴミ"
        }

        instance.notifyAlarm(arrayListOf(trash1,trash2))

        Assert.assertEquals(2, testAlarmView.trashList.size)
        Assert.assertEquals("もえるゴミ", testAlarmView.trashList[0])
        Assert.assertEquals("生ゴミ", testAlarmView.trashList[1])
    }

    @Test
    fun notifyAlarm_Duplicate() {
        val trash1 = TrashData().apply {
            type = "unburn"
        }
        val trash2 = TrashData().apply {
            type = "other"
            trash_val = "生ゴミ"
        }
        val trash3 = TrashData().apply {
            type = "other"
            trash_val = "生ゴミ"
        }
        val trash4 = TrashData().apply {
            type = "resource"
        }

        val trash5 = TrashData().apply {
            type = "unburn"
        }

        instance.notifyAlarm(arrayListOf(trash1,trash2,trash3,trash4,trash5))
        Assert.assertEquals(3, testAlarmView.trashList.size)
        Assert.assertEquals("もえないゴミ", testAlarmView.trashList[0])
        Assert.assertEquals("生ゴミ", testAlarmView.trashList[1])
        Assert.assertEquals("資源ごみ", testAlarmView.trashList[2])
    }
}