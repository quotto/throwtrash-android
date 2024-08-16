package net.mythrowaway.app.view.edit

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import net.mythrowaway.app.databinding.FragmentEditInputEvweekBinding
import net.mythrowaway.app.usecase.dto.IntervalWeeklyScheduleDTO
import net.mythrowaway.app.viewmodel.edit.EditTrashViewModel
import java.util.Calendar
import javax.inject.Inject

class IntervalWeeklyScheduleInputFragment: Fragment(), AdapterView.OnItemSelectedListener {
  private lateinit var _intervalWeeklyScheduleInputBinding: FragmentEditInputEvweekBinding

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
    _intervalWeeklyScheduleInputBinding = FragmentEditInputEvweekBinding.inflate(inflater, container, false)
    return _intervalWeeklyScheduleInputBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    Log.d(this.javaClass.simpleName, "onViewCreated ${arguments?.getInt(POSITION)}")
    val position = requireArguments().getInt(POSITION)
//    val intervalWeeklyScheduleDTO = _editTrashViewModel.scheduleDTOList.value[position] as IntervalWeeklyScheduleDTO
//    _intervalWeeklyScheduleInputBinding.evweekDateText.text = intervalWeeklyScheduleDTO.startDate
//    _intervalWeeklyScheduleInputBinding.evweekIntervalList.setSelection(intervalWeeklyScheduleDTO.interval)
//    _intervalWeeklyScheduleInputBinding.evweekWeekdayList.setSelection(intervalWeeklyScheduleDTO.dayOfWeek)
    _intervalWeeklyScheduleInputBinding.evweekDateText.setOnClickListener {
      val dateText = it as TextView

      val calendar = Calendar.getInstance()
      var year = calendar.get(Calendar.YEAR)
      var month = calendar.get(Calendar.MONTH)
      var dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

      if(dateText.text.isNotEmpty()) {
        Log.d(this.javaClass.simpleName,"Date text->${dateText.text}")
        val ymd = dateText.text.split("-")
        year = ymd[0].toInt()
        //画面上は実際の月なので-1する
        month = ymd[1].toInt()-1
        dayOfMonth = ymd[2].toInt()
      }

      val datePickerDialog = DatePickerDialog(
        requireContext(),
        { _, year, month, dayOfMonth ->
          dateText.text = DATE_FORMAT.format(year,month+1,dayOfMonth)
        },
        year,
        month,
        dayOfMonth
      )
      datePickerDialog.show()
    }
    _intervalWeeklyScheduleInputBinding.evweekDateText.addTextChangedListener { text ->
      _editTrashViewModel.updateIntervalWeeklyScheduleValue(
        requireArguments().getInt(POSITION),
        text.toString().replace("/", "-"),
        _intervalWeeklyScheduleInputBinding.evweekWeekdayList.selectedItemPosition,
        _intervalWeeklyScheduleInputBinding.evweekIntervalList.selectedItemPosition
      )
    }
    _intervalWeeklyScheduleInputBinding.evweekWeekdayList.onItemSelectedListener  = this
    _intervalWeeklyScheduleInputBinding.evweekIntervalList.onItemSelectedListener  = this
  }

  companion object {
    private const val POSITION = "position"
    private const val DATE_FORMAT = "%d/%02d/%02d"
    fun getInstance(position: Int): IntervalWeeklyScheduleInputFragment {
      val fragment = IntervalWeeklyScheduleInputFragment()
      fragment.arguments = Bundle().apply {
        putInt(POSITION, position)
      }
      return fragment
    }
  }

  override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    if(parent?.id == _intervalWeeklyScheduleInputBinding.evweekWeekdayList.id) {
      _editTrashViewModel.updateIntervalWeeklyScheduleValue(
        requireArguments().getInt(POSITION),
        _intervalWeeklyScheduleInputBinding.evweekDateText.text.toString(),
        position,
        _intervalWeeklyScheduleInputBinding.evweekIntervalList.selectedItemPosition
      )
    } else if(parent?.id == _intervalWeeklyScheduleInputBinding.evweekIntervalList.id){
      _editTrashViewModel.updateIntervalWeeklyScheduleValue(
        requireArguments().getInt(POSITION),
        _intervalWeeklyScheduleInputBinding.evweekDateText.text.toString(),
        _intervalWeeklyScheduleInputBinding.evweekWeekdayList.selectedItemPosition,
        position
      )
    }
  }

  override fun onNothingSelected(p0: AdapterView<*>?) {
    TODO("Not yet implemented")
  }
}