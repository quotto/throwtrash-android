package net.mythrowaway.app.adapter

import android.content.Context
import androidx.preference.PreferenceManager
import net.mythrowaway.app.adapter.repository.PreferencePersistImpl
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.domain.TrashSchedule
import net.mythrowaway.app.stub.StubSharedPreferencesImpl
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(
    PreferenceManager::class,
    Context::class
)
class PreferencePersistImplTest {
    private val stubSharedPreference =
        StubSharedPreferencesImpl()

    private val mockContext: Context = PowerMockito.mock(Context::class.java)

    @InjectMocks
    private lateinit var instance: PreferencePersistImpl

    @Before
    fun before() {
        PowerMockito.`when`(PreferenceManager.getDefaultSharedPreferences(mockContext)).thenReturn(stubSharedPreference)
        stubSharedPreference.edit().clear()
    }
    @Test
    fun saveTrashData() {
        stubSharedPreference.edit().apply {
            putString(
                PreferencePersistImpl.KEY_TRASH_DATA,
                """
                        [
                            {"id":1,"schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23","interval":3}}],"type":"burn"}
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

        assert(Regex("\\[\\{\"id\":\"1\",\"type\":\"burn\",\"schedules\":\\[\\{\"type\":\"weekday\",\"value\":\"0\"\\},\\{\"type\":\"evweek\",\"value\":\\{\"weekday\":\"2\",\"start\":\"2020\\-2\\-23\",\"interval\":3\\}\\}\\]},\\{\"id\":\"[0-9]+\",\"type\":\"resource\",\"schedules\":\\[\\{\"type\":\"weekday\",\"value\":\"5\"\\}\\]\\}\\]")
            .matches(stubSharedPreference.getString(PreferencePersistImpl.KEY_TRASH_DATA,"")!!))
    }

    @Test
    fun saveTrashData_EmptyData() {
        // 登録済みデータがデフォルト（空）の場合
        stubSharedPreference.edit().apply {
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
            .matches(stubSharedPreference.getString(PreferencePersistImpl.KEY_TRASH_DATA,"")!!))
    }

    @Test
    fun saveTrashData_NullData() {
        // 登録済みデータが無い（Nullの）場合
        stubSharedPreference.edit().apply {
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
            .matches(stubSharedPreference.getString(PreferencePersistImpl.KEY_TRASH_DATA,"")!!))
    }



    @Test
    fun updateTrashData() {
        stubSharedPreference.edit().apply {
            putString(
                PreferencePersistImpl.KEY_TRASH_DATA,
                """
                            [
                                {"id":"1","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23","interval":4}}],"type":"burn"},
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
                    [{"id":"1","type":"burn","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23","interval":4}}]},{"id":"999","type":"resource","schedules":[{"type":"weekday","value":"5"}]}]
        """.trimIndent()

        instance.updateTrashData(updateData)
        Assert.assertEquals(expect,stubSharedPreference.getString(PreferencePersistImpl.KEY_TRASH_DATA,""))
    }

    @Test
    fun updateTrashData_singleData() {
        stubSharedPreference.edit().apply {
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
        Assert.assertEquals(expect,stubSharedPreference.getString(PreferencePersistImpl.KEY_TRASH_DATA,""))
    }

    @Test
    fun deleteTrashData_singleData() {
        stubSharedPreference.edit().apply {
            putString(
                PreferencePersistImpl.KEY_TRASH_DATA,
                """
                        [{"id":"999","schedules":[{"type":"weekday","value":"3"}],"type": "petbottle"}]
                """.trimIndent()
            )
        }

        instance.deleteTrashData("999")
        Assert.assertEquals("[]",stubSharedPreference.getString(PreferencePersistImpl.KEY_TRASH_DATA,""))
    }

    @Test
    fun deleteTrashData_multiData() {
        stubSharedPreference.edit().apply {
            putString(
                PreferencePersistImpl.KEY_TRASH_DATA,
                """
                        [
                            {"id":"1","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23","interval": 2}}],"type":"burn"},
                            {"id":"999","schedules":[{"type":"weekday","value":"3"}],"type": "petbottle"}
                        ]
                """.trimIndent()
            )
        }
        instance.deleteTrashData("999")
        val expect = """
            [{"id":"1","type":"burn","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23","interval":2}}]}]
        """.trimIndent()
        Assert.assertEquals(expect,stubSharedPreference.getString(PreferencePersistImpl.KEY_TRASH_DATA,""))
    }

    @Test
    fun deleteTrashData_duplicateData() {
        /**
         * アプリケーションの仕様上はIDの重複は発生しないため、
         * 通常は起こらないパターン
         */
        stubSharedPreference.edit().apply {
            putString(
                PreferencePersistImpl.KEY_TRASH_DATA,
                """
                        [
                            {"id":"999","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23","interval":2}}],"type":"burn"},
                            {"id":"999","schedules":[{"type":"weekday","value":"3"}],"type": "petbottle"}
                        ]
                """.trimIndent()
            )
        }
        instance.deleteTrashData("999")
        // idが重複している場合は該当する全てのデータが削除される
        Assert.assertEquals("[]",stubSharedPreference.getString(PreferencePersistImpl.KEY_TRASH_DATA,""))
    }

    @Test
    fun getAllTrashSchedule_DataNone() {
        val result = instance.getAllTrashSchedule()
        Assert.assertEquals(0,result.size)

        stubSharedPreference.edit().apply {
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
        stubSharedPreference.edit().apply {
            putString(
                PreferencePersistImpl.KEY_TRASH_DATA,
                """
                        [
                            {"id":"1","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23","interval":4}}],"type":"burn"},
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
        stubSharedPreference.edit().apply {
            putString(
                PreferencePersistImpl.KEY_TRASH_DATA,
                """
                        [
                            {"id":"999","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23","interval":4}}],"type":"burn"},
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
        stubSharedPreference.edit().apply {
            putString(
                PreferencePersistImpl.KEY_TRASH_DATA,
                """
                        [
                            {"id":"999","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23","interval":3}}],"type":"burn"},
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
        stubSharedPreference.edit().apply {
            putString(
                PreferencePersistImpl.KEY_TRASH_DATA,"[]"
            )
        }

        val trashData = instance.getTrashData("3")
        // idが重複している場合は該当する1件目のデータが取得される
        Assert.assertEquals(null,trashData)
    }


}