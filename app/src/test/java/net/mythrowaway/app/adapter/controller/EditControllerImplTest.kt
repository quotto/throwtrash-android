package net.mythrowaway.app.adapter.controller

import com.nhaarman.mockito_kotlin.capture
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.domain.TrashType
import net.mythrowaway.app.usecase.*
import net.mythrowaway.app.viewmodel.EditItemViewModel
import net.mythrowaway.app.viewmodel.EditScheduleItem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.*
class EditControllerImplTest {
    @Mock private lateinit var mockUseCase: EditUseCase
    @InjectMocks
    private lateinit var instance: EditControllerImpl

    @Captor
    private lateinit var captorTrashData: ArgumentCaptor<TrashData>

    @BeforeEach
    fun before() {
        MockitoAnnotations.openMocks(this)
    }


    @Test
    fun saveTrashData_evweek() {
        val item = EditItemViewModel()
        item.type = TrashType.BURN
        item.id = "0001"
        item.scheduleItem = arrayListOf(
            EditScheduleItem().apply {
                type = "evweek"
                evweekWeekdayValue = "0"
                evweekStartValue = "2020/10/04"
                evweekIntervalValue = 2
            },
            EditScheduleItem().apply {
                type = "evweek"
                evweekWeekdayValue = "6"
                evweekStartValue = "2020/10/10"
                evweekIntervalValue = 4
            }
        )

        instance.saveTrashData(item)
        verify(mockUseCase, times(1)).updateTrashData(capture(captorTrashData))

        val actualTrashData = captorTrashData.value
        assertEquals("2020-10-4",(actualTrashData.schedules[0].value as HashMap<String,Any>)["start"])
        assertEquals("2020-10-4",(actualTrashData.schedules[1].value as HashMap<String,Any>)["start"])
   }

    @Test
    fun saveTrashData_ExcludeDate() {
        val item = EditItemViewModel()
        item.type = TrashType.BURN
        item.id = "0001"
        item.scheduleItem = arrayListOf(
            EditScheduleItem().apply {
                type = "evweek"
                evweekWeekdayValue = "0"
                evweekStartValue = "2020/10/04"
                evweekIntervalValue = 2
            },
            EditScheduleItem().apply {
                type = "evweek"
                evweekWeekdayValue = "6"
                evweekStartValue = "2020/10/10"
                evweekIntervalValue = 4
            }
        )
        item.excludes = arrayListOf(
            Pair(3,4),
            Pair(12,30)
        )

        instance.saveTrashData(item)
        Mockito.verify(mockUseCase,Mockito.times(1)).updateTrashData(capture(captorTrashData))
        assertEquals(3,captorTrashData.value.excludes[0].month)
        assertEquals(4,captorTrashData.value.excludes[0].date)
        assertEquals(12,captorTrashData.value.excludes[1].month)
        assertEquals(30,captorTrashData.value.excludes[1].date)
    }

    @Test
    fun saveTrashData_ExcludeDate_Empty() {
        val item = EditItemViewModel()
        item.type = TrashType.BURN
        item.id = "0001"
        item.scheduleItem = arrayListOf(
            EditScheduleItem().apply {
                type = "evweek"
                evweekWeekdayValue = "0"
                evweekStartValue = "2020/10/04"
                evweekIntervalValue = 2
            },
            EditScheduleItem().apply {
                type = "evweek"
                evweekWeekdayValue = "6"
                evweekStartValue = "2020/10/10"
                evweekIntervalValue = 4
            }
        )
        item.excludes = arrayListOf()

        instance.saveTrashData(item)
        Mockito.verify(mockUseCase,Mockito.times(1)).updateTrashData(capture(captorTrashData))
        assertEquals(0,captorTrashData.value.excludes.size)
    }
}