package net.mythrowaway.app.usecase

import kotlinx.coroutines.runBlocking
import net.mythrowaway.app.usecase.dto.StartAccountLinkResponse
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class AccountLinkUseCaseTest {
    @Mock lateinit var configRepository: VersionRepositoryInterface
    @Mock lateinit var apiAdapter: MobileApiInterface

    @InjectMocks lateinit var instance: AccountLinkUseCase

    @BeforeEach
    fun before() {
        MockitoAnnotations.openMocks(this)
        Mockito.reset(configRepository)
        Mockito.reset(apiAdapter)
    }

    @Test
    fun startAccountLinkWithAlexaApp_Success() {
//        Mockito.`when`(configRepository.getUserId()).thenReturn("dummy")
        Mockito.`when`(apiAdapter.accountLink("dummy")).thenReturn(
            StartAccountLinkResponse(
                url = "dummyUrl",
                token =  "dummyToken"
            )
        )

        runBlocking {
            val url = instance.startAccountLinkWithAlexaApp()
            Assertions.assertEquals("dummyUrl",url)
        }
    }

    @Test
    fun startAccountLinkWithAlexaApp_Failed_UserId_Is_Null() {
//        Mockito.`when`(configRepository.getUserId()).thenReturn(null)
        Mockito.`when`(apiAdapter.accountLink("dummy")).thenReturn(
            StartAccountLinkResponse(
                url = "dummyUrl",
                token = "dummyToken"
            )
        )

        runBlocking {
            instance.startAccountLinkWithAlexaApp()
            Assertions.fail<String>()
        }.runCatching {
            Assertions.assertEquals("UserId is null", this)
        }
    }
    @Test
    fun startAccountLinkWithAlexaApp_Failed_AccountLinkInfo_Is_Null() {
//        Mockito.`when`(configRepository.getUserId()).thenReturn("dummy")
        Mockito.`when`(apiAdapter.accountLink("dummy")).thenReturn(null)

        runBlocking {
            instance.startAccountLinkWithAlexaApp()
            Assertions.fail<String>()
        }.runCatching {
            Assertions.assertEquals("AccountLinkInfo is null", this)
        }
    }


    @Test
    fun startAccountLinkWithLWA_Success() {
//        Mockito.`when`(configRepository.getUserId()).thenReturn("dummy")
        Mockito.`when`(apiAdapter.accountLinkAsWeb("dummy")).thenReturn(
            StartAccountLinkResponse(
                url = "dummyUrl",
                token = "dummyToken"
            )
        )

        runBlocking {
            val url = instance.startAccountLinkWithLWA()
            Assertions.assertEquals("dummyUrl",url)
        }
    }

    @Test
    fun startAccountLinkWithLWA_Failed_UserId_Is_Null() {
//        Mockito.`when`(configRepository.getUserId()).thenReturn(null)
        Mockito.`when`(apiAdapter.accountLinkAsWeb("dummy")).thenReturn(
            StartAccountLinkResponse(
                url = "dummyUrl",
                token = "dummyToken"
            )
        )

        runBlocking {
            instance.startAccountLinkWithLWA()
            Assertions.fail<String>()
        }.runCatching {
            Assertions.assertEquals("UserId is null", this)
        }
    }
    @Test
    fun startAccountLinkWithLWA_Failed_AccountLinkInfo_Is_Null() {
//        Mockito.`when`(configRepository.getUserId()).thenReturn("dummy")
        Mockito.`when`(apiAdapter.accountLinkAsWeb("dummy")).thenReturn(null)

        runBlocking {
            instance.startAccountLinkWithLWA()
            Assertions.fail<String>()
        }.runCatching {
            Assertions.assertEquals("AccountLinkInfo is null", this)
        }
    }

}