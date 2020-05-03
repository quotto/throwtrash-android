package net.mythrowaway.app.usecase

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.domain.TrashSchedule
import net.mythrowaway.app.util.TestApiAdapterImpl
import net.mythrowaway.app.util.TestConfigRepositoryImpl
import net.mythrowaway.app.util.TestPersistImpl
import kotlin.collections.ArrayList
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.*

class CalendarUseCaseTest {
    class TestPresenter: ICalendarPresenter {
        var trashList: Array<ArrayList<String>> = arrayOf()
        var dateList: ArrayList<Int> = arrayListOf()
        val calenderList: ArrayList<Pair<Array<ArrayList<String>>,ArrayList<Int>>> = arrayListOf()
        var backCalendarFlg = false
        override fun setCalendar(
            year:Int,month:Int,
            trashList: Array<ArrayList<String>>,
            dateList: ArrayList<Int>
        ) {
            this.trashList = trashList
            this.dateList = dateList
            this.calenderList.add(Pair(trashList,dateList))
        }
    }

    class TestCalManager: ICalendarManager {
        override fun getYear(): Int {
            return 2020
        }

        override fun getMonth(): Int {
            return 1
        }

        override fun addYM(year: Int, month: Int, addMonth: Int): Pair<Int, Int> {
            return CalendarManager()
                .addYM(year,month,addMonth)
        }

        override fun subYM(year: Int, month: Int, subMonth: Int): Pair<Int, Int> {
            return CalendarManager()
                .subYM(year,month,subMonth)
        }

        override fun compareYM(param1: Pair<Int, Int>, param2: Pair<Int, Int>): Int {
            return CalendarManager()
                .compareYM(param1,param2)
        }

        override fun getTodayStringDate(cal: Calendar): String {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    private val testPresenter =
        TestPresenter()
    private val testPersist = TestPersistImpl()
    private val trashManager =
        TrashManager(testPersist)
    private val testConfig =
        TestConfigRepositoryImpl()
    private val testAdapter = TestApiAdapterImpl()
    private val usecase: CalendarUseCase =
        CalendarUseCase(
            testPresenter,
            trashManager,
            TestCalManager(),
            testPersist,
            testConfig,
            testAdapter
        )

    private val trash1 = TrashData().apply {
        type = "burn"
        schedules = arrayListOf(TrashSchedule().apply{
            type = "weekday"
            value = "1"
        }, TrashSchedule().apply{
            type = "weekday"
            value = "2"
        })
    }
    private val trash2 = TrashData().apply {
        type = "bin"
        schedules = arrayListOf(TrashSchedule().apply{
            type = "weekday"
            value = "1"
        })
    }

    private val adapterData1 = TrashData().apply {
        this.id = "123456"
        this.type = "burn"
        this.schedules = arrayListOf(
            TrashSchedule().apply{
            this.type = "biweek"
            this.value = "0-3"
        }, TrashSchedule().apply{
            this.type = "biweek"
            this.value = "6-1"
        })
    }
    private val adapterData2 = TrashData().apply {
        this.id = "5678"
        this.type = "other"
        this.trash_val = "家電"
        this.schedules = arrayListOf(
            TrashSchedule().apply{
            this.type = "biweek"
            this.value = "0-3"
        })
    }

    private val mapper = ObjectMapper()
    init {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }

    @Before
    fun cleanTestData() {
        testPersist.injectTestData(arrayListOf(trash1, trash2))
        trashManager.refresh()
        testPresenter.calenderList.clear()
        testPresenter.backCalendarFlg = false
        testConfig.setSyncState(CalendarUseCase.SYNC_NO)
        testConfig.setTimestamp(0)
        testConfig.setUserId("")

        testAdapter.currentUpdatedData = arrayListOf(adapterData1,adapterData2)
        testAdapter.currentUpdatedTimestamp = 0
    }

    @Test
    fun generateMonthSchedule() {
        // 1ヶ月（35日分）のゴミ出し予定リストを取得する
        usecase.generateMonthSchedule(2020,1)

        Assert.assertEquals(2,testPresenter.trashList[8].size)
        Assert.assertEquals("もえるゴミ",testPresenter.trashList[8][0])
        Assert.assertEquals("ビン",testPresenter.trashList[8][1])
        Assert.assertEquals(1,testPresenter.trashList[9].size)
        Assert.assertEquals(0,testPresenter.trashList[10].size)

        val expect: List<Int> = listOf(29,30,31,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,1)
        for (i in testPresenter.dateList.indices) {
            assert(testPresenter.dateList[i] == expect[i])
        }
    }

    @Test
    fun syncData_Register() {
        testConfig.setSyncState(CalendarUseCase.SYNC_WAITING)
        testAdapter.currentUpdatedTimestamp = 12345678
        usecase.syncData()

        // configにuserIdが未登録の場合は新規にIDが発行される
        Assert.assertEquals(TestApiAdapterImpl.REGISTER_ID_999,testConfig.getUserId())
        Assert.assertEquals(TestApiAdapterImpl.REGISTER_TIMESTAMP,testConfig.getTimeStamp())
        Assert.assertEquals(CalendarUseCase.SYNC_COMPLETE, testConfig.getSyncState())
    }

    @Test
    fun syncData_Init() {
        usecase.syncData()

        // 初回起動時は何もしない
        Assert.assertEquals("",testConfig.getUserId())
        Assert.assertEquals(0,testConfig.getTimeStamp())
        Assert.assertEquals(CalendarUseCase.SYNC_NO, testConfig.getSyncState())
    }

    @Test
    fun syncData_SyncFromDB() {
        testConfig.setSyncState(CalendarUseCase.SYNC_WAITING)
        testConfig.setTimestamp(123)
        testConfig.setUserId(TestApiAdapterImpl.SYNC_ID_001)
        testAdapter.currentUpdatedTimestamp = 12345678

        usecase.syncData()
        Assert.assertEquals(TestApiAdapterImpl.SYNC_TIMESTAMP_001,testConfig.getTimeStamp())
        val actualData:ArrayList<TrashData> = testPersist.getAllTrashSchedule()
        Assert.assertEquals(
            mapper.writeValueAsString(TestApiAdapterImpl.SYNC_DATA_001),
            mapper.writeValueAsString(actualData)
        )
        Assert.assertEquals(CalendarUseCase.SYNC_COMPLETE, testConfig.getSyncState())
    }

    @Test
    fun syncData_UpdateToDB() {
        val quiteLargeTimestamp:Long = 9999999999999
        testConfig.setSyncState(CalendarUseCase.SYNC_WAITING)
        testConfig.setTimestamp(quiteLargeTimestamp)
        testConfig.setUserId(TestApiAdapterImpl.UPDATE_ID_001)
        testAdapter.currentUpdatedTimestamp = 0

        usecase.syncData()
        // 実際にはリモートのタイムスタンプ>ローカルとなるがTestConfigRepositoryImplは登録済みの値をそのまま返す
        Assert.assertEquals(TestApiAdapterImpl.UPDATE_TIMESTAMP_001,testConfig.getTimeStamp())
        Assert.assertEquals(CalendarUseCase.SYNC_COMPLETE, testConfig.getSyncState())
        Assert.assertEquals(
            mapper.writeValueAsString(testPersist.getAllTrashSchedule()),
            mapper.writeValueAsString(testAdapter.currentUpdatedData)
        )
    }

    @Test
    fun syncData_Update_LocalSchedule_is_0() {
        val quiteLargeTimestamp:Long = 9999999999999
        testConfig.setSyncState(CalendarUseCase.SYNC_WAITING)
        testConfig.setTimestamp(quiteLargeTimestamp)
        testConfig.setUserId(TestApiAdapterImpl.UPDATE_ID_001)
        // ローカルのデータ件数を0にする
        testPersist.injectTestData(arrayListOf())
        // データが更新されないことを確認するために事前に設定しておく
        testAdapter.currentUpdatedData = TestApiAdapterImpl.SYNC_DATA_001
        testAdapter.currentUpdatedTimestamp = 0

        usecase.syncData()
        // タイムスタンプは更新されない
        Assert.assertEquals(quiteLargeTimestamp,testConfig.getTimeStamp())
        // Updateが呼ばれていないこと
        Assert.assertEquals(
            mapper.writeValueAsString(TestApiAdapterImpl.SYNC_DATA_001),
            mapper.writeValueAsString(testAdapter.currentUpdatedData)
        )
        // 同期状態は完了にする
        Assert.assertEquals(CalendarUseCase.SYNC_COMPLETE, testConfig.getSyncState())

    }
}