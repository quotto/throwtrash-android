package com.example.mythrowtrash

import com.example.mythrowtrash.adapter.APIAdapter
import com.example.mythrowtrash.domain.TrashData
import com.example.mythrowtrash.domain.TrashSchedule
import org.junit.Assert
import org.junit.Test

class APIAdapterTest {
    val instance = APIAdapter()
    @Test
    fun syncTest(){
        // テスト用データ（us-west-2）
        //
        // description:[{\"id\":\"1234567\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"},{\"type\":\"biweek\",\"value\":\"1-1\"}]},{\"id\":\"8901234\",\"type\":\"other\",\"trash_val\":\"空き缶\",\"schedules\":[{\"type\":\"evweek\",\"value\":{\"weekday\":\"2\",\"start\":\"2020-03-08\"}}]}]
        // timestamp: 1584691542469
        val result = instance.sync("8051b7f9eb654364ae77f0e770e347d2")
        Assert.assertEquals(2,result?.first?.size)
        Assert.assertEquals("burn", result?.first?.get(0)?.type)
        Assert.assertEquals("biweek", result?.first?.get(0)?.schedules?.get(1)?.type)
        Assert.assertEquals("1-1", result?.first?.get(0)?.schedules?.get(1)?.value)
        Assert.assertEquals("other", result?.first?.get(1)?.type)
        Assert.assertEquals("空き缶", result?.first?.get(1)?.trash_val)
        Assert.assertEquals("evweek", result?.first?.get(1)?.schedules?.get(0)?.type)
        Assert.assertEquals("2", (result?.first?.get(1)?.schedules?.get(0)?.value as HashMap<String,String>)["weekday"])
        Assert.assertEquals("2020-03-08", (result?.first?.get(1)?.schedules?.get(0)?.value as HashMap<String,String>)["start"])
        Assert.assertEquals(1584691542469,result?.second)
    }

    @Test
    fun syncTest_NotExistsID(){
        val result = instance.sync("dummy")
        Assert.assertEquals(null,result)
    }

    @Test
    fun update() {
        val trash1 = TrashData().apply {
            id = "12345"
            type = "burn"
            schedules = arrayListOf(TrashSchedule().apply{
                type = "biweek"
                value = "0-3"
            }, TrashSchedule().apply{
                type = "biweek"
                value = "6-1"
            })
        }
        val trash2 = TrashData().apply {
            id = "56789"
            type = "other"
            trash_val = "家電"
            schedules = arrayListOf(TrashSchedule().apply{
                type = "biweek"
                value = "0-3"
            })
        }
        val result = instance.update("901d9db9-9723-4845-8929-b88814f82e49", arrayListOf(trash1,trash2))
        println(result)
        assert(result != null)
    }

    @Test
    fun update_NotExistId() {
        val trash1 = TrashData().apply {
            type = "burn"
            schedules = arrayListOf(TrashSchedule().apply{
                type = "biweek"
                value = "0-3"
            }, TrashSchedule().apply{
                type = "biweek"
                value = "6-1"
            })
        }
        val trash2 = TrashData().apply {
            type = "other"
            trash_val = "家電"
            schedules = arrayListOf(TrashSchedule().apply{
                type = "biweek"
                value = "0-3"
            })
        }
        val result = instance.update("dummy", arrayListOf(trash1,trash2))
        Assert.assertEquals(null,result)
    }

    @Test
    fun update_InvalidData() {
        val result = instance.update("901d9db9-9723-4845-8929-b88814f82e49", arrayListOf())
        Assert.assertEquals(null,result)
    }

    @Test
    fun register() {
        val trash1 = TrashData().apply {
            id = "123456"
            type = "burn"
            schedules = arrayListOf(TrashSchedule().apply{
                type = "biweek"
                value = "0-3"
            }, TrashSchedule().apply{
                type = "biweek"
                value = "6-1"
            })
        }
        val trash2 = TrashData().apply {
            id = "5678"
            type = "other"
            trash_val = "家電"
            schedules = arrayListOf(TrashSchedule().apply{
                type = "biweek"
                value = "0-3"
            })
        }
        val result = instance.register(arrayListOf(trash1,trash2))
        Assert.assertEquals(36,result?.first?.length)
        assert(result?.second != null)
    }

    @Test
    fun register_InvalidData() {
        val result = instance.register(arrayListOf())
        assert(result == null)
    }

    @Test
    fun publishActivationCode() {
        val result = instance.publishActivationCode("901d9db9-9723-4845-8929-b88814f82e49")
        println(result)
        Assert.assertEquals(5,result?.length)
    }

    @Test
    fun publishActivationCode_NotExistId(){
        val result = instance.publishActivationCode("dummy")
        assert(result == null)
    }

    @Test
    fun activate() {
        // テスト用データ（us-west-2）
        // id:8051b7f9eb654364ae77f0e770e347d2
        // description:[{\"id\":\"1234567\",\"type\":\"burn\",\"trash_val\":\"\",\"schedules\":[{\"type\":\"weekday\",\"value\":\"0\"},{\"type\":\"biweek\",\"value\":\"1-1\"}]},{\"id\":\"8901234\",\"type\":\"other\",\"trash_val\":\"空き缶\",\"schedules\":[{\"type\":\"evweek\",\"value\":{\"weekday\":\"2\",\"start\":\"2020-03-08\"}}]}]
        // timestamp: 1584691542469
        val result = instance.activate("99999")
        Assert.assertEquals("8051b7f9eb654364ae77f0e770e347d2",result?.id)
        Assert.assertEquals("burn", result?.scheduleList?.get(0)?.type)
        Assert.assertEquals("biweek", result?.scheduleList?.get(0)?.schedules?.get(1)?.type)
        Assert.assertEquals("1-1", result?.scheduleList?.get(0)?.schedules?.get(1)?.value)
        Assert.assertEquals("other", result?.scheduleList?.get(1)?.type)
        Assert.assertEquals("空き缶", result?.scheduleList?.get(1)?.trash_val)
        Assert.assertEquals("evweek", result?.scheduleList?.get(1)?.schedules?.get(0)?.type)
        Assert.assertEquals("2", (result?.scheduleList?.get(1)?.schedules?.get(0)?.value as HashMap<String,String>)["weekday"])
        Assert.assertEquals("2020-03-08", (result?.scheduleList?.get(1)?.schedules?.get(0)?.value as HashMap<String,String>)["start"])
        Assert.assertEquals(1584691542469,result?.timestamp)
    }

    @Test
    fun activate_NotExistCode() {
        val result = instance.activate("dummy")
        assert(result == null)
    }
}