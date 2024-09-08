package net.mythrowaway.app.application.repository

import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.fuel.core.requests.DefaultBody
import com.nhaarman.mockito_kotlin.any
import net.mythrowaway.app.domain.trash.entity.trash.ExcludeDayOfMonthList
import net.mythrowaway.app.domain.trash.entity.trash.IntervalWeeklySchedule
import net.mythrowaway.app.domain.trash.entity.trash.OrdinalWeeklySchedule
import net.mythrowaway.app.domain.trash.entity.trash.Trash
import net.mythrowaway.app.domain.trash.entity.trash.TrashList
import net.mythrowaway.app.domain.trash.entity.trash.TrashType
import net.mythrowaway.app.domain.trash.infra.MobileApiImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.io.ByteArrayInputStream
import java.net.URL
import java.time.DayOfWeek
import java.time.LocalDate

class MobileApiImplTest {
  private val instance = MobileApiImpl("https://example.com")

  @Nested
  inner class GetRemoteTrash {
    @Test
    fun return_remoteTrash_when_status_code_is_200() {
      val responseContent = """
            {
                "id": "8051b7f9eb654364ae77f0e770e347d2",
                "description": "[{\"id\":\"1234567\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"},{\"type\":\"biweek\",\"value\":\"1-1\"}]},{\"id\":\"8901234\",\"type\":\"other\",\"trash_val\":\"空き缶\",\"schedules\":[{\"type\":\"evweek\",\"value\":{\"weekday\":\"2\",\"start\":\"2020-03-08\"}}],\"excludes\":[{\"month\": 12,\"date\": 3}]}]",
                "timestamp": 1584691542469
            }
        """
      val calculateLength: BodyLength = { responseContent.length.toLong() }
      val openStream: BodySource = { ByteArrayInputStream(responseContent.toByteArray()) }
      val body = DefaultBody.from(
        calculateLength = calculateLength,
        openStream = openStream
      )

      val mockClient = Mockito.mock(Client::class.java)
      Mockito.`when`(mockClient.executeRequest(any())).thenReturn(
        Response(
          statusCode = 200,
          body = body,
          url = URL("https://test.com")
        )
      )
      FuelManager.instance.client = mockClient

      val result = instance.getRemoteTrash("8051b7f9eb654364ae77f0e770e347d2")
      assertEquals(2, result.trashList.trashList.size)
      assertEquals(TrashType.BURN, result.trashList.trashList[0].type)
      assertEquals(
        OrdinalWeeklySchedule::class.java,
        result.trashList.trashList[0].schedules[1].javaClass
      )
      assertEquals(
        DayOfWeek.MONDAY,
        (result.trashList.trashList[0].schedules[1] as OrdinalWeeklySchedule).dayOfWeek
      )
      assertEquals(
        1,
        (result.trashList.trashList[0].schedules[1] as OrdinalWeeklySchedule).ordinalOfWeek
      )
      assertEquals(TrashType.OTHER, result.trashList.trashList[1].type)
      assertEquals("空き缶", result.trashList.trashList[1].displayName)
      assertEquals(
        IntervalWeeklySchedule::class.java,
        result.trashList.trashList[1].schedules[0].javaClass
      )
      assertEquals(
        DayOfWeek.TUESDAY,
        (result.trashList.trashList[1].schedules[0] as IntervalWeeklySchedule).dayOfWeek
      )
      assertEquals(
        2,
        (result.trashList.trashList[1].schedules[0] as IntervalWeeklySchedule).interval
      )
      assertEquals(
        LocalDate.parse("2020-03-08").toString(),
        (result.trashList.trashList[1].schedules[0] as IntervalWeeklySchedule).start.toString()
      )
      assertEquals(
        12,
        result.trashList.trashList[1].excludeDayOfMonth.members[0].month
      )
      assertEquals(
        3,
        result.trashList.trashList[1].excludeDayOfMonth.members[0].dayOfMonth
      )
      assertEquals(1584691542469, result.timestamp)
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
        instance.getRemoteTrash("dummy")
        fail()
      } catch (e: Exception) {
        assertTrue(true)
      }
    }
  }

  @Nested
  inner class Update {
    @Test
    fun return_success_when_status_code_is_200() {
      val trash1 = Trash(
        _id = "12345",
        _type = TrashType.BURN,
        schedules = listOf(
          OrdinalWeeklySchedule(_ordinalOfWeek = 3, _dayOfWeek = DayOfWeek.of(7)),
          OrdinalWeeklySchedule(_ordinalOfWeek = 1, _dayOfWeek = DayOfWeek.of(6))
        ),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(arrayListOf())
      )
      val trash2 = Trash(
        _id = "56789",
        _type = TrashType.OTHER,
        _displayName = "家電",
        schedules = listOf(
          OrdinalWeeklySchedule(_ordinalOfWeek = 3, _dayOfWeek = DayOfWeek.of(7))
        ),
        _excludeDayOfMonth = ExcludeDayOfMonthList(arrayListOf())
      )

      val responseContent = "{\"timestamp\": 123456789012345}"
      val calculateLength: BodyLength = { responseContent.length.toLong() }
      val openStream: BodySource = { ByteArrayInputStream(responseContent.toByteArray()) }
      val body = DefaultBody.from(
        calculateLength = calculateLength,
        openStream = openStream
      )

      val mockClient = Mockito.mock(Client::class.java)
      Mockito.`when`(mockClient.executeRequest(any())).thenReturn(
        Response(
          statusCode = 200,
          body = body,
          url = URL("https://test.com")
        )
      )


      FuelManager.instance.client = mockClient

      val result =
        instance.update(
          "901d9db9-9723-4845-8929-b88814f82e49",
          TrashList(listOf(trash1, trash2)),
          111111111111
        )
      assertEquals(200, result.statusCode)
      assertEquals(123456789012345, result.timestamp)
    }

    @Test
    fun throw_exception_when_status_code_is_not_200() {
      val trash1 = Trash(
        _id = "12345",
        _type = TrashType.BURN,
        schedules = listOf(
          OrdinalWeeklySchedule(_ordinalOfWeek = 3, _dayOfWeek = DayOfWeek.of(7)),
          OrdinalWeeklySchedule(_ordinalOfWeek = 1, _dayOfWeek = DayOfWeek.of(1))
        ),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(arrayListOf())
      )
      val trash2 = Trash(
        _id = "56789",
        _type = TrashType.OTHER,
        _displayName = "家電",
        schedules = listOf(
          OrdinalWeeklySchedule(_ordinalOfWeek = 3, _dayOfWeek = DayOfWeek.of(7))
        ),
        _excludeDayOfMonth = ExcludeDayOfMonthList(arrayListOf())
      )

      val responseContent = "{\"timestamp\": 123456789012345}"
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

      val result =
        instance.update(
          "901d9db9-9723-4845-8929-b88814f82e49",
          TrashList(listOf(trash1, trash2)),
          111111111111
        )
      assertEquals(400, result.statusCode)
      assertEquals(-1, result.timestamp)
    }
  }

  @Nested
  inner class Register {
    @Test
    fun return_registeredTrash_when_status_code_is_200() {
      val responseContent = """
            {"id": "8051b7f9eb654364ae77f0e770e347d2","timestamp": 1584691542469}
        """
      val calculateLength: BodyLength = { responseContent.length.toLong() }
      val openStream: BodySource = { ByteArrayInputStream(responseContent.toByteArray()) }
      val body = DefaultBody.from(
        calculateLength = calculateLength,
        openStream = openStream
      )

      val mockClient = Mockito.mock(Client::class.java)
      Mockito.`when`(mockClient.executeRequest(any())).thenReturn(
        Response(
          statusCode = 200,
          body = body,
          url = URL("https://test.com")
        )
      )

      FuelManager.instance.client = mockClient

      val trash1 = Trash(
        _id = "123456",
        _type = TrashType.BURN,
        schedules = listOf(
          OrdinalWeeklySchedule(_ordinalOfWeek = 3, _dayOfWeek = DayOfWeek.of(7)),
          OrdinalWeeklySchedule(_ordinalOfWeek = 1, _dayOfWeek = DayOfWeek.of(6))
        ),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(arrayListOf())
      )
      val trash2 = Trash(
        _id = "5678",
        _type = TrashType.OTHER,
        _displayName = "家電",
        schedules = listOf(
          OrdinalWeeklySchedule(_ordinalOfWeek = 3, _dayOfWeek = DayOfWeek.of(7))
        ),
        _excludeDayOfMonth = ExcludeDayOfMonthList(arrayListOf())
      )
      val result = instance.register(TrashList(listOf(trash1, trash2)))
      assertEquals("8051b7f9eb654364ae77f0e770e347d2", result.userId)
      assertEquals(1584691542469, result.latestTrashListRegisteredTimestamp)
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
        instance.register(TrashList(listOf()))
        fail()
      } catch (e: Exception) {
        assertTrue(true)
      }
    }
  }

  @Nested
  inner class PublishActivationCode {
    @Test
    fun return_valid_activation_code_when_status_code_is_200() {
      val responseContent = "{\"code\": \"234567\"}"
      val calculateLength: BodyLength = { responseContent.length.toLong() }
      val openStream: BodySource = { ByteArrayInputStream(responseContent.toByteArray()) }
      val body = DefaultBody.from(
        calculateLength = calculateLength,
        openStream = openStream
      )

      val mockClient = Mockito.mock(Client::class.java)
      Mockito.`when`(mockClient.executeRequest(any())).thenReturn(
        Response(
          statusCode = 200,
          body = body,
          url = URL("https://test.com")
        )
      )

      FuelManager.instance.client = mockClient

      val result = instance.publishActivationCode("901d9db9-9723-4845-8929-b88814f82e49")
      assertEquals("234567", result)
    }

    @Test
    fun throw_exception_when_status_code_is_not_200() {
      val responseContent = "{\"code\": \"234567\"}"
      val calculateLength: BodyLength = { responseContent.length.toLong() }
      val openStream: BodySource = { ByteArrayInputStream(responseContent.toByteArray()) }
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
        instance.publishActivationCode("901d9db9-9723-4845-8929-b88814f82e49")
        fail()
      } catch (e: Exception) {
        assertTrue(true)
      }
    }
  }

  @Nested
  inner class Activate {

    @Test
    fun return_remoteTrash_when_status_code_is_200() {
      // テスト用データ
      // id:8051b7f9eb654364ae77f0e770e347d2
      // description:[{\"id\":\"1234567\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"},{\"type\":\"biweek\",\"value\":\"1-1\"}]},{\"id\":\"8901234\",\"type\":\"other\",\"trash_val\":\"空き缶\",\"schedules\":[{\"type\":\"evweek\",\"value\":{\"weekday\":\"2\",\"start\":\"2020-03-08\"}}]}]
      // timestamp: 1584691542469

      val responseContent = """
            {"description": "[{\"id\":\"1234567\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"},{\"type\":\"biweek\",\"value\":\"1-1\"}]},{\"id\":\"8901234\",\"type\":\"other\",\"trash_val\":\"空き缶\",\"schedules\":[{\"type\":\"evweek\",\"value\":{\"weekday\":\"2\",\"start\":\"2020-03-08\"}}]}]","timestamp": 1584691542469}
        """
      val calculateLength: BodyLength = { responseContent.length.toLong() }
      val openStream: BodySource = { ByteArrayInputStream(responseContent.toByteArray()) }
      val body = DefaultBody.from(
        calculateLength = calculateLength,
        openStream = openStream
      )

      val mockClient = Mockito.mock(Client::class.java)
      Mockito.`when`(mockClient.executeRequest(any())).thenReturn(
        Response(
          statusCode = 200,
          body = body,
          url = URL("https://test.com")
        )
      )

      FuelManager.instance.client = mockClient

      val result = instance.activate("99999", "id001")
      assertEquals(TrashType.BURN, result.trashList.trashList[0].type)
      assertEquals(
        OrdinalWeeklySchedule::class.java,
        result.trashList.trashList[0].schedules[1].javaClass
      )
      assertEquals(
        DayOfWeek.MONDAY,
        (result.trashList.trashList[0].schedules[1] as OrdinalWeeklySchedule).dayOfWeek
      )
      assertEquals(
        1,
        (result.trashList.trashList[0].schedules[1] as OrdinalWeeklySchedule).ordinalOfWeek
      )
      assertEquals(TrashType.OTHER, result.trashList.trashList[1].type)
      assertEquals("空き缶", result.trashList.trashList[1].displayName)
      assertEquals(
        IntervalWeeklySchedule::class.java,
        result.trashList.trashList[1].schedules[0].javaClass
      )
      assertEquals(
        DayOfWeek.TUESDAY,
        (result.trashList.trashList[1].schedules[0] as IntervalWeeklySchedule).dayOfWeek
      )
      assertEquals(
        2,
        (result.trashList.trashList[1].schedules[0] as IntervalWeeklySchedule).interval
      )
      assertEquals(
        LocalDate.parse("2020-03-08").toString(),
        (result.trashList.trashList[1].schedules[0] as IntervalWeeklySchedule).start.toString()
      )
      assertEquals(1584691542469, result.timestamp)
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
        instance.activate("dummy", "id001")
        fail()
      } catch (e: Exception) {
        assertTrue(true)
      }
    }
  }

//  @Nested
//  inner class AccountLink {
//    @Test
//    fun return_startAccountLinkResponse_when_status_code_is_200() {
//      val responseContent = """
//            {"url": "https://test.com", "token": "123456"}
//        """
//      val calculateLength: BodyLength = { responseContent.length.toLong() }
//      val openStream: BodySource = { ByteArrayInputStream(responseContent.toByteArray()) }
//      val body = DefaultBody.from(
//        calculateLength = calculateLength,
//        openStream = openStream
//      )
//      val headers = Headers()
//      headers.append("Set-Cookie", "throwaway-session=123456")
//
//      val mockClient = Mockito.mock(Client::class.java)
//      Mockito.`when`(mockClient.executeRequest(any())).thenReturn(
//        Response(
//          statusCode = 200,
//          headers = headers,
//          body = body,
//          url = URL("https://test.com")
//        )
//      )
//
//      FuelManager.instance.client = mockClient
//      val result = instance.accountLink("dummy-id")
//      assertEquals(result.url, "https://test.com")
//      assertEquals(result.token, "123456")
//    }
//
//    @Test
//    fun throw_exception_when_status_code_is_not_200() {
//      val calculateLength: BodyLength = { "".length.toLong() }
//      val openStream: BodySource = { ByteArrayInputStream("".toByteArray()) }
//      val body = DefaultBody.from(
//        calculateLength = calculateLength,
//        openStream = openStream
//      )
//
//      val mockClient = Mockito.mock(Client::class.java)
//      Mockito.`when`(mockClient.executeRequest(any())).thenReturn(
//        Response(
//          statusCode = 500,
//          body = body,
//          url = URL("https://test.com")
//        )
//      )
//
//      FuelManager.instance.client = mockClient
//
//      try {
//        instance.accountLink("dummy")
//        fail()
//      } catch (e: Exception) {
//        assertTrue(true)
//      }
//    }
//  }
}