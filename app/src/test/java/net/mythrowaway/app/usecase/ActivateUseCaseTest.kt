package net.mythrowaway.app.usecase

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockito_kotlin.capture
import net.mythrowaway.app.domain.RegisteredData
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.domain.TrashSchedule
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.*
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(
    TrashManager::class
)
class ActivateUseCaseTest {
    @Mock
    private lateinit var mockConfigImpl: IConfigRepository
    @Mock
    private lateinit var mockPersistImpl: IPersistentRepository
    @Mock
    private lateinit var mockAPIAdapterImpl: IAPIAdapter
    @Mock
    private lateinit var mockPresenter: IActivatePresenter
    private val mockTrashManager = PowerMockito.mock(TrashManager::class.java)

    @InjectMocks
    private lateinit var instance: ActivateUseCase

    @Captor
    private lateinit var captorResultCode: ArgumentCaptor<ActivateUseCase.ActivationResult>
    @Captor
    private lateinit var captorId: ArgumentCaptor<String>
    @Captor
    private lateinit var captorTimeStamp: ArgumentCaptor<Long>
    @Captor
    private lateinit var captorSyncState: ArgumentCaptor<Int>
    @Captor
    private lateinit var captorScheduleList: ArgumentCaptor<ArrayList<TrashData>>

    @Before
    fun before() {
        Mockito.clearInvocations(mockConfigImpl)
        Mockito.clearInvocations(mockPersistImpl)
        Mockito.clearInvocations(mockPresenter)
    }

    @Test
    fun activate_success() {
        // 正常なアクティベーションコードが渡された場合は
        // ConfigにユーザーID,タイムスタンプ,SYNC_STATEを保存
        // Persistに登録データ保存
        // PresenterのnotifyにACTIVATE_SUCCESSを渡す
        Mockito.`when`(mockAPIAdapterImpl.activate("12345678910")).thenReturn(RegisteredData().apply{
            this.id = "id-00001"
            this.timestamp = 1234567890
            this.scheduleList = arrayListOf(TrashData().apply {
                this.type = "burn"
                this.schedules = arrayListOf(TrashSchedule().apply {
                    this.type = "weekday"
                    this.value = "1"
                })
            })
        })

        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)

        instance.activate("12345678910")

        Mockito.verify(mockPresenter,Mockito.times(1)).notify(capture(captorResultCode))
        assertEquals(ActivateUseCase.ActivationResult.ACTIVATE_SUCCESS,captorResultCode.value)

        Mockito.verify(mockConfigImpl,Mockito.times(1)).setUserId(capture(captorId))
        assertEquals("id-00001",captorId.value)

        Mockito.verify(mockConfigImpl,Mockito.times(1)).setTimestamp(capture(captorTimeStamp))
        assertEquals(1234567890,captorTimeStamp.value)

        Mockito.verify(mockConfigImpl,Mockito.times(1)).setSyncState(capture(captorSyncState))
        assertEquals(CalendarUseCase.SYNC_COMPLETE,captorSyncState.value)

        Mockito.verify(mockPersistImpl,Mockito.times(1)).importScheduleList(capture(captorScheduleList))
        assertEquals(1,captorScheduleList.value.size)

        Mockito.verify(mockPresenter,Mockito.times(1)).notify(capture(captorResultCode))
        assertEquals(ActivateUseCase.ActivationResult.ACTIVATE_SUCCESS,captorResultCode.value)

    }

    @Test
    fun activate_failed() {
        Mockito.`when`(mockAPIAdapterImpl.activate("failed_code")).thenReturn(null)
        instance.activate("failed_code")
        Mockito.verify(mockPresenter,Mockito.times(1)).notify(capture(captorResultCode))
        assertEquals(ActivateUseCase.ActivationResult.ACTIVATE_ERROR, captorResultCode.value)
    }

    @Test
    fun checkCode_valid() {
        instance.checkCode("1234567890")
        Mockito.verify(mockPresenter,Mockito.times(1)).notify(capture(captorResultCode))
        assertEquals(ActivateUseCase.ActivationResult.VALID_CODE, captorResultCode.value)
    }

    @Test
    fun checkCode_invalid() {
        instance.checkCode("123456789")
        instance.checkCode("")

        Mockito.verify(mockPresenter,Mockito.times(2)).notify(capture(captorResultCode))


        assertEquals(ActivateUseCase.ActivationResult.INVALID_CODE, captorResultCode.allValues[0])
        assertEquals(ActivateUseCase.ActivationResult.INVALID_CODE, captorResultCode.allValues[1])
    }

}