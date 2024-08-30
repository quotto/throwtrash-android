package net.mythrowaway.app.adapter.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import net.mythrowaway.app.domain.ExcludeDayOfMonth
import net.mythrowaway.app.domain.ExcludeDayOfMonthList
import net.mythrowaway.app.domain.OrdinalWeeklySchedule
import net.mythrowaway.app.domain.Trash
import net.mythrowaway.app.domain.TrashList
import net.mythrowaway.app.domain.TrashType
import net.mythrowaway.app.domain.WeeklySchedule
import net.mythrowaway.app.stub.StubSharedPreferencesImpl
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.time.DayOfWeek

class PreferenceDataRepositoryImplTest {
    private val stubSharedPreference =
        StubSharedPreferencesImpl()

    @Mock
    private lateinit var mockContext: Context

    @InjectMocks
    private lateinit var instance: PreferenceTrashRepositoryImpl

    private lateinit var mockedStatic: MockedStatic<PreferenceManager>
    @BeforeEach
    fun before() {
      MockitoAnnotations.openMocks(this)
      mockedStatic = Mockito.mockStatic(PreferenceManager::class.java)
      mockedStatic.`when`<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(mockContext) }
        .thenReturn(stubSharedPreference)
      stubSharedPreference.edit().clear()
    }

    @AfterEach
    fun after() {
      mockedStatic.close()
    }

  @Nested
  inner class SaveTrash {

    @Test
    fun saved_when_not_existed_data() {
      val addData = Trash(
        _id = "999",
        _type = TrashType.RESOURCE,
        schedules = listOf(WeeklySchedule(_dayOfWeek = DayOfWeek.FRIDAY)),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(_members = mutableListOf())
      )

      instance.saveTrash(addData)

      val result = stubSharedPreference.getString(PreferenceTrashRepositoryImpl.KEY_TRASH_DATA, "")
      assertEquals(
        """
        [{"id":"999","type":"resource","trash_val":"資源ごみ","schedules":[{"type":"weekday","value":"5"}],"excludes":[]}]
        """.trimIndent(),
        result
      )
    }

    @Test
    fun saved_when_existed_data_is_null() {
      stubSharedPreference.edit().apply {
        putString(
          PreferenceTrashRepositoryImpl.KEY_TRASH_DATA,
          null
        )
      }

      val addData = Trash(
        _id = "999",
        _type = TrashType.RESOURCE,
        schedules = listOf(WeeklySchedule(_dayOfWeek = DayOfWeek.FRIDAY)),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(_members = mutableListOf())
      )

      instance.saveTrash(addData)

      val result = stubSharedPreference.getString(PreferenceTrashRepositoryImpl.KEY_TRASH_DATA, "")
      assertEquals(
        """
                        [{"id":"999","type":"resource","trash_val":"資源ごみ","schedules":[{"type":"weekday","value":"5"}],"excludes":[]}]
                """.trimIndent(),
        result
      )
    }

    @Test
    fun append_when_existed_single_trash_with_single_schedule() {
      stubSharedPreference.edit().apply {
        putString(
          PreferenceTrashRepositoryImpl.KEY_TRASH_DATA,
          """
                        [{"id":"1","type":"burn","trash_val":"もえるゴミ","schedules":[{"type":"weekday","value":"0"}]}]
                """.trimIndent()
        )
      }

      val addData = Trash(
        _id = "999",
        _type = TrashType.RESOURCE,
        schedules = listOf(WeeklySchedule(_dayOfWeek = DayOfWeek.FRIDAY)),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(_members = mutableListOf())
      )

      instance.saveTrash(addData)

      val result = stubSharedPreference.getString(PreferenceTrashRepositoryImpl.KEY_TRASH_DATA, "")
      assertEquals(
        """
                        [{"id":"1","type":"burn","trash_val":"もえるゴミ","schedules":[{"type":"weekday","value":"0"}],"excludes":[]},{"id":"999","type":"resource","trash_val":"資源ごみ","schedules":[{"type":"weekday","value":"5"}],"excludes":[]}]
                """.trimIndent(),
        result
      )
    }

    @Test
    fun append_when_existed_single_trash_with_multi_schedule() {
      stubSharedPreference.edit().apply {
        putString(
          PreferenceTrashRepositoryImpl.KEY_TRASH_DATA,
          """
                        [{"id":"1","type":"burn","trash_val":"もえるゴミ","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23","interval":3}}]}]
                """.trimIndent()
        )
      }

      val addData = Trash(
        _id = "999",
        _type = TrashType.RESOURCE,
        schedules = listOf(WeeklySchedule(_dayOfWeek = DayOfWeek.FRIDAY)),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(_members = mutableListOf())
      )

      instance.saveTrash(addData)

      val result = stubSharedPreference.getString(PreferenceTrashRepositoryImpl.KEY_TRASH_DATA, "")
      assertEquals(
        """
                        [{"id":"1","type":"burn","trash_val":"もえるゴミ","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23","interval":3}}],"excludes":[]},{"id":"999","type":"resource","trash_val":"資源ごみ","schedules":[{"type":"weekday","value":"5"}],"excludes":[]}]
                """.trimIndent(),
        result
      )
    }

    @Test
    fun append_when_existed_multi_trash_with_single_schedule() {
      stubSharedPreference.edit().apply {
        putString(
          PreferenceTrashRepositoryImpl.KEY_TRASH_DATA,
          """
                        [{"id":"1","type":"burn","trash_val":"もえるゴミ","schedules":[{"type":"weekday","value":"0"}]},{"id":"2","type":"resource","trash_val":"資源ごみ","schedules":[{"type":"weekday","value":"3"}]}]
                """.trimIndent()
        )
      }

      val addData = Trash(
        _id = "999",
        _type = TrashType.PETBOTTLE,
        schedules = listOf(OrdinalWeeklySchedule(_dayOfWeek = DayOfWeek.FRIDAY, _ordinalOfWeek = 2)),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(_members = mutableListOf())
      )

      instance.saveTrash(addData)

      val result = stubSharedPreference.getString(PreferenceTrashRepositoryImpl.KEY_TRASH_DATA, "")
      assertEquals(
        """
          [
            {"id":"1","type":"burn","trash_val":"もえるゴミ","schedules":[{"type":"weekday","value":"0"}],"excludes":[]},
            {"id":"2","type":"resource","trash_val":"資源ごみ","schedules":[{"type":"weekday","value":"3"}],"excludes":[]},
            {"id":"999","type":"petbottle","trash_val":"ペットボトル","schedules":[{"type":"biweek","value":"5-2"}],"excludes":[]}
         ] 
        """.trimIndent().replace("\n", "").replace(" ", ""),
        result
      )
    }

    @Test
    fun append_when_existed_multi_trash_with_multi_schedule() {
      stubSharedPreference.edit().apply {
        putString(
          PreferenceTrashRepositoryImpl.KEY_TRASH_DATA,
          """
                        [{"id":"1","type":"burn","trash_val":"もえるゴミ","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23","interval":3}}]},{"id":"2","type":"resource","trash_val":"資源ごみ","schedules":[{"type":"weekday","value":"3"}]}]
                """.trimIndent()
        )
      }

      val addData = Trash(
        _id = "999",
        _type = TrashType.PETBOTTLE,
        schedules = listOf(WeeklySchedule(_dayOfWeek = DayOfWeek.FRIDAY)),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(_members = mutableListOf())
      )

      instance.saveTrash(addData)

      val result = stubSharedPreference.getString(PreferenceTrashRepositoryImpl.KEY_TRASH_DATA, "")
      assertEquals(
        """
          [
            {"id":"1","type":"burn","trash_val":"もえるゴミ","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23","interval":3}}],"excludes":[]},
            {"id":"2","type":"resource","trash_val":"資源ごみ","schedules":[{"type":"weekday","value":"3"}],"excludes":[]},
            {"id":"999","type":"petbottle","trash_val":"ペットボトル","schedules":[{"type":"weekday","value":"5"}],"excludes":[]}
         ] 
        """.trimIndent().replace("\n", "").replace(" ", ""),
        result
      )
    }

    @Test
    fun append_data_with_single_excludes() {
      stubSharedPreference.edit().apply {
        putString(
          PreferenceTrashRepositoryImpl.KEY_TRASH_DATA,
          """
                        [{"id":"1","type":"burn","trash_val":"もえるゴミ","schedules":[{"type":"weekday","value":"0"}]},{"id":"2","type":"resource","trash_val":"資源ごみ","schedules":[{"type":"weekday","value":"3"}]}]
                """.trimIndent()
        )
      }

      val addData = Trash(
        _id = "999",
        _type = TrashType.PETBOTTLE,
        schedules = listOf(OrdinalWeeklySchedule(_dayOfWeek = DayOfWeek.FRIDAY, _ordinalOfWeek = 2)),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(_members = mutableListOf(ExcludeDayOfMonth(_month = 1, _dayOfMonth = 1)))
      )

      instance.saveTrash(addData)

      val result = stubSharedPreference.getString(PreferenceTrashRepositoryImpl.KEY_TRASH_DATA, "")
      assertEquals(
        """
          [
            {"id":"1","type":"burn","trash_val":"もえるゴミ","schedules":[{"type":"weekday","value":"0"}],"excludes":[]},
            {"id":"2","type":"resource","trash_val":"資源ごみ","schedules":[{"type":"weekday","value":"3"}],"excludes":[]},
            {"id":"999","type":"petbottle","trash_val":"ペットボトル","schedules":[{"type":"biweek","value":"5-2"}],"excludes":[{"month":1,"date":1}]}
         ] 
        """.trimIndent().replace("\n", "").replace(" ", ""),
        result
      )
    }

    @Test
    fun append_data_with_multi_excludes() {
      stubSharedPreference.edit().apply {
        putString(
          PreferenceTrashRepositoryImpl.KEY_TRASH_DATA,
          """
                        [{"id":"1","type":"burn","trash_val":"もえるゴミ","schedules":[{"type":"weekday","value":"0"}]},{"id":"2","type":"resource","trash_val":"資源ごみ","schedules":[{"type":"weekday","value":"3"}]}]
                """.trimIndent()
        )
      }

      val addData = Trash(
        _id = "999",
        _type = TrashType.PETBOTTLE,
        schedules = listOf(OrdinalWeeklySchedule(_dayOfWeek = DayOfWeek.FRIDAY, _ordinalOfWeek = 2)),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(_members = mutableListOf(ExcludeDayOfMonth(_month = 1, _dayOfMonth = 1), ExcludeDayOfMonth(_month = 2, _dayOfMonth = 2)))
      )

      instance.saveTrash(addData)

      val result = stubSharedPreference.getString(PreferenceTrashRepositoryImpl.KEY_TRASH_DATA, "")
      assertEquals(
        """
          [
            {"id":"1","type":"burn","trash_val":"もえるゴミ","schedules":[{"type":"weekday","value":"0"}],"excludes":[]},
            {"id":"2","type":"resource","trash_val":"資源ごみ","schedules":[{"type":"weekday","value":"3"}],"excludes":[]},
            {"id":"999","type":"petbottle","trash_val":"ペットボトル","schedules":[{"type":"biweek","value":"5-2"}],"excludes":[{"month":1,"date":1},{"month":2,"date":2}]}
         ] 
        """.trimIndent().replace("\n", "").replace(" ", ""),
        result
      )
    }

    @Test
    fun append_when_existed_data_with_single_excludes() {
      stubSharedPreference.edit().apply {
        putString(
          PreferenceTrashRepositoryImpl.KEY_TRASH_DATA,
          """
                        [{"id":"1","type":"burn","trash_val":"もえるゴミ","schedules":[{"type":"weekday","value":"0"}],"excludes":[]},{"id":"2","type":"resource","trash_val":"資源ごみ","schedules":[{"type":"weekday","value":"3"}],"excludes":[{"month":12,"date":31}]}]
                """.trimIndent()
        )
      }

      val addData = Trash(
        _id = "999",
        _type = TrashType.PETBOTTLE,
        schedules = listOf(OrdinalWeeklySchedule(_dayOfWeek = DayOfWeek.FRIDAY, _ordinalOfWeek = 2)),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(_members = mutableListOf(ExcludeDayOfMonth(_month = 1, _dayOfMonth = 1)))
      )

      instance.saveTrash(addData)

      val result = stubSharedPreference.getString(PreferenceTrashRepositoryImpl.KEY_TRASH_DATA, "")
      assertEquals(
        """
          [
            {"id":"1","type":"burn","trash_val":"もえるゴミ","schedules":[{"type":"weekday","value":"0"}],"excludes":[]},
            {"id":"2","type":"resource","trash_val":"資源ごみ","schedules":[{"type":"weekday","value":"3"}],"excludes":[{"month":12,"date":31}]},
            {"id":"999","type":"petbottle","trash_val":"ペットボトル","schedules":[{"type":"biweek","value":"5-2"}],"excludes":[{"month":1,"date":1}]}
         ] 
        """.trimIndent().replace("\n", "").replace(" ", ""),
        result
      )
    }

    @Test
    fun append_when_existed_data_with_multi_excludes() {
      stubSharedPreference.edit().apply {
        putString(
          PreferenceTrashRepositoryImpl.KEY_TRASH_DATA,
          """
                        [{"id":"1","type":"burn","trash_val":"もえるゴミ","schedules":[{"type":"weekday","value":"0"}],"excludes":[]},{"id":"2","type":"resource","trash_val":"資源ごみ","schedules":[{"type":"weekday","value":"3"}],"excludes":[{"month":11,"date":30},{"month":12,"date":31}]}]
                """.trimIndent()
        )
      }

      val addData = Trash(
        _id = "999",
        _type = TrashType.PETBOTTLE,
        schedules = listOf(OrdinalWeeklySchedule(_dayOfWeek = DayOfWeek.FRIDAY, _ordinalOfWeek = 2)),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(_members = mutableListOf(ExcludeDayOfMonth(_month = 1, _dayOfMonth = 1), ExcludeDayOfMonth(_month = 2, _dayOfMonth = 2)))
      )

      instance.saveTrash(addData)

      val result = stubSharedPreference.getString(PreferenceTrashRepositoryImpl.KEY_TRASH_DATA, "")
      assertEquals(
        """
          [
            {"id":"1","type":"burn","trash_val":"もえるゴミ","schedules":[{"type":"weekday","value":"0"}],"excludes":[]},
            {"id":"2","type":"resource","trash_val":"資源ごみ","schedules":[{"type":"weekday","value":"3"}],"excludes":[{"month":11,"date":30},{"month":12,"date":31}]},
            {"id":"999","type":"petbottle","trash_val":"ペットボトル","schedules":[{"type":"biweek","value":"5-2"}],"excludes":[{"month":1,"date":1},{"month":2,"date":2}]}
         ] 
        """.trimIndent().replace("\n", "").replace(" ", ""),
        result
      )
    }
    @Test
    fun set_trashVal_and_excludes_to_existed_data_if_not_set_on_existed_data() {
      stubSharedPreference.edit().apply {
        putString(
          PreferenceTrashRepositoryImpl.KEY_TRASH_DATA,
          """
                        [
                            {"id":1,"schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23","interval":3}}],"type":"burn"}
                        ]
                """.trimIndent()
        )
      }

      val addData = Trash(
        _id = "999",
        _type = TrashType.RESOURCE,
        schedules = listOf(WeeklySchedule(_dayOfWeek = DayOfWeek.FRIDAY)),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(_members = mutableListOf())
      )

      instance.saveTrash(addData)

      val result = stubSharedPreference.getString(PreferenceTrashRepositoryImpl.KEY_TRASH_DATA, "")
      assertEquals(
        """
                        [{"id":"1","type":"burn","trash_val":"もえるゴミ","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23","interval":3}}],"excludes":[]},{"id":"999","type":"resource","trash_val":"資源ごみ","schedules":[{"type":"weekday","value":"5"}],"excludes":[]}]
                """.trimIndent(),
        result
      )
    }

    @Test
    fun append_data_with_type_is_other_when_existed_data_with_type_is_other() {
      stubSharedPreference.edit().apply {
        putString(
          PreferenceTrashRepositoryImpl.KEY_TRASH_DATA,
          """
                        [{"id":"1","type":"other","trash_val":"家電","schedules":[{"type":"weekday","value":"0"}],"excludes":[]}]
                """.trimIndent()
        )
      }

      val addData = Trash(
        _id = "999",
        _type = TrashType.OTHER,
        schedules = listOf(WeeklySchedule(_dayOfWeek = DayOfWeek.FRIDAY)),
        _displayName = "生ゴミ",
        _excludeDayOfMonth = ExcludeDayOfMonthList(_members = mutableListOf())
      )

      instance.saveTrash(addData)

      val result = stubSharedPreference.getString(PreferenceTrashRepositoryImpl.KEY_TRASH_DATA, "")
      assertEquals(
        """
          [
            {"id":"1","type":"other","trash_val":"家電","schedules":[{"type":"weekday","value":"0"}],"excludes":[]},
            {"id":"999","type":"other","trash_val":"生ゴミ","schedules":[{"type":"weekday","value":"5"}],"excludes":[]}
         ] 
        """.trimIndent().replace("\n", "").replace(" ", ""),
        result
      )
    }

    @Test
    fun overwrite_when_existed_data_with_same_id() {
      stubSharedPreference.edit().apply {
        putString(
          PreferenceTrashRepositoryImpl.KEY_TRASH_DATA,
          """
                        [{"id":"1","type":"burn","trash_val":"もえるゴミ","schedules":[{"type":"weekday","value":"0"}],"excludes":[]}]
                """.trimIndent()
        )
      }

      val addData = Trash(
        _id = "1",
        _type = TrashType.RESOURCE,
        schedules = listOf(WeeklySchedule(_dayOfWeek = DayOfWeek.FRIDAY)),
        _displayName = "",
        _excludeDayOfMonth = ExcludeDayOfMonthList(_members = mutableListOf())
      )

      instance.saveTrash(addData)

      val result = stubSharedPreference.getString(PreferenceTrashRepositoryImpl.KEY_TRASH_DATA, "")
      assertEquals(
        """
                        [{"id":"1","type":"resource","trash_val":"資源ごみ","schedules":[{"type":"weekday","value":"5"}],"excludes":[]}]
                """.trimIndent(),
        result
      )
    }
  }

  @Nested
  inner class DeleteTrash {

    @Test
    fun is_empty_when_existed_single_data() {
      stubSharedPreference.edit().apply {
        putString(
          PreferenceTrashRepositoryImpl.KEY_TRASH_DATA,
          """
                        [{"id":"999","schedules":[{"type":"weekday","value":"3"}],"type": "petbottle"}]
                """.trimIndent()
        )
      }

      instance.deleteTrash(
        Trash(
          _id = "999",
          _type = TrashType.PETBOTTLE,
          _displayName = "",
          schedules = listOf(WeeklySchedule(DayOfWeek.WEDNESDAY)),
          _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
        )
      )
      assertEquals(
        "[]",
        stubSharedPreference.getString(PreferenceTrashRepositoryImpl.KEY_TRASH_DATA, "")
      )
    }

    @Test
    fun is_single_data_when_existed_multi_data() {
      stubSharedPreference.edit().apply {
        putString(
          PreferenceTrashRepositoryImpl.KEY_TRASH_DATA,
          """
                        [
                            {"id":"1","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-2-23","interval": 2}}],"type":"burn"},
                            {"id":"999","schedules":[{"type":"weekday","value":"3"}],"type": "petbottle"}
                        ]
                """.trimIndent()
        )
      }
      instance.deleteTrash(
        Trash(
          _id = "999",
          _type = TrashType.PETBOTTLE,
          _displayName = "",
          schedules = listOf(WeeklySchedule(DayOfWeek.WEDNESDAY)),
          _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
        )
      )
      val expect = """
            [{"id":"1","type":"burn","trash_val":"もえるゴミ","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"2","start":"2020-02-23","interval":2}}],"excludes":[]}]
        """.trimIndent()
      assertEquals(
        expect,
        stubSharedPreference.getString(PreferenceTrashRepositoryImpl.KEY_TRASH_DATA, "")
      )
    }
  }

  @Nested
  inner class GetAllTrash {
    @Test
    fun empty_when_no_data() {
      val result = instance.getAllTrash()
      assertEquals(0, result.trashList.size)
    }

    @Test
    fun single_data() {
      stubSharedPreference.edit().apply {
        putString(
          PreferenceTrashRepositoryImpl.KEY_TRASH_DATA,
          """
          [{"id":"999","type":"burn","trash_val":"もえるゴミ","schedules":[{"type":"weekday","value":"3"}],"excludes":[]}]
          """.trimIndent()
        )
      }

      val result = instance.getAllTrash()
      assertEquals(1, result.trashList.size)
      assertEquals("999", result.trashList[0].id)
      assertEquals(TrashType.BURN, result.trashList[0].type)
      assertEquals("もえるゴミ", result.trashList[0].displayName)
      assertEquals(1, result.trashList[0].schedules.size)
      assertEquals(DayOfWeek.WEDNESDAY, (result.trashList[0].schedules[0] as WeeklySchedule).dayOfWeek)
      assertEquals(0, result.trashList[0].excludeDayOfMonth.members.size)
    }

    @Test
    fun multi_data() {
      stubSharedPreference.edit().apply {
        putString(
          PreferenceTrashRepositoryImpl.KEY_TRASH_DATA,
          """
          [
            {"id":"999","type":"burn","trash_val":"もえるゴミ","schedules":[{"type":"weekday","value":"3"}],"excludes":[]},
            {"id":"1","type":"resource","trash_val":"資源ごみ","schedules":[{"type":"weekday","value":"0"}],"excludes":[]}
          ]
          """.trimIndent()
        )
      }

      val result = instance.getAllTrash()
      assertEquals(2, result.trashList.size)
      assertEquals("999", result.trashList[0].id)
      assertEquals(TrashType.BURN, result.trashList[0].type)
      assertEquals("もえるゴミ", result.trashList[0].displayName)
      assertEquals(1, result.trashList[0].schedules.size)
      assertEquals(DayOfWeek.WEDNESDAY, (result.trashList[0].schedules[0] as WeeklySchedule).dayOfWeek)
      assertEquals(0, result.trashList[0].excludeDayOfMonth.members.size)

      assertEquals("1", result.trashList[1].id)
      assertEquals(TrashType.RESOURCE, result.trashList[1].type)
      assertEquals("資源ごみ", result.trashList[1].displayName)
      assertEquals(1, result.trashList[1].schedules.size)
      assertEquals(DayOfWeek.SUNDAY, (result.trashList[1].schedules[0] as WeeklySchedule).dayOfWeek)
      assertEquals(0, result.trashList[1].excludeDayOfMonth.members.size)
    }
  }

  @Nested
  inner class ImportScheduleList {
    @Test
    fun save_empty_data() {
      instance.importScheduleList(TrashList(listOf()))
      val result = stubSharedPreference.getString(PreferenceTrashRepositoryImpl.KEY_TRASH_DATA, "")
      assertEquals("[]", result)
    }

    @Test
    fun save_single_data() {
      instance.importScheduleList(
        TrashList(
          listOf(
            Trash(
              _id = "999",
              _type = TrashType.BURN,
              _displayName = "",
              schedules = listOf(
                WeeklySchedule(_dayOfWeek = DayOfWeek.FRIDAY)
              ),
              _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
            )
          )
        )
      )
      val result = stubSharedPreference.getString(PreferenceTrashRepositoryImpl.KEY_TRASH_DATA, "")
      assertEquals(
        """
          [{"id":"999","type":"burn","trash_val":"もえるゴミ","schedules":[{"type":"weekday","value":"5"}],"excludes":[]}]
        """.trimIndent(),
        result
      )
    }

    @Test
    fun save_multi_data() {
      instance.importScheduleList(
        TrashList(
          listOf(
            Trash(
              _id = "999",
              _type = TrashType.BURN,
              _displayName = "",
              schedules = listOf(
                WeeklySchedule(_dayOfWeek = DayOfWeek.FRIDAY)
              ),
              _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
            ),
            Trash(
              _id = "1",
              _type = TrashType.RESOURCE,
              _displayName = "",
              schedules = listOf(
                WeeklySchedule(_dayOfWeek = DayOfWeek.SUNDAY)
              ),
              _excludeDayOfMonth = ExcludeDayOfMonthList(mutableListOf())
            )
          )
        )
      )
      val result = stubSharedPreference.getString(PreferenceTrashRepositoryImpl.KEY_TRASH_DATA, "")
      assertEquals(
        """
          [
            {"id":"999","type":"burn","trash_val":"もえるゴミ","schedules":[{"type":"weekday","value":"5"}],"excludes":[]},
            {"id":"1","type":"resource","trash_val":"資源ごみ","schedules":[{"type":"weekday","value":"0"}],"excludes":[]}
          ]
        """.trimIndent().replace("\n", "").replace(" ", ""),
        result
      )
    }
  }

  @Nested
  inner class FindTrashById {
    @Test
    fun return_single_data_when_match_id_data_is_one() {
      stubSharedPreference.edit().apply {
        putString(
          PreferenceTrashRepositoryImpl.KEY_TRASH_DATA,
          """
          [{"id":"999","type":"burn","trash_val":"もえるゴミ","schedules":[{"type":"weekday","value":"3"}],"excludes":[]}]
          """.trimIndent()
        )
      }

      val result = instance.findTrashById("999")
      assertEquals("999", result?.id)
      assertEquals(TrashType.BURN, result?.type)
      assertEquals("もえるゴミ", result?.displayName)
      assertEquals(1, result?.schedules?.size)
      assertEquals(DayOfWeek.WEDNESDAY, (result?.schedules?.get(0) as WeeklySchedule).dayOfWeek)
      assertEquals(0, result.excludeDayOfMonth.members.size)
    }

    @Test
    fun return_null_when_no_match_id_data() {
      stubSharedPreference.edit().apply {
        putString(
          PreferenceTrashRepositoryImpl.KEY_TRASH_DATA,
          """
          [
            {"id":"999","type":"burn","trash_val":"もえるゴミ","schedules":[{"type":"weekday","value":"3"}],"excludes":[]},
            {"id":"1","type":"resource","trash_val":"資源ごみ","schedules":[{"type":"weekday","value":"0"}],"excludes":[]}
          ]
          """.trimIndent()
        )
      }
      val result = instance.findTrashById("2")
      assertNull(result)
    }

    @Test
    fun return_null_when_no_data() {
      val result = instance.findTrashById("2")
      assertNull(result)
    }
  }
}
