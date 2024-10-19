package net.mythrowaway.app.module.trash.presentation.view.share

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import net.mythrowaway.app.application.MyThrowTrash
import net.mythrowaway.app.application.di.ShareComponent
import net.mythrowaway.app.module.trash.presentation.view_model.share.ActivateViewModel
import net.mythrowaway.app.module.trash.presentation.view_model.share.PublishCodeViewModel
import net.mythrowaway.app.ui.theme.AppTheme
import javax.inject.Inject

class ShareActivity: AppCompatActivity() {
  @Inject
  lateinit var publishCodeViewModelFactory: PublishCodeViewModel.Factory
  @Inject
  lateinit var activateViewModelFactory: ActivateViewModel.Factory

  private lateinit var shareComponent: ShareComponent
  private val publishCodeViewModel: PublishCodeViewModel by lazy {
    ViewModelProvider(this, publishCodeViewModelFactory)[PublishCodeViewModel::class.java]
  }
  private val activateViewModel: ActivateViewModel by lazy {
    ViewModelProvider(this, activateViewModelFactory)[ActivateViewModel::class.java]
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    shareComponent = (application as MyThrowTrash).appComponent.shareComponent().create()
    shareComponent.inject(this)

    val screenType = intent.getStringExtra(SCREEN_TYPE)
    setContent {
      if (screenType == ShareScreenType.Publish.name) {
        AppTheme {
          PublishCodeScreen(
            viewModel = publishCodeViewModel,
          )
        }
      } else if (screenType == ShareScreenType.Activate.name) {
        AppTheme {
          ActivateScreen(
            viewModel = activateViewModel,
          )
        }
      }
    }
  }
  companion object {
    const val SCREEN_TYPE = "SCREEN_TYPE"
  }
}
enum class ShareScreenType {
  Activate,
  Publish
}
