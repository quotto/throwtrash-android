package net.mythrowaway.app.presenter

import com.nhaarman.mockito_kotlin.capture
import com.nhaarman.mockito_kotlin.mock
import net.mythrowaway.app.adapter.AlarmViewInterface
import net.mythrowaway.app.adapter.presenter.AlarmPresenterImpl
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.service.TrashManager
import net.mythrowaway.app.usecase.DataRepositoryInterface
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.*

class AlarmPresenterImplTest {
    @Mock
    private lateinit var mockAlarmView: AlarmViewInterface
    private val mockPersist: DataRepositoryInterface = mock()
    @Suppress("unused")
    private val mockTrashManager = Mockito.spy(TrashManager(mockPersist))

    @InjectMocks
    private lateinit var instance: AlarmPresenterImpl

    @Captor
    private lateinit var captorTrashList: ArgumentCaptor<List<String>>

    @BeforeEach
    fun before(){
        MockitoAnnotations.openMocks(this)
        instance.setView(mockAlarmView)
        Mockito.reset(mockAlarmView)
    }

    @Test
    fun notifyAlarm_UniqueData() {
        val trash1 = TrashData().apply {
            type = "burn"
        }
        val trash2 = TrashData().apply {
            type = "other"
            trash_val = "生ゴミ"
        }

        instance.notifyAlarm(arrayListOf(trash1,trash2))

        Mockito.verify(mockAlarmView,Mockito.times(1)).notify(capture(captorTrashList))


        assertEquals(2, captorTrashList.value.size)
        assertEquals("もえるゴミ", captorTrashList.value[0])
        assertEquals("生ゴミ", captorTrashList.value[1])
    }

    @Test
    fun notifyAlarm_Duplicate() {
        val trash1 = TrashData().apply {
            type = "unburn"
        }
        val trash2 = TrashData().apply {
            type = "other"
            trash_val = "生ゴミ"
        }
        val trash3 = TrashData().apply {
            type = "other"
            trash_val = "生ゴミ"
        }
        val trash4 = TrashData().apply {
            type = "resource"
        }

        val trash5 = TrashData().apply {
            type = "unburn"
        }

        instance.notifyAlarm(arrayListOf(trash1,trash2,trash3,trash4,trash5))

        Mockito.verify(mockAlarmView,Mockito.times(1)).notify(capture(captorTrashList))


        assertEquals(3, captorTrashList.value.size)
        assertEquals("もえないゴミ", captorTrashList.value[0])
        assertEquals("生ゴミ",  captorTrashList.value[1])
        assertEquals("資源ごみ", captorTrashList.value[2])
    }
}