package net.mythrowaway.app.presenter

import com.nhaarman.mockito_kotlin.capture
import net.mythrowaway.app.adapter.EditViewInterface
import net.mythrowaway.app.adapter.presenter.EditPresenterImpl
import net.mythrowaway.app.viewmodel.EditItemViewModel
import net.mythrowaway.app.domain.ExcludeDate
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.domain.TrashSchedule
import net.mythrowaway.app.service.CalendarManagerImpl
import net.mythrowaway.app.usecase.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(CalendarManagerImpl::class)
class EditPresenterImplTest {
    @Suppress("unused")
    private val mockCalendarManager = PowerMockito.mock(CalendarManagerImpl::class.java)
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


    @Before
    fun before() {
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
        trashData.type = "resource"

        instance.loadTrashData(trashData)
        Mockito.verify(mockView,Mockito.times(1)).setTrashData(capture(captorEditItemViewModel))

        Assert.assertEquals(trashData.id,captorEditItemViewModel.value.id)
        Assert.assertEquals(trashData.type,captorEditItemViewModel.value.type)
        Assert.assertEquals("weekday",captorEditItemViewModel.value.scheduleItem[0].type)
        Assert.assertEquals("5",captorEditItemViewModel.value.scheduleItem[0].weekdayValue)
        Assert.assertEquals(0,captorEditItemViewModel.value.excludes.size)
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
        Mockito.verify(mockView,Mockito.times(1)).setTrashData(capture(captorEditItemViewModel))

        Assert.assertEquals(trashData.id,captorEditItemViewModel.value.id)
        Assert.assertEquals(trashData.type,captorEditItemViewModel.value.type)
        Assert.assertEquals(trashData.trash_val,captorEditItemViewModel.value.trashVal)
        Assert.assertEquals("month",captorEditItemViewModel.value.scheduleItem[0].type)
        Assert.assertEquals("10",captorEditItemViewModel.value.scheduleItem[0].monthValue)
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
        Mockito.verify(mockView,Mockito.times(1)).setTrashData(capture(captorEditItemViewModel))

        Assert.assertEquals(trashData.id,captorEditItemViewModel.value.id)
        Assert.assertEquals(trashData.type,captorEditItemViewModel.value.type)
        Assert.assertEquals(trashData.trash_val,captorEditItemViewModel.value.trashVal)
        Assert.assertEquals("biweek",captorEditItemViewModel.value.scheduleItem[0].type)
        Assert.assertEquals("2",captorEditItemViewModel.value.scheduleItem[0].numOfWeekWeekdayValue)
        Assert.assertEquals("3",captorEditItemViewModel.value.scheduleItem[0].numOfWeekNumberValue)
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
        trashData.type = "other"
        trashData.trash_val = "生ゴミ"

        instance.loadTrashData(trashData)
        Mockito.verify(mockView,Mockito.times(1)).setTrashData(capture(captorEditItemViewModel))

        Assert.assertEquals(trashData.id,captorEditItemViewModel.value.id)
        Assert.assertEquals(trashData.type,captorEditItemViewModel.value.type)
        Assert.assertEquals(trashData.trash_val,captorEditItemViewModel.value.trashVal)
        Assert.assertEquals("evweek",captorEditItemViewModel.value.scheduleItem[0].type)
        Assert.assertEquals("0",captorEditItemViewModel.value.scheduleItem[0].evweekWeekdayValue)
        Assert.assertEquals("2020/01/05",captorEditItemViewModel.value.scheduleItem[0].evweekStartValue)
        Assert.assertEquals(2,captorEditItemViewModel.value.scheduleItem[0].evweekIntervalValue)

        Assert.assertEquals("evweek",captorEditItemViewModel.value.scheduleItem[1].type)
        Assert.assertEquals("4",captorEditItemViewModel.value.scheduleItem[1].evweekWeekdayValue)
        Assert.assertEquals("2020/01/19",captorEditItemViewModel.value.scheduleItem[1].evweekStartValue)
        Assert.assertEquals(3,captorEditItemViewModel.value.scheduleItem[1].evweekIntervalValue)

        Assert.assertEquals("evweek",captorEditItemViewModel.value.scheduleItem[2].type)
        Assert.assertEquals("0",captorEditItemViewModel.value.scheduleItem[2].evweekWeekdayValue)
        Assert.assertEquals("2020/01/05",captorEditItemViewModel.value.scheduleItem[2].evweekStartValue)
        Assert.assertEquals(2,captorEditItemViewModel.value.scheduleItem[2].evweekIntervalValue)

    }

    @Test
    fun loadTrashData_SetExcludeDate() {
        val schedule = TrashSchedule()
        schedule.type = "weekday"
        schedule.value = "5"
        val trashData = TrashData()
        trashData.id = "999"
        trashData.schedules = arrayListOf(schedule)
        trashData.type = "resource"
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

        Assert.assertEquals(trashData.id,captorEditItemViewModel.value.id)
        Assert.assertEquals(trashData.type,captorEditItemViewModel.value.type)
        Assert.assertEquals("weekday",captorEditItemViewModel.value.scheduleItem[0].type)
        Assert.assertEquals("5",captorEditItemViewModel.value.scheduleItem[0].weekdayValue)
        Assert.assertEquals(2,captorEditItemViewModel.value.excludes.size)
        Assert.assertEquals(1,captorEditItemViewModel.value.excludes[0].first)
        Assert.assertEquals(3,captorEditItemViewModel.value.excludes[0].second)
        Assert.assertEquals(12,captorEditItemViewModel.value.excludes[1].first)
        Assert.assertEquals(30,captorEditItemViewModel.value.excludes[1].second)
    }


    @Test
    fun showError_EmptyText() {
        instance.showError(EditUseCase.ResultCode.INVALID_OTHER_TEXT_OVER)
        instance.showError(EditUseCase.ResultCode.INVALID_OTHER_TEXT_EMPTY)
        instance.showError(EditUseCase.ResultCode.INVALID_OTHER_TEXT_CHARACTER)
        instance.showError(EditUseCase.ResultCode.SUCCESS)

        Mockito.verify(mockView, Mockito.times(4)).showOtherTextError(capture(captorResultCode))
        val allValues = captorResultCode.allValues
        Assert.assertEquals(1,allValues[0])
        Assert.assertEquals(1,allValues[1])
        Assert.assertEquals(2,allValues[2])
        Assert.assertEquals(0,allValues[3])
    }
}