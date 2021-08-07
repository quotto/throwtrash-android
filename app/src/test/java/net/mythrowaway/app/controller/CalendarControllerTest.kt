package net.mythrowaway.app.controller

import com.nhaarman.mockito_kotlin.capture
import com.nhaarman.mockito_kotlin.verify
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.mythrowaway.app.adapter.controller.CalendarControllerImpl
import net.mythrowaway.app.usecase.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(
    CalendarUseCase::class,
    CalendarManager::class
)
class CalendarControllerTest {
    private val mockUseCase: CalendarUseCase = PowerMockito.mock(CalendarUseCase::class.java)
    private val mockCalendarManager = PowerMockito.spy(CalendarManager())
    @InjectMocks
    private lateinit var controller: CalendarControllerImpl

    @Captor
    private lateinit var captorYear: ArgumentCaptor<Int>
    @Captor
    private lateinit var captorMonth: ArgumentCaptor<Int>

    @Before
    fun before() {
        Mockito.`when`(mockCalendarManager.getYear()).thenReturn(2020)
        Mockito.`when`(mockCalendarManager.getMonth()).thenReturn(1)
    }

    @Test
    fun generateCalendarFromPositionAsync_next_month_from_January() {
        // 1月の1ヶ月後のカレンダーは同じ年であること
        // 2020年1月の1ヶ月後
        runBlocking {
            launch {
                controller.generateCalendarFromPositionAsync(1)
            }
        }
        verify(mockUseCase, Mockito.times(1)).generateMonthSchedule(
            capture(captorYear),
            capture(captorMonth)
        )

        Assert.assertEquals(2020, captorYear.value)
        Assert.assertEquals(2, captorMonth.value)
    }

    fun generateCalendarFromPositionAsync_next_13th_month_from_January() {
        // 1月の13ヶ月後は翌年であること
        runBlocking {
            launch {
                controller.generateCalendarFromPositionAsync(13)
            }
        }

        Assert.assertEquals(2021,captorYear.allValues[1])
        Assert.assertEquals(2,captorMonth.allValues[1])
    }
}