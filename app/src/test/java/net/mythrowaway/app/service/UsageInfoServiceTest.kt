package net.mythrowaway.app.service

import android.content.Context
import com.nhaarman.mockito_kotlin.capture
import net.mythrowaway.app.usecase.IConfigRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.*

@RunWith(PowerMockRunner::class)
@PrepareForTest(
    Calendar::class
)
class UsageInfoServiceTest {
    @Mock
    private lateinit var mockedConfigRepository: IConfigRepository
    @Mock
    private lateinit var mockedApplicationContext: Context

    @InjectMocks
    private lateinit var usageInfoService: UsageInfoService

    @Captor
    private lateinit var captorContinuousDate: ArgumentCaptor<Int>

    val today: Calendar by lazy {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, 2021)
        cal.set(Calendar.MONTH, 0)
        cal.set(Calendar.DATE, 1)
        cal.set(Calendar.HOUR, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.timeInMillis
        cal
    }

    @Before
    fun before() {
        Mockito.reset(mockedConfigRepository)
        Mockito.`when`(mockedConfigRepository.getContinuousDate()).thenReturn(1)
    }

    @Test
    fun recordLastUsedTimeWhenNoConfig(){
        val mockedLastUsedTime = Calendar.getInstance()
        mockedLastUsedTime.set(Calendar.YEAR,2021)
        mockedLastUsedTime.set(Calendar.MONTH,0)
        mockedLastUsedTime.set(Calendar.DATE,1)
        mockedLastUsedTime.set(Calendar.HOUR,0)
        mockedLastUsedTime.set(Calendar.MINUTE,0)
        mockedLastUsedTime.set(Calendar.SECOND,0)
        mockedLastUsedTime.timeInMillis
        Mockito.`when`(mockedConfigRepository.getLastUsedTime()).thenReturn(mockedLastUsedTime.timeInMillis)

        usageInfoService.initialize()
        usageInfoService.recordLastUsedTime(today)

        Mockito.verify(mockedConfigRepository,Mockito.times(0))
            .updateContinuousDate(capture(captorContinuousDate))
        Mockito.verify(mockedConfigRepository, Mockito.times(1)).updateLastUsedTime()
    }

    @Test
    fun recordLastUsedTimeWhenUsedLastDay() {
        val yesterday = Calendar.getInstance()
        yesterday.set(Calendar.YEAR,2020)
        yesterday.set(Calendar.MONTH,11)
        yesterday.set(Calendar.DATE,31)
        yesterday.set(Calendar.HOUR,0)
        yesterday.set(Calendar.MINUTE,0)
        yesterday.set(Calendar.SECOND,0)

        Mockito.`when`(mockedConfigRepository.getLastUsedTime()).thenReturn(yesterday.timeInMillis)

        usageInfoService.initialize()
        usageInfoService.recordLastUsedTime(today)

        Mockito.verify(mockedConfigRepository,Mockito.times(1))
            .updateContinuousDate(capture(captorContinuousDate))
        Assert.assertEquals(2,captorContinuousDate.value)
        Mockito.verify(mockedConfigRepository, Mockito.times(1)).updateLastUsedTime()
    }

    @Test
    fun recordLastUsedTimeWhenUsedTwoDaysAgo() {
        val twoDaysAgo = Calendar.getInstance()
        twoDaysAgo.set(Calendar.YEAR,2020)
        twoDaysAgo.set(Calendar.MONTH,11)
        twoDaysAgo.set(Calendar.DATE,30)
        twoDaysAgo.set(Calendar.HOUR,0)
        twoDaysAgo.set(Calendar.MINUTE,0)
        twoDaysAgo.set(Calendar.SECOND,0)

        Mockito.`when`(mockedConfigRepository.getLastUsedTime()).thenReturn(twoDaysAgo.timeInMillis)

        usageInfoService.initialize()
        usageInfoService.recordLastUsedTime(today)

        Mockito.verify(mockedConfigRepository,Mockito.times(1))
            .updateContinuousDate(capture(captorContinuousDate))
        Assert.assertEquals(1,captorContinuousDate.value)
        Mockito.verify(mockedConfigRepository, Mockito.times(1)).updateLastUsedTime()
    }

    @Test
    fun isContinuousDateWhen1Days() {
        Mockito.`when`(mockedConfigRepository.getContinuousDate()).thenReturn(1)
        usageInfoService.initialize()
        Assert.assertEquals(false,usageInfoService.isContinuousUsed())
    }

    @Test
    fun isContinuousDateWhen3Days() {
        Mockito.`when`(mockedConfigRepository.getContinuousDate()).thenReturn(3)
        usageInfoService.initialize()
        Assert.assertEquals(true,usageInfoService.isContinuousUsed())
    }
}