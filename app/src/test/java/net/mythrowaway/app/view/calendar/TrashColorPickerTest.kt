package net.mythrowaway.app.view.calendar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import net.mythrowaway.app.R

class TrashColorPickerTest {
  @BeforeEach
  fun setUp() {
    TrashColorPicker.clearColorCode()
    TrashColorPicker.clearDrawableId()
  }

  @Test
  fun  getColorCodeWithTrashTypeOfUnBurn() {
    TrashColorPicker.registerColorCode("unburn", "#7fbf")
    val colorCode = TrashColorPicker.getColorCode("unburn")
    assertEquals("#7fbf", colorCode)
  }

  @Test
  fun registerColorCodeOfBurn() {
    TrashColorPicker.registerColorCode("burn", "#ff7f7f")
    val colorCode = TrashColorPicker.getColorCode("burn")
    assertEquals("#ff7f7f", colorCode)
  }

  @Test
  fun registerColorCodeOfBunTwice() {
    TrashColorPicker.registerColorCode("burn", "#ff7f7f")
    TrashColorPicker.registerColorCode("burn", "#ff7f7e")
    val colorCode = TrashColorPicker.getColorCode("burn")
    assertEquals("#ff7f7e", colorCode)
  }

  @Test
  fun registerColorCodeOfBurnAndUnburn() {
    TrashColorPicker.registerColorCode("burn", "#ff7f7f")
    TrashColorPicker.registerColorCode("unburn", "#7fbf")
    val burnColorCode = TrashColorPicker.getColorCode("burn")
    assertEquals("#ff7f7f", burnColorCode)
    val unBurnColorCode = TrashColorPicker.getColorCode("unburn")
    assertEquals("#7fbf", unBurnColorCode)
  }

  @Test
  fun clearColorCode() {
    TrashColorPicker.registerColorCode("burn", "#ff7f7f")
    TrashColorPicker.registerColorCode("unburn", "#7fbf")
    TrashColorPicker.clearColorCode()
    val burnColorCode = TrashColorPicker.getColorCode("burn")
    assertEquals("#ffffff", burnColorCode)
    val unBurnColorCode = TrashColorPicker.getColorCode("unburn")
    assertEquals("#ffffff", unBurnColorCode)
  }

  @Test
  fun getDrawableIdOfBurn() {
    TrashColorPicker.registerDrawableId("burn", R.drawable.background_calendar_trash_name_burn)
    val drawableId = TrashColorPicker.getDrawableIdByTrashType("burn")
    assertEquals(R.drawable.background_calendar_trash_name_burn, drawableId)
  }

  @Test
  fun registerDrawableIdOfBunTwice() {
    TrashColorPicker.registerDrawableId("burn", R.drawable.background_calendar_trash_name_burn)
    TrashColorPicker.registerDrawableId("burn", R.drawable.common_full_open_on_phone)
    val colorCode = TrashColorPicker.getDrawableIdByTrashType("burn")
    assertEquals(R.drawable.common_full_open_on_phone, colorCode)
  }

  @Test
  fun getDrawableIdOfUnRegisteredTrashType() {
    val drawableId = TrashColorPicker.getDrawableIdByTrashType("")
    assertEquals(R.drawable.background_calendar_trash_name_default, drawableId)
  }

  @Test
  fun clearDrawableId() {
    TrashColorPicker.registerDrawableId("burn", R.drawable.background_calendar_trash_name_burn)
    TrashColorPicker.registerDrawableId("unburn", R.drawable.background_calendar_trash_name_unburn)
    TrashColorPicker.clearDrawableId()
    val burnDrawableId = TrashColorPicker.getDrawableIdByTrashType("burn")
    assertEquals(R.drawable.background_calendar_trash_name_default, burnDrawableId)
    val unBurnDrawableId = TrashColorPicker.getDrawableIdByTrashType("unburn")
    assertEquals(R.drawable.background_calendar_trash_name_default, unBurnDrawableId)
  }

  @Test
  fun registerDrawableIdOfBurnAndUnburn() {
    TrashColorPicker.registerDrawableId("burn", R.drawable.background_calendar_trash_name_burn)
    TrashColorPicker.registerDrawableId("unburn", R.drawable.background_calendar_trash_name_unburn)
    val burnColorCode = TrashColorPicker.getDrawableIdByTrashType("burn")
    assertEquals(R.drawable.background_calendar_trash_name_burn, burnColorCode)
    val unBurnColorCode = TrashColorPicker.getDrawableIdByTrashType("unburn")
    assertEquals(R.drawable.background_calendar_trash_name_unburn, unBurnColorCode)
  }
}