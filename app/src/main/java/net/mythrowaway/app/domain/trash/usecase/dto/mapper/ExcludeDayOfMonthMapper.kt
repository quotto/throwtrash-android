package net.mythrowaway.app.domain.trash.usecase.dto.mapper

import net.mythrowaway.app.domain.trash.entity.ExcludeDayOfMonth
import net.mythrowaway.app.domain.trash.usecase.dto.ExcludeDayOfMonthDTO

class ExcludeDayOfMonthMapper {
  companion object {
    fun toDTO(excludeDayOfMonth: ExcludeDayOfMonth): ExcludeDayOfMonthDTO {
      return ExcludeDayOfMonthDTO(excludeDayOfMonth.month, excludeDayOfMonth.dayOfMonth)
    }

    fun toExcludeDayOfMonth(excludeDayOfMonthDTO: ExcludeDayOfMonthDTO): ExcludeDayOfMonth {
      return ExcludeDayOfMonth(excludeDayOfMonthDTO.month, excludeDayOfMonthDTO.dayOfMonth)
    }
  }
}