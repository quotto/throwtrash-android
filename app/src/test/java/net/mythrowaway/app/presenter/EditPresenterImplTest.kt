package net.mythrowaway.app.presenter

import com.nhaarman.mockito_kotlin.capture
import net.mythrowaway.app.adapter.IEditView
import net.mythrowaway.app.adapter.presenter.EditPresenterImpl
import net.mythrowaway.app.adapter.presenter.EditItem
import net.mythrowaway.app.domain.ExcludeDate
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.domain.TrashSchedule
import net.mythrowaway.app.usecase.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.*
import kotlin.collections.ArrayList

@RunWith(PowerMockRunner::class)
@PrepareForTest(IEditView::class, CalendarManager::class,IPersistentRepository::class)
class EditPresenterImplTest {
    private val calendarManager = PowerMockito.mock(CalendarManager::class.java)

    private val view:IEditView = PowerMockito.mock(IEditView::class.java)
    private val editItem:ArgumentCaptor<EditItem> = ArgumentCaptor.forClass(EditItem::class.java)
    private val resultCode: ArgumentCaptor<Int> = ArgumentCaptor.forClass(Int::class.java)

    private val instance =
        EditPresenterImpl(
            calendarManager,
            TrashManager(PowerMockito.mock(IPersistentRepository::class.java)),
            view
        )

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
        Mockito.verify(view,Mockito.times(1)).setTrashData(capture(editItem))

        Assert.assertEquals(trashData.id,editItem.value.id)
        Assert.assertEquals(trashData.type,editItem.value.type)
        Assert.assertEquals("weekday",editItem.value.scheduleItem[0].type)
        Assert.assertEquals("5",editItem.value.scheduleItem[0].weekdayValue)
        Assert.assertEquals(0,editItem.value.excludes.size)
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
        Mockito.verify(view,Mockito.times(1)).setTrashData(capture(editItem))

        Assert.assertEquals(trashData.id,editItem.value.id)
        Assert.assertEquals(trashData.type,editItem.value.type)
        Assert.assertEquals(trashData.trash_val,editItem.value.trashVal)
        Assert.assertEquals("month",editItem.value.scheduleItem[0].type)
        Assert.assertEquals("10",editItem.value.scheduleItem[0].monthValue)
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
        Mockito.verify(view,Mockito.times(1)).setTrashData(capture(editItem))

        Assert.assertEquals(trashData.id,editItem.value.id)
        Assert.assertEquals(trashData.type,editItem.value.type)
        Assert.assertEquals(trashData.trash_val,editItem.value.trashVal)
        Assert.assertEquals("biweek",editItem.value.scheduleItem[0].type)
        Assert.assertEquals("2",editItem.value.scheduleItem[0].numOfWeekWeekdayValue)
        Assert.assertEquals("3",editItem.value.scheduleItem[0].numOfWeekNumberValue)
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
        Mockito.verify(view,Mockito.times(1)).setTrashData(capture(editItem))

        Assert.assertEquals(trashData.id,editItem.value.id)
        Assert.assertEquals(trashData.type,editItem.value.type)
        Assert.assertEquals(trashData.trash_val,editItem.value.trashVal)
        Assert.assertEquals("evweek",editItem.value.scheduleItem[0].type)
        Assert.assertEquals("0",editItem.value.scheduleItem[0].evweekWeekdayValue)
        Assert.assertEquals("2020/01/05",editItem.value.scheduleItem[0].evweekStartValue)
        Assert.assertEquals(2,editItem.value.scheduleItem[0].evweekIntervalValue)

        Assert.assertEquals("evweek",editItem.value.scheduleItem[1].type)
        Assert.assertEquals("4",editItem.value.scheduleItem[1].evweekWeekdayValue)
        Assert.assertEquals("2020/01/19",editItem.value.scheduleItem[1].evweekStartValue)
        Assert.assertEquals(3,editItem.value.scheduleItem[1].evweekIntervalValue)

        Assert.assertEquals("evweek",editItem.value.scheduleItem[2].type)
        Assert.assertEquals("0",editItem.value.scheduleItem[2].evweekWeekdayValue)
        Assert.assertEquals("2020/01/05",editItem.value.scheduleItem[2].evweekStartValue)
        Assert.assertEquals(2,editItem.value.scheduleItem[2].evweekIntervalValue)

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
        Mockito.verify(view,Mockito.times(1)).setTrashData(capture(editItem))

        Assert.assertEquals(trashData.id,editItem.value.id)
        Assert.assertEquals(trashData.type,editItem.value.type)
        Assert.assertEquals("weekday",editItem.value.scheduleItem[0].type)
        Assert.assertEquals("5",editItem.value.scheduleItem[0].weekdayValue)
        Assert.assertEquals(2,editItem.value.excludes.size)
        Assert.assertEquals(1,editItem.value.excludes[0].first)
        Assert.assertEquals(3,editItem.value.excludes[0].second)
        Assert.assertEquals(12,editItem.value.excludes[1].first)
        Assert.assertEquals(30,editItem.value.excludes[1].second)
    }


    @Test
    fun showError_EmptyText() {
        instance.showError(EditUseCase.ResultCode.INVALID_OTHER_TEXT_OVER)
        instance.showError(EditUseCase.ResultCode.INVALID_OTHER_TEXT_EMPTY)
        instance.showError(EditUseCase.ResultCode.INVALID_OTHER_TEXT_CHARACTER)
        instance.showError(EditUseCase.ResultCode.SUCCESS)

        Mockito.verify(view, Mockito.times(4)).showOtherTextError(capture(resultCode))
        val allValues = resultCode.allValues
        Assert.assertEquals(1,allValues[0])
        Assert.assertEquals(1,allValues[1])
        Assert.assertEquals(2,allValues[2])
        Assert.assertEquals(0,allValues[3])
    }
}