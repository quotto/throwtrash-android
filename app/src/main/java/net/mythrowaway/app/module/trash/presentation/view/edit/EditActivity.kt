package net.mythrowaway.app.module.trash.presentation.view.edit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import net.mythrowaway.app.application.MyThrowTrash
import net.mythrowaway.app.application.di.EditComponent
import net.mythrowaway.app.ui.theme.AppTheme
import net.mythrowaway.app.module.trash.presentation.view_model.edit.EditTrashViewModel
import net.mythrowaway.app.module.trash.presentation.view_model.edit.TrashListViewModel
import javax.inject.Inject

class EditActivity : AppCompatActivity(),CoroutineScope by MainScope() {
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

  private lateinit var editComponent: EditComponent
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val screenType = intent.getStringExtra(SCREEN_TYPE) ?: EditScreenType.Edit.name
    editComponent = (application as MyThrowTrash).appComponent.editComponent().create()
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
