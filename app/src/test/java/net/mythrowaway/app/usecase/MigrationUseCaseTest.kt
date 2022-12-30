package net.mythrowaway.app.usecase

import com.nhaarman.mockito_kotlin.capture
import net.mythrowaway.app.adapter.repository.MigrationApiInterface
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
class MigrationUseCaseTest {
  @Mock private lateinit var mockConfigRepository: ConfigRepositoryInterface

  @Mock private lateinit var mockMigrationApi: MigrationApiInterface

  @InjectMocks
  private lateinit var useCase: MigrationUseCase

  @Captor private lateinit var captorUserId: ArgumentCaptor<String>
  @Captor private lateinit var captorConfigVersion: ArgumentCaptor<Int>
  @Captor private lateinit var captorUpdateTime: ArgumentCaptor<Long>

  @Before
  fun before(){
    Mockito.reset(mockConfigRepository)
    Mockito.reset(mockMigrationApi)
  }

  @Test
  fun migration_DoNotMigration_ConfigurationVersionIsLatest(){
    Mockito.`when`(mockConfigRepository.getUserId()).thenReturn("id001")
    Mockito.`when`(mockConfigRepository.getConfigVersion()).thenReturn(2)
    useCase.migration(2)
    Mockito.verify(mockMigrationApi,Mockito.times(0)).updateTrashScheduleTimestamp("id001")
  }

  @Test
  fun migration_DoNotMigration_NothingCurrentConfigurationVersion(){
    Mockito.`when`(mockConfigRepository.getUserId()).thenReturn("id001")
    Mockito.`when`(mockConfigRepository.getConfigVersion()).thenReturn(0)
    useCase.migration(2)
    Mockito.verify(mockMigrationApi,Mockito.times(0)).updateTrashScheduleTimestamp("id001")
    Mockito.verify(mockConfigRepository, Mockito.times(1)).updateConfigVersion(capture(captorConfigVersion))
    assertEquals(2, captorConfigVersion.value)
  }

  @Test
  fun migration_1to2() {
    Mockito.`when`(mockConfigRepository.getUserId()).thenReturn("id001")
    Mockito.`when`(mockConfigRepository.getConfigVersion()).thenReturn(1)
    Mockito.`when`(mockMigrationApi.updateTrashScheduleTimestamp("id001")).thenReturn(100000)
    useCase.migration(2)
    Mockito.verify(mockMigrationApi,Mockito.times(1)).updateTrashScheduleTimestamp(capture(captorUserId))
    assertEquals("id001", captorUserId.value)
    Mockito.verify(mockConfigRepository,Mockito.times(1)).setTimestamp(capture(captorUpdateTime))
    assertEquals(100000, captorUpdateTime.value)
    Mockito.verify(mockConfigRepository, Mockito.times(1)).updateConfigVersion(capture(captorConfigVersion))
    assertEquals(2, captorConfigVersion.value)
  }

  @Test
  fun migration_1to2_failed_updateTimestamp() {
    Mockito.`when`(mockConfigRepository.getUserId()).thenReturn("id001")
    Mockito.`when`(mockConfigRepository.getConfigVersion()).thenReturn(1)
    Mockito.`when`(mockMigrationApi.updateTrashScheduleTimestamp("id001")).thenReturn(-1)
    useCase.migration(2)
    Mockito.verify(mockMigrationApi,Mockito.times(1)).updateTrashScheduleTimestamp(capture(captorUserId))
    Mockito.verify(mockConfigRepository,Mockito.times(0)).setTimestamp(capture(captorUpdateTime))
    Mockito.verify(mockConfigRepository, Mockito.times(1)).updateConfigVersion(capture(captorConfigVersion))
    assertEquals(2, captorConfigVersion.value)
  }
  @Test
  fun migration_1to2_nothingUserId() {
    Mockito.`when`(mockConfigRepository.getUserId()).thenReturn(null)
    Mockito.`when`(mockConfigRepository.getConfigVersion()).thenReturn(1)
    useCase.migration(2)
    Mockito.verify(mockMigrationApi,Mockito.times(0)).updateTrashScheduleTimestamp(capture(captorUserId))
    Mockito.verify(mockConfigRepository, Mockito.times(1)).updateConfigVersion(capture(captorConfigVersion))
    assertEquals(2, captorConfigVersion.value)
  }
}