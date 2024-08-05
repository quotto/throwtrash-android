package net.mythrowaway.app.view.edit

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import net.mythrowaway.app.R

interface OnExcludeDatePickerDialogListener {
    fun notifySelectedValue(targetIndex: Int, selectedMonth: Int, selectedDate: Int)
}

class ExcludeDatePickerDialogFragment() : DialogFragment() {
    private var mSelectedMonth = 1
    private var mSelectedDate = 1
    private var mIndex: Int = -1
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        savedInstanceState?.also {
            mSelectedMonth = it.getInt(EXTRA_MONTH)
            mSelectedDate = it.getInt(EXTRA_DATE)
            mIndex = it.getInt(EXTRA_INDEX)
        } ?: arguments?.let {
            mSelectedMonth = it.getInt(EXTRA_MONTH)
            mSelectedDate = it.getInt(EXTRA_DATE)
            mIndex = it.getInt(EXTRA_INDEX)
        }

        val inflater = activity?.layoutInflater
        val dialogView = inflater?.inflate(R.layout.exclude_date_picker,null)
        val builder = AlertDialog.Builder(context)

        builder.setView(dialogView)
        builder.setTitle("日付の選択")
        builder.setPositiveButton("OK") { _,_ ->
            setFragmentResult(FRAGMENT_RESULT_KEY, Bundle().apply {
                putInt(FRAGMENT_RESULT_TARGET_INDEX, mIndex)
                putInt(FRAGMENT_RESULT_SELECTED_MONTH, mSelectedMonth)
                putInt(FRAGMENT_RESULT_SELECTED_DATE, mSelectedDate)
                }
            )
        }

        builder.setNegativeButton("Cancel") {_, _->

        }

        val npMonth = dialogView?.findViewById<NumberPicker>(R.id.picker_exclude_month)
        val npDate = dialogView?.findViewById<NumberPicker>(R.id.picker_exclude_date)

        npMonth?.minValue = 1
        npMonth?.maxValue = 12
        npMonth?.value = mSelectedMonth
        npMonth?.setOnValueChangedListener { _, _, newVal ->
            mSelectedMonth = newVal

            val maxDate = getMaxDate(mSelectedMonth)
            npDate?.maxValue = maxDate
            if(mSelectedDate > maxDate) {
                // Number Picker Dialog上の数値は自動で切り替わる
                mSelectedDate = maxDate
            }
        }

        npDate?.minValue = 1
        npDate?.maxValue = getMaxDate(mSelectedMonth)
        npDate?.value =  mSelectedDate
        npDate?.setOnValueChangedListener { _, _, newVal ->
            mSelectedDate = newVal
        }

        return builder.create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(EXTRA_INDEX,mIndex)
        outState.putInt(EXTRA_MONTH,mSelectedMonth)
        outState.putInt(EXTRA_DATE,mSelectedDate)
    }

    private fun getMaxDate(month: Int): Int {
        return if(listOf(1,3,5,7,8,10,12).contains(month)) 31 else if(listOf(4,6,9,11).contains(month)) 30 else 29
    }

    companion object {
        fun getInstance(index: Int, month: Int = 1, date: Int = 1): ExcludeDatePickerDialogFragment {
            return ExcludeDatePickerDialogFragment().apply {
                arguments = Bundle()
                arguments?.putInt(EXTRA_INDEX, index)
                arguments?.putInt(EXTRA_MONTH, month)
                arguments?.putInt(EXTRA_DATE, date)
            }
        }

        private const val EXTRA_INDEX = "EXTRA_INDEX"
        private const val EXTRA_MONTH = "EXTRA_MONTH"
        private const val EXTRA_DATE = "EXTRA_DATE"

        const val FRAGMENT_RESULT_KEY = "selectedExcludeDate"
        const val FRAGMENT_RESULT_TARGET_INDEX = "targetIndex"
        const val FRAGMENT_RESULT_SELECTED_MONTH = "selectedMonth"
        const val FRAGMENT_RESULT_SELECTED_DATE = "selectedDate"
    }
}