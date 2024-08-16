package net.mythrowaway.app.usecase

import com.nhaarman.mockito_kotlin.capture
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.domain.TrashSchedule
import net.mythrowaway.app.domain.TrashType
import net.mythrowaway.app.service.TrashManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.*

class ScheduleListUseCaseTest {
    @Mock private lateinit var mockPresenter: ScheduleListPresenterInterface
    @Mock private lateinit var mockPersist: DataRepositoryInterface
    @Mock private lateinit var mockConfig: ConfigRepositoryInterface
    @Mock private lateinit var mockTrashManager: TrashManager
    @InjectMocks private lateinit var target: ScheduleListUseCase//(

    @Captor private lateinit var captorTrashList: ArgumentCaptor<ArrayList<TrashData>>
    @Captor private lateinit var captorId: ArgumentCaptor<String>
    @Captor private lateinit var captorSyncState: ArgumentCaptor<Int>

    @BeforeEach
    fun before() {
        MockitoAnnotations.openMocks(this)
        Mockito.reset(mockPresenter)
        Mockito.reset(mockConfig)
        Mockito.reset(mockPersist)
        Mockito.reset(mockTrashManager)
    }

    @Test
    fun showScheduleList_usually_process() {
        // 登録データを表示するケース,正常にデータが取得できた場合はPresenterにそのまま渡す
        val trash1 = TrashData().apply {
            id = "1"
            type = TrashType.BURN
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
            type = TrashType.OTHER
            trash_val = "家電"
            schedules = arrayListOf(
                TrashSchedule().apply{
                type = "biweek"
                value = "0-3"
            })
        }

        Mockito.`when`(mockPersist.getAllTrash()).thenReturn(arrayListOf(trash1,trash2))
        target.showScheduleList()

        Mockito.verify(mockPresenter,Mockito.times(1)).showScheduleList(capture(captorTrashList))

        // PresenterにはTrashDataが全数渡されている
        assertEquals(2,captorTrashList.value.size)
    }

    @Test
    fun deleteSchedule() {
        // 登録データを削除するケース,パラメータなしのpresenter.showScheduleListを呼びだす
        val trash1 = TrashData().apply {
            id = "0"
            type = TrashType.BURN
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
            type = TrashType.OTHER
            trash_val = "家電"
            schedules = arrayListOf(
                TrashSchedule().apply{
                type = "biweek"
                value = "0-3"
            })
        }

        Mockito.`when`(mockPersist.getAllTrash()).thenReturn(arrayListOf(trash1,trash2))
        target.deleteList("0")

        // IPersistentRepositoryのdeleteTrashDataのパラメータにIDが指定されている
        Mockito.verify(mockPersist,Mockito.times(1)).deleteTrashData(capture(captorId))
        assertEquals("0",captorId.value)

        // Configの同期状態はSYNC_WAITINGに設定される
        Mockito.verify(mockConfig,Mockito.times(1)).setSyncState(capture(captorSyncState))
        assertEquals(CalendarUseCase.SYNC_WAITING,captorSyncState.value)

        // showScheduleList()経由でpresenter.showScheduleListが実行される
        Mockito.verify(mockPresenter,Mockito.times(1)).showScheduleList(capture(captorTrashList))

    }
}