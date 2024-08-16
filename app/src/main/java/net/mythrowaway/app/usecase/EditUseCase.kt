package net.mythrowaway.app.usecase

import android.util.Log
import net.mythrowaway.app.domain.ExcludeDayOfMonth
import net.mythrowaway.app.domain.ExcludeDayOfMonthList
import net.mythrowaway.app.domain.Trash
import net.mythrowaway.app.domain.TrashType
import net.mythrowaway.app.domain.WeeklySchedule
import net.mythrowaway.app.usecase.dto.ExcludeDayOfMonthDTO
import net.mythrowaway.app.usecase.dto.ScheduleDTO
import net.mythrowaway.app.usecase.dto.TrashDTO
import net.mythrowaway.app.usecase.dto.mapper.ExcludeDayOfMonthMapper
import net.mythrowaway.app.usecase.dto.mapper.ScheduleMapper
import net.mythrowaway.app.usecase.dto.mapper.TrashMapper
import java.time.DayOfWeek
import java.util.Calendar
import javax.inject.Inject

class EditUseCase @Inject constructor(
    private val persistence: DataRepositoryInterface,
) {
    fun saveTrash(id: String, trashType: TrashType, trashVal: String, schedules: List<ScheduleDTO>, excludes: List<ExcludeDayOfMonthDTO>): SaveResult {
        Log.i(this.javaClass.simpleName, "Save trash -> $trashType, $trashVal, $schedules, $excludes")

        val trashList = persistence.getAllTrash()
        if (trashList.canAddTrash()) {
            persistence.saveTrash(
                Trash(
                    id ,
                    trashType,
                    trashVal,
                    schedules.map { ScheduleMapper.toSchedule(it) },
                    ExcludeDayOfMonthList(excludes.map{ExcludeDayOfMonthMapper.toExcludeDayOfMonth(it)}.toMutableList())
                )
            )
            return SaveResult.SUCCESS
        } else {
            return SaveResult.ERROR_MAX_SCHEDULE
        }
    }

    fun createNewTrashDTO(): TrashDTO {
        val id = Calendar.getInstance().timeInMillis.toString()
        val trash = Trash(
            id,
            TrashType.BURN,
            "",
            listOf(WeeklySchedule(DayOfWeek.SUNDAY)),
            ExcludeDayOfMonthList(mutableListOf())
        )
        return TrashMapper.toTrashDTO(trash)
    }

    fun appendNewSchedule(trashDTO: TrashDTO): TrashDTO {
        val trash = TrashMapper.toTrash(trashDTO)
        if(trash.canAddSchedule()) {
            val schedule = WeeklySchedule(DayOfWeek.SUNDAY)
            trash.addSchedule(schedule)
        }
        return TrashMapper.toTrashDTO(trash)
    }

    fun canAddSchedule(trashDTO: TrashDTO): Boolean {
        val trash = TrashMapper.toTrash(trashDTO)
        return trash.canAddSchedule()
    }

    fun removeSchedule(trashDTO: TrashDTO, position: Int): TrashDTO {
        val trash = TrashMapper.toTrash(trashDTO)
        if(trash.canRemoveSchedule()) {
            trash.removeScheduleAt(position)
        }
        return TrashMapper.toTrashDTO(trash)
    }

    fun canRemoveSchedule(trashDTO: TrashDTO): Boolean {
        val trash = TrashMapper.toTrash(trashDTO)
        return trash.canRemoveSchedule()
    }

    fun addExcludeDay(excludeDayOfMonthDTOList: List<ExcludeDayOfMonthDTO>, month: Int, day: Int): List<ExcludeDayOfMonthDTO> {
        val excludeDay = ExcludeDayOfMonthList(excludeDayOfMonthDTOList.map { ExcludeDayOfMonthMapper.toExcludeDayOfMonth(it) }.toMutableList())
        excludeDay.add(ExcludeDayOfMonth(month, day))
        return excludeDay.members.map{ ExcludeDayOfMonthMapper.toDTO(it)}
    }

    fun removeExcludeDay(excludeDayOfMonthDTOList: List<ExcludeDayOfMonthDTO>, position: Int): List<ExcludeDayOfMonthDTO> {
        val excludeDay = ExcludeDayOfMonthList(excludeDayOfMonthDTOList.map {ExcludeDayOfMonthMapper.toExcludeDayOfMonth(it)}.toMutableList())
        excludeDay.removeAt(position)
        return excludeDay.members.map{ ExcludeDayOfMonthMapper.toDTO(it)}
    }

    fun canAddExcludeDay(excludeDayOfMonthDTOList: List<ExcludeDayOfMonthDTO>): Boolean {
        val excludeDay = ExcludeDayOfMonthList(excludeDayOfMonthDTOList.map {ExcludeDayOfMonthMapper.toExcludeDayOfMonth(it)}.toMutableList())
        return excludeDay.canAdd()
    }

    fun getTrashData(trashId: String): TrashDTO? {
        return persistence.findTrashById(trashId)?.let {
            TrashMapper.toTrashDTO(it)
        }
    }

    /**
     * その他のゴミの入力チェック
     */
    fun validateOtherTrashText(text:String): ResultCode {
        // TODO: バリデーションはドメイン層に移動する
        return when {
            text.isEmpty() -> {
                Log.d(this.javaClass.simpleName, "other trash text is empty")
                ResultCode.INVALID_OTHER_TEXT_EMPTY
            }
            text.length > 10 -> {
                Log.d(this.javaClass.simpleName, "other trash text is over length")
                ResultCode.INVALID_OTHER_TEXT_OVER
            }
            Regex("^[A-z0-9Ａ-ｚ０-９ぁ-んァ-ヶー一-龠\\s]+$").find(text)?.value == null -> {
                Log.d(this.javaClass.simpleName, "other trash text has invalid character")
                ResultCode.INVALID_OTHER_TEXT_CHARACTER
            }
            else -> ResultCode.SUCCESS
        }
    }


    enum class ResultCode {
        SUCCESS,
        INVALID_OTHER_TEXT_EMPTY,
        INVALID_OTHER_TEXT_OVER,
        INVALID_OTHER_TEXT_CHARACTER
    }

    enum class SaveResult {
        SUCCESS,
        ERROR_MAX_SCHEDULE
    }
}