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
import androidx.lifecycle.ViewModelProvider
import net.mythrowaway.app.databinding.FragmentEditInputEvweekBinding
import net.mythrowaway.app.viewmodel.EvWeekViewModel
import java.util.*

private const val ARG_WEEKDAY = "weekday"
private const val ARG_INTERVAL = "interval"
private const val ARG_DATE = "recentlyDate"

/**
 * 隔週入力用のFragment
 * EditInputFormFragmentから追加される
 */
class EditInputEvweekFragment : Fragment() {
    private  lateinit var fragmentEditInputEvweekBinding: FragmentEditInputEvweekBinding
    private val viewModel by lazy {
        parentFragment?.let {
            ViewModelProvider(it).get(EvWeekViewModel::class.java)
        }
    }
        @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentEditInputEvweekBinding = FragmentEditInputEvweekBinding.inflate(inflater, container, false)
        return  fragmentEditInputEvweekBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(savedInstanceState != null) {
            viewModel?.let {
                viewModel
                val weekday = it.weekday
                val interval = it.interval
                val start = it.start

                fragmentEditInputEvweekBinding.evweekWeekdayList.setSelection(weekday.toInt())
                fragmentEditInputEvweekBinding.evweekIntervalList.setSelection(interval - 2)

                fragmentEditInputEvweekBinding.evweekDateText.text = start
            }
        }else{
            // 初期表示時はデフォルト値を設定
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)+1
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            fragmentEditInputEvweekBinding.evweekDateText.text = DATE_FORMAT.format(year,month,dayOfMonth)
        }

        fragmentEditInputEvweekBinding.evweekDateText.setOnClickListener {
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
                requireContext(),
                { _, _year, _month, _dayOfMonth ->
                    dateText.text = DATE_FORMAT.format(_year,_month+1,_dayOfMonth)
                },
                year,
                month,
                dayOfMonth
            )
            datePickerDialog.show()
        }
    }

    /**
     * ViewModelに設定値を反映する
     */
    override fun onPause() {
        super.onPause()
        updateViewModel()
    }

    fun getEvWeekViewModel(): EvWeekViewModel {
        updateViewModel()
        if(viewModel != null) {
            return viewModel as EvWeekViewModel
        }
        throw Exception("unknownError: EvWeekViewModel is Null")
    }

    private fun updateViewModel() {
        viewModel?.let {
            it.weekday = fragmentEditInputEvweekBinding.evweekWeekdayList.selectedItemPosition.toString()
            it.interval = fragmentEditInputEvweekBinding.evweekIntervalList.selectedItemPosition + 2
            it.start = fragmentEditInputEvweekBinding.evweekDateText.text.toString()
        }
    }

    companion object {
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
        private val DATE_FORMAT = "%d/%02d/%02d"
    }
}
