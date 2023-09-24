package net.mythrowaway.app.adapter.presenter

import com.nhaarman.mockito_kotlin.capture
import com.nhaarman.mockito_kotlin.mock
import net.mythrowaway.app.adapter.ScheduleListViewInterface
import net.mythrowaway.app.viewmodel.ScheduleViewModel
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.domain.TrashSchedule
import net.mythrowaway.app.domain.TrashType
import net.mythrowaway.app.service.TrashManager
import net.mythrowaway.app.usecase.DataRepositoryInterface
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.*
class ScheduleListPresenterTest {
    @Mock
    private lateinit var mockView: ScheduleListViewInterface
    private val mockPersist: DataRepositoryInterface = mock()
    @Suppress("unused")
    private val mockTrashManager = Mockito.spy(TrashManager(mockPersist))

    @InjectMocks
    private lateinit var presenter: ScheduleListPresenterImpl

    @Captor
    private lateinit var captorViewModel: ArgumentCaptor<ArrayList<ScheduleViewModel>>

    @BeforeEach
    fun before() {
        MockitoAnnotations.openMocks(this)
        presenter.setView(mockView)
        Mockito.reset(mockView)
    }

    @Test
    fun showSchedule() {
        val trash1 = TrashData().apply {
            id = "1"
            type = TrashType.BURN
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
            type = TrashType.OTHER
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
        assertEquals(2,captorViewModel.value.size)
        assertEquals("1",captorViewModel.value[0].id)
        assertEquals("もえるゴミ",captorViewModel.value[0].trashName)
        assertEquals("毎週日曜日",captorViewModel.value[0].scheduleList[0])
        assertEquals("第1土曜日",captorViewModel.value[0].scheduleList[1])
        assertEquals("2",captorViewModel.value[1].id)
        assertEquals("家電",captorViewModel.value[1].trashName)
        assertEquals("隔週水曜日",captorViewModel.value[1].scheduleList[0])
        assertEquals("毎月11日",captorViewModel.value[1].scheduleList[1])
    }

    @Test
    fun showSchedule_NoData() {
        presenter.showScheduleList(arrayListOf())
        Mockito.verify(mockView,Mockito.times(1)).update(capture(captorViewModel))
        assertEquals(0,captorViewModel.value.size)
    }
}