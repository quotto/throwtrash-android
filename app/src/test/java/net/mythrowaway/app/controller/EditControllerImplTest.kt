package net.mythrowaway.app.controller

import com.nhaarman.mockito_kotlin.capture
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import net.mythrowaway.app.adapter.DIContainer
import net.mythrowaway.app.adapter.PreferenceConfigImpl
import net.mythrowaway.app.adapter.PreferencePersistImpl
import net.mythrowaway.app.adapter.controller.EditControllerImpl
import net.mythrowaway.app.adapter.presenter.EditItem
import net.mythrowaway.app.adapter.presenter.EditPresenterImpl
import net.mythrowaway.app.adapter.presenter.EditScheduleItem
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.usecase.*
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(
    EditControllerImpl::class,
    EditUseCase::class,
    EditPresenterImpl::class,
    PreferencePersistImpl::class,
    PreferenceConfigImpl::class,
    TrashManager::class
)
class EditControllerImplTest {
    private val captorTrashData: ArgumentCaptor<TrashData> = ArgumentCaptor.forClass(TrashData::class.java)
    private val instance = EditControllerImpl(presenterMock)

    companion object {
        private val usecaseMock = PowerMockito.mock(EditUseCase::class.java)
        private val presenterMock = PowerMockito.mock(EditPresenterImpl::class.java)

        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            PowerMockito.`whenNew`(EditUseCase::class.java).withAnyArguments()
                .thenReturn(usecaseMock)
            DIContainer.register(IPersistentRepository::class.java,PowerMockito.mock(PreferencePersistImpl::class.java))
            DIContainer.register(IConfigRepository::class.java,PowerMockito.mock(PreferenceConfigImpl::class.java))
            DIContainer.register(TrashManager::class.java,PowerMockito.mock(TrashManager::class.java))
        }
    }

    @Test
    fun saveTrashData_evweek() {
        val item = EditItem()
        item.type = "burn"
        item.id = "0001"
        item.scheduleItem = arrayListOf(
            EditScheduleItem().apply {
                type = "evweek"
                evweekWeekdayValue = "0"
                evweekStartValue = "2020/10/04"
                evweekIntervalValue = 2
            },
            EditScheduleItem().apply {
                type = "evweek"
                evweekWeekdayValue = "6"
                evweekStartValue = "2020/10/10"
                evweekIntervalValue = 4
            }
        )

        instance.saveTrashData(item)
        verify(usecaseMock, times(1)).updateTrashData(capture(captorTrashData))

        val actualTrashData = captorTrashData.value
        Assert.assertEquals("2020-10-4",(actualTrashData.schedules[0].value as HashMap<String,Any>)["start"])
        Assert.assertEquals("2020-10-4",(actualTrashData.schedules[1].value as HashMap<String,Any>)["start"])
   }

    @Test
    fun saveTrashData_ExcludeDate() {
        val item = EditItem()
        item.type = "burn"
        item.id = "0001"
        item.scheduleItem = arrayListOf(
            EditScheduleItem().apply {
                type = "evweek"
                evweekWeekdayValue = "0"
                evweekStartValue = "2020/10/04"
                evweekIntervalValue = 2
            },
            EditScheduleItem().apply {
                type = "evweek"
                evweekWeekdayValue = "6"
                evweekStartValue = "2020/10/10"
                evweekIntervalValue = 4
            }
        )
        item.excludes = arrayListOf(
            Pair(3,4),
            Pair(12,30)
        )

        instance.saveTrashData(item)
        Mockito.verify(usecaseMock,Mockito.times(1)).updateTrashData(capture(captorTrashData))
        Assert.assertEquals(3,captorTrashData.value.excludes[0].month)
        Assert.assertEquals(4,captorTrashData.value.excludes[0].date)
        Assert.assertEquals(12,captorTrashData.value.excludes[1].month)
        Assert.assertEquals(30,captorTrashData.value.excludes[1].date)
    }

    @Test
    fun saveTrashData_ExcludeDate_Empty() {
        val item = EditItem()
        item.type = "burn"
        item.id = "0001"
        item.scheduleItem = arrayListOf(
            EditScheduleItem().apply {
                type = "evweek"
                evweekWeekdayValue = "0"
                evweekStartValue = "2020/10/04"
                evweekIntervalValue = 2
            },
            EditScheduleItem().apply {
                type = "evweek"
                evweekWeekdayValue = "6"
                evweekStartValue = "2020/10/10"
                evweekIntervalValue = 4
            }
        )
        item.excludes = arrayListOf()

        instance.saveTrashData(item)
        Mockito.verify(usecaseMock,Mockito.times(1)).updateTrashData(capture(captorTrashData))
        Assert.assertEquals(0,captorTrashData.value.excludes.size)
    }
}