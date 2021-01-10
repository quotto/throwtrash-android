package net.mythrowaway.app.view

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.fragment_edit_input_evweek.*
import net.mythrowaway.app.R
import java.util.*

private const val ARG_WEEKDAY = "weekday"
private const val ARG_INTERVAL = "interval"
private const val ARG_DATE = "recentlyDate"

/**
 * 隔週入力用のFragment
 * EditInputFormFragmentから追加される
 */
class EditInputEvweekFragment : Fragment() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_input_evweek, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.also {
            val weekday = it.getString(ARG_WEEKDAY)
            val interval = it.getInt(ARG_INTERVAL)
            val start = it.getString((ARG_DATE))

            evweekWeekdayList.setSelection(weekday!!.toInt())
            evweekIntervalList.setSelection(interval - 2)

            evweekDateText.text = start
        }?: run {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)+1
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            evweekDateText.text = "%d/%02d/%02d".format(year,month,dayOfMonth)
        }

        evweekDateText.setOnClickListener {
            val dateText = it as TextView

            val calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            if(dateText.text.isNotEmpty()) {
                Log.d(this.javaClass.simpleName,"Date text->${dateText.text}")
                val ymd = dateText.text.split("/")
                year = ymd[0].toInt()
                //画面上は実際の月なので-1する
                month = ymd[1].toInt()-1
                dayOfMonth = ymd[2].toInt()
            }

            val datePickerDialog = DatePickerDialog(
                context!!,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth -> dateText.text = "%d/%02d/%02d".format(year,month+1,dayOfMonth) },
                year,
                month,
                dayOfMonth
            )
            datePickerDialog.show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ARG_WEEKDAY,evweekWeekdayList.selectedItemPosition.toString())
        outState.putInt(ARG_INTERVAL,evweekIntervalList.selectedItemPosition + 2)
        outState.putString(ARG_DATE,evweekDateText.text.toString())
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditInputEvweekFragment.
         */
        @JvmStatic
        fun newInstance() = EditInputEvweekFragment()
        @JvmStatic
        fun newInstance(weekday: String,interval: Int,start: String) = EditInputEvweekFragment().apply {
            this.arguments = Bundle().apply {
                putString(ARG_WEEKDAY, weekday)
                putInt(ARG_INTERVAL, interval)
                putString(ARG_DATE, start)
            }

        }
    }
}
