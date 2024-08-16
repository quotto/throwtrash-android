package net.mythrowaway.app.view.edit

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import net.mythrowaway.app.databinding.InputWeekdayBinding
import net.mythrowaway.app.usecase.dto.WeeklyScheduleDTO
import net.mythrowaway.app.viewmodel.edit.EditTrashViewModel
import javax.inject.Inject

class WeeklyScheduleInputFragment: Fragment(), AdapterView.OnItemSelectedListener {
  private lateinit var _weeklyScheduleInputBinding: InputWeekdayBinding

  @Inject
  lateinit var editTrashViewModelFactory: EditTrashViewModel.Factory

  private val _editTrashViewModel: EditTrashViewModel by lazy {
    ViewModelProvider(requireActivity(), editTrashViewModelFactory).get(EditTrashViewModel::class.java)
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    Log.d(this.javaClass.simpleName, "onAttach ${arguments?.getInt(POSITION)}")
    (activity as NeoEditActivity).editComponent.inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _weeklyScheduleInputBinding = InputWeekdayBinding.inflate(inflater, container, false)
    return _weeklyScheduleInputBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    Log.d(this.javaClass.simpleName, "onViewCreated ${arguments?.getInt(POSITION)}")
    val position = requireArguments().getInt(POSITION)
//    val dayOfWeek = (_editTrashViewModel.scheduleDTOList.value.get(position) as WeeklyScheduleDTO).dayOfWeek
//    Log.d(this.javaClass.simpleName, "dayOfWeek: $dayOfWeek")
//    _weeklyScheduleInputBinding.weekdayWeekdayList.setSelection(dayOfWeek)
    _weeklyScheduleInputBinding.weekdayWeekdayList.onItemSelectedListener  = this
  }

  companion object {
    private const val POSITION = "position"
    fun getInstance(position: Int): WeeklyScheduleInputFragment {
      val fragment = WeeklyScheduleInputFragment()
      fragment.arguments = Bundle().apply {
        putInt(POSITION, position)
      }
      return fragment
    }
  }

  override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    _editTrashViewModel.updateWeeklyScheduleValue(requireArguments().getInt(POSITION), position)
  }

  override fun onNothingSelected(p0: AdapterView<*>?) {
    TODO("Not yet implemented")
  }
}