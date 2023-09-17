package net.mythrowaway.app.controller

import com.nhaarman.mockito_kotlin.capture
import com.nhaarman.mockito_kotlin.verify
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.mythrowaway.app.adapter.controller.CalendarControllerImpl
import net.mythrowaway.app.service.CalendarManagerImpl
import net.mythrowaway.app.usecase.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.Spy

class CalendarControllerTest {
    @InjectMocks
    private lateinit var controller: CalendarControllerImpl

    @Captor
    private lateinit var captorYear: ArgumentCaptor<Int>
    @Captor
    private lateinit var captorMonth: ArgumentCaptor<Int>

    @Mock
    private lateinit var mockUseCase: CalendarUseCase

    @Spy
    private lateinit var mockCalendarManager: CalendarManagerImpl

    @BeforeEach
    fun before() {
        MockitoAnnotations.openMocks(this)
        Mockito.`when`(mockCalendarManager.getYear()).thenReturn(2020)
        Mockito.`when`(mockCalendarManager.getMonth()).thenReturn(1)
    }

    @AfterEach
    fun after() {
        Mockito.reset(mockCalendarManager)
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

        assertEquals(2020, captorYear.value)
        assertEquals(2, captorMonth.value)
    }

    @Test
    fun generateCalendarFromPositionAsync_next_13th_month_from_January() {
        // 1月の13ヶ月後は翌年であること
        runBlocking {
            launch {
                controller.generateCalendarFromPositionAsync(13)
            }
        }
        verify(mockUseCase, Mockito.times(1)).generateMonthSchedule(
            capture(captorYear),
            capture(captorMonth)
        )

        assertEquals(2021,captorYear.value)
        assertEquals(2,captorMonth.value)
    }
}