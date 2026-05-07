package net.mythrowaway.app.module.trash.infra.schedule_search

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ScheduleSearchRequestFactoryTest {
  @Test
  fun create_postal_code_request_when_input_matches_japanese_postal_code_with_hyphen() {
    val result = ScheduleSearchRequestFactory.create("160-0023")

    assertEquals("160-0023", result.postalCode)
    assertNull(result.address)
  }

  @Test
  fun create_postal_code_request_when_input_matches_japanese_postal_code_without_hyphen() {
    val result = ScheduleSearchRequestFactory.create("1600023")

    assertEquals("1600023", result.postalCode)
    assertNull(result.address)
  }

  @Test
  fun create_address_request_when_input_does_not_match_japanese_postal_code() {
    val result = ScheduleSearchRequestFactory.create("東京都新宿区西新宿2丁目")

    assertEquals("東京都新宿区西新宿2丁目", result.address)
    assertNull(result.postalCode)
  }

  @Test
  fun trim_input_before_classifying() {
    val result = ScheduleSearchRequestFactory.create(" 160-0023 ")

    assertEquals("160-0023", result.postalCode)
    assertNull(result.address)
  }
}
