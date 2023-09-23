package net.mythrowaway.app.view.calendar

import net.mythrowaway.app.R

class TrashColorPicker {
  companion object {
    private val COLOR_CODE_MAP = HashMap<String,String>()
    private val DRAWABLE_ID_MAP = HashMap<String,Int>()
    @JvmStatic
    fun getColorCode(trashType: String): String {
      return COLOR_CODE_MAP[trashType] ?: "#ffffff"
    }

    @JvmStatic
    fun registerColorCode(trashType: String, colorCode: String) {
      COLOR_CODE_MAP[trashType] = colorCode
    }

    @JvmStatic
    fun clearColorCode() {
      COLOR_CODE_MAP.clear()
    }

    @JvmStatic
    fun getDrawableIdByTrashType(trashType: String): Int {
      return DRAWABLE_ID_MAP[trashType] ?: R.drawable.background_calendar_trash_name_default
    }

    @JvmStatic
    fun registerDrawableId(trashType: String, drawableId: Int) {
      DRAWABLE_ID_MAP[trashType] = drawableId
    }

    @JvmStatic
    fun clearDrawableId() {
      DRAWABLE_ID_MAP.clear()
    }
  }
}