package net.mythrowaway.app.view.edit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import net.mythrowaway.app.adapter.MyThrowTrash
import net.mythrowaway.app.adapter.di.EditComponent
import net.mythrowaway.app.ui.theme.AppTheme
import net.mythrowaway.app.view.edit.compose.MainScreen
import net.mythrowaway.app.viewmodel.edit.EditTrashViewModel
import javax.inject.Inject

class EditComposeActivity : AppCompatActivity(),CoroutineScope by MainScope() {
  @Inject
  lateinit var editTrashViewModelFactory: EditTrashViewModel.Factory

  private val _editTrashViewModel: EditTrashViewModel by lazy {
    ViewModelProvider(this, editTrashViewModelFactory).get(EditTrashViewModel::class.java)
  }
  lateinit var editComponent: EditComponent
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    editComponent = (application as MyThrowTrash).appComponent.editComponent().create();
    editComponent.inject(this)
    setContent {
      AppTheme {
        MainScreen(viewModel = _editTrashViewModel)
      }
    }
  }
}
