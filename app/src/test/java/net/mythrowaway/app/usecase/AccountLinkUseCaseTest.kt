package net.mythrowaway.app.usecase

import com.nhaarman.mockito_kotlin.any
import kotlinx.coroutines.runBlocking
import net.mythrowaway.app.domain.AccountLinkInfo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class AccountLinkUseCaseTest {
    @Mock lateinit var configRepository: ConfigRepositoryInterface
    @Mock lateinit var presenter: AccountLinkPresenterInterface
    @Mock lateinit var apiAdapter: MobileApiInterface

    @InjectMocks lateinit var instance: AccountLinkUseCase

    @BeforeEach
    fun before() {
        MockitoAnnotations.openMocks(this)
        Mockito.reset(configRepository)
        Mockito.reset(presenter)
        Mockito.reset(apiAdapter)
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