package net.mythrowaway.app.usecase.dto.mapper

import net.mythrowaway.app.domain.ExcludeDayOfMonth
import net.mythrowaway.app.usecase.dto.ExcludeDayOfMonthDTO

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