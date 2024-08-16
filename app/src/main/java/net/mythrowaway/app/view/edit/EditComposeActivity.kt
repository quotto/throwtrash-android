package net.mythrowaway.app.view.edit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import net.mythrowaway.app.adapter.MyThrowTrash
import net.mythrowaway.app.adapter.di.EditComponent
import net.mythrowaway.app.ui.theme.AppTheme
import net.mythrowaway.app.view.edit.compose.EditScreenType
import net.mythrowaway.app.view.edit.compose.MainScreen
import net.mythrowaway.app.viewmodel.edit.EditTrashViewModel
import net.mythrowaway.app.viewmodel.list.TrashListViewModel
import javax.inject.Inject

class EditComposeActivity : AppCompatActivity(),CoroutineScope by MainScope() {
  @Inject
  lateinit var editTrashViewModelFactory: EditTrashViewModel.Factory

  @Inject
  lateinit var trashListViewModelFactory: TrashListViewModel.Factory

  private val _editTrashViewModel: EditTrashViewModel by lazy {
    ViewModelProvider(this, editTrashViewModelFactory)[EditTrashViewModel::class.java]
  }
  private val _trashListViewModel: TrashListViewModel by lazy {
    ViewModelProvider(this, trashListViewModelFactory)[TrashListViewModel::class.java]
  }

  lateinit var editComponent: EditComponent
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val screenType = intent.getStringExtra(SCREEN_TYPE) ?: EditScreenType.Edit.name
    editComponent = (application as MyThrowTrash).appComponent.editComponent().create();
    editComponent.inject(this)
    setContent {
      AppTheme {
        MainScreen(
          editViewModel = _editTrashViewModel,
          trashListViewModel = _trashListViewModel,
          startDestination = screenType
        )
      }
    }
  }

  companion object {
    const val SCREEN_TYPE = "screen_type"
  }
}
