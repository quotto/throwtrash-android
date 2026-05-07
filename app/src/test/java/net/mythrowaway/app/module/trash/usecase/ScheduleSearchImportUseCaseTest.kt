package net.mythrowaway.app.module.trash.usecase

import net.mythrowaway.app.module.trash.entity.sync.SyncState
import net.mythrowaway.app.module.trash.entity.trash.ExcludeDayOfMonthList
import net.mythrowaway.app.module.trash.entity.trash.Trash
import net.mythrowaway.app.module.trash.entity.trash.TrashList
import net.mythrowaway.app.module.trash.entity.trash.TrashType
import net.mythrowaway.app.module.trash.entity.trash.WeeklySchedule
import net.mythrowaway.app.module.trash.infra.schedule_search.ScheduleSearchResponse
import net.mythrowaway.app.module.trash.infra.schedule_search.ScheduleSearchScheduleItem
import net.mythrowaway.app.module.trash.infra.schedule_search.ScheduleSearchTrashItem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.DayOfWeek

class ScheduleSearchImportUseCaseTest {
  @Test
  fun replace_local_trash_and_set_sync_wait_when_api_returns_importable_trash() {
    val trashRepository = FakeScheduleSearchTrashRepository()
    val syncRepository = FakeScheduleSearchSyncRepository()
    val stateRepository = FakeScheduleSearchStateRepository()
    val api = FakeScheduleSearchApi(
      response = ScheduleSearchResponse(
        trashes = listOf(
          ScheduleSearchTrashItem(
            type = "burn",
            trashName = null,
            schedule = listOf(ScheduleSearchScheduleItem(type = "weekday", value = "1"))
          )
        )
      )
    )
    val useCase = ScheduleSearchImportUseCase(api, trashRepository, syncRepository, stateRepository)

    val result = useCase.import("160-0023")

    assertEquals(ScheduleSearchImportResult.SUCCESS, result)
    assertEquals(1, trashRepository.getAllTrash().trashList.size)
    assertEquals(TrashType.BURN, trashRepository.getAllTrash().trashList[0].type)
    assertEquals(SyncState.Wait, syncRepository.getSyncState())
    assertEquals("ゴミ出し予定を取り込みました", stateRepository.consumeImportMessage())
    assertEquals("160-0023", api.lastPostalCode)
  }

  @Test
  fun save_unsupported_schedule_message_when_response_has_unmatched_schedule() {
    val stateRepository = FakeScheduleSearchStateRepository()
    val useCase = ScheduleSearchImportUseCase(
      api = FakeScheduleSearchApi(
        response = ScheduleSearchResponse(
          trashes = listOf(
            ScheduleSearchTrashItem(
              type = "other",
              trashName = "剪定枝",
              schedule = listOf(
                ScheduleSearchScheduleItem(type = "weekday", value = "6"),
                ScheduleSearchScheduleItem(type = "unmatched", value = "電話申込")
              )
            )
          )
        )
      ),
      trashRepository = FakeScheduleSearchTrashRepository(),
      syncRepository = FakeScheduleSearchSyncRepository(),
      stateRepository = stateRepository
    )

    val result = useCase.import("東京都新宿区西新宿2丁目")

    assertEquals(ScheduleSearchImportResult.SUCCESS_WITH_NOTICE, result)
    assertEquals("取り込めない日程があります\n剪定枝: 電話申込", stateRepository.consumeImportMessage())
  }

  @Test
  fun do_not_replace_local_trash_when_response_has_no_importable_trash() {
    val trashRepository = FakeScheduleSearchTrashRepository(
      TrashList(
        listOf(
          Trash(
            _id = "existing",
            _type = TrashType.BURN,
            _displayName = "",
            schedules = listOf(WeeklySchedule(DayOfWeek.MONDAY)),
            _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
          )
        )
      )
    )
    val stateRepository = FakeScheduleSearchStateRepository()
    val useCase = ScheduleSearchImportUseCase(
      api = FakeScheduleSearchApi(ScheduleSearchResponse(trashes = listOf(), message = "特定できませんでした")),
      trashRepository = trashRepository,
      syncRepository = FakeScheduleSearchSyncRepository(),
      stateRepository = stateRepository
    )

    val result = useCase.import("東京都新宿区西新宿2丁目")

    assertEquals(ScheduleSearchImportResult.FAILURE, result)
    assertEquals("existing", trashRepository.getAllTrash().trashList[0].id)
    assertEquals("取り込みに失敗しました\n特定できませんでした", stateRepository.consumeImportMessage())
  }

  @Test
  fun save_failure_message_when_api_throws_exception() {
    val stateRepository = FakeScheduleSearchStateRepository()
    val useCase = ScheduleSearchImportUseCase(
      api = FakeScheduleSearchApi(exception = IllegalStateException("network error")),
      trashRepository = FakeScheduleSearchTrashRepository(),
      syncRepository = FakeScheduleSearchSyncRepository(),
      stateRepository = stateRepository
    )

    val result = useCase.import("東京都新宿区西新宿2丁目")

    assertEquals(ScheduleSearchImportResult.FAILURE, result)
    assertEquals("取り込みに失敗しました", stateRepository.consumeImportMessage())
  }
}

private class FakeScheduleSearchApi(
  private val response: ScheduleSearchResponse? = null,
  private val exception: Exception? = null,
) : ScheduleSearchApiInterface {
  var lastPostalCode: String? = null

  override fun search(request: ScheduleSearchRequest): ScheduleSearchResponse {
    lastPostalCode = request.postalCode
    exception?.let { throw it }
    return response ?: ScheduleSearchResponse(emptyList())
  }
}

private class FakeScheduleSearchTrashRepository(
  private var trashList: TrashList = TrashList(emptyList())
) : TrashRepositoryInterface {
  override fun saveTrash(trash: Trash) {
    trashList.addTrash(trash)
  }

  override fun findTrashById(id: String): Trash? = trashList.trashList.firstOrNull { it.id == id }
  override fun deleteTrash(trash: Trash) = trashList.removeTrash(trash)
  override fun getAllTrash(): TrashList = trashList
  override fun replaceTrashList(trashList: TrashList) {
    this.trashList = trashList
  }
}

private class FakeScheduleSearchSyncRepository : SyncRepositoryInterface {
  private var syncState: SyncState = SyncState.Synced
  override fun getSyncState(): SyncState = syncState
  override fun getTimeStamp(): Long = 0
  override fun setTimestamp(timestamp: Long) = Unit
  override fun setSyncWait() {
    syncState = SyncState.Wait
  }
  override fun setSyncComplete() {
    syncState = SyncState.Synced
  }
}

private class FakeScheduleSearchStateRepository : ScheduleSearchStateRepositoryInterface {
  private var message: String? = null
  override fun shouldShowStartupDialog(): Boolean = true
  override fun suppressStartupDialog() = Unit
  override fun saveImportMessage(message: String) {
    this.message = message
  }
  override fun consumeImportMessage(): String? {
    val result = message
    message = null
    return result
  }
}
