package net.mythrowaway.app.domain.trash.presentation.view_model.edit

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mythrowaway.app.domain.trash.usecase.DeleteTrashUseCase
import net.mythrowaway.app.domain.trash.usecase.ListTrashesUseCase
import net.mythrowaway.app.domain.trash.dto.TrashDTO
import javax.inject.Inject

enum class TrashDeleteStatus {
  INIT,
  SUCCESS,
  FAILURE
}
class TrashListViewModel(
  private val _listUseCase: ListTrashesUseCase,
  private val _deleteUseCase: DeleteTrashUseCase
): ViewModel() {
  private val _trashList: MutableState<MutableList<TrashDTO>> = mutableStateOf(mutableListOf());
  private val _deleteStatus: MutableState<TrashDeleteStatus> = mutableStateOf(TrashDeleteStatus.INIT);

  val trashList: State<List<TrashDTO>> = _trashList
  val deleteStatus: State<TrashDeleteStatus> = _deleteStatus

  init {
    _trashList.value = _listUseCase.getTrashList().toMutableList()
  }

  suspend fun deleteTrash(trashId: String) {
    withContext(Dispatchers.IO){
      try {
        _deleteUseCase.deleteTrash(trashId)
        _trashList.value = _listUseCase.getTrashList().toMutableList()
        _deleteStatus.value = TrashDeleteStatus.SUCCESS
      } catch (e: Exception) {
        _deleteStatus.value = TrashDeleteStatus.FAILURE
      }
    }
  }

  fun resetDeleteStatus() {
    _deleteStatus.value = TrashDeleteStatus.INIT
  }

  class Factory @Inject constructor(private val _listUseCase: ListTrashesUseCase, private val _deleteUseCase: DeleteTrashUseCase): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return TrashListViewModel(_listUseCase, _deleteUseCase) as T
    }
  }
}