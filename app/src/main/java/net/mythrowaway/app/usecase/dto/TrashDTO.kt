package net.mythrowaway.app.usecase.dto

import net.mythrowaway.app.domain.TrashType

class TrashDTO (
  private val _type: TrashType,
  private val _displayName: String
){
  val type: TrashType
    get() = _type
  val displayName: String
    get() = _displayName
}