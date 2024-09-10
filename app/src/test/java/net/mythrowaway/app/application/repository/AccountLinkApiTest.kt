package net.mythrowaway.app.application.repository

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.BodyLength
import com.github.kittinunf.fuel.core.BodySource
import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.requests.DefaultBody
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.capture
import net.mythrowaway.app.domain.account_link.infra.AccountLinkApi
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import java.io.ByteArrayInputStream
import java.net.URL

class AccountLinkApiTest {
  @Captor private val captor: ArgumentCaptor<Request> = ArgumentCaptor.forClass(Request::class.java)

  private val instance = AccountLinkApi("https://example.com")
  @Nested
  inner class AccountLink {
    @Test
    fun return_startAccountLinkResponse_when_status_code_is_200() {
      val responseContent = """
            {"url": "https://test.com", "token": "123456"}
        """
      val calculateLength: BodyLength = { responseContent.length.toLong() }
      val openStream: BodySource = { ByteArrayInputStream(responseContent.toByteArray()) }
      val body = DefaultBody.from(
        calculateLength = calculateLength,
        openStream = openStream
      )
      val headers = Headers()
      headers.append("Set-Cookie", "throwaway-session=123456")

      val mockClient = Mockito.mock(Client::class.java)
      Mockito.`when`(mockClient.executeRequest(any())).thenReturn(
        Response(
          statusCode = 200,
          headers = headers,
          body = body,
          url = URL("https://test.com")
        )
      )

      FuelManager.instance.client = mockClient

      val result = instance.accountLink("dummy-id")
      Mockito.verify(mockClient).executeRequest(capture(captor))
      Assertions.assertEquals(captor.value.url.toString(), "https://example.com/start_link?user_id=dummy-id&platform=android")
      Assertions.assertEquals(result.url, "https://test.com")
      Assertions.assertEquals(result.token, "123456")
    }

    @Test
    fun throw_exception_when_status_code_is_not_200() {
      val calculateLength: BodyLength = { "".length.toLong() }
      val openStream: BodySource = { ByteArrayInputStream("".toByteArray()) }
      val body = DefaultBody.from(
        calculateLength = calculateLength,
        openStream = openStream
      )

      val mockClient = Mockito.mock(Client::class.java)
      Mockito.`when`(mockClient.executeRequest(any())).thenReturn(
        Response(
          statusCode = 500,
          body = body,
          url = URL("https://test.com")
        )
      )

      FuelManager.instance.client = mockClient

      try {
        instance.accountLink("dummy")
        Assertions.fail()
      } catch (e: Exception) {
        Assertions.assertTrue(true)
      }
    }
  }

  @Nested
  inner class AccountLinkAsWeb {
    @Test
    fun return_startAccountLinkResponse_when_status_code_is_200() {
      val responseContent = """
            {"url": "https://test.com", "token": "123456"}
        """
      val calculateLength: BodyLength = { responseContent.length.toLong() }
      val openStream: BodySource = { ByteArrayInputStream(responseContent.toByteArray()) }
      val body = DefaultBody.from(
        calculateLength = calculateLength,
        openStream = openStream
      )
      val headers = Headers()
      headers.append("Set-Cookie", "throwaway-session=123456")

      val mockClient = Mockito.mock(Client::class.java)
      Mockito.`when`(mockClient.executeRequest(any())).thenReturn(
        Response(
          statusCode = 200,
          headers = headers,
          body = body,
          url = URL("https://test.com")
        )
      )

      FuelManager.instance.client = mockClient

      val result = instance.accountLinkAsWeb("dummy-id")
      Mockito.verify(mockClient).executeRequest(capture(captor))
      Assertions.assertEquals(
        captor.value.url.toString(),
        "https://example.com/start_link?user_id=dummy-id&platform=web"
      )
      Assertions.assertEquals(result.url, "https://test.com")
      Assertions.assertEquals(
        result
          .token, "123456"
      )
    }

    @Test
    fun throw_exception_when_status_code_is_not_200() {
      val calculateLength: BodyLength = { "".length.toLong() }
      val openStream: BodySource = { ByteArrayInputStream("".toByteArray()) }
      val body = DefaultBody.from(
        calculateLength = calculateLength,
        openStream = openStream
      )

      val mockClient = Mockito.mock(Client::class.java)
      Mockito.`when`(mockClient.executeRequest(any())).thenReturn(
        Response(
          statusCode = 500,
          body = body,
          url = URL("https://test.com")
        )
      )

      FuelManager.instance.client = mockClient

      try {
        instance.accountLinkAsWeb("dummy")
        Assertions.fail()
      } catch (e: Exception) {
        Assertions.assertTrue(true)
      }
    }
  }
}