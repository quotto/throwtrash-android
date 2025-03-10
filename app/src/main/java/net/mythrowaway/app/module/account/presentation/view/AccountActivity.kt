package net.mythrowaway.app.module.account.presentation.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import net.mythrowaway.app.application.MyThrowTrash
import net.mythrowaway.app.application.di.AccountComponent
import net.mythrowaway.app.ui.theme.AppTheme
import net.mythrowaway.app.module.account.presentation.view_model.AccountViewModel
import javax.inject.Inject

class AccountActivity : AppCompatActivity() {
    @Inject lateinit var accountViewModelFactory: AccountViewModel.Factory

    private lateinit var accountComponent: AccountComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        accountComponent = (application as MyThrowTrash).appComponent.accountComponent().create()
        accountComponent.inject(this)
        super.onCreate(savedInstanceState)

        setContent{
            AppTheme {
                AccountScreen(
                    viewModel = ViewModelProvider.create(
                        this,
                        accountViewModelFactory
                    )[AccountViewModel::class.java]
                )
            }
        }
    }
}