package net.mythrowaway.app.adapter.presenter

import com.nhaarman.mockito_kotlin.capture
import net.mythrowaway.app.adapter.CalendarViewInterface
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.viewmodel.CalendarViewModel
import net.mythrowaway.app.service.CalendarManagerImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.*
import kotlin.collections.ArrayList

class CalendarPresenterImplTest {
    @Mock
    private lateinit var mockView: CalendarViewInterface
    private val mockCalendarManager: CalendarManagerImpl = Mockito.spy(CalendarManagerImpl())

    @InjectMocks
    private lateinit var presenter: CalendarPresenterImpl

    @Captor
    private lateinit var captorViewModel: ArgumentCaptor<CalendarViewModel>

    @BeforeEach
    fun before() {
        MockitoAnnotations.openMocks(this)
        Mockito.`when`(mockCalendarManager.getYear()).thenReturn(2020)
        Mockito.`when`(mockCalendarManager.getMonth()).thenReturn(1)
        presenter.setView(mockView)
        Mockito.reset(mockView)
    }

    @Test
    fun setCalendarSameMonth() {
        // 202001を想定したカレンダー日付
        val trash1 = TrashData().apply{type="burn";trash_val="ゴミ１"}
        val trash2 = TrashData().apply{type="unburn";trash_val="ゴミ2"}
        val dateList: ArrayList<Int> = arrayListOf(29,30,31,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,1)
        val trashList: Array<ArrayList<TrashData>> = Array(35) { arrayListOf(
            trash1,trash2
            )}

        //年月を受け取ってインデックスに変換する
        presenter.setCalendar(2020,1,trashList,dateList)

        Mockito.verify(mockView,Mockito.times(1)).update(capture(captorViewModel))

        assertEquals(2020, captorViewModel.value.year)
        assertEquals(1, captorViewModel.value.month)
        assertEquals(0, captorViewModel.value.position)
        repeat(trashList.size) {
            assertTrue(captorViewModel.value.trashList[it][0].equalsWithTypeAndValue(trash1))
            assertTrue(captorViewModel.value.trashList[it][1].equalsWithTypeAndValue(trash2))
        }
        repeat(dateList.size) {
            assertEquals(dateList[it], captorViewModel.value.dateList[it])
        }
    }

    @Test
    fun setCalendarSameYear() {
        // 202001を想定したカレンダー日付
        val trash1 = TrashData().apply{type="burn";trash_val="ゴミ１"}
        val trash2 = TrashData().apply{type="unburn";trash_val="ゴミ2"}
        val dateList: ArrayList<Int> = arrayListOf(29,30,31,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,1)
        val trashList: Array<ArrayList<TrashData>> = Array(35) {
            arrayListOf(trash1, trash2)
        }

        //年月を受け取ってインデックスに変換する
        presenter.setCalendar(2020,4,trashList,dateList)

        Mockito.verify(mockView,Mockito.times(1)).update(capture(captorViewModel))

        assertEquals(2020, captorViewModel.value.year)
        assertEquals(4, captorViewModel.value.month)
        assertEquals(3, captorViewModel.value.position)
        repeat(trashList.size) {
            assertTrue(captorViewModel.value.trashList[it][0].equalsWithTypeAndValue(trash1))
            assertTrue(captorViewModel.value.trashList[it][1].equalsWithTypeAndValue(trash2))
        }
        repeat(dateList.size) {
            assertEquals(dateList[it], captorViewModel.value.dateList[it])
        }
    }

    @Test
    fun setCalendarOverYear() {
        // 202001を想定したカレンダー日付
        val trash1 = TrashData().apply{type="burn";trash_val="ゴミ１"}
        val trash2 = TrashData().apply{type="unburn";trash_val="ゴミ2"}
        val dateList: ArrayList<Int> = arrayListOf(29,30,31,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,1)
        val trashList: Array<ArrayList<TrashData>> = Array(35) {
            arrayListOf(trash1, trash2)
        }

        //年月を受け取ってインデックスに変換する
        presenter.setCalendar(2021,4,trashList,dateList)

        Mockito.verify(mockView,Mockito.times(1)).update(capture(captorViewModel))

        assertEquals(2021, captorViewModel.value.year)
        assertEquals(4, captorViewModel.value.month)
        assertEquals(15, captorViewModel.value.position)
        repeat(trashList.size) {
            assertTrue(captorViewModel.value.trashList[it][0].equalsWithTypeAndValue(trash1))
            assertTrue(captorViewModel.value.trashList[it][1].equalsWithTypeAndValue(trash2))
        }
        repeat(dateList.size) {
            assertEquals(dateList[it], captorViewModel.value.dateList[it])
        }
    }

    @Test
    fun removeDuplicate() {
        // 202001を想定したカレンダー日付
        val trash1 = TrashData().apply{type="burn";trash_val="ゴミ１"}
        val trash2 = TrashData().apply{type="burn";trash_val="ゴミ１"}
        val trash3 = TrashData().apply{type="unburn";trash_val="ゴミ2"}
        val dateList: ArrayList<Int> = arrayListOf(29,30,31,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,1)
        val trashList: Array<ArrayList<TrashData>> = Array(35) {
            arrayListOf(trash1, trash2,trash3)}

        presenter.setCalendar(2020,1,trashList,dateList)

        Mockito.verify(mockView,Mockito.times(1)).update(capture(captorViewModel))

        captorViewModel.value.trashList.forEach {
            assertEquals(2, it.size)
            assertTrue(it[0].equalsWithTypeAndValue(trash1))
            assertTrue( it[1].equalsWithTypeAndValue(trash3))
        }
    }
}