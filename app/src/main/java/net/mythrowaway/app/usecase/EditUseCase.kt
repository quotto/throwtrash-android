package net.mythrowaway.app.usecase

import android.util.Log
import net.mythrowaway.app.domain.ExcludeDayOfMonth
import net.mythrowaway.app.domain.ExcludeDayOfMonthList
import net.mythrowaway.app.domain.Trash
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.domain.TrashList
import net.mythrowaway.app.domain.TrashType
import net.mythrowaway.app.domain.WeeklySchedule
import net.mythrowaway.app.service.TrashManager
import net.mythrowaway.app.usecase.dto.ExcludeDayDTO
import net.mythrowaway.app.usecase.dto.ScheduleDTO
import net.mythrowaway.app.usecase.dto.TrashDTO
import net.mythrowaway.app.usecase.dto.mapper.ExcludeDayOfMonthMapper
import net.mythrowaway.app.usecase.dto.mapper.ScheduleMapper
import net.mythrowaway.app.usecase.dto.mapper.TrashMapper
import java.time.DayOfWeek
import java.util.Calendar
import javax.inject.Inject

class EditUseCase @Inject constructor(
    private val presenter: EditPresenterInterface,
    private val persistence: DataRepositoryInterface,
    private val trashManager: TrashManager
) {
    private var scheduleCount:Int = 0

    /**
     * 入力されたゴミ出し予定に対してIDを採番して永続データに保存する
     */
    fun saveTrashData(trashData: TrashData) {
        Log.i(this.javaClass.simpleName, "Save new trash -> $trashData")

        persistence.saveTrashData(trashData)
        trashManager.refresh()
        presenter.complete(ResultCode.SUCCESS)
    }

    fun saveTrash(id: String, trashType: TrashType, trashVal: String, schedules: List<ScheduleDTO>, excludes: List<ExcludeDayDTO>): SaveResult {
        Log.i(this.javaClass.simpleName, "Save new trash -> $trashType, $trashVal, $schedules, $excludes")

        // TODO: リポジトリ側でTrashListを返すように修正
        val allTrashData = persistence.getAllTrashSchedule()
        val rawTrashList: List<Trash> = allTrashData.map { it.toTrash() }
        val trashList = TrashList(rawTrashList)

        if (trashList.canAddTrash()) {
            // TODO: リポジトリ側でTrashを受け取るように修正
            persistence.saveTrashData(
                TrashData.fromTrash(
                    Trash(
                        id ,
                        trashType,
                        trashVal,
                        schedules.map { ScheduleMapper.toSchedule(it) },
                        ExcludeDayOfMonthList(excludes.map{ExcludeDayOfMonthMapper.toExcludeDayOfMonth(it)}.toMutableList())
                    )
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

    /**
     * 入力スケジュールが追加された
     */
    fun addTrashSchedule() {
        if(scheduleCount < 3) {
            scheduleCount++
            Log.d(this.javaClass.simpleName, "add schedule, now schedule count -> $scheduleCount")
            presenter.addTrashSchedule(scheduleCount)
        }
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

    fun addExcludeDay(excludeDayDTOList: List<ExcludeDayDTO>, month: Int, day: Int): List<ExcludeDayDTO> {
        val excludeDay = ExcludeDayOfMonthList(excludeDayDTOList.map { ExcludeDayOfMonthMapper.toExcludeDayOfMonth(it) }.toMutableList())
        excludeDay.add(ExcludeDayOfMonth(month, day))
        return excludeDay.members.map{ ExcludeDayOfMonthMapper.toDTO(it)}
    }

    fun removeExcludeDay(excludeDayDTOList: List<ExcludeDayDTO>, position: Int): List<ExcludeDayDTO> {
        val excludeDay = ExcludeDayOfMonthList(excludeDayDTOList.map {ExcludeDayOfMonthMapper.toExcludeDayOfMonth(it)}.toMutableList())
        excludeDay.removeAt(position)
        return excludeDay.members.map{ ExcludeDayOfMonthMapper.toDTO(it)}
    }

    fun canAddExcludeDay(excludeDayDTOList: List<ExcludeDayDTO>): Boolean {
        val excludeDay = ExcludeDayOfMonthList(excludeDayDTOList.map {ExcludeDayOfMonthMapper.toExcludeDayOfMonth(it)}.toMutableList())
        return excludeDay.canAdd()
    }


    /**
     * 入力スケジュールが削除された
     */
    fun deleteTrashSchedule(delete_index:Int) {
        if(scheduleCount > 1) {
            scheduleCount--
            Log.d(this.javaClass.simpleName, "delete schedule, now schedule count -> $scheduleCount")
            presenter.deleteTrashSchedule(delete_index, scheduleCount)
        }
    }

    /**
     * 登録済みスケジュールの更新
     */
    fun updateTrashData(updateData: TrashData) {
        persistence.updateTrashData(updateData)
        Log.i(this.javaClass.simpleName, "update trash data -> $updateData")
        trashManager.refresh()
        presenter.complete(ResultCode.SUCCESS)
    }

    /**
     * 登録済みスケジュールを表示する
     */
    fun loadTrashData(id:String) {
        persistence.getTrashData(id)?.let {
            Log.i(this.javaClass.simpleName, "load trash data -> $it")
            scheduleCount = it.schedules.size
            presenter.loadTrashData(it)
        }
    }

    /**
     * 現在のスケジュールカウントを設定する
     */
    fun setScheduleCount(count:Int) {
        scheduleCount = count
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
//        MAX_SCHEDULE,
        INVALID_OTHER_TEXT_EMPTY,
        INVALID_OTHER_TEXT_OVER,
        INVALID_OTHER_TEXT_CHARACTER
    }

    enum class SaveResult {
        SUCCESS,
        ERROR_MAX_SCHEDULE
    }
}