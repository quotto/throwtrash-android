package net.mythrowaway.app.module.trash.usecase

import net.mythrowaway.app.module.trash.dto.ExcludeDayOfMonthDTO
import net.mythrowaway.app.module.trash.entity.trash.ExcludeDayOfMonth
import net.mythrowaway.app.module.trash.entity.trash.ExcludeDayOfMonthList
import net.mythrowaway.app.module.trash.entity.trash.TrashList
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class CommonExcludeDayOfMonthUseCaseTest {

  @Test
  fun getCommonExcludeDays_returns_globalExcludes() {
    val repository = mock<TrashRepositoryInterface>()
    val syncRepository = mock<SyncRepositoryInterface>()
    val trashList = TrashList(
      trashList = listOf(),
      _globalExcludeDayOfMonthList = ExcludeDayOfMonthList(
        mutableListOf(ExcludeDayOfMonth(1, 2))
      )
    )
    whenever(repository.getAllTrash()).thenReturn(trashList)

    val useCase = CommonExcludeDayOfMonthUseCase(repository, syncRepository)
    val result = useCase.getCommonExcludeDays()

    assertEquals(1, result.size)
    assertEquals(1, result[0].month)
    assertEquals(2, result[0].dayOfMonth)
  }

  @Test
  fun saveCommonExcludeDays_updates_globalExcludes() {
    val repository = mock<TrashRepositoryInterface>()
    val syncRepository = mock<SyncRepositoryInterface>()
    val trashList = TrashList(trashList = listOf())
    whenever(repository.getAllTrash()).thenReturn(trashList)

    val useCase = CommonExcludeDayOfMonthUseCase(repository, syncRepository)
    useCase.saveCommonExcludeDays(listOf(ExcludeDayOfMonthDTO(3, 4)))

    val captor = argumentCaptor<TrashList>()
    verify(repository).replaceTrashList(captor.capture())
    verify(syncRepository).setSyncWait()
    val updated = captor.firstValue
    assertEquals(1, updated.globalExcludeDayOfMonthList.members.size)
    assertEquals(3, updated.globalExcludeDayOfMonthList.members[0].month)
    assertEquals(4, updated.globalExcludeDayOfMonthList.members[0].dayOfMonth)
  }

  @Test
  fun saveCommonExcludeDays_trims_over_10_items() {
    val repository = mock<TrashRepositoryInterface>()
    val syncRepository = mock<SyncRepositoryInterface>()
    val trashList = TrashList(trashList = listOf())
    whenever(repository.getAllTrash()).thenReturn(trashList)

    val excludes = (1..11).map { ExcludeDayOfMonthDTO(1, it.coerceAtMost(28)) }

    val useCase = CommonExcludeDayOfMonthUseCase(repository, syncRepository)
    useCase.saveCommonExcludeDays(excludes)

    val captor = argumentCaptor<TrashList>()
    verify(repository).replaceTrashList(captor.capture())
    verify(syncRepository).setSyncWait()
    val updated = captor.firstValue
    assertEquals(10, updated.globalExcludeDayOfMonthList.members.size)
  }
}
