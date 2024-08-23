package net.mythrowaway.app.view.info

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import net.mythrowaway.app.adapter.MyThrowTrash
import net.mythrowaway.app.adapter.di.InformationComponent
import net.mythrowaway.app.ui.theme.AppTheme
import net.mythrowaway.app.viewmodel.InformationViewModel
import javax.inject.Inject

class InformationActivity : AppCompatActivity() {
    @Inject
    lateinit var informationViewModelFactory: InformationViewModel.Factory

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
                )
            }
        }
    }
}