package net.mythrowaway.app.module.trash.infra.schedule_search

import net.mythrowaway.app.module.trash.usecase.ScheduleSearchRequest
import java.text.Normalizer

object ScheduleSearchRequestFactory {
  private val postalCodeRegex = Regex("^\\d{3}-?\\d{4}$")

  fun create(input: String): ScheduleSearchRequest {
    val trimmed = input.trim()
    val postalCodeCandidate = normalizePostalCode(trimmed)
    return if (postalCodeRegex.matches(postalCodeCandidate)) {
      ScheduleSearchRequest(address = null, postalCode = postalCodeCandidate)
    } else {
      ScheduleSearchRequest(address = trimmed, postalCode = null)
    }
  }

  private fun normalizePostalCode(value: String): String {
    return Normalizer.normalize(value, Normalizer.Form.NFKC)
  }
}
