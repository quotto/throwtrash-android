package net.mythrowaway.app.presenter

import net.mythrowaway.app.adapter.PreferencePersistImpl
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.domain.TrashSchedule
import net.mythrowaway.app.util.TestSharedPreferencesImpl
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PreferencePersistImplTest {
    private val testPreference =
        TestSharedPreferencesImpl()
    private val instance: PreferencePersistImpl = PreferencePersistImpl(testPreference)
    @Before
    fun before() {
        testPreference.edit().clear()
    }
    @Test
    fun saveTrashData() {
        testPreference.edit().apply {
            putString(
                PreferencePersistImpl.KEY_TRASH_DATA,
                """
                        [
                            {"id":1,"schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23"}}],"type":"burn"}
                        ]
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

        assert(Regex("\\[\\{\"id\":\"1\",\"type\":\"burn\",\"schedules\":\\[\\{\"type\":\"weekday\",\"value\":\"0\"\\},\\{\"type\":\"evweek\",\"value\":\\{\"weekday\":\"2\",\"start\":\"2020\\-2\\-23\"\\}\\}\\]},\\{\"id\":\"[0-9]+\",\"type\":\"resource\",\"schedules\":\\[\\{\"type\":\"weekday\",\"value\":\"5\"\\}\\]\\}\\]")
            .matches(testPreference.getString(PreferencePersistImpl.KEY_TRASH_DATA,"")!!))
    }

    @Test
    fun saveTrashData_EmptyData() {
        // 登録済みデータがデフォルト（空）の場合
        testPreference.edit().apply {
            putString(
                PreferencePersistImpl.KEY_TRASH_DATA,
                PreferencePersistImpl.DEFAULT_KEY_TRASH_DATA
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

        assert(Regex("\\[\\{\"id\":\"[0-9]+\",\"type\":\"resource\",\"schedules\":\\[\\{\"type\":\"weekday\",\"value\":\"5\"\\}\\]\\}\\]")
            .matches(testPreference.getString(PreferencePersistImpl.KEY_TRASH_DATA,"")!!))
    }

    @Test
    fun saveTrashData_NullData() {
        // 登録済みデータが無い（Nullの）場合
        testPreference.edit().apply {
            putString(
                PreferencePersistImpl.KEY_TRASH_DATA,null
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

        assert(Regex("\\[\\{\"id\":\"[0-9]+\",\"type\":\"resource\",\"schedules\":\\[\\{\"type\":\"weekday\",\"value\":\"5\"\\}\\]\\}\\]")
            .matches(testPreference.getString(PreferencePersistImpl.KEY_TRASH_DATA,"")!!))
    }



    @Test
    fun updateTrashData() {
        testPreference.edit().apply {
            putString(
                PreferencePersistImpl.KEY_TRASH_DATA,
                """
                            [
                                {"id":"1","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23"}}],"type":"burn"},
                                {"id":"999","schedules":[{"type":"weekday","value":"3"}],"type": "petbottle"} 
                            ]
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
                    [{"id":"1","type":"burn","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23"}}]},{"id":"999","type":"resource","schedules":[{"type":"weekday","value":"5"}]}]
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
                        [{"id":"999","schedules":[{"type":"weekday","value":"3"}],"type": "petbottle"}]
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
                    [{"id":"999","type":"resource","schedules":[{"type":"weekday","value":"5"}]}]
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
                        [{"id":"999","schedules":[{"type":"weekday","value":"3"}],"type": "petbottle"}]
                """.trimIndent()
            )
        }

        instance.deleteTrashData("999")
        Assert.assertEquals("[]",testPreference.getString(PreferencePersistImpl.KEY_TRASH_DATA,""))
    }

    @Test
    fun deleteTrashData_multiData() {
        testPreference.edit().apply {
            putString(
                PreferencePersistImpl.KEY_TRASH_DATA,
                """
                        [
                            {"id":"1","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23"}}],"type":"burn"},
                            {"id":"999","schedules":[{"type":"weekday","value":"3"}],"type": "petbottle"}
                        ]
                """.trimIndent()
            )
        }
        instance.deleteTrashData("999")
        val expect = """
            [{"id":"1","type":"burn","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23"}}]}]
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
                        [
                            {"id":"999","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23"}}],"type":"burn"},
                            {"id":"999","schedules":[{"type":"weekday","value":"3"}],"type": "petbottle"}
                        ]
                """.trimIndent()
            )
        }
        instance.deleteTrashData("999")
        // idが重複している場合は該当する全てのデータが削除される
        Assert.assertEquals("[]",testPreference.getString(PreferencePersistImpl.KEY_TRASH_DATA,""))
    }

    @Test
    fun getAllTrashSchedule_DataNone() {
        val result = instance.getAllTrashSchedule()
        Assert.assertEquals(0,result.size)

        testPreference.edit().apply {
            putString(
                PreferencePersistImpl.KEY_TRASH_DATA,"[]"
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
                        [
                            {"id":"1","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23"}}],"type":"burn"},
                            {"id":"999","schedules":[{"type":"weekday","value":"3"}],"type": "petbottle"}
                        ]
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
                        [
                            {"id":"999","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23"}}],"type":"burn"},
                            {"id":"999","schedules":[{"type":"weekday","value":"3"}],"type": "petbottle"}
                        ]
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
                        [
                            {"id":"999","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23"}}],"type":"burn"},
                            {"id":"999","schedules":[{"type":"weekday","value":"3"}],"type": "petbottle"}
                        ]
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
                PreferencePersistImpl.KEY_TRASH_DATA,"[]"
            )
        }

        val trashData = instance.getTrashData("3")
        // idが重複している場合は該当する1件目のデータが取得される
        Assert.assertEquals(null,trashData)
    }


}
