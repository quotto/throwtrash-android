package net.mythrowaway.app.usecase

import androidx.preference.PreferenceManager
import androidx.test.platform.app.InstrumentationRegistry
import net.mythrowaway.app.domain.account_link.usecase.AccountLinkUseCase
import net.mythrowaway.app.domain.account_link.dto.StartAccountLinkResponse
import net.mythrowaway.app.domain.account_link.entity.FinishAccountLinkRequestInfo
import net.mythrowaway.app.domain.account_link.infra.PreferenceAccountLinkRepositoryImpl
import net.mythrowaway.app.domain.account_link.usecase.AccountLinkApiInterface
import net.mythrowaway.app.domain.info.infra.PreferenceUserRepositoryImpl
import net.mythrowaway.app.domain.info.service.UserIdService
import net.mythrowaway.app.domain.info.usecase.InformationUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class AccountLinkUseCaseTest {
    @Mock lateinit var apiAdapter: AccountLinkApiInterface

    private val accountLinkRepository = PreferenceAccountLinkRepositoryImpl(
        InstrumentationRegistry.getInstrumentation().context
    )
    private val userRepository = PreferenceUserRepositoryImpl(
        InstrumentationRegistry.getInstrumentation().context
    )
    private lateinit var useCase: AccountLinkUseCase

    private val preferences = PreferenceManager.getDefaultSharedPreferences(
        InstrumentationRegistry.getInstrumentation().context
    )

    @Before
    fun before() {
        preferences.edit().clear().commit()
        MockitoAnnotations.openMocks(this)
        Mockito.reset(apiAdapter)
        useCase = AccountLinkUseCase(
            api = apiAdapter,
            accountLinkRepository = accountLinkRepository,
            userIdService = UserIdService(
                InformationUseCase(
                    userRepository
                )
            )
        )
    }

    @Test
    fun get_start_url_and_save_token_and_redirectUri_when_start_account_link_with_alexa_app_success() {
        userRepository.saveUserId("dummy")
        val dummyUrl = "https://dummyUrl&redirect_uri=https://dummyRedirectUri"
        Mockito.`when`(apiAdapter.accountLink("dummy")).thenReturn(
            StartAccountLinkResponse(
                url = dummyUrl,
                token =  "dummyToken"
            )
        )

        val url = useCase.startAccountLinkWithAlexaApp()
        assertEquals(dummyUrl,url)
        val accountLinkRequest = accountLinkRepository.getAccountLinkRequestInfo()
        assertEquals("dummyToken",accountLinkRequest?.token)
        assertEquals("https://dummyRedirectUri",accountLinkRequest?.redirectUri)
    }

    @Test
    fun throw_exception_if_user_id_is_null_when_start_account_link_with_alexa_app() {
        Mockito.`when`(apiAdapter.accountLink("dummy")).thenReturn(
            StartAccountLinkResponse(
                url = "dummyUrl",
                token = "dummyToken"
            )
        )
        try {
            useCase.startAccountLinkWithAlexaApp()
            fail("Expected exception not thrown")
        } catch (e: Exception) {
            assertEquals("User ID is null", e.message)
        }
    }
    @Test
    fun throw_exception_when_start_account_link_with_alexa_app_and_response_invalid_redirect_uri() {
        userRepository.saveUserId("dummy")
        Mockito.`when`(apiAdapter.accountLink("dummy")).thenReturn(
            StartAccountLinkResponse(
                url = "dummyUrl",
                token = "dummyToken"
            )
        )

        try {
            useCase.startAccountLinkWithAlexaApp()
            fail("Expected exception not thrown")
        } catch (e: Exception) {
            assertEquals("Failed to extract redirect_uri", e.message)
        }
    }


    @Test
    fun get_start_url_and_save_token_and_redirectUri_when_start_account_link_with_lwa_success() {
        userRepository.saveUserId("dummy")
        val dummyUrl = "https://dummyUrl&redirect_uri=https://dummyRedirectUri"
        Mockito.`when`(apiAdapter.accountLinkAsWeb("dummy")).thenReturn(
            StartAccountLinkResponse(
                url = dummyUrl,
                token = "dummyToken"
            )
        )

        val url = useCase.startAccountLinkWithLWA()
        assertEquals(dummyUrl,url)
        val accountLinkRequest = accountLinkRepository.getAccountLinkRequestInfo()
        assertEquals("dummyToken",accountLinkRequest?.token)
        assertEquals("https://dummyRedirectUri",accountLinkRequest?.redirectUri)
    }

    @Test
    fun throw_exception_if_user_id_is_null_when_start_account_link_with_lwa() {
        Mockito.`when`(apiAdapter.accountLinkAsWeb("dummy")).thenReturn(
            StartAccountLinkResponse(
                url = "dummyUrl",
                token = "dummyToken"
            )
        )

        try {
            useCase.startAccountLinkWithLWA()
            fail("Expected exception not thrown")
        }catch (e: Exception)  {
            assertEquals("User ID is null", e.message)
        }
    }
    @Test
    fun throw_exception_when_start_account_link_with_lwa_and_response_invalid_redirect_uri() {
        userRepository.saveUserId("dummy")
        Mockito.`when`(apiAdapter.accountLinkAsWeb("dummy")).thenReturn(
            StartAccountLinkResponse(
                url = "dummyUrl",
                token = "dummyToken"
            )
        )

        try {
            useCase.startAccountLinkWithLWA()
            fail()
        }catch(e: Exception) {
            assertEquals("Failed to extract redirect_uri", e.message)
        }
    }

    @Test
    fun get_account_link_request_info() {
        userRepository.saveUserId("dummy")
        accountLinkRepository.saveAccountLinkRequestInfo(
            FinishAccountLinkRequestInfo(
                token = "dummyToken",
                redirectUri = "https://dummyRedirectUri"
            )
        )

        val accountLinkRequest = useCase.getAccountLinkRequest()
        assertEquals("dummyToken",accountLinkRequest.token)
        assertEquals("https://dummyRedirectUri",accountLinkRequest.redirectUri)
    }

    @Test
    fun throw_exception_if_not_saved() {
        userRepository.saveUserId("dummy")
        try {
            useCase.getAccountLinkRequest()
            fail("Expected exception not thrown")
        } catch (e: Exception) {
            assertEquals("Account link request info not found", e.message)
        }
    }

    @Test
    fun throw_exception_if_user_id_is_null_when_get_account_link_request_info() {
        try {
            useCase.getAccountLinkRequest()
            fail("Expected exception not thrown")
        } catch (e: Exception) {
            assertEquals("User ID is null", e.message)
        }
    }
}