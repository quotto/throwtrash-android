package net.mythrowaway.app.adapter.presenter

import com.nhaarman.mockito_kotlin.capture
import net.mythrowaway.app.adapter.EditViewInterface
import net.mythrowaway.app.viewmodel.EditItemViewModel
import net.mythrowaway.app.domain.ExcludeDate
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.domain.TrashSchedule
import net.mythrowaway.app.domain.TrashType
import net.mythrowaway.app.service.CalendarManagerImpl
import net.mythrowaway.app.usecase.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.*
class EditPresenterImplTest {
    @Suppress("unused")
    private val mockCalendarManager = Mockito.mock(CalendarManagerImpl::class.java)
    @Mock
    private lateinit var mockView:EditViewInterface
    @Mock
    private lateinit var mockConfig: ConfigRepositoryInterface
    @InjectMocks
    private lateinit var instance: EditPresenterImpl

    @Captor
    private lateinit var captorEditItemViewModel:ArgumentCaptor<EditItemViewModel>
    @Captor
    private lateinit var captorResultCode: ArgumentCaptor<Int>


    @BeforeEach
    fun before() {
        MockitoAnnotations.openMocks(this)
        instance.setView(mockView)
    }

    @Test
    fun loadTrashData_Weekday() {
        val schedule = TrashSchedule()
        schedule.type = "weekday"
        schedule.value = "5"
        val trashData = TrashData()
        trashData.id = "999"
        trashData.schedules = arrayListOf(schedule)
        trashData.type = TrashType.RESOURCE

        instance.loadTrashData(trashData)
        Mockito.verify(mockView,Mockito.times(1)).setTrashData(capture(captorEditItemViewModel))

        assertEquals(trashData.id,captorEditItemViewModel.value.id)
        assertEquals(trashData.type,captorEditItemViewModel.value.type)
        assertEquals("weekday",captorEditItemViewModel.value.scheduleItem[0].type)
        assertEquals("5",captorEditItemViewModel.value.scheduleItem[0].weekdayValue)
        assertEquals(0,captorEditItemViewModel.value.excludes.size)
    }

    @Test
    fun loadTrashData_Month() {
        val schedule = TrashSchedule()
        schedule.type = "month"
        schedule.value = "10"
        val trashData = TrashData()
        trashData.id = "999"
        trashData.schedules = arrayListOf(schedule)
        trashData.type = TrashType.OTHER
        trashData.trash_val = "生ゴミ"

        instance.loadTrashData(trashData)
        Mockito.verify(mockView,Mockito.times(1)).setTrashData(capture(captorEditItemViewModel))

        assertEquals(trashData.id,captorEditItemViewModel.value.id)
        assertEquals(trashData.type,captorEditItemViewModel.value.type)
        assertEquals(trashData.trash_val,captorEditItemViewModel.value.trashVal)
        assertEquals("month",captorEditItemViewModel.value.scheduleItem[0].type)
        assertEquals("10",captorEditItemViewModel.value.scheduleItem[0].monthValue)
    }

    @Test
    fun loadTrashData_NumOfWeek() {
        val schedule = TrashSchedule()
        schedule.type = "biweek"
        schedule.value = "2-3"
        val trashData = TrashData()
        trashData.id = "999"
        trashData.schedules = arrayListOf(schedule)
        trashData.type = TrashType.OTHER
        trashData.trash_val = "生ゴミ"

        instance.loadTrashData(trashData)
        Mockito.verify(mockView,Mockito.times(1)).setTrashData(capture(captorEditItemViewModel))

        assertEquals(trashData.id,captorEditItemViewModel.value.id)
        assertEquals(trashData.type,captorEditItemViewModel.value.type)
        assertEquals(trashData.trash_val,captorEditItemViewModel.value.trashVal)
        assertEquals("biweek",captorEditItemViewModel.value.scheduleItem[0].type)
        assertEquals("2",captorEditItemViewModel.value.scheduleItem[0].numOfWeekWeekdayValue)
        assertEquals("3",captorEditItemViewModel.value.scheduleItem[0].numOfWeekNumberValue)
    }

    @Test
    fun loadTrashData_Evweek() {
        val schedule1 = TrashSchedule()
        schedule1.type = "evweek"
        schedule1.value = hashMapOf("start" to "2020-1-5","weekday" to  "0", "interval" to 2)
        val schedule2 = TrashSchedule()
        schedule2.type = "evweek"
        schedule2.value = hashMapOf("start" to "2020-1-15","weekday" to  "4", "interval" to 3)
        val schedule3 = TrashSchedule()
        schedule3.type = "evweek"
        schedule3.value = hashMapOf("start" to "2020-1-5","weekday" to  "0") //intervalが無いデータは旧バージョンデータのためデフォルト値2週間で処理される
        val trashData = TrashData()
        trashData.id = "999"
        trashData.schedules = arrayListOf(schedule1,schedule2,schedule3)
        trashData.type = TrashType.OTHER
        trashData.trash_val = "生ゴミ"

        instance.loadTrashData(trashData)
        Mockito.verify(mockView,Mockito.times(1)).setTrashData(capture(captorEditItemViewModel))

        assertEquals(trashData.id,captorEditItemViewModel.value.id)
        assertEquals(trashData.type,captorEditItemViewModel.value.type)
        assertEquals(trashData.trash_val,captorEditItemViewModel.value.trashVal)
        assertEquals("evweek",captorEditItemViewModel.value.scheduleItem[0].type)
        assertEquals("0",captorEditItemViewModel.value.scheduleItem[0].evweekWeekdayValue)
        assertEquals("2020/01/05",captorEditItemViewModel.value.scheduleItem[0].evweekStartValue)
        assertEquals(2,captorEditItemViewModel.value.scheduleItem[0].evweekIntervalValue)

        assertEquals("evweek",captorEditItemViewModel.value.scheduleItem[1].type)
        assertEquals("4",captorEditItemViewModel.value.scheduleItem[1].evweekWeekdayValue)
        assertEquals("2020/01/19",captorEditItemViewModel.value.scheduleItem[1].evweekStartValue)
        assertEquals(3,captorEditItemViewModel.value.scheduleItem[1].evweekIntervalValue)

        assertEquals("evweek",captorEditItemViewModel.value.scheduleItem[2].type)
        assertEquals("0",captorEditItemViewModel.value.scheduleItem[2].evweekWeekdayValue)
        assertEquals("2020/01/05",captorEditItemViewModel.value.scheduleItem[2].evweekStartValue)
        assertEquals(2,captorEditItemViewModel.value.scheduleItem[2].evweekIntervalValue)

    }

    @Test
    fun loadTrashData_SetExcludeDate() {
        val schedule = TrashSchedule()
        schedule.type = "weekday"
        schedule.value = "5"
        val trashData = TrashData()
        trashData.id = "999"
        trashData.schedules = arrayListOf(schedule)
        trashData.type = TrashType.RESOURCE
        trashData.excludes = listOf(
            ExcludeDate().apply {
                month = 1
                date = 3
            },
            ExcludeDate().apply {
                month = 12
                date = 30
            }
        )

        instance.loadTrashData(trashData)
        Mockito.verify(mockView,Mockito.times(1)).setTrashData(capture(captorEditItemViewModel))

        assertEquals(trashData.id,captorEditItemViewModel.value.id)
        assertEquals(trashData.type,captorEditItemViewModel.value.type)
        assertEquals("weekday",captorEditItemViewModel.value.scheduleItem[0].type)
        assertEquals("5",captorEditItemViewModel.value.scheduleItem[0].weekdayValue)
        assertEquals(2,captorEditItemViewModel.value.excludes.size)
        assertEquals(1,captorEditItemViewModel.value.excludes[0].first)
        assertEquals(3,captorEditItemViewModel.value.excludes[0].second)
        assertEquals(12,captorEditItemViewModel.value.excludes[1].first)
        assertEquals(30,captorEditItemViewModel.value.excludes[1].second)
    }


    @Test
    fun showError_EmptyText() {
        instance.showError(EditUseCase.ResultCode.INVALID_OTHER_TEXT_OVER)
        instance.showError(EditUseCase.ResultCode.INVALID_OTHER_TEXT_EMPTY)
        instance.showError(EditUseCase.ResultCode.INVALID_OTHER_TEXT_CHARACTER)
        instance.showError(EditUseCase.ResultCode.SUCCESS)

        Mockito.verify(mockView, Mockito.times(4)).showOtherTextError(capture(captorResultCode))
        val allValues = captorResultCode.allValues
        assertEquals(1,allValues[0])
        assertEquals(1,allValues[1])
        assertEquals(2,allValues[2])
        assertEquals(0,allValues[3])
    }
}