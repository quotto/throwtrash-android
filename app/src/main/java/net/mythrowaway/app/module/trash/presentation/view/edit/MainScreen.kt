package net.mythrowaway.app.module.trash.presentation.view.edit

import android.util.Log
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.activity.compose.LocalActivity
import net.mythrowaway.app.R
import net.mythrowaway.app.module.trash.presentation.view_model.edit.EditTrashViewModel
import net.mythrowaway.app.module.trash.presentation.view_model.edit.CommonExcludeDayOfMonthViewModel
import net.mythrowaway.app.module.trash.presentation.view_model.edit.TrashListViewModel
import net.mythrowaway.app.module.trash.presentation.view_model.ScheduleSearchImportViewModel

enum class EditScreenType {
  Edit,
  ExcludeDayOfMonth,
  CommonExcludeDayOfMonth,
  List
}

private val dropdownHorizontalPadding = 16.dp
private val dropdownTrailingIconWidth = 24.dp
private val dropdownTrailingIconSpacing = 12.dp

internal fun calculateDropdownFieldWidth(textWidth: Dp, showTrailingIcon: Boolean): Dp {
  val baseWidth = textWidth + (dropdownHorizontalPadding * 2)
  return if (showTrailingIcon) {
    baseWidth + dropdownTrailingIconWidth + dropdownTrailingIconSpacing
  } else {
    baseWidth
  }
}

@Composable
fun MainScreen(
  editViewModel: EditTrashViewModel,
  commonExcludeViewModel: CommonExcludeDayOfMonthViewModel,
  trashListViewModel: TrashListViewModel,
  scheduleSearchImportViewModel: ScheduleSearchImportViewModel,
  modifier: Modifier = Modifier,
  navController: NavHostController = rememberNavController(),
  startDestination: String = EditScreenType.Edit.name,
  onScheduleSearchImportRequested: () -> Unit = {}
) {
  NavHost(
    navController = navController,
    startDestination = startDestination,
    modifier = modifier,
  ) {
    composable(EditScreenType.Edit.name) {
      EditScreen(
        editTrashViewModel = editViewModel,
        onClickToExcludeDayOfMonth = {
          navController.navigate(EditScreenType.ExcludeDayOfMonth.name)
        }
      )
    }
    composable(EditScreenType.ExcludeDayOfMonth.name) {
      ExcludeDayOfMonthScreen(
        viewModel = editViewModel,
        navController = navController
      )
    }
    composable(EditScreenType.CommonExcludeDayOfMonth.name) {
      val activity = LocalActivity.current
      CommonExcludeDayOfMonthScreen(
        viewModel = commonExcludeViewModel,
        onClose = {
          val popped = navController.popBackStack()
          if (!popped) {
            activity?.finish()
          }
        }
      )
    }
    composable(EditScreenType.List.name) {
      TrashListScreen(
        editTrashViewModel = editViewModel,
        trashListViewModel = trashListViewModel,
        scheduleSearchImportViewModel = scheduleSearchImportViewModel,
        onScheduleSearchImportRequested = onScheduleSearchImportRequested,
        navController = navController
      )
    }
  }
}
@Composable
fun calculateTextWidth(text: String, style: TextStyle): Dp {
  val density = LocalDensity.current
  val textMeasurer = rememberTextMeasurer()
  Log.d("calculateTextWidth", "current density: ${density.density}")
  val dp = with(density) {
    textMeasurer.measure(
      text = AnnotatedString(text),
      style = style,
    ).size.width.toDp()
  }
  Log.d("calculateTextWidth", "$text, width: $dp")
  return dp
}

@Composable
fun calculateDropdownFieldWidth(text: String, style: TextStyle, showTrailingIcon: Boolean): Dp {
  return calculateDropdownFieldWidth(
    textWidth = calculateTextWidth(text = text, style = style),
    showTrailingIcon = showTrailingIcon
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropDown(
  modifier: Modifier = Modifier,
  items: List<String>,
  selectedText: String,
  expanded: Boolean,
  dropDownColor: Color = Color.Transparent,
  textColor: Color = MaterialTheme.colorScheme.onSurface,
  indicatorColor: Color = MaterialTheme.colorScheme.primary,
  textStyle: TextStyle = MaterialTheme.typography.bodySmall,
  showTrailingIcon: Boolean = true,
  useIntrinsicWidth: Boolean = true,
  onExpandedChange: () -> Unit,
  onItemSelected: (Int) -> Unit,
  onDismissRequest: () -> Unit,
  testTag: String = ""
) {
  val widthBaseText = items.maxByOrNull { it.length } ?: ""
  val widthMeasureText = if (showTrailingIcon) "$widthBaseText ▼" else widthBaseText
  val widthModifier = if (useIntrinsicWidth) {
    Modifier.width(
      calculateDropdownFieldWidth(
        text = widthMeasureText,
        style = textStyle,
        showTrailingIcon = showTrailingIcon
      )
    )
  } else {
    Modifier.fillMaxWidth()
  }
  ExposedDropdownMenuBox(
    modifier = modifier.height(48.dp),
    expanded = expanded,
    onExpandedChange = { onExpandedChange() }
  ) {
    TextField(
      modifier = Modifier
        .menuAnchor(
          type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
          enabled = true,
        )
        .then(widthModifier)
        .testTag(testTag),
      value = selectedText,
      onValueChange = {},
      readOnly = true,
      trailingIcon = if (showTrailingIcon) {
        { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
      } else {
        null
      },
      singleLine = true,
      textStyle = textStyle,
      colors = TextFieldDefaults.colors(
        unfocusedContainerColor = dropDownColor,
        focusedContainerColor = dropDownColor,
        focusedIndicatorColor = indicatorColor,
        unfocusedIndicatorColor = indicatorColor,
        disabledIndicatorColor = indicatorColor,
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        disabledTextColor = textColor,
      )
    )
    ExposedDropdownMenu(
      modifier = Modifier.testTag("${testTag}${stringResource(R.string.testTag_suffix_dropdown_menu_item)}"),
      expanded = expanded,
      onDismissRequest = { onDismissRequest() }
    ) {
      items.forEachIndexed { index, item ->
        DropdownMenuItem(
          onClick = {
            onItemSelected(index)
            onExpandedChange()
          },
          text = { Text(
            text = item,
            style = MaterialTheme.typography.bodySmall,
          ) }
        )
      }
    }
  }
}
