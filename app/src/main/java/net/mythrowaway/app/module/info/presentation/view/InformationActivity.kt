package net.mythrowaway.app.module.info.presentation.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import net.mythrowaway.app.application.MyThrowTrash
import net.mythrowaway.app.application.di.InformationComponent
import net.mythrowaway.app.module.info.infra.AuthManager
import net.mythrowaway.app.ui.theme.AppTheme
import net.mythrowaway.app.module.info.presentation.view_model.InformationViewModel
import net.mythrowaway.app.module.info.usecase.UserApiInterface
import net.mythrowaway.app.module.info.usecase.UserRepositoryInterface
import net.mythrowaway.app.module.trash.service.TrashService
import javax.inject.Inject

class InformationActivity : AppCompatActivity() {
    @Inject lateinit var informationViewModelFactory: InformationViewModel.Factory
    @Inject lateinit var authManager: AuthManager
    @Inject lateinit var trashService: TrashService
    @Inject lateinit var userRepository: UserRepositoryInterface
    @Inject lateinit var userApi: UserApiInterface

    private lateinit var informationComponent: InformationComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        informationComponent = (application as MyThrowTrash).appComponent.informationComponent().create()
        informationComponent.inject(this)
        super.onCreate(savedInstanceState)

        setContent{
            AppTheme {
                InformationScreen(
                    viewModel = ViewModelProvider.create(
                        this,
                        informationViewModelFactory
                    )[InformationViewModel::class.java],
                    authManager,
                    userRepository,
                    userApi,
                    trashService
                )
            }
        }
    }
}