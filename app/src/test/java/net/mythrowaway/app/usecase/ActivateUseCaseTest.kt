package net.mythrowaway.app.usecase

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import net.mythrowaway.app.adapter.TrashDataConverter
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.util.TestApiAdapterImpl
import net.mythrowaway.app.util.TestConfigRepositoryImpl
import net.mythrowaway.app.util.TestPersistImpl
import org.junit.Test
import org.junit.Assert.*
import java.lang.reflect.Field

internal class ActivateUseCaseTest {
    inner class TestPresenter: IActivatePresenter {
        lateinit  var currentResultCode: ActivateUseCase.ActivationResult
        override fun notify(resultCode: ActivateUseCase.ActivationResult) {
            currentResultCode = resultCode
        }
    }

    private val presenter = TestPresenter()
    private val config = TestConfigRepositoryImpl()
    private val persist = TestPersistImpl()
    private val trashManager = TrashManager(persist)
    private val instance = ActivateUseCase(
        trashManager = trashManager,
        presenter = presenter,
        adapter = TestApiAdapterImpl(),
        config = config,
        persist = persist
    )

    @Test
    fun activate_success() {
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)

        instance.activate(TestApiAdapterImpl.ACTIVATE_CODE)
        assertEquals(ActivateUseCase.ActivationResult.ACTIVATE_SUCCESS,presenter.currentResultCode)
        assertEquals(TestApiAdapterImpl.ACTIVATE_ID_001,config.getUserId())
        assertEquals(TestApiAdapterImpl.ACTIVATE_TIMESTAMP_001,config.getTimeStamp())
        assertEquals(
            mapper.writeValueAsString(TestApiAdapterImpl.ACTIVATE_DATA_001),
            mapper.writeValueAsString(persist.getAllTrashSchedule())
        )
        val field:Field = trashManager.javaClass.getDeclaredField("mSchedule")
        field.isAccessible = true
        assertEquals(
            mapper.writeValueAsString(TestApiAdapterImpl.ACTIVATE_DATA_001),
            mapper.writeValueAsString((field.get(trashManager) as ArrayList<TrashData>))
        )

    }

    @Test
    fun activate_failed() {
        instance.activate("failed_code")
        assertEquals(ActivateUseCase.ActivationResult.ACTIVATE_ERROR, presenter.currentResultCode)
    }

    @Test
    fun checkCode_valid() {
        instance.checkCode("1234567890")
        assertEquals(ActivateUseCase.ActivationResult.VALID_CODE, presenter.currentResultCode)
    }

    @Test
    fun checkCode_invalid() {
        instance.checkCode("123456789")
        assertEquals(ActivateUseCase.ActivationResult.INVALID_CODE, presenter.currentResultCode)

        instance.checkCode("")
        assertEquals(ActivateUseCase.ActivationResult.INVALID_CODE, presenter.currentResultCode)
    }

}