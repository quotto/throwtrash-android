package net.mythrowaway.app.usecase

import net.mythrowaway.app.domain.AlarmConfig
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.domain.TrashSchedule
import net.mythrowaway.app.stub.TestConfigRepositoryImpl
import net.mythrowaway.app.stub.TestPersistImpl
import org.junit.Assert.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class TestPresenter: IAlarmPresenter {
    var receivedTrashList:ArrayList<TrashData> = arrayListOf()
    var called = false
    override fun notifyAlarm(trashArray: ArrayList<TrashData>) {
        receivedTrashList = trashArray
        called = true
    }

    override fun loadAlarmConfig(alarmConfig: AlarmConfig) {
    }
}

internal class AlarmUseCaseTest {


    companion object {
        private val config = TestConfigRepositoryImpl()
        private val presenter = TestPresenter()
        private val persist = TestPersistImpl()
        private val trashManager = TrashManager(persist)
        private val instance:AlarmUseCase = AlarmUseCase(
            config = config,
            presenter = presenter,
            trashManager = trashManager)

        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            val trash1 = TrashData().apply {
                type = "burn"
                schedules = arrayListOf(TrashSchedule().apply {
                    type = "weekday"
                    value = "1"
                }, TrashSchedule().apply {
                    type = "weekday"
                    value = "2"
                })
            }
            val trash2 = TrashData().apply {
                type = "other"
                trash_val = "生ゴミ"
                schedules = arrayListOf(TrashSchedule().apply {
                    type = "weekday"
                    value = "2"
                })
            }
            persist.injectTestData(arrayListOf(trash1, trash2))
            trashManager.refresh()
        }
    }


    @Before
    fun before(){
        presenter.receivedTrashList.clear()
        presenter.called = false
    }

    @Test
    fun alarmToday_disabled() {
        val alarmConfig = AlarmConfig().apply {
            enabled = false
        }
        config.saveAlarmConfig(alarmConfig)
        instance.alarmToday(2020,5,4)   // 月曜日

        // presenterは呼び出しされない
        assertEquals(0,presenter.receivedTrashList.size)
        assertFalse(presenter.called)
    }

    @Test
    fun alarmToday_enabled_single() {
        val alarmConfig = AlarmConfig().apply {
            enabled = true
            notifyEveryday = true
        }
        config.saveAlarmConfig(alarmConfig)
        instance.alarmToday(2020,5,4)   // 月曜日

        assertEquals(1,presenter.receivedTrashList.size)
        assertTrue(presenter.called)
    }

    @Test
    fun alarmToday_enabled_multiple() {
        val alarmConfig = AlarmConfig().apply {
            enabled = true
            notifyEveryday = true
        }
        config.saveAlarmConfig(alarmConfig)
        instance.alarmToday(2020,5,5)   // 火曜日

        assertEquals(2,presenter.receivedTrashList.size)
        assertTrue(presenter.called)
    }

    @Test
    fun alarmToday_enabled_everyday_zero() {
        val alarmConfig = AlarmConfig().apply {
            enabled = true
            notifyEveryday = true
        }
        config.saveAlarmConfig(alarmConfig)
        instance.alarmToday(2020,5,6)   // 火曜日

        assertEquals(0,presenter.receivedTrashList.size)
        // ゴミが無くても呼ばれる
        assertTrue(presenter.called)
    }

    @Test
    fun alarmToday_enabled_not_everyday_zero() {
        val alarmConfig = AlarmConfig().apply {
            enabled = true
            notifyEveryday = false
        }
        config.saveAlarmConfig(alarmConfig)
        instance.alarmToday(2020,5,6)   // 火曜日

        assertEquals(0,presenter.receivedTrashList.size)
        // ゴミが無ければ呼ばれない
        assertFalse(presenter.called)
    }
}