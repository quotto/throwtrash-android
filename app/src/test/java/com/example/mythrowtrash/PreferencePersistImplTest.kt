package com.example.mythrowtrash

import com.example.mythrowtrash.adapter.PreferencePersistImpl
import com.example.mythrowtrash.domain.TrashData
import com.example.mythrowtrash.domain.TrashSchedule
import com.example.mythrowtrash.util.TestSharedPreferencesImpl
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Method

class PreferencePersistImplTest {
    private val testPreference = TestSharedPreferencesImpl()
    private val instance:PreferencePersistImpl = PreferencePersistImpl(testPreference)
    @Before
    fun before() {
        testPreference.edit().clear()
    }
    @Test
    fun jsonToTrashData_notOther() {
        val data = """
            {"type":"burn","schedules":[{"type":"weekday","value":"0"}]}
        """.trimIndent()
        val method: Method = instance.javaClass.getDeclaredMethod("jsonToTrashData",String::class.java)
        method.setAccessible(true)
        val trashData:TrashData = method.invoke(instance,data) as TrashData

        Assert.assertEquals("burn", trashData.type)
        Assert.assertEquals(null, trashData.trash_val)
        Assert.assertEquals("weekday", trashData.schedules[0].type)
        Assert.assertEquals("0", trashData.schedules[0].value)
    }

    @Test
    fun jsonToTrashData_Other_MultiSchedule() {
        val data = """
            {"type":"other","trash_val":"生ゴミ","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"1","start":"2020-02-01"}}]}
        """.trimIndent()
        val method: Method = instance.javaClass.getDeclaredMethod("jsonToTrashData",String::class.java)
        method.setAccessible(true)
        val trashData:TrashData = method.invoke(instance,data) as TrashData

        Assert.assertEquals("other", trashData.type)
        Assert.assertEquals("生ゴミ", trashData.trash_val)
        Assert.assertEquals("weekday", trashData.schedules[0].type)
        Assert.assertEquals("0", trashData.schedules[0].value)
        Assert.assertEquals("evweek", trashData.schedules[1].type)
        Assert.assertEquals("1", (trashData.schedules[1].value as HashMap<String,String>)["weekday"])
        Assert.assertEquals("2020-02-01", (trashData.schedules[1].value as HashMap<String,String>)["start"])
    }

    @Test
    fun jsonToTrashList() {
        val data = """
            [
                {"type": "burn","schedules":[{"type":"weekday", "value": "1"},{"type":"month","value":"2"}]},
                {"type": "other","trash_val":"生ゴミ","schedules":[{"type":"evweek","value":{"weekday":"1","start":"2020-02-01"}}]}
            ]
        """

        val method: Method = instance.javaClass.getDeclaredMethod("jsonToTrashList",String::class.java)
        method.setAccessible(true)
        val trashList:ArrayList<TrashData> = method.invoke(instance,data) as ArrayList<TrashData>

        Assert.assertEquals("burn",trashList[0].type)
        Assert.assertEquals("weekday",trashList[0].schedules[0].type)
        Assert.assertEquals("1",trashList[0].schedules[0].value)
        Assert.assertEquals("month",trashList[0].schedules[1].type)
        Assert.assertEquals("2",trashList[0].schedules[1].value)
        Assert.assertEquals("other",trashList[1].type)
        Assert.assertEquals("生ゴミ",trashList[1].trash_val)
        Assert.assertEquals("evweek",trashList[1].schedules[0].type)
        Assert.assertEquals("1",(trashList[1].schedules[0].value as HashMap<String,String>)["weekday"])
        Assert.assertEquals("2020-02-01",(trashList[1].schedules[0].value as HashMap<String,String>)["start"])
    }

    @Test
    fun jsonToTrashList_Empty() {
        val data = "[]"
        val method: Method = instance.javaClass.getDeclaredMethod("jsonToTrashList",String::class.java)
        method.setAccessible(true)
        val result:ArrayList<TrashData> = method.invoke(instance,data) as ArrayList<TrashData>
        assert(result.isEmpty())
    }

    @Test
    fun saveTrashData() {
        testPreference.edit().apply {
            putString(
                PreferencePersistImpl.KEY_TRASH_DATA,
                """
                        {"id":1,"schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23"}}],"type":"burn"}
                """.trimIndent()
            )
        }

        val schedule = TrashSchedule()
        schedule.type = "weekday"
        schedule.value = "5"
        val addData = TrashData()
        addData.schedules = arrayListOf(schedule)
        addData.type = "resource"

        instance.saveTrashData(addData)

        val expect = """
                    {"id":1,"schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23"}}],"type":"burn"}&{"id":999,"type":"resource","schedules":[{"type":"weekday","value":"5"}]}
        """.trimIndent()
        Assert.assertEquals(2,testPreference.getString(PreferencePersistImpl.KEY_TRASH_DATA,"")?.split("&")?.size)
        assert(Regex("\\{\"id\":1,\"schedules\":\\[\\{\"type\":\"weekday\",\"value\":\"0\"\\},\\{\"type\":\"evweek\",\"value\":\\{\"weekday\":\"2\",\"start\":\"2020\\-2\\-23\"\\}\\}\\],\"type\":\"burn\"}&\\{\"id\":\"[0-9]+\",\"type\":\"resource\",\"schedules\":\\[\\{\"type\":\"weekday\",\"value\":\"5\"\\}\\]\\}")
            .matches(testPreference.getString(PreferencePersistImpl.KEY_TRASH_DATA,"")!!))
    }

    @Test
    fun saveTrashData_EmptyData() {
        // 登録済みデータがブランクの場合
        testPreference.edit().apply {
            putString(
                PreferencePersistImpl.KEY_TRASH_DATA,""
            )
        }

        val schedule = TrashSchedule()
        schedule.type = "weekday"
        schedule.value = "5"
        val addData = TrashData()
        addData.id = "999"
        addData.schedules = arrayListOf(schedule)
        addData.type = "resource"

        instance.saveTrashData(addData)

        val expect = """
                    {"id":"[0-9],"type":"resource","schedules":[{"type":"weekday","value":"5"}]}
        """.trimIndent()
        Assert.assertEquals(1,testPreference.getString(PreferencePersistImpl.KEY_TRASH_DATA,"")?.split("&")?.size)
        assert(Regex("\\{\"id\":\"[0-9]+\",\"type\":\"resource\",\"schedules\":\\[\\{\"type\":\"weekday\",\"value\":\"5\"\\}\\]\\}")
            .matches(testPreference.getString(PreferencePersistImpl.KEY_TRASH_DATA,"")!!))
    }

    @Test
    fun saveTrashData_NullData() {
        // 登録済みデータが無い（Nullの）場合

        val schedule = TrashSchedule()
        schedule.type = "weekday"
        schedule.value = "5"
        val addData = TrashData()
        addData.id = "999"
        addData.schedules = arrayListOf(schedule)
        addData.type = "resource"

        instance.saveTrashData(addData)

        Assert.assertEquals(1,testPreference.getString(PreferencePersistImpl.KEY_TRASH_DATA,"")?.split("&")?.size)
        assert(Regex("\\{\"id\":\"[0-9]+\",\"type\":\"resource\",\"schedules\":\\[\\{\"type\":\"weekday\",\"value\":\"5\"\\}\\]\\}")
            .matches(testPreference.getString(PreferencePersistImpl.KEY_TRASH_DATA,"")!!))
    }



    @Test
    fun updateTrashData() {
        testPreference.edit().apply {
            putString(
                PreferencePersistImpl.KEY_TRASH_DATA,
                """
                        {"id":"1","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23"}}],"type":"burn"}&{"id":"999","schedules":[{"type":"weekday","value":"3"}],"type": "petbottle"}
                """.trimIndent()
            )
        }

        val schedule = TrashSchedule()
        schedule.type = "weekday"
        schedule.value = "5"
        val updateData = TrashData()
        updateData.id = "999"
        updateData.schedules = arrayListOf(schedule)
        updateData.type = "resource"

        val expect = """
                    {"id":"1","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23"}}],"type":"burn"}&{"id":"999","type":"resource","schedules":[{"type":"weekday","value":"5"}]}
        """.trimIndent()

        instance.updateTrashData(updateData)
        Assert.assertEquals(expect,testPreference.getString(PreferencePersistImpl.KEY_TRASH_DATA,""))
    }

    @Test
    fun updateTrashData_singleData() {
        testPreference.edit().apply {
            putString(
                PreferencePersistImpl.KEY_TRASH_DATA,
                """
                        {"id":"999","schedules":[{"type":"weekday","value":"3"}],"type": "petbottle"}
                """.trimIndent()
            )
        }

        val schedule = TrashSchedule()
        schedule.type = "weekday"
        schedule.value = "5"
        val updateData = TrashData()
        updateData.id = "999"
        updateData.schedules = arrayListOf(schedule)
        updateData.type = "resource"

        val expect = """
                    {"id":"999","type":"resource","schedules":[{"type":"weekday","value":"5"}]}
        """.trimIndent()

        instance.updateTrashData(updateData)
        Assert.assertEquals(expect,testPreference.getString(PreferencePersistImpl.KEY_TRASH_DATA,""))
    }

    @Test
    fun deleteTrashData_singleData() {
        testPreference.edit().apply {
            putString(
                PreferencePersistImpl.KEY_TRASH_DATA,
                """
                        {"id":"999","schedules":[{"type":"weekday","value":"3"}],"type": "petbottle"}
                """.trimIndent()
            )
        }

        instance.deleteTrashData("999")
        Assert.assertEquals("",testPreference.getString(PreferencePersistImpl.KEY_TRASH_DATA,""))
    }

    @Test
    fun deleteTrashData_multiData() {
        testPreference.edit().apply {
            putString(
                PreferencePersistImpl.KEY_TRASH_DATA,
                """
                        {"id":"1","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23"}}],"type":"burn"}&{"id":"999","schedules":[{"type":"weekday","value":"3"}],"type": "petbottle"}
                """.trimIndent()
            )
        }
        instance.deleteTrashData("999")
        val expect = """
            {"id":"1","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23"}}],"type":"burn"}
        """.trimIndent()
        Assert.assertEquals(expect,testPreference.getString(PreferencePersistImpl.KEY_TRASH_DATA,""))
    }

    @Test
    fun deleteTrashData_duplicateData() {
        /**
         * アプリケーションの仕様上はIDの重複は発生しないため、
         * 通常は起こらないパターン
         */
        testPreference.edit().apply {
            putString(
                PreferencePersistImpl.KEY_TRASH_DATA,
                """
                        {"id":"999","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23"}}],"type":"burn"}&{"id":"999","schedules":[{"type":"weekday","value":"3"}],"type": "petbottle"}
                """.trimIndent()
            )
        }
        instance.deleteTrashData("999")
        // idが重複している場合は該当する全てのデータが削除される
        Assert.assertEquals("",testPreference.getString(PreferencePersistImpl.KEY_TRASH_DATA,""))
    }

    @Test
    fun getAllTrashSchedule_DataNone() {
        val result = instance.getAllTrashSchedule()
        Assert.assertEquals(0,result.size)

        testPreference.edit().apply {
            putString(
                PreferencePersistImpl.KEY_TRASH_DATA,""
            )
            commit()
        }

        val result2 = instance.getAllTrashSchedule()
        Assert.assertEquals(0,result2.size)
    }

    @Test
    fun getTrashData_MultiData() {
        testPreference.edit().apply {
            putString(
                PreferencePersistImpl.KEY_TRASH_DATA,
                """
                        {"id":"1","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23"}}],"type":"burn"}&{"id":"999","schedules":[{"type":"weekday","value":"3"}],"type": "petbottle"}
                """.trimIndent()
            )
            commit()
        }

        val trashData = instance.getTrashData("999")
        Assert.assertEquals(trashData?.id,"999")
    }

    @Test
    fun getTrashData_duplicateData() {
        /**
         * アプリケーションの仕様上はIDの重複は発生しないため、
         * 通常は起こらないパターン
         */
        testPreference.edit().apply {
            putString(
                PreferencePersistImpl.KEY_TRASH_DATA,
                """
                        {"id":"999","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23"}}],"type":"burn"}&{"id":"999","schedules":[{"type":"weekday","value":"3"}],"type": "petbottle"}
                """.trimIndent()
            )
        }
        val trashData = instance.getTrashData("999")
        // idが重複している場合は該当する1件目のデータが取得される
        Assert.assertEquals("burn",trashData?.type)
    }

    @Test
    fun getTrashData_DataNotFound() {
        /**
         * アプリケーションの仕様上は発生しないパターン
         */
        testPreference.edit().apply {
            putString(
                PreferencePersistImpl.KEY_TRASH_DATA,
                """
                        {"id":"999","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23"}}],"type":"burn"}&{"id":"999","schedules":[{"type":"weekday","value":"3"}],"type": "petbottle"}
                """.trimIndent()
            )
        }
        val trashData = instance.getTrashData("3")
        // idが重複している場合は該当する1件目のデータが取得される
        Assert.assertEquals(null,trashData)
    }

    @Test
    fun getTrashData_NoneData() {
        val trashData = instance.getTrashData("3")
        // idが重複している場合は該当する1件目のデータが取得される
        Assert.assertEquals(null,trashData)
    }

    @Test
    fun getTrashData_EmptyData() {
        testPreference.edit().apply {
            putString(
                PreferencePersistImpl.KEY_TRASH_DATA,""
            )
        }

        val trashData = instance.getTrashData("3")
        // idが重複している場合は該当する1件目のデータが取得される
        Assert.assertEquals(null,trashData)
    }


}
