package net.mythrowaway.app.usecase

import androidx.preference.PreferenceManager
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import net.mythrowaway.app.module.account_link.usecase.AccountLinkUseCase
import net.mythrowaway.app.module.account_link.dto.StartAccountLinkResponse
import net.mythrowaway.app.module.account_link.entity.FinishAccountLinkRequestInfo
import net.mythrowaway.app.module.account_link.infra.PreferenceAccountLinkRepositoryImpl
import net.mythrowaway.app.module.account_link.usecase.AccountLinkApiInterface
import net.mythrowaway.app.module.account.infra.PreferenceUserRepositoryImpl
import net.mythrowaway.app.module.account.service.AuthService
import net.mythrowaway.app.module.account.service.UserIdService
import net.mythrowaway.app.module.account.usecase.AccountUseCase
import net.mythrowaway.app.module.account.usecase.AuthManagerInterface
import net.mythrowaway.app.module.account.usecase.UserApiInterface
import net.mythrowaway.app.module.trash.infra.PreferenceSyncRepositoryImpl
import net.mythrowaway.app.module.trash.infra.PreferenceTrashRepositoryImpl
import net.mythrowaway.app.module.trash.service.TrashService
import net.mythrowaway.app.module.trash.usecase.ResetTrashUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class AccountLinkUseCaseTest {
    @Mock lateinit var mockAccountLinkApi: AccountLinkApiInterface
    @Mock lateinit var mockUserApi: UserApiInterface
    @Mock lateinit var mockAuthManager: AuthManagerInterface

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

    private val trashRepository = PreferenceTrashRepositoryImpl(
        InstrumentationRegistry.getInstrumentation().context
    )
    private val syncRepository = PreferenceSyncRepositoryImpl(
        InstrumentationRegistry.getInstrumentation().context
    )
    private val trashService = TrashService(
        trashRepository = trashRepository,
        syncRepository = syncRepository,
        resetTrashUseCase = ResetTrashUseCase(
            trashRepository = trashRepository,
            syncRepository = syncRepository,
        )
    )


    @Before
    fun before() {
        preferences.edit().clear().commit()
        MockitoAnnotations.openMocks(this)
        Mockito.reset(mockAccountLinkApi)
        Mockito.reset(mockUserApi)
        Mockito.reset(mockAuthManager)

        runBlocking {
            Mockito.`when`(mockAuthManager.getIdToken(Mockito.anyBoolean())).thenReturn(
                Result.success("dummy-token")
            )
        }

        useCase = AccountLinkUseCase(
            api = mockAccountLinkApi,
            accountLinkRepository = accountLinkRepository,
            userIdService = UserIdService(
                AccountUseCase(
                    userRepository,
                    userApi = mockUserApi,
                    authManager = mockAuthManager,
                    trashService = trashService
                )
            ),
            authService = AuthService(mockAuthManager)
        )
    }

    @Test
    fun get_start_url_and_save_token_and_redirectUri_when_start_account_link_with_alexa_app_success() {
        userRepository.saveUserId("dummy")
        val dummyUrl = "https://dummyUrl&redirect_uri=https://dummyRedirectUri"
        Mockito.`when`(mockAccountLinkApi.accountLink("dummy", "dummy-token")).thenReturn(
            StartAccountLinkResponse(
                url = dummyUrl,
                token =  "dummyToken"
            )
        )

        runBlocking {
            val url = useCase.startAccountLinkWithAlexaApp()
            assertEquals(dummyUrl, url)
            val accountLinkRequest = accountLinkRepository.getAccountLinkRequestInfo()
            assertEquals("dummyToken", accountLinkRequest?.token)
            assertEquals("https://dummyRedirectUri", accountLinkRequest?.redirectUri)
        }
    }

    @Test
    fun throw_exception_if_user_id_is_null_when_start_account_link_with_alexa_app() {
        Mockito.`when`(mockAccountLinkApi.accountLink("dummy", "dummy-token")).thenReturn(
            StartAccountLinkResponse(
                url = "dummyUrl",
                token = "dummyToken"
            )
        )
        runBlocking {
            try {
                useCase.startAccountLinkWithAlexaApp()
                fail("Expected exception not thrown")
            } catch (e: Exception) {
                assertEquals("User ID is null", e.message)
            }
        }
    }
    @Test
    fun throw_exception_when_start_account_link_with_alexa_app_and_response_invalid_redirect_uri() {
        userRepository.saveUserId("dummy")
        Mockito.`when`(mockAccountLinkApi.accountLink("dummy", "dummy-token")).thenReturn(
            StartAccountLinkResponse(
                url = "dummyUrl",
                token = "dummyToken"
            )
        )

        runBlocking {
            try {
                useCase.startAccountLinkWithAlexaApp()
                fail("Expected exception not thrown")
            } catch (e: Exception) {
                assertEquals("Failed to extract redirect_uri", e.message)
            }
        }
    }


    @Test
    fun get_start_url_and_save_token_and_redirectUri_when_start_account_link_with_lwa_success() {
        userRepository.saveUserId("dummy")
        val dummyUrl = "https://dummyUrl&redirect_uri=https://dummyRedirectUri"
        Mockito.`when`(mockAccountLinkApi.accountLinkAsWeb("dummy", "dummy-token")).thenReturn(
            StartAccountLinkResponse(
                url = dummyUrl,
                token = "dummyToken"
            )
        )

        runBlocking {
            val url = useCase.startAccountLinkWithLWA()
            assertEquals(dummyUrl, url)
            val accountLinkRequest = accountLinkRepository.getAccountLinkRequestInfo()
            assertEquals("dummyToken", accountLinkRequest?.token)
            assertEquals("https://dummyRedirectUri", accountLinkRequest?.redirectUri)
        }
    }

    @Test
    fun throw_exception_if_user_id_is_null_when_start_account_link_with_lwa() {
        Mockito.`when`(mockAccountLinkApi.accountLinkAsWeb("dummy", "dummy-token")).thenReturn(
            StartAccountLinkResponse(
                url = "dummyUrl",
                token = "dummyToken"
            )
        )

        runBlocking {
            try {
                useCase.startAccountLinkWithLWA()
                fail("Expected exception not thrown")
            }catch (e: Exception)  {
                assertEquals("User ID is null", e.message)
            }
        }
    }
    @Test
    fun throw_exception_when_start_account_link_with_lwa_and_response_invalid_redirect_uri() {
        userRepository.saveUserId("dummy")
        Mockito.`when`(mockAccountLinkApi.accountLinkAsWeb("dummy", "dummy-token")).thenReturn(
            StartAccountLinkResponse(
                url = "dummyUrl",
                token = "dummyToken"
            )
        )

        runBlocking {
            try {
                useCase.startAccountLinkWithLWA()
                fail()
            } catch (e: Exception) {
                assertEquals("Failed to extract redirect_uri", e.message)
            }
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