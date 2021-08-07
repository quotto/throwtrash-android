package net.mythrowaway.app.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import net.mythrowaway.app.R

interface OnExcludeDatePickerDialogListener {
    fun notifySelectedValue(targetIndex: Int, selectedMonth: Int, selectedDate: Int)
}

class ExcludeDatePickerDialogFragment() : DialogFragment() {
    private lateinit var listener: OnExcludeDatePickerDialogListener
    private var mSelectedMonth = 1
    private var mSelectedDate = 1
    private var mIndex: Int = -1

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is OnExcludeDatePickerDialogListener) {
            listener = context
        }
    }

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
            listener.notifySelectedValue(mIndex, mSelectedMonth, mSelectedDate)
        }

        builder.setNegativeButton("Cancel") {_, _->

        }

        val np_month = dialogView?.findViewById<NumberPicker>(R.id.picker_exclude_month)
        val np_date = dialogView?.findViewById<NumberPicker>(R.id.picker_exclude_date)

        np_month?.minValue = 1
        np_month?.maxValue = 12
        np_month?.value = mSelectedMonth
        np_month?.setOnValueChangedListener { _, _, newVal ->
            mSelectedMonth = newVal

            val maxDate = getMaxDate(mSelectedMonth)
            np_date?.maxValue = maxDate
            if(mSelectedDate > maxDate) {
                // Number Picker Dialog上の数値は自動で切り替わる
                mSelectedDate = maxDate
            }
        }

        np_date?.minValue = 1
        np_date?.maxValue = getMaxDate(mSelectedMonth)
        np_date?.value =  mSelectedDate
        np_date?.setOnValueChangedListener { _, _, newVal ->
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

        private val EXTRA_INDEX = "EXTRA_INDEX"
        private val EXTRA_MONTH = "EXTRA_MONTH"
        private val EXTRA_DATE = "EXTRA_DATE"
    }
}