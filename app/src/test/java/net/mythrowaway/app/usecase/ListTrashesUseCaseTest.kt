package net.mythrowaway.app.usecase

import net.mythrowaway.app.domain.ExcludeDayOfMonthList
import net.mythrowaway.app.domain.OrdinalWeeklySchedule
import net.mythrowaway.app.domain.Trash
import net.mythrowaway.app.domain.TrashList
import net.mythrowaway.app.domain.TrashType
import net.mythrowaway.app.usecase.dto.OrdinalWeeklyScheduleDTO
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.*
import java.time.DayOfWeek

class ListTrashesUseCaseTest {
    @Mock private lateinit var mockPersist: TrashRepositoryInterface
    @Mock private lateinit var mockConfig: VersionRepositoryInterface
    @InjectMocks private lateinit var target: ListTrashesUseCase//(

    @BeforeEach
    fun before() {
        MockitoAnnotations.openMocks(this)
        Mockito.reset(mockConfig)
        Mockito.reset(mockPersist)
    }

    @Test
    fun getTrashList_show_all_trash() {
        // 登録データを表示するケース,正常にデータが取得できた場合はPresenterにそのまま渡す
        val trash1 = Trash(
            _id = "1",
            _type = TrashType.BURN,
            schedules = listOf(
                OrdinalWeeklySchedule(_dayOfWeek = DayOfWeek.SUNDAY, _ordinalOfWeek = 3),
                OrdinalWeeklySchedule(_dayOfWeek = DayOfWeek.SATURDAY, _ordinalOfWeek = 1)
            ),
            _displayName = "",
            _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf()))
        val trash2 = Trash(
            _id = "2",
            _type = TrashType.OTHER,
            _displayName = "家電",
            schedules = listOf(
                OrdinalWeeklySchedule(_dayOfWeek = DayOfWeek.SUNDAY, _ordinalOfWeek = 3)
            ),
            _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf()))

        Mockito.`when`(mockPersist.getAllTrash()).thenReturn(
            TrashList(listOf(trash1,trash2)))
        val trashList = target.getTrashList()

        assertEquals(2,trashList.size)
        assertEquals("1",trashList[0].id)
        assertEquals("2",trashList[1].id)
        assertEquals(TrashType.BURN,trashList[0].type)
        assertEquals(TrashType.OTHER,trashList[1].type)
        assertEquals("家電",trashList[1].displayName)
        assertEquals(2,trashList[0].scheduleViewData.size)
        assertEquals(1,trashList[1].scheduleViewData.size)
        assertEquals(OrdinalWeeklyScheduleDTO::class.java, trashList[0].scheduleViewData[0].javaClass)
        assertEquals(OrdinalWeeklyScheduleDTO::class.java, trashList[0].scheduleViewData[1].javaClass)
        assertEquals(OrdinalWeeklyScheduleDTO::class.java, trashList[1].scheduleViewData[0].javaClass)
        assertEquals(0, (trashList[0].scheduleViewData[0] as OrdinalWeeklyScheduleDTO).dayOfWeek)
        assertEquals(3, (trashList[0].scheduleViewData[0] as OrdinalWeeklyScheduleDTO).ordinal)
        assertEquals(6, (trashList[0].scheduleViewData[1] as OrdinalWeeklyScheduleDTO).dayOfWeek)
        assertEquals(1, (trashList[0].scheduleViewData[1] as OrdinalWeeklyScheduleDTO).ordinal)
        assertEquals(0, (trashList[1].scheduleViewData[0] as OrdinalWeeklyScheduleDTO).dayOfWeek)
        assertEquals(3, (trashList[1].scheduleViewData[0] as OrdinalWeeklyScheduleDTO).ordinal)
    }

}