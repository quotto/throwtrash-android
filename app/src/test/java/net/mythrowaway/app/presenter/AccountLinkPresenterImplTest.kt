package net.mythrowaway.app.presenter

import kotlinx.coroutines.runBlocking
import net.mythrowaway.app.adapter.IAccountLinkView
import net.mythrowaway.app.adapter.presenter.AccountLinkPresenterImpl
import net.mythrowaway.app.domain.AccountLinkInfo
import net.mythrowaway.app.viewmodel.ACCOUNT_LINK_TYPE
import net.mythrowaway.app.viewmodel.AccountLinkViewModel
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito

class AccountLinkPresenterImplTest {
    private val instance = AccountLinkPresenterImpl()
    @Mock
    val view: IAccountLinkView = Mockito.mock(IAccountLinkView::class.java)

    @Before
    fun before() {
        Mockito.reset(view)
        instance.setView(view)
    }

    @Test
    fun startAccountLink_with_LWA() {
        val viewModel = AccountLinkViewModel()
        viewModel.type = ACCOUNT_LINK_TYPE.WEB
        instance.setViewModel(viewModel)
        runBlocking {
            instance.startAccountLink(AccountLinkInfo().apply {
                linkUrl = "https://web.com"
                sessionId = "throwaway-session"
                sessionValue = "123456"
            });
        }
        Mockito.verify(view,Mockito.times(1)).startAccountLinkWithLWA()
    }

    @Test
    fun startAccountLink_with_App() {
        val viewModel = AccountLinkViewModel()
        viewModel.type = ACCOUNT_LINK_TYPE.APP
        instance.setViewModel(viewModel)
        runBlocking {
            instance.startAccountLink(AccountLinkInfo().apply {
                linkUrl = "https://app.com"
                sessionId = "throwaway-session"
                sessionValue = "123456"
            });
            Mockito.verify(view,Mockito.times(1)).startAccountLinkWithAlexaApp()
        }
    }

}