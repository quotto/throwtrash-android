package net.mythrowaway.app.domain.trash.presentation.view.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import net.mythrowaway.app.domain.trash.presentation.view_model.edit.EditTrashViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExcludeDayOfMonthScreen(
  viewModel: EditTrashViewModel,
  trashTypeName: String,
  navController: NavHostController
) {
  Scaffold (
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "$trashTypeName の除外日設定",
            style = MaterialTheme.typography.titleSmall,
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
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(innerPadding),
      contentAlignment = Alignment.TopCenter
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
      ) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
        ) {
          viewModel.excludeDayOfMonthViewDataList.value.forEachIndexed { index, excludeDayOfMonthViewData ->
            MonthAndDayDropDown(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(MaterialTheme.shapes.medium),
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
      }
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
      ) {
        SmallFloatingActionButton(
          modifier = Modifier.padding(16.dp).clickable(
            enabled = viewModel.enabledAddExcludeDayButton.value,
            onClick = {}
          ),
          onClick = {
            viewModel.appendExcludeDayOfMonth()
          },
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
          Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add Schedule",
          )
        }
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
    color = MaterialTheme.colorScheme.secondaryContainer,
  ) {
    Row(
      modifier = Modifier.padding(8.dp),
      horizontalArrangement = Arrangement.Start,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      IconButton(
        modifier = Modifier.background(Color.Transparent),
        onClick = {
          onDeleteExcludeDay()
        },
        colors = IconButtonColors(
          contentColor = MaterialTheme.colorScheme.error,
          disabledContentColor = MaterialTheme.colorScheme.inverseOnSurface,
          containerColor = Color.Transparent,
          disabledContainerColor = MaterialTheme.colorScheme.background,
        )
      ) {
        Icon(
          imageVector = Icons.Filled.Delete,
          contentDescription = "Delete",
        )
      }
      CustomDropDown(
        modifier = Modifier
          .padding(end = 8.dp)
          .weight(0.5f),
        items = months,
        selectedText = months[monthIndex],
        expanded = monthExpanded,
        onItemSelected = { selectedMonthIndex: Int ->
          val adjustDayIndex = getMaxDateIndexOfMonth(selectedMonthIndex + 1, dayIndex)
          onMonthSelected(selectedMonthIndex, adjustDayIndex)
        },
        onExpandedChange = { monthExpanded = !monthExpanded },
        onDismissRequest = { monthExpanded = false }
      )
      CustomDropDown(
        modifier = Modifier
          .weight(0.5f)
          .padding(end = 8.dp),
        items = days,
        selectedText = days[dayIndex],
        expanded = dayExpanded,
        onItemSelected = { selectedDayIndex: Int ->
          onDaySelected(selectedDayIndex)
        },
        onExpandedChange = { dayExpanded = !dayExpanded },
        onDismissRequest = { dayExpanded = false }
      )
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

