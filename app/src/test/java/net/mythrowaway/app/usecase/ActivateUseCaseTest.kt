package net.mythrowaway.app.usecase

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockito_kotlin.capture
import net.mythrowaway.app.domain.LatestTrashData
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.domain.TrashSchedule
import net.mythrowaway.app.domain.TrashType
import net.mythrowaway.app.service.TrashManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.*

class ActivateUseCaseTest {
    @Mock private lateinit var mockConfigImpl: ConfigRepositoryInterface
    @Mock private lateinit var mockPersistImpl: DataRepositoryInterface
    @Mock private lateinit var mockAPIAdapterImpl: MobileApiInterface
    @Mock private lateinit var mockPresenter: ActivatePresenterInterface
    @Mock private lateinit var mockTrashManager: TrashManager

    @InjectMocks private lateinit var instance: ActivateUseCase

    @Captor
    private lateinit var captorResultCode: ArgumentCaptor<ActivateUseCase.ActivationResult>
    @Captor
    private lateinit var captorTimeStamp: ArgumentCaptor<Long>
    @Captor
    private lateinit var captorSyncState: ArgumentCaptor<Int>
    @Captor
    private lateinit var captorScheduleList: ArgumentCaptor<ArrayList<TrashData>>

    @BeforeEach
    fun before() {
        MockitoAnnotations.openMocks(this)
        Mockito.reset(mockConfigImpl)
        Mockito.reset(mockPersistImpl)
        Mockito.reset(mockPresenter)
        Mockito.reset(mockTrashManager)

        Mockito.`when`(mockConfigImpl.getUserId()).thenReturn("id001")
    }

    @Test
    fun activate_success() {
        // 正常なアクティベーションコードが渡された場合は
        // Configにタイムスタンプ,SYNC_STATEを保存
        // Persistに登録データ保存
        // PresenterのnotifyにACTIVATE_SUCCESSを渡す
        Mockito.`when`(mockAPIAdapterImpl.activate("12345678910", "id001")).thenReturn(LatestTrashData().apply{
            this.timestamp = 1234567890
            this.scheduleList = arrayListOf(TrashData().apply {
                this.type = TrashType.BURN
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

        Mockito.verify(mockConfigImpl,Mockito.times(1)).setTimestamp(capture(captorTimeStamp))
        assertEquals(1234567890,captorTimeStamp.value)

        Mockito.verify(mockConfigImpl,Mockito.times(1)).setSyncState(capture(captorSyncState))
        assertEquals(CalendarUseCase.SYNC_COMPLETE,captorSyncState.value)

        Mockito.verify(mockPersistImpl,Mockito.times(1)).importScheduleList(capture(captorScheduleList))
        assertEquals(1,captorScheduleList.value.size)

        Mockito.verify(mockTrashManager,Mockito.times(1)).refresh()

        Mockito.verify(mockPresenter,Mockito.times(1)).notify(capture(captorResultCode))
        assertEquals(ActivateUseCase.ActivationResult.ACTIVATE_SUCCESS,captorResultCode.value)

    }

    @Test
    fun activate_failed() {
        Mockito.`when`(mockAPIAdapterImpl.activate("failed_code", "id001")).thenReturn(null)
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