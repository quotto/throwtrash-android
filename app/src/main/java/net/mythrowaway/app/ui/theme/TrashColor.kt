package net.mythrowaway.app.ui.theme

import androidx.compose.ui.graphics.Color
import net.mythrowaway.app.domain.TrashType

class TrashColor {
  companion object {
    val burn = Color(0xFFFF7F7F)
    val unburn = Color(0xFF7FBFFF)
    val bottole = Color(0xFF7F7FFF)
    val can = Color(0xFFFF7FBF)
    val coarse = Color(0xFFF38A1A)
    val other = Color(0xFFB1B0B0)
    val paper = Color(0xFFFFbF7F)
    val petbottle = Color(0xFF5BB8B8)
    val resource = Color(0xFF4EC239)
    val plastic = Color(0xFF53AF53)

    fun getColor(trashType: TrashType): Color {
      return when (trashType) {
        TrashType.BURN -> burn
        TrashType.UNBURN -> unburn
        TrashType.BOTTLE -> bottole
        TrashType.CAN -> can
        TrashType.COARSE -> coarse
        TrashType.OTHER -> other
        TrashType.PAPER -> paper
        TrashType.PETBOTTLE -> petbottle
        TrashType.RESOURCE -> resource
        TrashType.PLASTIC -> plastic
      }
    }
  }
}