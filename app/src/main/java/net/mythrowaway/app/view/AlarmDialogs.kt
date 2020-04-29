package net.mythrowaway.app.view

import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import net.mythrowaway.app.R
import java.util.*

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    interface OnTimeSelectedListener {
        fun onSelected(hourOfDay: Int, minute: Int)
    }

    private var listener: OnTimeSelectedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        when(context) {
            is OnTimeSelectedListener -> listener = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        return TimePickerDialog(context, this, hour, minute, true)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        listener?.onSelected(hourOfDay, minute)
    }
}

class AlarmTrashDialog: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        var message:String = resources.getString(R.string.message_noneTrash)
        arguments?.getStringArray(TRASH_ARRAY)?.let {
            if(it.isNotEmpty()) {
                message = it.joinToString("\n")
            }
        }
        val builder =AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.title_alarm_dialog)
        builder.setMessage(message)
        builder.setPositiveButton("OK") {_, _ ->
            activity?.finish()
        }
        return builder.create()
    }

    companion object {
        private const val TRASH_ARRAY:String = "TRASH_ARRAY"
        fun newInstance(trashList:List<String>): AlarmTrashDialog {
            val fragment = AlarmTrashDialog()
            fragment.arguments = Bundle().apply {
                putStringArray(TRASH_ARRAY, trashList.toTypedArray())
            }
            return fragment
        }
    }
}
