package net.mythrowaway.app.usecase

import androidx.preference.PreferenceManager
import androidx.test.platform.app.InstrumentationRegistry
import net.mythrowaway.app.module.trash.dto.IntervalWeeklyScheduleDTO
import net.mythrowaway.app.module.trash.dto.MonthlyScheduleDTO
import net.mythrowaway.app.module.trash.entity.trash.ExcludeDayOfMonthList
import net.mythrowaway.app.module.trash.entity.trash.OrdinalWeeklySchedule
import net.mythrowaway.app.module.trash.entity.trash.Trash
import net.mythrowaway.app.module.trash.entity.trash.TrashType
import net.mythrowaway.app.module.trash.usecase.ListTrashesUseCase
import net.mythrowaway.app.module.trash.dto.OrdinalWeeklyScheduleDTO
import net.mythrowaway.app.module.trash.dto.WeeklyScheduleDTO
import net.mythrowaway.app.module.trash.entity.trash.ExcludeDayOfMonth
import net.mythrowaway.app.module.trash.entity.trash.IntervalWeeklySchedule
import net.mythrowaway.app.module.trash.entity.trash.MonthlySchedule
import net.mythrowaway.app.module.trash.entity.trash.WeeklySchedule
import net.mythrowaway.app.module.trash.infra.PreferenceTrashRepositoryImpl
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

class ListTrashesUseCaseTest {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(
        InstrumentationRegistry.getInstrumentation().context
    )
    private val trashRepository = PreferenceTrashRepositoryImpl(
        InstrumentationRegistry.getInstrumentation().context
    )
    private val usecase =  ListTrashesUseCase(trashRepository)

    @Before
    fun before() {
        preferences.edit().clear().commit()
    }

    @Test
    fun get_multiple_trash_list() {
        // 登録データを表示するケース,正常にデータが取得できた場合はPresenterにそのまま渡す
        val trash1 = Trash(
            _id = "1",
            _type = TrashType.BURN,
            schedules = listOf(
                WeeklySchedule(_dayOfWeek = DayOfWeek.SUNDAY),
                OrdinalWeeklySchedule(_dayOfWeek = DayOfWeek.SATURDAY, _ordinalOfWeek = 1)
            ),
            _displayName = "",
            _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
        )
        val trash2 = Trash(
            _id = "2",
            _type = TrashType.OTHER,
            _displayName = "家電",
            schedules = listOf(
                MonthlySchedule(_day = 30),
                IntervalWeeklySchedule(_dayOfWeek = DayOfWeek.SUNDAY, _interval = 4, _start = LocalDate.of(2021, 1, 1))
            ),
            _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf(
                ExcludeDayOfMonth(_month = 1, _dayOfMonth = 1),
                ExcludeDayOfMonth(_month = 2, _dayOfMonth = 2)
                )
            )
        )
        trashRepository.saveTrash(trash1)
        trashRepository.saveTrash(trash2)

        val trashList = usecase.getTrashList()

        assertEquals(2,trashList.size)
        assertEquals("1",trashList[0].id)
        assertEquals("2",trashList[1].id)
        assertEquals(TrashType.BURN,trashList[0].type)
        assertEquals(TrashType.OTHER,trashList[1].type)
        assertEquals("家電",trashList[1].displayName)
        assertEquals(2,trashList[0].scheduleDTOList.size)
        assertEquals(2,trashList[1].scheduleDTOList.size)
        assertEquals(WeeklyScheduleDTO::class.java, trashList[0].scheduleDTOList[0].javaClass)
        assertEquals(OrdinalWeeklyScheduleDTO::class.java, trashList[0].scheduleDTOList[1].javaClass)
        assertEquals(MonthlyScheduleDTO::class.java, trashList[1].scheduleDTOList[0].javaClass)
        assertEquals(IntervalWeeklyScheduleDTO::class.java, trashList[1].scheduleDTOList[1].javaClass)
        assertEquals(0, (trashList[0].scheduleDTOList[0] as WeeklyScheduleDTO).dayOfWeek)
        assertEquals(6, (trashList[0].scheduleDTOList[1] as OrdinalWeeklyScheduleDTO).dayOfWeek)
        assertEquals(1, (trashList[0].scheduleDTOList[1] as OrdinalWeeklyScheduleDTO).ordinal)
        assertEquals(30, (trashList[1].scheduleDTOList[0] as MonthlyScheduleDTO).dayOfMonth)
        assertEquals(0, (trashList[1].scheduleDTOList[1] as IntervalWeeklyScheduleDTO).dayOfWeek)
        assertEquals(4, (trashList[1].scheduleDTOList[1] as IntervalWeeklyScheduleDTO).interval)
        assertEquals(LocalDate.of(2021, 1, 1), (trashList[1].scheduleDTOList[1] as IntervalWeeklyScheduleDTO).startDate)
        assertEquals(0, trashList[0].excludeDayOfMonthDTOList.size)
        assertEquals(2, (trashList[1].excludeDayOfMonthDTOList.size))
        assertEquals(1, (trashList[1].excludeDayOfMonthDTOList[0].month))
        assertEquals(1, (trashList[1].excludeDayOfMonthDTOList[0].dayOfMonth))
        assertEquals(2, (trashList[1].excludeDayOfMonthDTOList[1].month))
        assertEquals(2, (trashList[1].excludeDayOfMonthDTOList[1].dayOfMonth))
    }

    @Test
    fun get_single_trash_list() {
        val trash1 = Trash(
            _id = "1",
            _type = TrashType.PLASTIC,
            schedules = listOf(
                WeeklySchedule(_dayOfWeek = DayOfWeek.FRIDAY),
                OrdinalWeeklySchedule(_dayOfWeek = DayOfWeek.SUNDAY, _ordinalOfWeek = 1),
                IntervalWeeklySchedule(_dayOfWeek = DayOfWeek.SATURDAY, _interval = 2, _start = LocalDate.of(2021, 12, 31))
            ),
            _displayName = "",
            _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
        )
        trashRepository.saveTrash(trash1)

        val trashList = usecase.getTrashList()

        assertEquals(1,trashList.size)
        assertEquals("1",trashList[0].id)
        assertEquals(TrashType.PLASTIC,trashList[0].type)
        assertEquals(3, trashList[0].scheduleDTOList.size)
        assertEquals(WeeklyScheduleDTO::class.java, trashList[0].scheduleDTOList[0].javaClass)
        assertEquals(OrdinalWeeklyScheduleDTO::class.java, trashList[0].scheduleDTOList[1].javaClass)
        assertEquals(IntervalWeeklyScheduleDTO::class.java, trashList[0].scheduleDTOList[2].javaClass)
        assertEquals(5, (trashList[0].scheduleDTOList[0] as WeeklyScheduleDTO).dayOfWeek)
        assertEquals(0, (trashList[0].scheduleDTOList[1] as OrdinalWeeklyScheduleDTO).dayOfWeek)
        assertEquals(1, (trashList[0].scheduleDTOList[1] as OrdinalWeeklyScheduleDTO).ordinal)
        assertEquals(6, (trashList[0].scheduleDTOList[2] as IntervalWeeklyScheduleDTO).dayOfWeek)
        assertEquals(2, (trashList[0].scheduleDTOList[2] as IntervalWeeklyScheduleDTO).interval)
        assertEquals(LocalDate.of(2021, 12, 31), (trashList[0].scheduleDTOList[2] as IntervalWeeklyScheduleDTO).startDate)
        assertEquals(0, trashList[0].excludeDayOfMonthDTOList.size)
    }
}