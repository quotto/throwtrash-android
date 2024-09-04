package net.mythrowaway.app.usecase

import net.mythrowaway.app.domain.migration.usecase.VersionRepositoryInterface
import net.mythrowaway.app.domain.trash.entity.trash.ExcludeDayOfMonthList
import net.mythrowaway.app.domain.trash.entity.trash.OrdinalWeeklySchedule
import net.mythrowaway.app.domain.trash.entity.trash.Trash
import net.mythrowaway.app.domain.trash.entity.trash.TrashList
import net.mythrowaway.app.domain.trash.entity.trash.TrashType
import net.mythrowaway.app.domain.trash.usecase.DeleteTrashUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.time.DayOfWeek

class DeleteTrashUseCaseTest {
  @Mock
  private lateinit var mockPersist: TrashRepositoryInterface
  @Mock
  private lateinit var mockConfig: VersionRepositoryInterface
  @InjectMocks
  private lateinit var target: DeleteTrashUseCase

  @Captor
  private lateinit var captorId: ArgumentCaptor<String>

  @BeforeEach
  fun before() {
    MockitoAnnotations.openMocks(this)
    Mockito.reset(mockConfig)
    Mockito.reset(mockPersist)
  }

  @Test
  fun deleteTrash() {
    val trash1 = Trash(
      _id = "0",
      _type = TrashType.BURN,
      schedules = listOf(
        OrdinalWeeklySchedule(_dayOfWeek = DayOfWeek.SUNDAY, _ordinalOfWeek = 3),
        OrdinalWeeklySchedule(_dayOfWeek = DayOfWeek.SATURDAY, _ordinalOfWeek = 1)
      ),
      _displayName = "",
      _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
    )
    val trash2 = Trash(
      _id = "1",
      _type = TrashType.OTHER,
      _displayName = "家電",
      schedules = listOf(
        OrdinalWeeklySchedule(_dayOfWeek = DayOfWeek.SUNDAY, _ordinalOfWeek = 3)
      ),
      _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
    )

    Mockito.`when`(mockPersist.getAllTrash()).thenReturn(
      TrashList(listOf(trash1, trash2))
    )
    target.deleteTrash("0")

    // IPersistentRepositoryのdeleteTrashDataのパラメータにIDが指定されている
//    Mockito.verify(mockPersist, Mockito.times(1)).deleteTrash(capture(captorId))
//    Assertions.assertEquals("0", captorId.value)
//
//    // Configの同期状態はSYNC_WAITINGに設定される
//    Mockito.verify(mockConfig, Mockito.times(1)).setSyncWait()
  }
}