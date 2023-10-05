package net.mythrowaway.app.adapter.repository

import net.mythrowaway.app.R
import net.mythrowaway.app.domain.TrashType

class TrashColorPicker: TrashDesignRepository {
  private val colorCodeMap = HashMap<String,String>()
  private val drawableIdMap = HashMap<String,Int>()

  override fun getColorCode(trashType: TrashType): String {
    return colorCodeMap[trashType.toString()] ?: "#ffffff"
  }

  override fun setColorCode(trashType: TrashType, colorCode: String) {
    colorCodeMap[trashType.toString()] = colorCode
  }

  override fun clearColorCode() {
    colorCodeMap.clear()
  }

  override fun getDrawableId(trashType: TrashType): Int {
    return drawableIdMap[trashType.toString()] ?: R.drawable.background_calendar_trash_name_default
  }

  override fun setDrawableId(trashType: TrashType, drawableId: Int) {
    drawableIdMap[trashType.toString()] = drawableId
  }

  override fun clearDrawableId() {
    drawableIdMap.clear()
  }
}