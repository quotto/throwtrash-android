package net.mythrowaway.app.module.trash.usecase

import android.util.Log
import net.mythrowaway.app.module.trash.infra.schedule_search.ScheduleSearchErrorType
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
        val message = ScheduleSearchImportMessageMapper.toMessage(response.errorType)
        val result = ScheduleSearchImportResult(ScheduleSearchImportStatus.FAILURE, message)
        stateRepository.saveImportResult(result)
        return result
      }

      trashRepository.replaceTrashList(mappingResult.trashList)
      syncRepository.setSyncWait()

      if (response.errorType == ScheduleSearchErrorType.NONE && mappingResult.messages.isEmpty()) {
        val message = "ゴミ出し予定を取り込みました"
        val result = ScheduleSearchImportResult(ScheduleSearchImportStatus.SUCCESS, message)
        stateRepository.saveImportResult(result)
        result
      } else {
        val message = ScheduleSearchImportMessageMapper.toMessage(
          if (response.errorType == ScheduleSearchErrorType.NONE) {
            ScheduleSearchErrorType.UNSUPPORTED_SCHEDULE
          } else {
            response.errorType
          },
          mappingResult.messages
        )
        val result = ScheduleSearchImportResult(ScheduleSearchImportStatus.SUCCESS_WITH_NOTICE, message)
        stateRepository.saveImportResult(result)
        result
      }
    } catch (e: Exception) {
      Log.e(this.javaClass.simpleName, "Schedule search import failed", e)
      val message = ScheduleSearchImportMessageMapper.toMessage(ScheduleSearchErrorType.UNKNOWN)
      val result = ScheduleSearchImportResult(ScheduleSearchImportStatus.FAILURE, message)
      stateRepository.saveImportResult(result)
      result
    }
  }
}

data class ScheduleSearchImportResult(
  val status: ScheduleSearchImportStatus,
  val message: String
)

enum class ScheduleSearchImportStatus {
  SUCCESS,
  SUCCESS_WITH_NOTICE,
  FAILURE
}

object ScheduleSearchImportMessageMapper {
  fun toMessage(errorType: ScheduleSearchErrorType, details: List<String> = emptyList()): String {
    val baseMessage = when (errorType) {
      ScheduleSearchErrorType.NONE -> "ゴミ出し予定を取り込みました"
      ScheduleSearchErrorType.INVALID_ADDRESS -> "入力された住所に対応するゴミ出し予定を特定できませんでした。町名・丁目までのおおよその住所で再度お試しください。"
      ScheduleSearchErrorType.INVALID_POSTAL_CODE -> "入力された郵便番号に対応するゴミ出し予定を特定できませんでした。住所での取り込みをお試しください。"
      ScheduleSearchErrorType.UNSUPPORTED_SCHEDULE -> "一部のゴミ出し予定を取り込めませんでした。取り込めなかった内容は手動で確認してください。"
      ScheduleSearchErrorType.UNKNOWN -> "ゴミ出し予定の取り込みに失敗しました。時間をおいて再度お試しください。"
    }
    return if (details.isEmpty()) {
      baseMessage
    } else {
      "$baseMessage\n${details.joinToString("\n")}"
    }
  }
}
