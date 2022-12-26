package net.mythrowaway.app.presenter

import kotlinx.coroutines.runBlocking
import net.mythrowaway.app.adapter.AccountLinkViewInterface
import net.mythrowaway.app.adapter.presenter.AccountLinkPresenterImpl
import net.mythrowaway.app.domain.AccountLinkInfo
import net.mythrowaway.app.viewmodel.AccountLinkType
import net.mythrowaway.app.viewmodel.AccountLinkViewModel
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito

class AccountLinkPresenterImplTest {
    private val instance = AccountLinkPresenterImpl()
    @Mock
    val view: AccountLinkViewInterface = Mockito.mock(AccountLinkViewInterface::class.java)

    @Before
    fun before() {
        Mockito.reset(view)
        instance.setView(view)
    }

    @Test
    fun startAccountLink_with_LWA() {
        val viewModel = AccountLinkViewModel()
        viewModel.type = AccountLinkType.WEB
        instance.setViewModel(viewModel)
        runBlocking {
            instance.startAccountLink(AccountLinkInfo().apply {
                linkUrl = "https://web.com"
                token = "token001"
            })
        }
        Mockito.verify(view,Mockito.times(1)).startAccountLinkWithLWA()
    }

    @Test
    fun startAccountLink_with_App() {
        val viewModel = AccountLinkViewModel()
        viewModel.type = AccountLinkType.APP
        instance.setViewModel(viewModel)
        runBlocking {
            instance.startAccountLink(AccountLinkInfo().apply {
                linkUrl = "https://app.com"
                token = "token001"
            })
            Mockito.verify(view,Mockito.times(1)).startAccountLinkWithAlexaApp()
        }
    }

}