package net.mythrowaway.app.edit

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import net.mythrowaway.app.R
import net.mythrowaway.app.module.trash.entity.trash.Trash
import net.mythrowaway.app.module.trash.entity.trash.TrashList
import net.mythrowaway.app.module.trash.presentation.view.edit.CommonExcludeDayOfMonthScreen
import net.mythrowaway.app.module.trash.presentation.view_model.edit.CommonExcludeDayOfMonthViewModel
import net.mythrowaway.app.module.trash.usecase.CommonExcludeDayOfMonthUseCase
import net.mythrowaway.app.module.trash.entity.sync.SyncState
import net.mythrowaway.app.module.trash.usecase.SyncRepositoryInterface
import net.mythrowaway.app.module.trash.usecase.TrashRepositoryInterface
import org.junit.Rule
import org.junit.Test

@LargeTest
class CommonExcludeDayOfMonthScreenTest {
  @get:Rule
  val composeRule = createComposeRule()

  private val resource = InstrumentationRegistry.getInstrumentation().targetContext.resources

  @Test
  fun can_add_common_exclude_day_max_10() {
    val viewModel = CommonExcludeDayOfMonthViewModel(
      CommonExcludeDayOfMonthUseCase(FakeTrashRepository(), FakeSyncRepository())
    )
    composeRule.setContent {
      CommonExcludeDayOfMonthScreen(
        viewModel = viewModel,
        onClose = {}
      )
    }

    repeat(10) {
      composeRule.onNodeWithTag(resource.getString(R.string.testTag_add_common_exclude_day_button)).performClick()
    }
    composeRule.onNodeWithTag(resource.getString(R.string.testTag_add_common_exclude_day_button)).assertIsNotEnabled()
  }

  @Test
  fun can_delete_common_exclude_day_and_reenable_add_button() {
    val viewModel = CommonExcludeDayOfMonthViewModel(
      CommonExcludeDayOfMonthUseCase(FakeTrashRepository(), FakeSyncRepository())
    )
    composeRule.setContent {
      CommonExcludeDayOfMonthScreen(
        viewModel = viewModel,
        onClose = {}
      )
    }

    repeat(2) {
      composeRule.onNodeWithTag(resource.getString(R.string.testTag_add_common_exclude_day_button)).performClick()
    }
    composeRule.onAllNodesWithTag(resource.getString(R.string.testTag_delete_exclude_day_of_month_button))[0].performClick()

    composeRule.onAllNodesWithTag(resource.getString(R.string.testTag_month_of_exclude_day_of_month_dropdown)).assertCountEquals(1)
    composeRule.onNodeWithTag(resource.getString(R.string.testTag_add_common_exclude_day_button)).assertIsEnabled()
  }
}

private class FakeTrashRepository : TrashRepositoryInterface {
  private var trashList = TrashList(listOf())

  override fun saveTrash(trash: Trash) {
  }

  override fun findTrashById(id: String): Trash? {
    return null
  }

  override fun deleteTrash(trash: Trash) {
  }

  override fun getAllTrash(): TrashList {
    return trashList
  }

  override fun replaceTrashList(trashList: TrashList) {
    this.trashList = trashList
  }
}

private class FakeSyncRepository : SyncRepositoryInterface {
  private var syncState: SyncState = SyncState.NotInit
  private var timestamp: Long = 0L

  override fun getSyncState(): SyncState {
    return syncState
  }

  override fun getTimeStamp(): Long {
    return timestamp
  }

  override fun setTimestamp(timestamp: Long) {
    this.timestamp = timestamp
  }

  override fun setSyncWait() {
    syncState = SyncState.Wait
  }

  override fun setSyncComplete() {
    syncState = SyncState.Synced
  }
}
