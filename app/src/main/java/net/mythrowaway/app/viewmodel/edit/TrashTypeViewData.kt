package net.mythrowaway.app.viewmodel.edit

import net.mythrowaway.app.domain.TrashType

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