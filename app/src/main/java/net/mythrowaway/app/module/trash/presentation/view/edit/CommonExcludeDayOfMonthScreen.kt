package net.mythrowaway.app.module.trash.presentation.view.edit

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.mythrowaway.app.R
import net.mythrowaway.app.module.trash.presentation.view_model.edit.CommonExcludeDayOfMonthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonExcludeDayOfMonthScreen(
  viewModel: CommonExcludeDayOfMonthViewModel,
  onClose: () -> Unit
) {
  val coroutineScope = rememberCoroutineScope()
  Scaffold (
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Text(
            text = stringResource(id = R.string.text_title_common_exclude_day_of_month),
            style = MaterialTheme.typography.titleMedium,
          )
        },
        navigationIcon = {
          IconButton(
            modifier = Modifier.size(24.dp),
            onClick = onClose
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
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            .padding(12.dp)
        ) {
          Text(
            text = stringResource(id = R.string.text_common_exclude_day_notice_line1),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
          )
          Text(
            text = stringResource(id = R.string.text_common_exclude_day_notice_line2),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
          )
        }
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
          .testTag(stringResource(id = R.string.testTag_add_common_exclude_day_button)),
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
          coroutineScope.launch {
            viewModel.save()
            onClose()
          }
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
