package net.mythrowaway.app.presenter

import com.nhaarman.mockito_kotlin.capture
import com.nhaarman.mockito_kotlin.mock
import net.mythrowaway.app.adapter.IAlarmView
import net.mythrowaway.app.adapter.presenter.AlarmPresenterImpl
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.service.TrashManager
import net.mythrowaway.app.usecase.IPersistentRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(
    TrashManager::class
)
class AlarmPresenterImplTest {
    @Mock
    private lateinit var mockAlarmView: IAlarmView

    private val mockPersist: IPersistentRepository = mock()
    private val mockTrashManager: TrashManager = PowerMockito.spy(TrashManager(mockPersist))

    @InjectMocks
    private lateinit var instance: AlarmPresenterImpl

    @Captor
    private lateinit var captorTrashList: ArgumentCaptor<List<String>>

    @Before
    fun before(){
        Mockito.clearInvocations(mockAlarmView)
        instance.setView(mockAlarmView)
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


        Assert.assertEquals(2, captorTrashList.value.size)
        Assert.assertEquals("もえるゴミ", captorTrashList.value[0])
        Assert.assertEquals("生ゴミ", captorTrashList.value[1])
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


        Assert.assertEquals(3, captorTrashList.value.size)
        Assert.assertEquals("もえないゴミ", captorTrashList.value[0])
        Assert.assertEquals("生ゴミ",  captorTrashList.value[1])
        Assert.assertEquals("資源ごみ", captorTrashList.value[2])
    }
}