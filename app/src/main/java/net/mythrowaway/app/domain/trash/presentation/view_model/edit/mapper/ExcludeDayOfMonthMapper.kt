package net.mythrowaway.app.domain.trash.presentation.view_model.edit.mapper

import net.mythrowaway.app.domain.trash.usecase.dto.ExcludeDayOfMonthDTO
import net.mythrowaway.app.domain.trash.presentation.view_model.edit.data.ExcludeDayOfMonthViewData

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