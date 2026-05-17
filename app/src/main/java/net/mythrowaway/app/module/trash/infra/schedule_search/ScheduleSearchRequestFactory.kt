package net.mythrowaway.app.module.trash.infra.schedule_search

import net.mythrowaway.app.module.trash.usecase.ScheduleSearchRequest

object ScheduleSearchRequestFactory {
  private val postalCodeRegex = Regex("^\\d{3}-?\\d{4}$")

  fun create(input: String): ScheduleSearchRequest {
    val trimmed = input.trim()
    return if (postalCodeRegex.matches(trimmed)) {
      ScheduleSearchRequest(address = null, postalCode = trimmed)
    } else {
      ScheduleSearchRequest(address = trimmed, postalCode = null)
    }
  }
}
