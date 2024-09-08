package net.mythrowaway.app.domain.trash.usecase

import android.util.Log
import net.mythrowaway.app.domain.trash.entity.trash.ExcludeDayOfMonthList
import net.mythrowaway.app.domain.trash.entity.trash.Trash
import net.mythrowaway.app.domain.trash.entity.trash.TrashType
import net.mythrowaway.app.domain.trash.entity.trash.WeeklySchedule
import net.mythrowaway.app.domain.trash.dto.ExcludeDayOfMonthDTO
import net.mythrowaway.app.domain.trash.dto.ScheduleDTO
import net.mythrowaway.app.domain.trash.dto.TrashDTO
import net.mythrowaway.app.domain.trash.dto.mapper.ExcludeDayOfMonthMapper
import net.mythrowaway.app.domain.trash.dto.mapper.ScheduleMapper
import net.mythrowaway.app.domain.trash.dto.mapper.TrashMapper
import java.time.DayOfWeek
import java.util.Calendar
import javax.inject.Inject

class EditUseCase @Inject constructor(
    private val syncRepository: SyncRepositoryInterface,
    private val persistence: TrashRepositoryInterface,
) {
    fun saveTrash(id: String, trashType: TrashType, trashVal: String, schedules: List<ScheduleDTO>, excludes: List<ExcludeDayOfMonthDTO>) {
        Log.i(this.javaClass.simpleName, "Save trash -> $trashType, $trashVal, $schedules, $excludes")

        try {
            val trashList = persistence.getAllTrash()
            if (trashList.canAddTrash()) {
                persistence.saveTrash(
                    Trash(
                        id,
                        trashType,
                        trashVal,
                        schedules.map { ScheduleMapper.toSchedule(it) },
                        ExcludeDayOfMonthList(excludes.map {
                            ExcludeDayOfMonthMapper.toExcludeDayOfMonth(
                                it
                            )
                        }.toMutableList())
                    )
                )
                syncRepository.setSyncWait()
            } else {
                throw MaxScheduleException()
            }
        }  catch (e: MaxScheduleException) {
            throw e
        } catch (e: Exception) {
            Log.e(this.javaClass.simpleName, "Save trash error", e)
            throw EditUseCaseException(e.message ?: "Unknown error")
        }
    }

    fun createNewTrash(): TrashDTO {
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

    fun getTrashById(trashId: String): TrashDTO? {
        return persistence.findTrashById(trashId)?.let {
            TrashMapper.toTrashDTO(it)
        }
    }
}