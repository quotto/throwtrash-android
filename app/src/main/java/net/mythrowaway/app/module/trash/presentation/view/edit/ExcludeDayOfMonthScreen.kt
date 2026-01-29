package net.mythrowaway.app.module.trash.presentation.view.edit

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import net.mythrowaway.app.R
import net.mythrowaway.app.module.trash.presentation.view_model.edit.EditTrashViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExcludeDayOfMonthScreen(
  viewModel: EditTrashViewModel,
  navController: NavHostController
) {
  Scaffold (
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Text(
            text = stringResource(id = R.string.text_title_exclude_day_of_month),
            style = MaterialTheme.typography.titleMedium,
          )
        },
        navigationIcon = {
          IconButton(
            modifier = Modifier.size(24.dp),
            onClick = {
              navController.popBackStack()
            }
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
            )
          }
        }
      )
    }
  ){ innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Column(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        viewModel.excludeDayOfMonthViewDataList.value.forEachIndexed { index, excludeDayOfMonthViewData ->
          MonthAndDayDropDown(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 8.dp),
            monthIndex = excludeDayOfMonthViewData.month,
            dayIndex = excludeDayOfMonthViewData.day,
            onMonthSelected = { monthIndex, dayIndex ->
              viewModel.updateExcludeDayOfMonth(index, monthIndex, dayIndex)
            },
            onDaySelected = { dayIndex ->
              viewModel.updateExcludeDayOfMonth(index, excludeDayOfMonthViewData.month, dayIndex)
            },
            onDeleteExcludeDay = {
              viewModel.removeExcludeDayOfMonth(index)
            }
          )
        }
      }
      IconButton(
        modifier = Modifier
          .size(40.dp)
          .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
          .clip(CircleShape)
          .testTag(stringResource(id = R.string.testTag_add_exclude_day_of_month_button)),
        enabled = viewModel.enabledAddExcludeDayButton.value,
        onClick = {
          viewModel.appendExcludeDayOfMonth()
        },
        colors = IconButtonDefaults.iconButtonColors().copy(
          contentColor = MaterialTheme.colorScheme.primary,
          containerColor = Color.Transparent
        )
      ) {
        Icon(
          imageVector = Icons.Filled.Add,
          contentDescription = "Add Schedule",
        )
      }
      FilledTonalButton(
        modifier = Modifier
          .padding(top = 16.dp, bottom = 32.dp)
          .width(120.dp)
          .height(40.dp),
        onClick = {
          navController.popBackStack()
        },
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.filledTonalButtonColors().copy(
          containerColor = MaterialTheme.colorScheme.primaryContainer,
        )
      ) {
        Text(
          text = stringResource(id = R.string.text_exclude_day_of_month_apply_button),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary
        )
      }
    }
  }
}

@Composable
fun MonthAndDayDropDown(
  monthIndex: Int,
  dayIndex: Int,
  modifier: Modifier = Modifier,
  onMonthSelected: (Int,Int) -> Unit,
  onDaySelected: (Int) -> Unit,
  onDeleteExcludeDay: () -> Unit
) {
  val months = (1..12).map{"$it 月"}
  val days = (1..getMaxDateOfMonth(monthIndex + 1)).map{"$it 日"}
  var monthExpanded by remember { mutableStateOf(false) }
  var dayExpanded by remember { mutableStateOf(false) }

  Surface(
    modifier = modifier,
    color = Color.Transparent,
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(
        modifier = Modifier.weight(1f),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        CustomDropDown(
          modifier = Modifier
            .weight(0.5f),
          items = months,
          selectedText = months[monthIndex],
          expanded = monthExpanded,
          dropDownColor = Color.Transparent,
          textColor = MaterialTheme.colorScheme.primary,
          indicatorColor = Color.Transparent,
          showTrailingIcon = false,
          useIntrinsicWidth = false,
          onItemSelected = { selectedMonthIndex: Int ->
            val adjustDayIndex = getMaxDateIndexOfMonth(selectedMonthIndex + 1, dayIndex)
            onMonthSelected(selectedMonthIndex, adjustDayIndex)
          },
          onExpandedChange = { monthExpanded = !monthExpanded },
          onDismissRequest = { monthExpanded = false },
          testTag = stringResource(id = R.string.testTag_month_of_exclude_day_of_month_dropdown)
        )
        CustomDropDown(
          modifier = Modifier
            .weight(0.5f),
          items = days,
          selectedText = days[dayIndex],
          expanded = dayExpanded,
          dropDownColor = Color.Transparent,
          textColor = MaterialTheme.colorScheme.primary,
          indicatorColor = Color.Transparent,
          showTrailingIcon = false,
          useIntrinsicWidth = false,
          onItemSelected = { selectedDayIndex: Int ->
            onDaySelected(selectedDayIndex)
          },
          onExpandedChange = { dayExpanded = !dayExpanded },
          onDismissRequest = { dayExpanded = false },
          testTag = stringResource(id = R.string.testTag_day_of_exclude_day_of_month_dropdown)
        )
      }
      IconButton(
        modifier = Modifier
          .size(28.dp)
          .testTag(stringResource(id = R.string.testTag_delete_exclude_day_of_month_button)),
        onClick = {
          onDeleteExcludeDay()
        },
        colors = IconButtonDefaults.iconButtonColors().copy(
          contentColor = MaterialTheme.colorScheme.error,
          containerColor = Color.Transparent
        )
      ) {
        Icon(
          imageVector = Icons.Filled.Delete,
          contentDescription = "Delete",
        )
      }
    }
  }
}

fun getMaxDateOfMonth(month: Int): Int {
  return when(month) {
    1, 3, 5, 7, 8, 10, 12 -> 31
    4, 6, 9, 11 -> 30
    2 -> 29
    else -> throw IllegalArgumentException("Invalid month: $month")
  }
}
fun getMaxDateIndexOfMonth(month: Int, currentDayIndex: Int): Int {
  val maxIndex = when(month) {
    1, 3, 5, 7, 8, 10, 12 -> 30
    4, 6, 9, 11 -> 29
    2 -> 28
    else -> throw IllegalArgumentException("Invalid month: $month")
  }
  if(currentDayIndex > maxIndex) {
    return maxIndex
  }
  return currentDayIndex
}
