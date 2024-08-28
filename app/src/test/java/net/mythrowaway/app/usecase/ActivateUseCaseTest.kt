package net.mythrowaway.app.usecase

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockito_kotlin.capture
import net.mythrowaway.app.domain.ExcludeDayOfMonthList
import net.mythrowaway.app.domain.Trash
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.domain.TrashList
import net.mythrowaway.app.domain.TrashType
import net.mythrowaway.app.domain.WeeklySchedule
import net.mythrowaway.app.domain.sync.RemoteTrash
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.*
import java.time.DayOfWeek

class ActivateUseCaseTest {
  @Mock private lateinit var mockConfigImpl: ConfigRepositoryInterface
  @Mock private lateinit var mockPersistImpl: DataRepositoryInterface
  @Mock private lateinit var mockAPIAdapterImpl: MobileApiInterface

  @InjectMocks private lateinit var instance: ActivateUseCase

  @Captor
  private lateinit var captorTimeStamp: ArgumentCaptor<Long>
  @Captor
  private lateinit var captorSyncState: ArgumentCaptor<Int>
  @Captor
  private lateinit var captorImportedTrashList: ArgumentCaptor<TrashList>

  @BeforeEach
  fun before() {
    MockitoAnnotations.openMocks(this)
    Mockito.reset(mockConfigImpl)
    Mockito.reset(mockPersistImpl)

    Mockito.`when`(mockConfigImpl.getUserId()).thenReturn("id001")
  }

  @Test
  fun activate_success() {
    // 正常なアクティベーションコードが渡された場合は
    // Configにタイムスタンプ,SYNC_STATEを保存
    // Persistに登録データ保存
    Mockito.`when`(mockAPIAdapterImpl.activate("12345678910", "id001")).thenReturn(
      RemoteTrash(
        _timestamp = 1234567890,
        _trashList = TrashList(listOf(
          Trash(
            _id = "id001",
            _type = TrashType.BURN,
            _displayName = "",
            schedules = listOf(
              WeeklySchedule(
                _dayOfWeek = DayOfWeek.MONDAY
              )
            ),
            _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
          )
        ))
      )
    )

    val mapper = ObjectMapper()
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)

    val result = instance.activate("12345678910")

    assertEquals(ActivateUseCase.ActivationResult.ACTIVATE_SUCCESS, result)

    Mockito.verify(mockConfigImpl,Mockito.times(1)).setTimestamp(capture(captorTimeStamp))
    assertEquals(1234567890,captorTimeStamp.value)

    Mockito.verify(mockConfigImpl,Mockito.times(1)).setSyncComplete()
    assertEquals(CalendarUseCase.SYNC_COMPLETE,captorSyncState.value)

    Mockito.verify(mockPersistImpl,Mockito.times(1)).importScheduleList(capture(captorImportedTrashList))
    assertEquals(1,captorImportedTrashList.value.trashList.size)
  }

  @Test
  fun activate_failed() {
    Mockito.`when`(mockAPIAdapterImpl.activate("failed_code", "id001")).thenReturn(null)
    val result = instance.activate("failed_code")
    assertEquals(ActivateUseCase.ActivationResult.ACTIVATE_ERROR, result)
  }
}