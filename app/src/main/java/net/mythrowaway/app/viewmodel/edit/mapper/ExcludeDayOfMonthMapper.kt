package net.mythrowaway.app.viewmodel.edit.mapper

import net.mythrowaway.app.usecase.dto.ExcludeDayOfMonthDTO
import net.mythrowaway.app.viewmodel.edit.ExcludeDayOfMonthViewData

class ExcludeDayOfMonthMapper {
  companion object {
    fun toViewData(excludeDayOfMonthDTO: ExcludeDayOfMonthDTO): ExcludeDayOfMonthViewData {
      return ExcludeDayOfMonthViewData(excludeDayOfMonthDTO.month - 1, excludeDayOfMonthDTO.dayOfMonth - 1)
    }

    fun toDTO(excludeDayOfMonthViewData: ExcludeDayOfMonthViewData): ExcludeDayOfMonthDTO {
      return ExcludeDayOfMonthDTO(excludeDayOfMonthViewData.month + 1, excludeDayOfMonthViewData.day + 1)
    }
  }
}