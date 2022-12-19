package net.mythrowaway.app.presenter

import com.nhaarman.mockito_kotlin.capture
import com.nhaarman.mockito_kotlin.mock
import net.mythrowaway.app.adapter.IScheduleListView
import net.mythrowaway.app.adapter.presenter.ScheduleListPresenterImpl
import net.mythrowaway.app.viewmodel.ScheduleViewModel
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.domain.TrashSchedule
import net.mythrowaway.app.service.TrashManager
import net.mythrowaway.app.usecase.IPersistentRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(TrashManager::class)
class ScheduleListPresenterTest {
    @Mock
    private lateinit var mockView: IScheduleListView
    private val mockPersist: IPersistentRepository = mock()
    @Suppress("unused")
    private val mockTrashManager = PowerMockito.spy(TrashManager(mockPersist))

    @InjectMocks
    private lateinit var presenter: ScheduleListPresenterImpl

    @Captor
    private lateinit var captorViewModel: ArgumentCaptor<ArrayList<ScheduleViewModel>>

    @Before
    fun before() {
        presenter.setView(mockView)
        Mockito.reset(mockView)
    }

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

        Mockito.verify(mockView,Mockito.times(1)).update(capture(captorViewModel))
        Assert.assertEquals(2,captorViewModel.value.size)
        Assert.assertEquals("1",captorViewModel.value[0].id)
        Assert.assertEquals("もえるゴミ",captorViewModel.value[0].trashName)
        Assert.assertEquals("毎週日曜日",captorViewModel.value[0].scheduleList[0])
        Assert.assertEquals("第1土曜日",captorViewModel.value[0].scheduleList[1])
        Assert.assertEquals("2",captorViewModel.value[1].id)
        Assert.assertEquals("家電",captorViewModel.value[1].trashName)
        Assert.assertEquals("隔週水曜日",captorViewModel.value[1].scheduleList[0])
        Assert.assertEquals("毎月11日",captorViewModel.value[1].scheduleList[1])
    }

    @Test
    fun showSchedule_NoData() {
        presenter.showScheduleList(arrayListOf())
        Mockito.verify(mockView,Mockito.times(1)).update(capture(captorViewModel))
        Assert.assertEquals(0,captorViewModel.value.size)
    }
}