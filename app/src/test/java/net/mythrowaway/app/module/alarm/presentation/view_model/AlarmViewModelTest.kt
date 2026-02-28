package net.mythrowaway.app.module.alarm.presentation.view_model

import net.mythrowaway.app.module.alarm.dto.AlarmTrashDTO
import net.mythrowaway.app.module.alarm.entity.AlarmConfig
import net.mythrowaway.app.module.alarm.usecase.AlarmManager
import net.mythrowaway.app.module.alarm.usecase.AlarmRepositoryInterface
import net.mythrowaway.app.module.alarm.usecase.AlarmUseCase
import net.mythrowaway.app.module.trash.entity.sync.SyncState
import net.mythrowaway.app.module.trash.entity.trash.Trash
import net.mythrowaway.app.module.trash.entity.trash.TrashList
import net.mythrowaway.app.module.trash.service.TrashService
import net.mythrowaway.app.module.trash.usecase.SyncRepositoryInterface
import net.mythrowaway.app.module.trash.usecase.TrashRepositoryInterface
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AlarmViewModelTest {
  @Test
  fun `初期化時に保存済み設定がUI状態へ反映される`() {
    val fakeAlarmRepository = FakeAlarmRepository(
      alarmConfig = AlarmConfig(
        _enabled = true,
        _hourOfDay = 21,
        _minute = 35,
        _notifyEveryday = true,
        _notifyTomorrow = true
      )
    )
    val viewModel = AlarmViewModel(
      AlarmUseCase(fakeAlarmRepository, createTrashService()),
      FakeAlarmManager()
    )

    val state = viewModel.uiState.value
    assertTrue(state.notifyChecked)
    assertEquals(21, state.hour)
    assertEquals(35, state.minute)
    assertTrue(state.notifyEverydayChecked)
    assertTrue(state.notifyTomorrowChecked)
    assertEquals("21:35", state.timeText)
  }

  @Test
  fun `翌日通知トグル変更がUI状態に反映される`() {
    val viewModel = AlarmViewModel(
      AlarmUseCase(FakeAlarmRepository(), createTrashService()),
      FakeAlarmManager()
    )

    viewModel.changeNotifyTomorrow(true)

    assertTrue(viewModel.uiState.value.notifyTomorrowChecked)
  }

  @Test
  fun `保存時に翌日通知フラグを含む設定が保存される`() {
    val fakeAlarmRepository = FakeAlarmRepository()
    val viewModel = AlarmViewModel(
      AlarmUseCase(fakeAlarmRepository, createTrashService()),
      FakeAlarmManager()
    )
    viewModel.toggleNotify(true)
    viewModel.changeNotifyEveryday(true)
    viewModel.changeNotifyTomorrow(true)
    viewModel.changeTime(22, 10)

    viewModel.saveAlarm()

    val saved = fakeAlarmRepository.savedAlarmConfig
    assertTrue(saved != null)
    assertTrue(saved!!.enabled)
    assertEquals(22, saved.hourOfDay)
    assertEquals(10, saved.minute)
    assertTrue(saved.notifyEveryday)
    assertTrue(saved.notifyTomorrow)
    assertTrue(viewModel.uiState.value.alarmSavedStatus is AlarmSavedStatus.Success)
  }

  @Test
  fun `保存処理で例外発生時は失敗状態になる`() {
    val viewModel = AlarmViewModel(
      AlarmUseCase(
        FakeAlarmRepository(throwOnSave = true),
        createTrashService()
      ),
      FakeAlarmManager()
    )

    viewModel.saveAlarm()

    assertTrue(viewModel.uiState.value.alarmSavedStatus is AlarmSavedStatus.Failure)
  }

  private fun createTrashService(): TrashService {
    return TrashService(
      object : TrashRepositoryInterface {
        override fun saveTrash(trash: Trash) {
        }

        override fun findTrashById(id: String): Trash? {
          return null
        }

        override fun deleteTrash(trash: Trash) {
        }

        override fun getAllTrash(): TrashList {
          return TrashList(listOf())
        }

        override fun replaceTrashList(trashList: TrashList) {
        }
      },
      object : SyncRepositoryInterface {
        override fun getSyncState(): SyncState {
          return SyncState.NotInit
        }

        override fun getTimeStamp(): Long {
          return 0
        }

        override fun setTimestamp(timestamp: Long) {
        }

        override fun setSyncWait() {
        }

        override fun setSyncComplete() {
        }
      }
    )
  }
}

private class FakeAlarmRepository(
  private var alarmConfig: AlarmConfig? = null,
  private val throwOnSave: Boolean = false
) : AlarmRepositoryInterface {
  var savedAlarmConfig: AlarmConfig? = null

  override fun getAlarmConfig(): AlarmConfig? {
    return alarmConfig
  }

  override fun saveAlarmConfig(alarmConfig: AlarmConfig) {
    if (throwOnSave) {
      throw RuntimeException("save failed")
    }
    this.alarmConfig = alarmConfig
    this.savedAlarmConfig = alarmConfig
  }
}

private class FakeAlarmManager : AlarmManager {
  override fun showAlarmMessage(notifyTrashList: List<AlarmTrashDTO>) {
  }

  override fun setAlarm(hourOfDay: Int, minute: Int) {
  }

  override fun cancelAlarm() {
  }
}
