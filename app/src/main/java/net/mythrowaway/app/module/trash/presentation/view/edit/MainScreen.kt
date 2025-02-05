package net.mythrowaway.app.module.trash.presentation.view.edit

import android.util.Log
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphIntrinsics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.createFontFamilyResolver
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.mythrowaway.app.R
import net.mythrowaway.app.module.trash.presentation.view_model.edit.EditTrashViewModel
import net.mythrowaway.app.module.trash.presentation.view_model.edit.TrashListViewModel

enum class EditScreenType {
  Edit,
  ExcludeDayOfMonth,
  List
}

@Composable
fun MainScreen(
  editViewModel: EditTrashViewModel,
  trashListViewModel: TrashListViewModel,
  modifier: Modifier = Modifier,
  navController: NavHostController = rememberNavController(),
  startDestination: String = EditScreenType.Edit.name
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
        trashTypeName = editViewModel.trashType.value.displayName,
        navController = navController
      )
    }
    composable(EditScreenType.List.name) {
      TrashListScreen(
        editTrashViewModel = editViewModel,
        trashListViewModel = trashListViewModel,
        navController = navController
      )
    }
  }
}
@Composable
fun calculateTextWidth(text: String, style: TextStyle): Dp {
  Log.d("calculateTextWidth", "current density: ${LocalDensity.current.density}")
  val dp = ParagraphIntrinsics(
    text = text,
    style = style,
    density = LocalDensity.current,
    fontFamilyResolver = createFontFamilyResolver(LocalContext.current),
  ).maxIntrinsicWidth.dp
  Log.d("calculateTextWidth", "$text, width: $dp")
  return dp
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropDown(
  modifier: Modifier = Modifier,
  items: List<String>,
  selectedText: String,
  expanded: Boolean,
  dropDownColor: Color = Color.Transparent,
  onExpandedChange: () -> Unit,
  onItemSelected: (Int) -> Unit,
  onDismissRequest: () -> Unit,
  testTag: String = ""
) {
  val widthBaseText = items.maxByOrNull { it.length } ?: ""
  ExposedDropdownMenuBox(
    modifier = modifier.height(48.dp),
    expanded = expanded,
    onExpandedChange = { onExpandedChange() }
  ) {
    TextField(
      modifier = Modifier
        .menuAnchor()
        .width(
          calculateTextWidth(
            text = "$widthBaseText ▼",
            style = MaterialTheme.typography.bodySmall
          )
        )
        .testTag(testTag),
      value = selectedText,
      onValueChange = {},
      readOnly = true,
      trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
      singleLine = true,
      textStyle = MaterialTheme.typography.bodySmall,
      colors = TextFieldDefaults.colors(
        unfocusedContainerColor = dropDownColor,
        focusedContainerColor = dropDownColor,
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