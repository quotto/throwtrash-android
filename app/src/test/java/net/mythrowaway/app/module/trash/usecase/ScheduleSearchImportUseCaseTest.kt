package net.mythrowaway.app.module.trash.usecase

import net.mythrowaway.app.module.trash.entity.sync.SyncState
import net.mythrowaway.app.module.trash.entity.trash.ExcludeDayOfMonthList
import net.mythrowaway.app.module.trash.entity.trash.Trash
import net.mythrowaway.app.module.trash.entity.trash.TrashList
import net.mythrowaway.app.module.trash.entity.trash.TrashType
import net.mythrowaway.app.module.trash.entity.trash.WeeklySchedule
import net.mythrowaway.app.module.trash.infra.schedule_search.ScheduleSearchErrorType
import net.mythrowaway.app.module.trash.infra.schedule_search.ScheduleSearchResponse
import net.mythrowaway.app.module.trash.infra.schedule_search.ScheduleSearchScheduleItem
import net.mythrowaway.app.module.trash.infra.schedule_search.ScheduleSearchTrashItem
import org.junit.jupiter.api.Assertions.assertEquals
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

    assertEquals(ScheduleSearchImportStatus.SUCCESS, result.status)
    assertEquals("ゴミ出し予定を取り込みました", result.message)
    assertEquals(1, trashRepository.getAllTrash().trashList.size)
    assertEquals(TrashType.BURN, trashRepository.getAllTrash().trashList[0].type)
    assertEquals(SyncState.Wait, syncRepository.getSyncState())
    assertEquals(ScheduleSearchImportStatus.SUCCESS, stateRepository.consumeImportResult()?.status)
    assertEquals("ゴミ出し予定を取り込みました", stateRepository.lastConsumedMessage)
    assertEquals("160-0023", api.lastPostalCode)
  }

  @Test
  fun save_unsupported_schedule_message_when_response_has_unsupported_schedule() {
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
                ScheduleSearchScheduleItem(type = "unsupported", value = "電話申込")
            )
          )
        ),
        errorType = ScheduleSearchErrorType.UNSUPPORTED_SCHEDULE
      )
      ),
      trashRepository = FakeScheduleSearchTrashRepository(),
      syncRepository = FakeScheduleSearchSyncRepository(),
      stateRepository = stateRepository
    )

    val result = useCase.import("東京都新宿区西新宿2丁目")

    assertEquals(ScheduleSearchImportStatus.SUCCESS_WITH_NOTICE, result.status)
    assertEquals("一部のゴミ出し予定を取り込めませんでした。取り込めなかった内容は手動で確認してください。\n剪定枝: 電話申込", result.message)
    assertEquals(ScheduleSearchImportStatus.SUCCESS_WITH_NOTICE, stateRepository.consumeImportResult()?.status)
    assertEquals("一部のゴミ出し予定を取り込めませんでした。取り込めなかった内容は手動で確認してください。\n剪定枝: 電話申込", stateRepository.lastConsumedMessage)
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
      api = FakeScheduleSearchApi(
        ScheduleSearchResponse(
          trashes = listOf(),
          message = "APIから返された文言は表示しない",
          errorType = ScheduleSearchErrorType.INVALID_ADDRESS
        )
      ),
      trashRepository = trashRepository,
      syncRepository = FakeScheduleSearchSyncRepository(),
      stateRepository = stateRepository
    )

    val result = useCase.import("東京都新宿区西新宿2丁目")

    assertEquals(ScheduleSearchImportStatus.FAILURE, result.status)
    assertEquals("入力された住所に対応するゴミ出し予定を特定できませんでした。町名・丁目までのおおよその住所で再度お試しください。", result.message)
    assertEquals("existing", trashRepository.getAllTrash().trashList[0].id)
    assertEquals(ScheduleSearchImportStatus.FAILURE, stateRepository.consumeImportResult()?.status)
    assertEquals("入力された住所に対応するゴミ出し予定を特定できませんでした。町名・丁目までのおおよその住所で再度お試しください。", stateRepository.lastConsumedMessage)
  }

  @Test
  fun use_fixed_message_when_postal_code_is_invalid() {
    val stateRepository = FakeScheduleSearchStateRepository()
    val useCase = ScheduleSearchImportUseCase(
      api = FakeScheduleSearchApi(
        ScheduleSearchResponse(
          trashes = emptyList(),
          message = "APIから返された文言は表示しない",
          errorType = ScheduleSearchErrorType.INVALID_POSTAL_CODE
        )
      ),
      trashRepository = FakeScheduleSearchTrashRepository(),
      syncRepository = FakeScheduleSearchSyncRepository(),
      stateRepository = stateRepository
    )

    val result = useCase.import("160-0023")

    assertEquals(ScheduleSearchImportStatus.FAILURE, result.status)
    assertEquals("入力された郵便番号に対応するゴミ出し予定を特定できませんでした。住所での取り込みをお試しください。", result.message)
    assertEquals(ScheduleSearchImportStatus.FAILURE, stateRepository.consumeImportResult()?.status)
    assertEquals("入力された郵便番号に対応するゴミ出し予定を特定できませんでした。住所での取り込みをお試しください。", stateRepository.lastConsumedMessage)
  }

  @Test
  fun use_fixed_message_when_error_type_is_unknown() {
    val stateRepository = FakeScheduleSearchStateRepository()
    val useCase = ScheduleSearchImportUseCase(
      api = FakeScheduleSearchApi(
        ScheduleSearchResponse(
          trashes = emptyList(),
          message = "APIから返された文言は表示しない",
          errorType = ScheduleSearchErrorType.UNKNOWN
        )
      ),
      trashRepository = FakeScheduleSearchTrashRepository(),
      syncRepository = FakeScheduleSearchSyncRepository(),
      stateRepository = stateRepository
    )

    val result = useCase.import("東京都新宿区西新宿2丁目")

    assertEquals(ScheduleSearchImportStatus.FAILURE, result.status)
    assertEquals("ゴミ出し予定の取り込みに失敗しました。時間をおいて再度お試しください。", result.message)
    assertEquals(ScheduleSearchImportStatus.FAILURE, stateRepository.consumeImportResult()?.status)
    assertEquals("ゴミ出し予定の取り込みに失敗しました。時間をおいて再度お試しください。", stateRepository.lastConsumedMessage)
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

    assertEquals(ScheduleSearchImportStatus.FAILURE, result.status)
    assertEquals("ゴミ出し予定の取り込みに失敗しました。時間をおいて再度お試しください。", result.message)
    assertEquals(ScheduleSearchImportStatus.FAILURE, stateRepository.consumeImportResult()?.status)
    assertEquals("ゴミ出し予定の取り込みに失敗しました。時間をおいて再度お試しください。", stateRepository.lastConsumedMessage)
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
  private var result: ScheduleSearchImportResult? = null
  var lastConsumedMessage: String? = null
    private set
  override fun shouldShowStartupDialog(): Boolean = true
  override fun suppressStartupDialog() = Unit
  override fun saveImportResult(result: ScheduleSearchImportResult) {
    this.result = result
  }
  override fun consumeImportResult(): ScheduleSearchImportResult? {
    val consumed = result
    lastConsumedMessage = consumed?.message
    result = null
    return consumed
  }
}
