package net.mythrowaway.app.presenter

import com.nhaarman.mockito_kotlin.capture
import net.mythrowaway.app.adapter.ICalendarView
import net.mythrowaway.app.adapter.presenter.CalendarPresenterImpl
import net.mythrowaway.app.viewmodel.CalendarViewModel
import net.mythrowaway.app.usecase.CalendarManager
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import kotlin.collections.ArrayList

@RunWith(PowerMockRunner::class)
@PrepareForTest(
    CalendarManager::class
)
class CalendarPresenterImplTest {
    @Mock
    private lateinit var mockView: ICalendarView
    private val mockCalendarManager: CalendarManager = PowerMockito.spy(CalendarManager())

    @InjectMocks
    private lateinit var presenter: CalendarPresenterImpl

    @Captor
    private lateinit var captorViewModel: ArgumentCaptor<CalendarViewModel>

    @Before
    fun before() {
        Mockito.`when`(mockCalendarManager.getYear()).thenReturn(2020)
        Mockito.`when`(mockCalendarManager.getMonth()).thenReturn(1)
        presenter.setView(mockView)
        Mockito.reset(mockView)
    }

    @Test
    fun setCalendarSameMonth() {
        // 202001を想定したカレンダー日付
        val dateList: ArrayList<Int> = arrayListOf(29,30,31,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,1)
        val trashList: Array<ArrayList<String>> = Array(35) { arrayListOf("ゴミ１", "ゴミ2")}

        //年月を受け取ってインデックスに変換する
        presenter.setCalendar(2020,1,trashList,dateList)

        Mockito.verify(mockView,Mockito.times(1)).update(capture(captorViewModel))

        Assert.assertEquals(2020, captorViewModel.value.year)
        Assert.assertEquals(1, captorViewModel.value.month)
        Assert.assertEquals(0, captorViewModel.value.position)
        repeat(trashList.size) {
            Assert.assertEquals(trashList[it], captorViewModel.value.trashList[it])
        }
        repeat(dateList.size) {
            Assert.assertEquals(dateList[it], captorViewModel.value.dateList[it])
        }
    }

    @Test
    fun setCalendarSameYear() {
        // 202001を想定したカレンダー日付
        val dateList: ArrayList<Int> = arrayListOf(29,30,31,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,1)
        val trashList: Array<ArrayList<String>> = Array(35) { arrayListOf("ゴミ１", "ゴミ2")}

        //年月を受け取ってインデックスに変換する
        presenter.setCalendar(2020,4,trashList,dateList)

        Mockito.verify(mockView,Mockito.times(1)).update(capture(captorViewModel))

        Assert.assertEquals(2020, captorViewModel.value.year)
        Assert.assertEquals(4, captorViewModel.value.month)
        Assert.assertEquals(3, captorViewModel.value.position)
        repeat(trashList.size) {
            Assert.assertEquals(trashList[it], captorViewModel.value.trashList[it])
        }
        repeat(dateList.size) {
            Assert.assertEquals(dateList[it], captorViewModel.value.dateList[it])
        }
    }

    @Test
    fun setCalendarOverYear() {
        // 202001を想定したカレンダー日付
        val dateList: ArrayList<Int> = arrayListOf(29,30,31,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,1)
        val trashList: Array<ArrayList<String>> = Array(35) { arrayListOf("ゴミ１", "ゴミ2")}

        //年月を受け取ってインデックスに変換する
        presenter.setCalendar(2021,4,trashList,dateList)

        Mockito.verify(mockView,Mockito.times(1)).update(capture(captorViewModel))

        Assert.assertEquals(2021, captorViewModel.value.year)
        Assert.assertEquals(4, captorViewModel.value.month)
        Assert.assertEquals(15, captorViewModel.value.position)
        repeat(trashList.size) {
            Assert.assertEquals(trashList[it], captorViewModel.value.trashList[it])
        }
        repeat(dateList.size) {
            Assert.assertEquals(dateList[it], captorViewModel.value.dateList[it])
        }
    }

    @Test
    fun removeDuplicate() {
        // 202001を想定したカレンダー日付
        val dateList: ArrayList<Int> = arrayListOf(29,30,31,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,1)
        val trashList: Array<ArrayList<String>> = Array(35) { arrayListOf("ゴミ1", "ゴミ1","ゴミ2")}

        presenter.setCalendar(2020,1,trashList,dateList)

        Mockito.verify(mockView,Mockito.times(1)).update(capture(captorViewModel))

        captorViewModel.value.trashList.forEach {
            Assert.assertEquals(2, it.size)
            Assert.assertEquals("ゴミ1", it[0])
            Assert.assertEquals("ゴミ2", it[1])
        }
    }
}