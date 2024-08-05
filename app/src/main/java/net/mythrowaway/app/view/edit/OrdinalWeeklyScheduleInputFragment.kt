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
import net.mythrowaway.app.databinding.InputNumOfWeekBinding
import net.mythrowaway.app.usecase.dto.OrdinalWeeklyScheduleDTO
import net.mythrowaway.app.viewmodel.edit.EditTrashViewModel
import javax.inject.Inject

class OrdinalWeeklyScheduleInputFragment: Fragment(), AdapterView.OnItemSelectedListener {
  private lateinit var _ordinalWeeklyInputBinding: InputNumOfWeekBinding

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
    _ordinalWeeklyInputBinding = InputNumOfWeekBinding.inflate(inflater, container, false)
    return _ordinalWeeklyInputBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    Log.d(this.javaClass.simpleName, "onViewCreated ${arguments?.getInt(POSITION)}")
    val position = requireArguments().getInt(POSITION)
    val ordinalWeeklyScheduleDTO = _editTrashViewModel.scheduleDTOList.value.get(position) as OrdinalWeeklyScheduleDTO
    val orderOfWeek = ordinalWeeklyScheduleDTO.ordinal
    val dayOfWeek = ordinalWeeklyScheduleDTO.dayOfWeek

    _ordinalWeeklyInputBinding.numOfWeekList.setSelection(orderOfWeek)
    _ordinalWeeklyInputBinding.numOfWeekWeekdayList.setSelection(dayOfWeek)
    _ordinalWeeklyInputBinding.numOfWeekList.onItemSelectedListener  = this
    _ordinalWeeklyInputBinding.numOfWeekWeekdayList.onItemSelectedListener  = this
  }

  companion object {
    private const val POSITION = "position"
    fun getInstance(position: Int): OrdinalWeeklyScheduleInputFragment {
      val fragment = OrdinalWeeklyScheduleInputFragment()
      fragment.arguments = Bundle().apply {
        putInt(POSITION, position)
      }
      return fragment
    }
  }

  override fun onItemSelected(parent: AdapterView<*>?, view: View?, index: Int, id: Long) {
    Log.d(this.javaClass.simpleName, "onItemSelected ${parent?.id} ${index}")
    val position = requireArguments().getInt(POSITION)
    if(parent?.id == _ordinalWeeklyInputBinding.numOfWeekList.id) {
      _editTrashViewModel.updateOrdinalWeeklyScheduleValue(position, index, _ordinalWeeklyInputBinding.numOfWeekWeekdayList.selectedItemPosition)
    } else if(parent?.id == _ordinalWeeklyInputBinding.numOfWeekWeekdayList.id){
      _editTrashViewModel.updateOrdinalWeeklyScheduleValue(position, _ordinalWeeklyInputBinding.numOfWeekList.selectedItemPosition,index)
    }
  }

  override fun onNothingSelected(p0: AdapterView<*>?) {
    TODO("Not yet implemented")
  }
}