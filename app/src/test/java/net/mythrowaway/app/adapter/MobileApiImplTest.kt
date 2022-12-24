package net.mythrowaway.app.adapter

import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.fuel.core.requests.DefaultBody
import com.nhaarman.mockito_kotlin.any
import net.mythrowaway.app.adapter.repository.MobileApiImpl
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.domain.TrashSchedule
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import java.io.ByteArrayInputStream
import java.net.URL

class MobileApiImplTest {
    private val instance = MobileApiImpl("https://example.com")

    @Test
    fun syncTest() {
        val responseContent = """
            {
                "id": "8051b7f9eb654364ae77f0e770e347d2",
                "description": "[{\"id\":\"1234567\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"},{\"type\":\"biweek\",\"value\":\"1-1\"}]},{\"id\":\"8901234\",\"type\":\"other\",\"trash_val\":\"空き缶\",\"schedules\":[{\"type\":\"evweek\",\"value\":{\"weekday\":\"2\",\"start\":\"2020-03-08\"}}],\"excludes\":[{\"month\": 12,\"date\": 3}]}]",
                "timestamp": 1584691542469
            }
        """
        val calculateLength: BodyLength = {responseContent.length.toLong()}
        val openStream: BodySource = { ByteArrayInputStream(responseContent.toByteArray())}
        val body = DefaultBody.from(
            calculateLength = calculateLength,
            openStream = openStream
        )

        val mockClient = Mockito.mock(Client::class.java)
        Mockito.`when`(mockClient.executeRequest(any())).thenReturn(Response(
                statusCode = 200,
                body = body,
                url = URL("https://test.com")
            ))
        FuelManager.instance.client = mockClient

        val result = instance.sync("8051b7f9eb654364ae77f0e770e347d2")
        Assert.assertEquals(2, result?.first?.size)
        Assert.assertEquals("burn", result?.first?.get(0)?.type)
        Assert.assertEquals("biweek", result?.first?.get(0)?.schedules?.get(1)?.type)
        Assert.assertEquals("1-1", result?.first?.get(0)?.schedules?.get(1)?.value)
        Assert.assertEquals("other", result?.first?.get(1)?.type)
        Assert.assertEquals("空き缶", result?.first?.get(1)?.trash_val)
        Assert.assertEquals("evweek", result?.first?.get(1)?.schedules?.get(0)?.type)
        Assert.assertEquals(
            "2",
            (result?.first?.get(1)?.schedules?.get(0)?.value as HashMap<String, String>)["weekday"]
        )
        Assert.assertEquals(
            "2020-03-08",
            (result.first.get(1).schedules.get(0).value as HashMap<String, String>)["start"]
        )
        Assert.assertEquals(
            12,
            (result.first.get(1)?.excludes.get(0).month)
        )
        Assert.assertEquals(
            3,
            (result.first.get(1).excludes.get(0).date)
        )
        Assert.assertEquals(1584691542469, result.second)
    }

    @Test
    fun syncTest_failed() {
        val calculateLength: BodyLength = {"".length.toLong()}
        val openStream: BodySource = { ByteArrayInputStream("".toByteArray())}
        val body = DefaultBody.from(
            calculateLength = calculateLength,
            openStream = openStream
        )

        val mockClient = Mockito.mock(Client::class.java)
        Mockito.`when`(mockClient.executeRequest(any())).thenReturn(Response(
            statusCode = 500,
            body = body,
            url = URL("https://test.com")
        ))

        FuelManager.instance.client = mockClient

        val result = instance.sync("dummy")
        Assert.assertNull(result)
    }

    @Test
    fun update_success() {
        val trash1 = TrashData().apply {
            id = "12345"
            type = "burn"
            schedules = arrayListOf(
                TrashSchedule().apply {
                    type = "biweek"
                    value = "0-3"
                }, TrashSchedule().apply {
                    type = "biweek"
                    value = "6-1"
                })
        }
        val trash2 = TrashData().apply {
            id = "56789"
            type = "other"
            trash_val = "家電"
            schedules = arrayListOf(
                TrashSchedule().apply {
                    type = "biweek"
                    value = "0-3"
                })
        }

        val responseContent = "{\"timestamp\": 123456789012345}"
        val calculateLength: BodyLength = {responseContent.length.toLong()}
        val openStream: BodySource = { ByteArrayInputStream(responseContent.toByteArray())}
        val body = DefaultBody.from(
            calculateLength = calculateLength,
            openStream = openStream
        )

        val mockClient = Mockito.mock(Client::class.java)
        Mockito.`when`(mockClient.executeRequest(any())).thenReturn(Response(
            statusCode = 200,
            body = body,
            url = URL("https://test.com")
        ))


        FuelManager.instance.client = mockClient

        val result =
            instance.update("901d9db9-9723-4845-8929-b88814f82e49", arrayListOf(trash1, trash2), 111111111111)
        Assert.assertEquals(200, result.statusCode)
        Assert.assertEquals(123456789012345, result.timestamp)
    }

    @Test
    fun update_user_error() {
        val trash1 = TrashData().apply {
            id = "12345"
            type = "burn"
            schedules = arrayListOf(
                TrashSchedule().apply {
                    type = "biweek"
                    value = "0-3"
                }, TrashSchedule().apply {
                    type = "biweek"
                    value = "6-1"
                })
        }
        val trash2 = TrashData().apply {
            id = "56789"
            type = "other"
            trash_val = "家電"
            schedules = arrayListOf(
                TrashSchedule().apply {
                    type = "biweek"
                    value = "0-3"
                })
        }

        val responseContent = "{\"timestamp\": 123456789012345}"
        val calculateLength: BodyLength = {responseContent.length.toLong()}
        val openStream: BodySource = { ByteArrayInputStream(responseContent.toByteArray())}
        val body = DefaultBody.from(
            calculateLength = calculateLength,
            openStream = openStream
        )

        val mockClient = Mockito.mock(Client::class.java)
        Mockito.`when`(mockClient.executeRequest(any())).thenReturn(Response(
            statusCode = 400,
            body = body,
            url = URL("https://test.com")
        ))

        FuelManager.instance.client = mockClient

        val result =
            instance.update("901d9db9-9723-4845-8929-b88814f82e49", arrayListOf(trash1, trash2), 111111111111)
        Assert.assertEquals(400,result.statusCode)
        Assert.assertEquals(-1,result.timestamp)
    }

    @Test
    fun register() {
        val responseContent = """
            {"id": "8051b7f9eb654364ae77f0e770e347d2","timestamp": 1584691542469}
        """
        val calculateLength: BodyLength = {responseContent.length.toLong()}
        val openStream: BodySource = { ByteArrayInputStream(responseContent.toByteArray())}
        val body = DefaultBody.from(
            calculateLength = calculateLength,
            openStream = openStream
        )

        val mockClient = Mockito.mock(Client::class.java)
        Mockito.`when`(mockClient.executeRequest(any())).thenReturn(Response(
            statusCode = 200,
            body = body,
            url = URL("https://test.com")
        ))

        FuelManager.instance.client = mockClient

        val trash1 = TrashData().apply {
            id = "123456"
            type = "burn"
            schedules = arrayListOf(
                TrashSchedule().apply {
                    type = "biweek"
                    value = "0-3"
                }, TrashSchedule().apply {
                    type = "biweek"
                    value = "6-1"
                })
        }
        val trash2 = TrashData().apply {
            id = "5678"
            type = "other"
            trash_val = "家電"
            schedules = arrayListOf(
                TrashSchedule().apply {
                    type = "biweek"
                    value = "0-3"
                })
        }
        val result = instance.register(arrayListOf(trash1, trash2))
        Assert.assertEquals("8051b7f9eb654364ae77f0e770e347d2", result?.id)
        Assert.assertEquals(1584691542469, result?.timestamp)

    }

    @Test
    fun register_failed() {
        val calculateLength: BodyLength = {"".length.toLong()}
        val openStream: BodySource = { ByteArrayInputStream("".toByteArray())}
        val body = DefaultBody.from(
            calculateLength = calculateLength,
            openStream = openStream
        )

        val mockClient = Mockito.mock(Client::class.java)
        Mockito.`when`(mockClient.executeRequest(any())).thenReturn(Response(
            statusCode = 500,
            body = body,
            url = URL("https://test.com")
        ))

        FuelManager.instance.client = mockClient

        val result = instance.register(arrayListOf())
        Assert.assertNull(result)
    }

    @Test
    fun publishActivationCode() {
        val responseContent = "{\"code\": \"234567\"}"
        val calculateLength: BodyLength = {responseContent.length.toLong()}
        val openStream: BodySource = { ByteArrayInputStream(responseContent.toByteArray())}
        val body = DefaultBody.from(
            calculateLength = calculateLength,
            openStream = openStream
        )

        val mockClient = Mockito.mock(Client::class.java)
        Mockito.`when`(mockClient.executeRequest(any())).thenReturn(Response(
            statusCode = 200,
            body = body,
            url = URL("https://test.com")
        ))

        FuelManager.instance.client = mockClient

        val result = instance.publishActivationCode("901d9db9-9723-4845-8929-b88814f82e49")
        Assert.assertEquals("234567", result)
    }

    @Test
    fun publishActivationCode_failed() {
        val responseContent = "{\"code\": \"234567\"}"
        val calculateLength: BodyLength = {responseContent.length.toLong()}
        val openStream: BodySource = { ByteArrayInputStream(responseContent.toByteArray())}
        val body = DefaultBody.from(
            calculateLength = calculateLength,
            openStream = openStream
        )

        val mockClient = Mockito.mock(Client::class.java)
        Mockito.`when`(mockClient.executeRequest(any())).thenReturn(Response(
            statusCode = 500,
            body = body,
            url = URL("https://test.com")
        ))

        FuelManager.instance.client = mockClient

        val result = instance.publishActivationCode("901d9db9-9723-4845-8929-b88814f82e49")
        Assert.assertNull(result)
    }

    @Test
    fun activate() {
        // テスト用データ
        // id:8051b7f9eb654364ae77f0e770e347d2
        // description:[{\"id\":\"1234567\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"},{\"type\":\"biweek\",\"value\":\"1-1\"}]},{\"id\":\"8901234\",\"type\":\"other\",\"trash_val\":\"空き缶\",\"schedules\":[{\"type\":\"evweek\",\"value\":{\"weekday\":\"2\",\"start\":\"2020-03-08\"}}]}]
        // timestamp: 1584691542469

        val responseContent = """
            {"description": "[{\"id\":\"1234567\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"},{\"type\":\"biweek\",\"value\":\"1-1\"}]},{\"id\":\"8901234\",\"type\":\"other\",\"trash_val\":\"空き缶\",\"schedules\":[{\"type\":\"evweek\",\"value\":{\"weekday\":\"2\",\"start\":\"2020-03-08\"}}]}]","timestamp": 1584691542469}
        """
        val calculateLength: BodyLength = {responseContent.length.toLong()}
        val openStream: BodySource = { ByteArrayInputStream(responseContent.toByteArray())}
        val body = DefaultBody.from(
            calculateLength = calculateLength,
            openStream = openStream
        )

        val mockClient = Mockito.mock(Client::class.java)
        Mockito.`when`(mockClient.executeRequest(any())).thenReturn(Response(
            statusCode = 200,
            body = body,
            url = URL("https://test.com")
        ))

        FuelManager.instance.client = mockClient

        val result = instance.activate("99999", "id001")
        Assert.assertEquals("burn", result?.scheduleList?.get(0)?.type)
        Assert.assertEquals("biweek", result?.scheduleList?.get(0)?.schedules?.get(1)?.type)
        Assert.assertEquals("1-1", result?.scheduleList?.get(0)?.schedules?.get(1)?.value)
        Assert.assertEquals("other", result?.scheduleList?.get(1)?.type)
        Assert.assertEquals("空き缶", result?.scheduleList?.get(1)?.trash_val)
        Assert.assertEquals("evweek", result?.scheduleList?.get(1)?.schedules?.get(0)?.type)
        Assert.assertEquals(
            "2",
            (result?.scheduleList?.get(1)?.schedules?.get(0)?.value as HashMap<String, String>)["weekday"]
        )
        Assert.assertEquals(
            "2020-03-08",
            (result.scheduleList.get(1).schedules.get(0).value as HashMap<String, String>)["start"]
        )
        Assert.assertEquals(1584691542469, result.timestamp)
    }

    @Test
    fun activate_failed() {
        val calculateLength: BodyLength = {"".length.toLong()}
        val openStream: BodySource = { ByteArrayInputStream("".toByteArray())}
        val body = DefaultBody.from(
            calculateLength = calculateLength,
            openStream = openStream
        )

        val mockClient = Mockito.mock(Client::class.java)
        Mockito.`when`(mockClient.executeRequest(any())).thenReturn(Response(
            statusCode = 500,
            body = body,
            url = URL("https://test.com")
        ))

        FuelManager.instance.client = mockClient
        val result = instance.activate("dummy", "id001")
        Assert.assertNull(result)
    }

    @Test
    fun accountLink_Success_with_SingleCookie() {
        val responseContent = """
            {"url": "https://test.com", "token": "123456}
        """
        val calculateLength: BodyLength = {responseContent.length.toLong()}
        val openStream: BodySource = { ByteArrayInputStream(responseContent.toByteArray())}
        val body = DefaultBody.from(
            calculateLength = calculateLength,
            openStream = openStream
        )
        val headers = Headers()
        headers.append("Set-Cookie", "throwaway-session=123456")

        val mockClient = Mockito.mock(Client::class.java)
        Mockito.`when`(mockClient.executeRequest(any())).thenReturn(Response(
            statusCode = 200,
            headers =headers,
            body = body,
            url = URL("https://test.com")
        ))

        FuelManager.instance.client = mockClient
        val result = instance.accountLink("dummy-id")
        Assert.assertEquals(result?.linkUrl, "https://test.com")
        Assert.assertEquals(result?.token, "123456")
    }

    @Test
    fun accountLink_Success_with_MultiCookie() {
        val responseContent = """
            {"url": "https://test.com", "token": "123456}
        """
        val calculateLength: BodyLength = {responseContent.length.toLong()}
        val openStream: BodySource = { ByteArrayInputStream(responseContent.toByteArray())}
        val body = DefaultBody.from(
            calculateLength = calculateLength,
            openStream = openStream
        )
        val headers = Headers()
        headers.append("Set-Cookie", "another-key=another-value")
        headers.append("Set-Cookie", "throwaway-session=123456; MaxAge=11111; Expire=111111")

        val mockClient = Mockito.mock(Client::class.java)
        Mockito.`when`(mockClient.executeRequest(any())).thenReturn(Response(
            statusCode = 200,
            headers =headers,
            body = body,
            url = URL("https://test.com")
        ))

        FuelManager.instance.client = mockClient
        val result1 = instance.accountLink("dummy-id")
        Assert.assertEquals("https://test.com",result1?.linkUrl )
        Assert.assertEquals("123456",result1?.token )
    }

    @Test
    fun accountLink_failed() {
        val calculateLength: BodyLength = {"".length.toLong()}
        val openStream: BodySource = { ByteArrayInputStream("".toByteArray())}
        val body = DefaultBody.from(
            calculateLength = calculateLength,
            openStream = openStream
        )

        val mockClient = Mockito.mock(Client::class.java)
        Mockito.`when`(mockClient.executeRequest(any())).thenReturn(Response(
            statusCode = 500,
            body = body,
            url = URL("https://test.com")
        ))

        FuelManager.instance.client = mockClient
        val result = instance.accountLink("dummy")
        Assert.assertNull(result)
    }
}