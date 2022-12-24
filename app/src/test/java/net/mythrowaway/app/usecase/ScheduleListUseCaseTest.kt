package net.mythrowaway.app.usecase

import com.nhaarman.mockito_kotlin.capture
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.domain.TrashSchedule
import net.mythrowaway.app.service.TrashManager
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(
    ScheduleListPresenterInterface::class,
    DataRepositoryInterface::class,
    ConfigRepositoryInterface::class
)
class ScheduleListUseCaseTest {
    private val mockPresenter = PowerMockito.mock(ScheduleListPresenterInterface::class.java)
    private val mockPersist = PowerMockito.mock(DataRepositoryInterface::class.java)
    private val mockConfig = PowerMockito.mock(ConfigRepositoryInterface::class.java)
    private val mockTrashManager = TrashManager(mockPersist)
    private val target = ScheduleListUseCase(
        mockTrashManager,
        mockPersist,
        mockConfig,
        mockPresenter
    )

    @Captor
    private lateinit var captorTrashList: ArgumentCaptor<ArrayList<TrashData>>
    @Captor
    private lateinit var captorId: ArgumentCaptor<String>
    @Captor
    private lateinit var captorSyncState: ArgumentCaptor<Int>

    @Before
    fun before() {
        Mockito.reset(mockPresenter)
        Mockito.reset(mockConfig)
        Mockito.reset(mockPersist)
    }

    @Test
    fun showScheduleList_usually_process() {
        // 登録データを表示するケース,正常にデータが取得できた場合はPresenterにそのまま渡す
        val trash1 = TrashData().apply {
            id = "1"
            type = "burn"
            schedules = arrayListOf(
                TrashSchedule().apply{
                type = "biweek"
                value = "0-3"
            },  TrashSchedule().apply{
                type = "biweek"
                value = "6-1"
            })
        }
        val trash2 = TrashData().apply {
            id = "2"
            type = "other"
            trash_val = "家電"
            schedules = arrayListOf(
                TrashSchedule().apply{
                type = "biweek"
                value = "0-3"
            })
        }

        Mockito.`when`(mockPersist.getAllTrashSchedule()).thenReturn(arrayListOf(trash1,trash2))
        target.showScheduleList()

        Mockito.verify(mockPresenter,Mockito.times(1)).showScheduleList(capture(captorTrashList))

        // PresenterにはTrashDataが全数渡されている
        Assert.assertEquals(2,captorTrashList.value.size)
    }

    @Test
    fun deleteSchedule() {
        // 登録データを削除するケース,パラメータなしのpresenter.showScheduleListを呼びだす
        val trash1 = TrashData().apply {
            id = "0"
            type = "burn"
            schedules = arrayListOf(
                TrashSchedule().apply{
                type = "biweek"
                value = "0-3"
            }, TrashSchedule().apply{
                type = "biweek"
                value = "6-1"
            })
        }
        val trash2 = TrashData().apply {
            id = "1"
            type = "other"
            trash_val = "家電"
            schedules = arrayListOf(
                TrashSchedule().apply{
                type = "biweek"
                value = "0-3"
            })
        }

        Mockito.`when`(mockPersist.getAllTrashSchedule()).thenReturn(arrayListOf(trash1,trash2))
        target.deleteList("0")

        // IPersistentRepositoryのdeleteTrashDataのパラメータにIDが指定されている
        Mockito.verify(mockPersist,Mockito.times(1)).deleteTrashData(capture(captorId))
        Assert.assertEquals("0",captorId.value)

        // Configの同期状態はSYNC_WAITINGに設定される
        Mockito.verify(mockConfig,Mockito.times(1)).setSyncState(capture(captorSyncState))
        Assert.assertEquals(CalendarUseCase.SYNC_WAITING,captorSyncState.value)

        // showScheduleList()経由でpresenter.showScheduleListが実行される
        Mockito.verify(mockPresenter,Mockito.times(1)).showScheduleList(capture(captorTrashList))

    }
}