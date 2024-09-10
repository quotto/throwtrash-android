package net.mythrowaway.app.module.trash.dto.mapper

import net.mythrowaway.app.module.trash.entity.trash.ExcludeDayOfMonth
import net.mythrowaway.app.module.trash.dto.ExcludeDayOfMonthDTO

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