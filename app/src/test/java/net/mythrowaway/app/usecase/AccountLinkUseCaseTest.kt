package net.mythrowaway.app.usecase

import com.nhaarman.mockito_kotlin.any
import kotlinx.coroutines.runBlocking
import net.mythrowaway.app.domain.AccountLinkInfo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(
    IAccountLinkPresenter::class,
    IConfigRepository::class,
    IAPIAdapter::class,
)
class AccountLinkUseCaseTest {
    @Mock
    lateinit var configRepository: IConfigRepository
    @Mock
    lateinit var presenter: IAccountLinkPresenter
    @Mock
    lateinit var apiAdapter: IAPIAdapter

    @InjectMocks
    lateinit var instance: AccountLinkUseCase

    @Before
    fun before() {
        Mockito.clearInvocations(configRepository)
        Mockito.clearInvocations(presenter)
        Mockito.clearInvocations(apiAdapter)
    }

    @Test
    fun startAccountLinkWithAlexaApp_Success() {
        Mockito.`when`(configRepository.getUserId()).thenReturn("dummy")
        Mockito.`when`(apiAdapter.accountLink("dummy")).thenReturn(AccountLinkInfo())

        runBlocking {
            instance.startAccountLinkWithAlexaApp()
            Mockito.verify(presenter,Mockito.times(1)).startAccountLink(accountLinkInfo = any())
        }
    }

    @Test
    fun startAccountLinkWithAlexaApp_Failed_UserId_Is_Null() {
        Mockito.`when`(configRepository.getUserId()).thenReturn(null)
        Mockito.`when`(apiAdapter.accountLink("dummy")).thenReturn(AccountLinkInfo())

        runBlocking {
            instance.startAccountLinkWithAlexaApp()
            Mockito.verify(presenter,Mockito.times(1)).handleError()
        }
    }
    @Test
    fun startAccountLinkWithAlexaApp_Failed_AccountLinkInfo_Is_Null() {
        Mockito.`when`(configRepository.getUserId()).thenReturn("dummy")
        Mockito.`when`(apiAdapter.accountLink("dummy")).thenReturn(null)

        runBlocking {
            instance.startAccountLinkWithAlexaApp()
            Mockito.verify(presenter,Mockito.times(1)).handleError()
        }
    }


    @Test
    fun startAccountLinkWithLWA_Success() {
        Mockito.`when`(configRepository.getUserId()).thenReturn("dummy")
        Mockito.`when`(apiAdapter.accountLinkAsWeb("dummy")).thenReturn(AccountLinkInfo())

        runBlocking {
            instance.startAccountLinkWithLWA()
            Mockito.verify(presenter,Mockito.times(1)).startAccountLink(accountLinkInfo = any())
        }
    }

    @Test
    fun startAccountLinkWithLWA_Failed_UserId_Is_Null() {
        Mockito.`when`(configRepository.getUserId()).thenReturn(null)
        Mockito.`when`(apiAdapter.accountLinkAsWeb("dummy")).thenReturn(AccountLinkInfo())

        runBlocking {
            instance.startAccountLinkWithLWA()
            Mockito.verify(presenter,Mockito.times(1)).handleError()
        }
    }
    @Test
    fun startAccountLinkWithLWA_Failed_AccountLinkInfo_Is_Null() {
        Mockito.`when`(configRepository.getUserId()).thenReturn("dummy")
        Mockito.`when`(apiAdapter.accountLinkAsWeb("dummy")).thenReturn(null)

        runBlocking {
            instance.startAccountLinkWithLWA()
            Mockito.verify(presenter,Mockito.times(1)).handleError()
        }
    }

}