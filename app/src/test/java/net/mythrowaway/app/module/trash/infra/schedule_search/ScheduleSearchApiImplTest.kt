package net.mythrowaway.app.module.trash.infra.schedule_search

import com.github.kittinunf.fuel.core.BodyLength
import com.github.kittinunf.fuel.core.BodySource
import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.requests.DefaultBody
import com.nhaarman.mockito_kotlin.any
import net.mythrowaway.app.module.trash.usecase.ScheduleSearchRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.io.ByteArrayInputStream
import java.net.SocketException
import java.net.URL

class ScheduleSearchApiImplTest {
  @Test
  fun parse_error_type_when_status_code_is_not_200_and_body_is_error_response() {
    val responseContent = """
      {
        "trashes": [],
        "message": "APIから返された文言は表示しない",
        "error_type": "invalid_postalcode"
      }
    """.trimIndent()
    val calculateLength: BodyLength = { responseContent.length.toLong() }
    val openStream: BodySource = { ByteArrayInputStream(responseContent.toByteArray()) }
    val body = DefaultBody.from(
      calculateLength = calculateLength,
      openStream = openStream
    )
    val mockClient = Mockito.mock(Client::class.java)
    Mockito.`when`(mockClient.executeRequest(any())).thenReturn(
      Response(
        statusCode = 400,
        body = body,
        url = URL("https://test.com")
      )
    )
    FuelManager.instance.client = mockClient

    val result = ScheduleSearchApiImpl("https://example.com", "test-key").search(
      ScheduleSearchRequest(address = null, postalCode = "000-0000")
    )

    assertEquals(ScheduleSearchErrorType.INVALID_POSTAL_CODE, result.errorType)
    assertEquals(0, result.trashes.size)
  }

  @Test
  fun return_unknown_error_when_endpoint_or_api_key_is_not_configured() {
    val result = ScheduleSearchApiImpl("", "").search(
      ScheduleSearchRequest(address = "東京都新宿区西新宿2丁目", postalCode = null)
    )

    assertEquals(ScheduleSearchErrorType.UNKNOWN, result.errorType)
    assertEquals(0, result.trashes.size)
  }

  @Test
  fun return_unknown_error_when_gateway_error_response_does_not_have_error_type() {
    val responseContent = """
      {
        "message": "Forbidden"
      }
    """.trimIndent()
    val calculateLength: BodyLength = { responseContent.length.toLong() }
    val openStream: BodySource = { ByteArrayInputStream(responseContent.toByteArray()) }
    val body = DefaultBody.from(
      calculateLength = calculateLength,
      openStream = openStream
    )
    val mockClient = Mockito.mock(Client::class.java)
    Mockito.`when`(mockClient.executeRequest(any())).thenReturn(
      Response(
        statusCode = 403,
        body = body,
        url = URL("https://test.com")
      )
    )
    FuelManager.instance.client = mockClient

    val result = ScheduleSearchApiImpl("https://example.com", "test-key").search(
      ScheduleSearchRequest(address = "東京都新宿区西新宿2丁目", postalCode = null)
    )

    assertEquals(ScheduleSearchErrorType.UNKNOWN, result.errorType)
    assertEquals(0, result.trashes.size)
  }

  @Test
  fun retry_when_connection_is_aborted_before_response_is_read() {
    val responseContent = """
      {
        "trashes": [
          {
            "type": "burn",
            "trash_name": null,
            "schedule": [
              { "type": "weekday", "value": "1" }
            ]
          }
        ],
        "error_type": ""
      }
    """.trimIndent()
    val calculateLength: BodyLength = { responseContent.length.toLong() }
    val openStream: BodySource = { ByteArrayInputStream(responseContent.toByteArray()) }
    val body = DefaultBody.from(
      calculateLength = calculateLength,
      openStream = openStream
    )
    val mockClient = Mockito.mock(Client::class.java)
    Mockito.`when`(mockClient.executeRequest(any()))
      .thenThrow(RuntimeException(SocketException("Software caused connection abort")))
      .thenReturn(
        Response(
          statusCode = 200,
          body = body,
          url = URL("https://test.com")
        )
      )
    FuelManager.instance.client = mockClient

    val result = ScheduleSearchApiImpl(
      endpoint = "https://example.com",
      apiKey = "test-key",
      maxRetryCount = 1,
      retryDelayMillis = 0,
      sleep = {}
    ).search(
      ScheduleSearchRequest(address = "東京都新宿区西新宿2丁目", postalCode = null)
    )

    assertEquals(ScheduleSearchErrorType.NONE, result.errorType)
    assertEquals(1, result.trashes.size)
    Mockito.verify(mockClient, Mockito.times(2)).executeRequest(any())
  }
}
