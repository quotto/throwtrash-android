package net.mythrowaway.app.usecase

import androidx.preference.PreferenceManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.nhaarman.mockito_kotlin.any
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.mythrowaway.app.module.account.infra.PreferenceUserRepositoryImpl
import net.mythrowaway.app.module.account.service.AuthService
import net.mythrowaway.app.module.account.service.UserIdService
import net.mythrowaway.app.module.account.usecase.AccountUseCase
import net.mythrowaway.app.module.account.usecase.AuthManagerInterface
import net.mythrowaway.app.module.account.usecase.UserApiInterface
import net.mythrowaway.app.module.migration.infra.MigrationApiInterface
import net.mythrowaway.app.module.migration.infra.PreferenceVersionRepositoryImpl
import net.mythrowaway.app.module.migration.usecase.MigrationUseCase
import net.mythrowaway.app.module.trash.infra.PreferenceSyncRepositoryImpl
import net.mythrowaway.app.module.trash.infra.PreferenceTrashRepositoryImpl
import net.mythrowaway.app.module.trash.service.TrashService
import net.mythrowaway.app.module.trash.usecase.ResetTrashUseCase
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class MigrationUseCaseTest {
  @Mock private lateinit var mockMigrationApi: MigrationApiInterface

  @Mock private lateinit var mockUserApi: UserApiInterface
  @Mock private lateinit var mockAuthManager: AuthManagerInterface

  private val preferences by lazy {
    PreferenceManager.getDefaultSharedPreferences(
      InstrumentationRegistry.getInstrumentation().context
    )
  }

  private val versionRepository = PreferenceVersionRepositoryImpl(
    InstrumentationRegistry.getInstrumentation().context
  )

  private val userRepository = PreferenceUserRepositoryImpl(
    InstrumentationRegistry.getInstrumentation().context
  )
  private val syncRepository = PreferenceSyncRepositoryImpl(
    InstrumentationRegistry.getInstrumentation().context
  )
  private val trashRepository = PreferenceTrashRepositoryImpl(
    InstrumentationRegistry.getInstrumentation().context
  )

  private lateinit var usecase: MigrationUseCase

  @Before
  fun before() {
    preferences.edit().clear().commit()
    MockitoAnnotations.openMocks(this)
    Mockito.reset(mockMigrationApi)
    val resetTrashUseCase = ResetTrashUseCase(
      syncRepository = syncRepository,
      trashRepository = trashRepository
    )
    usecase = MigrationUseCase(
      repository = versionRepository,
      userIdService = UserIdService(
        AccountUseCase(
          userRepository = userRepository,
          userApi = mockUserApi,
          authManager = mockAuthManager,
          trashService = TrashService(
            trashRepository = trashRepository,
            syncRepository = syncRepository,
            resetTrashUseCase = resetTrashUseCase
          ),
        )
      ),
      api = mockMigrationApi,
      authService = AuthService(
        usecase = AccountUseCase(
          userRepository = userRepository,
          userApi = mockUserApi,
          authManager = mockAuthManager,
          trashService = TrashService(
            trashRepository = trashRepository,
            syncRepository = syncRepository,
            resetTrashUseCase = resetTrashUseCase
          ),
        )
      )
    )
    runBlocking {
      Mockito.`when`(mockAuthManager.getIdToken(any())).thenReturn(Result.success("dummyIdToken"))
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun signup_complete_and_update_config_version_to_4_when_current_config_version_is_3() = runTest {
    versionRepository.updateConfigVersion(3)
    userRepository.saveUserId("id001")

    launch {
      usecase.migration(4)
    }
    advanceUntilIdle()
    Mockito.verify(mockMigrationApi, Mockito.times(1)).signUp("id001", "dummyIdToken")
    Assert.assertEquals(4, versionRepository.getConfigVersion())
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun signup_complete_and_update_config_version_to_4_when_current_config_version_is_0() = runTest{
    versionRepository.updateConfigVersion(0)
    userRepository.saveUserId("id001")

    launch {
      usecase.migration(4)
    }
    advanceUntilIdle()
    Assert.assertEquals(4, versionRepository.getConfigVersion())
    Mockito.verify(mockMigrationApi, Mockito.times(1)).signUp("id001", "dummyIdToken")
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun signup_is_not_executed_when_migration_4_to_4() = runTest {
    versionRepository.updateConfigVersion(4)
    launch {
      usecase.migration(4)
    }
    advanceUntilIdle()

    Assert.assertEquals(4, versionRepository.getConfigVersion())
    Mockito.verify(mockMigrationApi, Mockito.times(0)).signUp(any(), any())
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun signup_is_not_execute_and_update_config_version_to_4_when_user_id_is_null_and_config_version_is_older_then_4() = runTest {
    versionRepository.updateConfigVersion(3)
    userRepository.deleteUserId()

    launch {
      usecase.migration(4)
    }
    advanceUntilIdle()

    Assert.assertEquals(4, versionRepository.getConfigVersion())
    Mockito.verify(mockMigrationApi, Mockito.times(0)).signUp(any(), any())
  }

}