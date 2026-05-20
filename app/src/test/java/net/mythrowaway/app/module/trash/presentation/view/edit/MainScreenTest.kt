package net.mythrowaway.app.module.trash.presentation.view.edit

import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MainScreenTest {
  @Test
  fun calculate_dropdown_field_width_adds_text_field_padding_and_icon_space() {
    val width = calculateDropdownFieldWidth(
      textWidth = 40.dp,
      showTrailingIcon = true
    )

    assertEquals(108.dp, width)
  }

  @Test
  fun calculate_dropdown_field_width_without_trailing_icon_adds_only_text_field_padding() {
    val width = calculateDropdownFieldWidth(
      textWidth = 40.dp,
      showTrailingIcon = false
    )

    assertEquals(72.dp, width)
  }
}
