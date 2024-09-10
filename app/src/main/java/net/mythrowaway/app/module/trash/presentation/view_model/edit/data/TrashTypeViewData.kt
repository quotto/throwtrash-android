package net.mythrowaway.app.module.trash.presentation.view_model.edit.data

data class TrashTypeViewData (
  private val _type: String,
  private val _displayName: String,
  private val _inputName: String
){
  val type: String
    get() = _type
  val displayName: String
    get() = _displayName
  val inputName: String
    get() = _inputName
}