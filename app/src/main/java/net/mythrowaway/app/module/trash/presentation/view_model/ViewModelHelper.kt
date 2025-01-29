package net.mythrowaway.app.module.trash.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
fun <T : ViewModel> viewModelFactory(
  factory: () -> T
): ViewModelProvider.Factory {
  return object : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return factory() as T
    }
  }
}

