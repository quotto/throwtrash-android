package net.mythrowaway.app.usecase

import androidx.core.content.edit
import androidx.preference.PreferenceManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.nhaarman.mockito_kotlin.capture
import net.mythrowaway.app.module.info.infra.PreferenceUserRepositoryImpl
import net.mythrowaway.app.module.info.service.UserIdService
import net.mythrowaway.app.module.info.usecase.InformationUseCase
import net.mythrowaway.app.module.migration.infra.MigrationApiInterface
import net.mythrowaway.app.module.migration.infra.PreferenceMigrationRepositoryImpl
import net.mythrowaway.app.module.migration.infra.PreferenceVersionRepositoryImpl
import net.mythrowaway.app.module.migration.usecase.MigrationUseCase
import net.mythrowaway.app.module.review.infra.PreferenceReviewRepositoryImpl
import net.mythrowaway.app.module.review.service.ReviewService
import net.mythrowaway.app.module.trash.infra.PreferenceSyncRepositoryImpl
import net.mythrowaway.app.module.trash.infra.PreferenceTrashRepositoryImpl
import net.mythrowaway.app.module.trash.service.TrashService
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class MigrationUseCaseTest {
  @Mock
  private lateinit var mockMigrationApi: MigrationApiInterface

  @Captor
  private lateinit var captorUserId: ArgumentCaptor<String>

  private val preferences by lazy {
    PreferenceManager.getDefaultSharedPreferences(
      InstrumentationRegistry.getInstrumentation().context
    )
  }

  private val versionRepository = PreferenceVersionRepositoryImpl(
    InstrumentationRegistry.getInstrumentation().context
  )

  private val reviewRepository = PreferenceReviewRepositoryImpl(
    InstrumentationRegistry.getInstrumentation().context
  )

  private val userRepository = PreferenceUserRepositoryImpl(
    InstrumentationRegistry.getInstrumentation().context
  )

  private lateinit var usecase: MigrationUseCase

  @Before
  fun before() {
    preferences.edit().clear().commit()
    MockitoAnnotations.openMocks(this)
    Mockito.reset(mockMigrationApi)
    usecase = MigrationUseCase(
      repository = versionRepository,
      migrationRepository = PreferenceMigrationRepositoryImpl(
        InstrumentationRegistry.getInstrumentation().context
      ),
      userIdService = UserIdService(
        InformationUseCase(
          userRepository = userRepository
        )
      ),
      trashService = TrashService(
        syncRepository = PreferenceSyncRepositoryImpl(
          InstrumentationRegistry.getInstrumentation().context
        ),
        trashRepository = PreferenceTrashRepositoryImpl(
          InstrumentationRegistry.getInstrumentation().context
        )
      ),
      reviewService = ReviewService(
        reviewRepository = reviewRepository
      ),
      api = mockMigrationApi
    )
  }

  @Test
  fun saved_review_data_when_migration_2_to_3_and_old_data_is_empty() {
    versionRepository.updateConfigVersion(2)

    usecase.migration(3)

    val review = reviewRepository.find()

    assert(review?.reviewed == false)
    assert(review?.reviewedAt == 0L)
    assert(review?.continuousUseDateCount == 0)
    assert(review?.lastLaunchedAt == 0L)
    Assert.assertEquals(3, versionRepository.getConfigVersion())
  }

  @Test
  fun converted_review_data_when_migration_2_to_3_and_old_data_is_not_empty() {
    versionRepository.updateConfigVersion(2)

    preferences.edit(commit = true) {
      putLong("KEY_LAST_USED_TIME", 1614556800L)
      putInt("KEY_CONTINUOUS_DATE", 1)
      putBoolean("KEY_REVIEWED", true)
    }
    usecase.migration(3)

    val review = reviewRepository.find()

    Assert.assertTrue(review?.reviewed!!)
    Assert.assertEquals(review.reviewedAt, 0L)
    Assert.assertEquals(review.continuousUseDateCount, 1)
    Assert.assertEquals(review.lastLaunchedAt, 1614556800L)
    Assert.assertEquals(3, versionRepository.getConfigVersion())
  }

  @Test
  fun migration_not_executed_when_migration_3_to_3(){
    versionRepository.updateConfigVersion(3)
    usecase.migration(3)
    Assert.assertEquals(3, versionRepository.getConfigVersion())
  }

  @Test
  fun migration_not_executed_and_set_config_version_3_when_current_version_is_0(){
    versionRepository.updateConfigVersion(0)
    usecase.migration(3)
    Assert.assertEquals(3, versionRepository.getConfigVersion())
  }

  @Test
  fun update_trash_schedule_when_migration_1_to_2()  {
    versionRepository.updateConfigVersion(1)
    userRepository.saveUserId("id001")

    Mockito.`when`(mockMigrationApi.updateTrashScheduleTimestamp("id001")).thenReturn(100000)

    usecase.migration(2)

    Mockito.verify(mockMigrationApi, Mockito.times(1)).updateTrashScheduleTimestamp(capture(captorUserId))
    Assert.assertEquals("id001", captorUserId.value)
    Mockito.verify(mockMigrationApi, Mockito.times(1)).updateTrashScheduleTimestamp("id001")
    Assert.assertEquals(2, versionRepository.getConfigVersion())
  }

  @Test
  fun not_update_trash_schedule_when_migration_1_to_2_and_user_id_is_empty()  {
    versionRepository.updateConfigVersion(1)

    usecase.migration(2)

    Mockito.verify(mockMigrationApi, Mockito.times(0)).updateTrashScheduleTimestamp(capture(captorUserId))
    Assert.assertEquals(2, versionRepository.getConfigVersion())
  }

  @Test
  fun migration_executed_and_set_config_version_3_when_migration_1_to_3() {
    versionRepository.updateConfigVersion(1)
    userRepository.saveUserId("id001")
    Mockito.`when`(mockMigrationApi.updateTrashScheduleTimestamp("id001")).thenReturn(100000)

    usecase.migration(3)

    Mockito.verify(mockMigrationApi, Mockito.times(1)).updateTrashScheduleTimestamp(capture(captorUserId))
    Assert.assertEquals("id001", captorUserId.value)
    Mockito.verify(mockMigrationApi, Mockito.times(1)).updateTrashScheduleTimestamp("id001")

    val review = reviewRepository.find()
    Assert.assertFalse(review?.reviewed!!)
    Assert.assertEquals(review.reviewedAt, 0L)
    Assert.assertEquals(review.continuousUseDateCount, 0)
    Assert.assertEquals(review.lastLaunchedAt, 0L)
    Assert.assertEquals(3, versionRepository.getConfigVersion())
  }

}