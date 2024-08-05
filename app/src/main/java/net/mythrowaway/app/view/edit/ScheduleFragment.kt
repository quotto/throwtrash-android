package net.mythrowaway.app.view.edit

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import net.mythrowaway.app.R
import net.mythrowaway.app.databinding.FragmentEditInputBinding
import net.mythrowaway.app.usecase.dto.IntervalWeeklyScheduleDTO
import net.mythrowaway.app.usecase.dto.MonthlyScheduleDTO
import net.mythrowaway.app.usecase.dto.OrdinalWeeklyScheduleDTO
import net.mythrowaway.app.usecase.dto.WeeklyScheduleDTO
import net.mythrowaway.app.viewmodel.edit.EditTrashViewModel
import javax.inject.Inject

class ScheduleFragment: Fragment() {
  private lateinit var fragmentEditInputBinding: FragmentEditInputBinding
  @Inject
  lateinit var editTrashViewModelFactory: EditTrashViewModel.Factory
  private val _editTrashViewModel: EditTrashViewModel by lazy {
    ViewModelProvider(requireActivity(), editTrashViewModelFactory).get(EditTrashViewModel::class.java)
  }


  override fun onAttach(context: Context) {
    super.onAttach(context)
    (activity as NeoEditActivity).editComponent.inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    Log.d(this.javaClass.simpleName, "onCreateView")
    fragmentEditInputBinding = FragmentEditInputBinding.inflate(inflater, container, false)
    return fragmentEditInputBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)


    fragmentEditInputBinding.removeScheduleButton.setOnClickListener {
      lifecycleScope.launch {
        _editTrashViewModel.removeSchedule(requireArguments().getInt(POSITION))
      }
    }


    lifecycleScope.launch {
      viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//        _editTrashViewModel.enabledRemoveButton.collect {
//          fragmentEditInputBinding.removeScheduleButton.visibility =
//            if (it) View.VISIBLE else View.INVISIBLE
//        }
      }
    }

    refreshSchedule()

    listOf(
      fragmentEditInputBinding.toggleEveryweek,
      fragmentEditInputBinding.toggleEveryMonth,
      fragmentEditInputBinding.toggleNumOfWeek,
      fragmentEditInputBinding.toggleEvWeek
    ).forEach { clickedButton ->
      clickedButton.setOnCheckedChangeListener { _, isChecked ->
        if (isChecked) {
          Log.d(this.javaClass.simpleName, "ToggleButtons.checked: ${clickedButton.tag}")
          val scheduleType = when (clickedButton.id) {
            R.id.toggleEveryweek -> "Weekly"
            R.id.toggleEveryMonth -> "Monthly"
            R.id.toggleNumOfWeek -> "OrdinalWeekly"
            R.id.toggleEvWeek -> "IntervalWeekly"
            else -> {
              throw IllegalArgumentException("Unknown schedule type")
            }
          }
          lifecycleScope.launch {
            _editTrashViewModel.changeScheduleType(
              requireArguments().getInt(POSITION),
              scheduleType
            )
          }
        }
      }
    }
  }

  public fun updatePosition(position: Int) {
    Log.d(javaClass.simpleName, "updatePosition from ${requireArguments().getInt(POSITION)} to $position")
    requireArguments().putInt(POSITION, position)
    Log.d(javaClass.simpleName, "updatePosition: ${arguments?.getInt(POSITION)}")
  }

  fun refreshSchedule() {
    val position = requireArguments().getInt(POSITION)
    when (_editTrashViewModel.scheduleDTOList.value[position]) {
      is WeeklyScheduleDTO -> {
        toggleButtons(R.id.toggleEveryweek)
        childFragmentManager.beginTransaction().replace(
          R.id.scheduleInput,
          WeeklyScheduleInputFragment.getInstance(
            position
          )
        ).commit()
      }

      is MonthlyScheduleDTO -> {
        toggleButtons(R.id.toggleEveryMonth)
        childFragmentManager.beginTransaction().replace(
          R.id.scheduleInput,
          MonthlyScheduleInputFragment.getInstance(
            position
          )
        ).commit()
      }

      is OrdinalWeeklyScheduleDTO -> {
        toggleButtons(R.id.toggleNumOfWeek)
        childFragmentManager.beginTransaction().replace(
          R.id.scheduleInput,
          OrdinalWeeklyScheduleInputFragment.getInstance(
            position
          )
        ).commit()
      }

      is IntervalWeeklyScheduleDTO -> {
        toggleButtons(R.id.toggleEvWeek)
        childFragmentManager.beginTransaction().replace(
          R.id.scheduleInput,
          IntervalWeeklyScheduleInputFragment.getInstance(
            position
          )
        ).commit()
      }

      else -> {
        throw IllegalArgumentException("Unknown schedule type")
      }
    }
  }

  private fun toggleButtons(checkedId: Int) {
    Log.d(this.javaClass.simpleName, "toggleButtons: $checkedId")
    listOf(
      fragmentEditInputBinding.toggleEveryweek,
      fragmentEditInputBinding.toggleEveryMonth,
      fragmentEditInputBinding.toggleNumOfWeek,
      fragmentEditInputBinding.toggleEvWeek
    ).forEach {
      Log.d(this.javaClass.simpleName, "check: ${it.id}")
      it.isChecked = it.id == checkedId
      it.isClickable = it.id != checkedId
    }

  }

  companion object {
    @JvmStatic
    fun newInstance(position: Int) =
      ScheduleFragment().apply {
        arguments = Bundle().apply {
          putInt(POSITION, position)
        }
      }

    private const val POSITION = "position"
  }
}