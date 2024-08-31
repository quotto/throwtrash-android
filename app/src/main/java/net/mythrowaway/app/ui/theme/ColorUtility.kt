package net.mythrowaway.app.ui.theme

import net.mythrowaway.app.R
import net.mythrowaway.app.domain.trash.entity.TrashType

class ColorUtility {
  companion object {
    fun getTrashDrawableId(type: TrashType): Int {
      return when(type) {
        TrashType.BURN -> R.drawable.background_calendar_trash_name_burn
        TrashType.CAN -> R.drawable.background_calendar_trash_name_can
        TrashType.UNBURN -> R.drawable.background_calendar_trash_name_unburn
        TrashType.PLASTIC -> R.drawable.background_calendar_trash_name_plastic
        TrashType.PETBOTTLE -> R.drawable.background_calendar_trash_name_petbottle
        TrashType.BOTTLE -> R.drawable.background_calendar_trash_name_bin
        TrashType.PAPER -> R.drawable.background_calendar_trash_name_paper
        TrashType.COARSE -> R.drawable.background_calendar_trash_name_coarse
        TrashType.RESOURCE -> R.drawable.background_calendar_trash_name_resource
        TrashType.OTHER -> R.drawable.background_calendar_trash_name_other
      }
    }
  }
}