package net.mythrowaway.app.usecase

import net.mythrowaway.app.domain.migration.usecase.VersionRepositoryInterface
import org.junit.jupiter.api.BeforeEach
import org.mockito.*

class AlarmUseCaseTest {
    @Mock
    private lateinit var mockConfig: VersionRepositoryInterface
    @Mock
    private lateinit var mockPersist: TrashRepositoryInterface
    @BeforeEach
    fun before(){
        MockitoAnnotations.openMocks(this)
        Mockito.reset(mockConfig)
        Mockito.reset(mockPersist)
    }
//
//    @Test
//    fun alarmToday_disabled() {
//        // ConfigでAlarmがfalseの場合はPresenterのメソッドは呼び出されない
//        Mockito.`when`(mockConfig.getAlarmConfig()).thenReturn(AlarmConfig().apply{this.enabled=false})
//        target.alarmToday(2020,5,4)   // 月曜日
//
//        Mockito.verify(mockPresenter,Mockito.times(0)).notifyAlarm(capture(captorTrashList))
//    }
//
//    @Test
//    fun alarmToday_enabled_single() {
//        // AlarmConfigでenabled=true,notifyEveryday=true,登録されているゴミが1件の場合は通知される
//        Mockito.`when`(mockConfig.getAlarmConfig()).thenReturn(AlarmConfig().apply{
//            this.enabled=true
//            this.notifyEveryday=true
//        })
//        Mockito.`when`(mockTrashManager.getTodaysTrash(2020,5,4)).thenReturn(
//            arrayListOf(trash1)
//        )
//        target.alarmToday(2020,5,4)   // 月曜日
//
//        Mockito.verify(mockPresenter,Mockito.times(1)).notifyAlarm(capture(captorTrashList))
//
//        // 1件のデータが引数に指定される
//        assertEquals(1,captorTrashList.value.size)
//    }
//
//    @Test
//    fun alarmToday_enabled_everyday_zero() {
//        // AlarmConfigでenabled=true,notifyEveryday=true,登録データが0件の場合は通知される
//        Mockito.`when`(mockConfig.getAlarmConfig()).thenReturn(AlarmConfig().apply{
//            this.enabled=true
//            this.notifyEveryday=true
//        })
//        Mockito.`when`(mockTrashManager.getTodaysTrash(2020,5,6)).thenReturn(
//            arrayListOf()
//        )
//        target.alarmToday(2020,5,6)
//
//        Mockito.verify(mockPresenter,Mockito.times(1)).notifyAlarm(capture(captorTrashList))
//
//        // 0件のデータが引数に指定される
//        assertEquals(0,captorTrashList.value.size)
//    }
//
//    @Test
//    fun alarmToday_enabled_not_everyday_one_data() {
//        // AlarmConfigでenabled=true,notifyEveryday=true,登録データが1件の場合は通知される
//        Mockito.`when`(mockConfig.getAlarmConfig()).thenReturn(AlarmConfig().apply{
//            this.enabled=true
//            this.notifyEveryday=false
//        })
//        Mockito.`when`(mockTrashManager.getTodaysTrash(2020,5,4)).thenReturn(
//            arrayListOf(trash1)
//        )
//        target.alarmToday(2020,5,4)
//
//        Mockito.verify(mockPresenter,Mockito.times(1)).notifyAlarm(capture(captorTrashList))
//
//        // 1件のデータが引数に指定される
//        assertEquals(1,captorTrashList.value.size)
//    }
//
//
//    @Test
//    fun alarmToday_enabled_not_everyday_zero() {
//        // AlarmConfigでenabled=true,notifyEveryday=false,登録データが0件の場合は通知されない
//        Mockito.`when`(mockConfig.getAlarmConfig()).thenReturn(AlarmConfig().apply{
//            this.enabled=true
//            this.notifyEveryday=false
//        })
//        Mockito.`when`(mockTrashManager.getTodaysTrash(2020,5,6)).thenReturn(
//            arrayListOf()
//        )
//        target.alarmToday(2020,5,6)   // 月曜日
//
//        // ゴミ出し予定が無い場合でも通知する=falseのためpresenterの処理は呼ばれない
//        Mockito.verify(mockPresenter,Mockito.times(0)).notifyAlarm(capture(captorTrashList))
//    }
}