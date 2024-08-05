package net.mythrowaway.app.view.edit

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import net.mythrowaway.app.R
import net.mythrowaway.app.databinding.ActivityEditExcludeDayBinding
import net.mythrowaway.app.viewmodel.edit.EditTrashViewModel
import net.mythrowaway.app.viewmodel.edit.ExcludeDayMessage
import java.util.Calendar
import javax.inject.Inject

class ExcludeDayFragment: Fragment() {
  private lateinit var _excludeDayBinding: ActivityEditExcludeDayBinding

  @Inject
  lateinit var editTrashViewModelFactory: EditTrashViewModel.Factory

  private val _editTrashViewModel: EditTrashViewModel by lazy {
    ViewModelProvider(requireActivity(), editTrashViewModelFactory).get(EditTrashViewModel::class.java)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _excludeDayBinding = ActivityEditExcludeDayBinding.inflate(inflater, container, false)
    return _excludeDayBinding.root
  }
  override fun onAttach(context: Context) {
    super.onAttach(context)
    (activity as NeoEditActivity).editComponent.inject(this)
  }
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    _excludeDayBinding.trashName.text = _editTrashViewModel.trashTypeName
//    _editTrashViewModel.excludeDayDTOList.forEach {
//      appendDate(it.month,it.dayOfMonth)
//    }

    _excludeDayBinding.buttonAddExcludeDate.setOnClickListener {
      lifecycleScope.launch {
        _editTrashViewModel.appendExcludeDayOfMonth()
      }
    }

    lifecycleScope.launch {
//        _editTrashViewModel.excludeDayMessage.collect {
//          when(it) {
//            is ExcludeDayMessage.Add -> {
//              val appendedExcludeDay = _editTrashViewModel.excludeDayDTOList.last()
//              appendDate(appendedExcludeDay.month,appendedExcludeDay.dayOfMonth)
//            }
//            is ExcludeDayMessage.Remove -> {
//              _excludeDayBinding.listExcludeDate.removeViewAt(it.position)
//              for (i in it.position until _excludeDayBinding.listExcludeDate.childCount - 1) {
//                setRowColor(_excludeDayBinding.listExcludeDate.getChildAt(i),i)
//              }
//            }
//            is ExcludeDayMessage.Update -> {
//              val excludeDateInputText = _excludeDayBinding.listExcludeDate[it.position]
//              val textExcludeDate = excludeDateInputText.findViewById<TextView>(R.id.textExcludeDate)
//              textExcludeDate.text = "${it.month} 月 ${it.dayOfMonth} 日"
//            }
//            else -> {}
//          }
//      }
    }
//    lifecycleScope.launch {
//      _editTrashViewModel.enabledAddExcludeDayButton.collect {
//        _excludeDayBinding.buttonAddExcludeDate.visibility = if(it) View.VISIBLE else View.INVISIBLE
//      }
//    }

    childFragmentManager.setFragmentResultListener(
      ExcludeDatePickerDialogFragment.FRAGMENT_RESULT_KEY,
      viewLifecycleOwner
    ) { _, bundle ->
      Log.d(javaClass.simpleName,"onFragmentResult: ${ExcludeDatePickerDialogFragment.FRAGMENT_RESULT_KEY}")
      val targetIndex = bundle.getInt(ExcludeDatePickerDialogFragment.FRAGMENT_RESULT_TARGET_INDEX)
      val selectedMonth = bundle.getInt(ExcludeDatePickerDialogFragment.FRAGMENT_RESULT_SELECTED_MONTH)
      val selectedDate = bundle.getInt(ExcludeDatePickerDialogFragment.FRAGMENT_RESULT_SELECTED_DATE)
      lifecycleScope.launch {
        _editTrashViewModel.updateExcludeDayOfMonth(targetIndex, selectedMonth, selectedDate)
      }
    }
  }
  private fun appendDate(month:Int=1, date:Int=1) {
    val excludeDateInputText = layoutInflater.inflate(R.layout.exclude_date,null)

    excludeDateInputText.findViewById<ImageButton>(R.id.buttonRemoveExcludeDate)
      .setOnClickListener {
        lifecycleScope.launch {
          val position = _excludeDayBinding.listExcludeDate.indexOfChild(excludeDateInputText)
          _editTrashViewModel.removeExcludeDayOfMonth(position)
        }
      }

    val textExcludeDate = excludeDateInputText.findViewById<TextView>(R.id.textExcludeDate)
    textExcludeDate.text = "$month 月 $date 日"
    textExcludeDate.tag = Calendar.getInstance().timeInMillis.toString()
//    textExcludeDate.setOnClickListener {
//      val index = _excludeDayBinding.listExcludeDate.indexOfChild(excludeDateInputText)
//      _editTrashViewModel.excludeDayDTOList.get(index)?.let {
//        val dialog = ExcludeDatePickerDialogFragment.getInstance(index, it.month, it.dayOfMonth)
//        dialog.show(childFragmentManager, "日付の選択")
//      }
//    }

    setRowColor(excludeDateInputText,_excludeDayBinding.listExcludeDate.childCount)
    _excludeDayBinding.listExcludeDate.addView(
      excludeDateInputText,_excludeDayBinding.listExcludeDate.childCount - 1
    )
  }
  private fun setRowColor(view: View,index:Int) {
    if (index%2 == 0) view.setBackgroundResource(R.color.tableRowEven) else view.setBackgroundColor(
      Color.WHITE)
  }

}