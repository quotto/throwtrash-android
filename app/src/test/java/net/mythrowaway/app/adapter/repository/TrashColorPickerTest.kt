package net.mythrowaway.app.adapter.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import net.mythrowaway.app.R
import net.mythrowaway.app.domain.TrashType

class TrashColorPickerTest {
  private val trashColorPicker = TrashColorPicker()
  @BeforeEach
  fun setUp() {
    trashColorPicker.clearColorCode()
    trashColorPicker.clearDrawableId()
  }

  @Test
  fun  getColorCode_setColorCode_burn() {
    trashColorPicker.setColorCode(TrashType.BURN, "#7fbf")
    val colorCode = trashColorPicker.getColorCode(TrashType.BURN)
    assertEquals("#7fbf", colorCode)
  }

  @Test
  fun setColorCode_twoTimes_burn() {
    trashColorPicker.setColorCode(TrashType.BURN, "#ff7f7f")
    trashColorPicker.setColorCode(TrashType.BURN, "#ff7f7e")
    val colorCode = trashColorPicker.getColorCode(TrashType.BURN)
    assertEquals("#ff7f7e", colorCode)
  }

  @Test
  fun setColorCode_burn_unburn() {
    trashColorPicker.setColorCode(TrashType.BURN, "#ff7f7f")
    trashColorPicker.setColorCode(TrashType.UNBURN, "#7fbf")
    val burnColorCode = trashColorPicker.getColorCode(TrashType.BURN)
    assertEquals("#ff7f7f", burnColorCode)
    val unBurnColorCode = trashColorPicker.getColorCode(TrashType.UNBURN)
    assertEquals("#7fbf", unBurnColorCode)
  }

  @Test
  fun clearColorCode() {
    trashColorPicker.setColorCode(TrashType.BURN, "#ff7f7f")
    trashColorPicker.setColorCode(TrashType.UNBURN, "#7fbf")
    trashColorPicker.clearColorCode()
    val burnColorCode = trashColorPicker.getColorCode(TrashType.BURN)
    assertEquals("#ffffff", burnColorCode)
    val unBurnColorCode = trashColorPicker.getColorCode(TrashType.UNBURN)
    assertEquals("#ffffff", unBurnColorCode)
  }

  @Test
  fun getDrawableId_burn() {
    trashColorPicker.setDrawableId(TrashType.BURN, R.drawable.background_calendar_trash_name_burn)
    val drawableId = trashColorPicker.getDrawableId(TrashType.BURN)
    assertEquals(R.drawable.background_calendar_trash_name_burn, drawableId)
  }

  @Test
  fun setDrawableId_twoTimes_burn() {
    trashColorPicker.setDrawableId(TrashType.BURN, R.drawable.background_calendar_trash_name_burn)
    trashColorPicker.setDrawableId(TrashType.BURN, R.drawable.common_full_open_on_phone)
    val colorCode = trashColorPicker.getDrawableId(TrashType.BURN)
    assertEquals(R.drawable.common_full_open_on_phone, colorCode)
  }

  @Test
  fun clearDrawableId() {
    trashColorPicker.setDrawableId(TrashType.BURN, R.drawable.background_calendar_trash_name_burn)
    trashColorPicker.setDrawableId(TrashType.UNBURN, R.drawable.background_calendar_trash_name_unburn)
    trashColorPicker.clearDrawableId()
    val burnDrawableId = trashColorPicker.getDrawableId(TrashType.BURN)
    assertEquals(R.drawable.background_calendar_trash_name_default, burnDrawableId)
    val unBurnDrawableId = trashColorPicker.getDrawableId(TrashType.UNBURN)
    assertEquals(R.drawable.background_calendar_trash_name_default, unBurnDrawableId)
  }

  @Test
  fun setDrawableId_burn_unburn() {
    trashColorPicker.setDrawableId(TrashType.BURN, R.drawable.background_calendar_trash_name_burn)
    trashColorPicker.setDrawableId(TrashType.UNBURN, R.drawable.background_calendar_trash_name_unburn)
    val burnColorCode = trashColorPicker.getDrawableId(TrashType.BURN)
    assertEquals(R.drawable.background_calendar_trash_name_burn, burnColorCode)
    val unBurnColorCode = trashColorPicker.getDrawableId(TrashType.UNBURN)
    assertEquals(R.drawable.background_calendar_trash_name_unburn, unBurnColorCode)
  }
}