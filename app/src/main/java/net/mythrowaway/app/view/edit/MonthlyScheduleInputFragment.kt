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
import net.mythrowaway.app.databinding.InputMonthBinding
import net.mythrowaway.app.usecase.dto.MonthlyScheduleDTO
import net.mythrowaway.app.viewmodel.edit.EditTrashViewModel
import javax.inject.Inject

class MonthlyScheduleInputFragment: Fragment(), AdapterView.OnItemSelectedListener {
  private lateinit var _monthlyScheduleInputBinding: InputMonthBinding

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
    _monthlyScheduleInputBinding = InputMonthBinding.inflate(inflater, container, false)
    return _monthlyScheduleInputBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    Log.d(this.javaClass.simpleName, "onViewCreated ${arguments?.getInt(POSITION)}")
    val position = requireArguments().getInt(POSITION)
//    _monthlyScheduleInputBinding.monthDateList.setSelection((_editTrashViewModel.scheduleDTOList.value.get(position) as MonthlyScheduleDTO).dayOfMonth)
    _monthlyScheduleInputBinding.monthDateList.onItemSelectedListener  = this
  }

  companion object {
    private const val POSITION = "position"
    fun getInstance(position: Int): MonthlyScheduleInputFragment {
      val fragment = MonthlyScheduleInputFragment()
      fragment.arguments = Bundle().apply {
        putInt(POSITION, position)
      }
      return fragment
    }
  }

  override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    _editTrashViewModel.updateMonthlyScheduleValue(requireArguments().getInt(POSITION), position)
  }

  override fun onNothingSelected(p0: AdapterView<*>?) {
    TODO("Not yet implemented")
  }
}