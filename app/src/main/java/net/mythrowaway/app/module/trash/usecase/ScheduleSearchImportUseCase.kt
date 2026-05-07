package net.mythrowaway.app.module.trash.usecase

import android.util.Log
import net.mythrowaway.app.module.trash.infra.schedule_search.ScheduleSearchRequestFactory
import net.mythrowaway.app.module.trash.infra.schedule_search.ScheduleSearchResponseMapper
import javax.inject.Inject

class ScheduleSearchImportUseCase @Inject constructor(
  private val api: ScheduleSearchApiInterface,
  private val trashRepository: TrashRepositoryInterface,
  private val syncRepository: SyncRepositoryInterface,
  private val stateRepository: ScheduleSearchStateRepositoryInterface
) {
  fun import(input: String): ScheduleSearchImportResult {
    return try {
      val response = api.search(ScheduleSearchRequestFactory.create(input))
      val mappingResult = ScheduleSearchResponseMapper.toTrashList(response)
      if (mappingResult.trashList.trashList.isEmpty()) {
        stateRepository.saveImportMessage(
          listOfNotNull("取り込みに失敗しました", response.message).joinToString("\n")
        )
        return ScheduleSearchImportResult.FAILURE
      }

      trashRepository.replaceTrashList(mappingResult.trashList)
      syncRepository.setSyncWait()

      if (mappingResult.messages.isEmpty()) {
        stateRepository.saveImportMessage("ゴミ出し予定を取り込みました")
        ScheduleSearchImportResult.SUCCESS
      } else {
        stateRepository.saveImportMessage(
          "取り込めない日程があります\n${mappingResult.messages.joinToString("\n")}"
        )
        ScheduleSearchImportResult.SUCCESS_WITH_NOTICE
      }
    } catch (e: Exception) {
      Log.e(this.javaClass.simpleName, "Schedule search import failed", e)
      stateRepository.saveImportMessage("取り込みに失敗しました")
      ScheduleSearchImportResult.FAILURE
    }
  }
}

enum class ScheduleSearchImportResult {
  SUCCESS,
  SUCCESS_WITH_NOTICE,
  FAILURE
}
