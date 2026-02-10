package net.mythrowaway.app.module.trash.presentation.view.edit

import android.util.Log
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import net.mythrowaway.app.R
import net.mythrowaway.app.module.trash.entity.trash.TrashType
import net.mythrowaway.app.ui.theme.TrashColor
import net.mythrowaway.app.module.trash.dto.IntervalWeeklyScheduleDTO
import net.mythrowaway.app.module.trash.dto.MonthlyScheduleDTO
import net.mythrowaway.app.module.trash.dto.OrdinalWeeklyScheduleDTO
import net.mythrowaway.app.module.trash.dto.ScheduleDTO
import net.mythrowaway.app.module.trash.dto.WeeklyScheduleDTO
import net.mythrowaway.app.module.trash.presentation.view_model.edit.EditTrashViewModel
import net.mythrowaway.app.module.trash.presentation.view_model.edit.LoadStatus
import net.mythrowaway.app.module.trash.presentation.view_model.edit.TrashDeleteStatus
import net.mythrowaway.app.module.trash.presentation.view_model.edit.TrashListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashListScreen(
  editTrashViewModel: EditTrashViewModel,
  trashListViewModel: TrashListViewModel,
  navController: NavHostController
) {
  val hostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()
  val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
  val failedLoadMessage = stringResource(id = R.string.message_failed_load_trash_list)
  val completeDeleteMessage = stringResource(id = R.string.message_complete_delete_trash)
  val failedDeleteMessage = stringResource(id = R.string.message_failed_delete_trash)

  LaunchedEffect(editTrashViewModel.loadStatus.value) {
    when (editTrashViewModel.loadStatus.value) {
      LoadStatus.SUCCESS -> {
        navController.navigate(EditScreenType.Edit.name)
        editTrashViewModel.resetLoadStatus()
      }
      LoadStatus.ERROR -> {
        hostState.showSnackbar(failedLoadMessage, duration = SnackbarDuration.Long)
        editTrashViewModel.resetLoadStatus()
      }
      else -> {
        Log.d(javaClass.simpleName, "no load status")
      }
    }
  }

  LaunchedEffect(trashListViewModel.deleteStatus.value) {
    when (trashListViewModel.deleteStatus.value) {
      TrashDeleteStatus.SUCCESS -> {
        hostState.showSnackbar(completeDeleteMessage, duration = SnackbarDuration.Long)
        trashListViewModel.resetDeleteStatus()
      }
      TrashDeleteStatus.FAILURE -> {
        hostState.showSnackbar(failedDeleteMessage, duration = SnackbarDuration.Long)
        trashListViewModel.resetDeleteStatus()
      }
      else -> {
        Log.d(javaClass.simpleName, "no delete status")
      }
    }
  }

  Scaffold (
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = stringResource(id = R.string.text_title_trash_list),
            style = MaterialTheme.typography.titleMedium
          )
        },
        navigationIcon = {
          IconButton(
            modifier = Modifier.size(24.dp),
            onClick = {
              dispatcher?.onBackPressed()
            }
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
            )
          }
        }
      )
    },
    snackbarHost = {
      SnackbarHost(
      hostState,
      snackbar = { data ->
        Snackbar(
          snackbarData = data,
          containerColor =
            if(
              trashListViewModel.deleteStatus.value == TrashDeleteStatus.SUCCESS ||
              editTrashViewModel.loadStatus.value == LoadStatus.SUCCESS
              ) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.errorContainer,
          contentColor =
            if(
              trashListViewModel.deleteStatus.value == TrashDeleteStatus.SUCCESS ||
              editTrashViewModel.loadStatus.value == LoadStatus.SUCCESS
            ) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.error,
        )
      }
    )
    },
  ){
    paddingValues ->
    Column(
      modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize()
    ) {
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
        ) {
          if (trashListViewModel.trashList.value.isEmpty()) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = stringResource(id = R.string.text_trash_list_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          } else {
            trashListViewModel.trashList.value.map { trashDTO  ->
              TrashRow(
                modifier = Modifier
                  .padding(top = 4.dp, bottom = 4.dp, start = 8.dp, end = 8.dp),
                trashName = if(trashDTO.type == TrashType.OTHER) trashDTO.displayName else trashDTO.type.getTrashText() ,
                schedules = trashDTO.scheduleDTOList.map { toScheduleText(it) },
                onClickDeleteButton = {
                  scope.launch {
                    trashListViewModel.deleteTrash(trashDTO.id)
                  }
                },
                backgroundColor = TrashColor.getColor(trashDTO.type),
                onSelectDataRow = {
                  scope.launch {
                    editTrashViewModel.setTrash(trashDTO.id)
                  }
                }
              )
            }
          }
        }
      }
      FilledTonalButton(
        modifier = Modifier
          .padding(horizontal = 16.dp, vertical = 12.dp)
          .fillMaxWidth()
          .height(40.dp)
          .testTag(stringResource(R.string.testTag_common_exclude_day_button)),
        onClick = {
          navController.navigate(EditScreenType.CommonExcludeDayOfMonth.name)
        },
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.filledTonalButtonColors().copy(
          containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
      ) {
        Text(
          text = stringResource(id = R.string.text_common_exclude_day_button),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary
        )
      }
    }
  }
}

@Composable
fun TrashRow(
  modifier: Modifier = Modifier,
  trashName : String,
  schedules: List<String>,
  onSelectDataRow: () -> Unit,
  onClickDeleteButton: () -> Unit,
  backgroundColor: Color = MaterialTheme.colorScheme.surface,
) {
  Box(
    modifier =
    modifier
      .fillMaxWidth()
      .clip(MaterialTheme.shapes.extraSmall)
      .background(backgroundColor)
      .border(
        width = 0.5.dp,
        color = MaterialTheme.colorScheme.onSurface,
        shape = MaterialTheme.shapes.extraSmall
      )
      .clickable(true) {
        onSelectDataRow()
      }
    .testTag(stringResource(id = R.string.testTag_trash_list_item))
  ) {
    Row(
      modifier = Modifier.padding(4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(
        modifier = Modifier
          .weight(1f)
//          .testTag(stringResource(id = R.string.testTag_trash_list_item))
      ) {
        Text(
          text = trashName,
          style = MaterialTheme.typography.titleMedium,
          color = Color.White
        )
        Text(
          text = schedules.joinToString(separator = ","),
          style = MaterialTheme.typography.labelSmall,
          color = Color.White
        )
      }
      IconButton(
        modifier = Modifier.testTag("DeleteTrashButton"),
        onClick = { onClickDeleteButton() },
        colors = IconButtonColors(
          contentColor = MaterialTheme.colorScheme.error,
          disabledContentColor = MaterialTheme.colorScheme.inverseOnSurface,
          containerColor = Color.Transparent,
          disabledContainerColor = Color.Transparent,
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

@Composable
fun toScheduleText(scheduleDTO: ScheduleDTO): String {
  return when (scheduleDTO) {
    is WeeklyScheduleDTO -> {
      val dayOfWeekList = stringArrayResource(id = R.array.list_weekday_select)
      val index = scheduleDTO.dayOfWeek
      "毎週${dayOfWeekList[index]}"
    }
    is MonthlyScheduleDTO -> {
     "毎月${scheduleDTO.dayOfMonth}日"
    }
    is OrdinalWeeklyScheduleDTO -> {
      val dayOfWeekList = stringArrayResource(id = R.array.list_weekday_select)
      val index = scheduleDTO.dayOfWeek
      val ordinalIndex = scheduleDTO.ordinal
      "第${ordinalIndex}${dayOfWeekList[index]}"
    }
    is IntervalWeeklyScheduleDTO -> {
      val dayOfWeekList = stringArrayResource(id = R.array.list_weekday_select)
      val index = scheduleDTO.dayOfWeek
      "${scheduleDTO.interval}週間ごとの${dayOfWeekList[index]}"
    }
    else -> {
      throw IllegalArgumentException("不正なスケジュールです")
    }
  }
}
