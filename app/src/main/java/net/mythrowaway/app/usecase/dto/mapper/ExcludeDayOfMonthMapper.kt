package net.mythrowaway.app.usecase.dto.mapper

import net.mythrowaway.app.domain.ExcludeDayOfMonth
import net.mythrowaway.app.usecase.dto.ExcludeDayDTO

class ExcludeDayOfMonthMapper {
  companion object {
    fun toDTO(excludeDayOfMonth: ExcludeDayOfMonth): ExcludeDayDTO {
      return ExcludeDayDTO(excludeDayOfMonth.month - 1, excludeDayOfMonth.dayOfMonth - 1)
    }

    fun toExcludeDayOfMonth(excludeDayDTO: ExcludeDayDTO): ExcludeDayOfMonth {
      return ExcludeDayOfMonth(excludeDayDTO.month + 1, excludeDayDTO.dayOfMonth + 1)
    }
  }
}