package net.mythrowaway.app.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import net.mythrowaway.app.R
import kotlinx.android.synthetic.main.fragment_calendar.*
import net.mythrowaway.app.viewmodel.CalendarItemViewModel
import net.mythrowaway.app.viewmodel.CalendarViewModel
import javax.inject.Inject

class CalendarFragment : Fragment(),
    CalendarAdapter.CalendarAdapterListener {

    @Inject
    lateinit var adapter: CalendarAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val horizontalDivider = DividerItemDecoration(
            calendar.context,
            LinearLayoutManager.HORIZONTAL
        )
        ContextCompat.getDrawable(context!!, R.drawable.divider_border_horizontal)?.let {
            horizontalDivider.setDrawable(it)
        }
        val verticalDivider = DividerItemDecoration(calendar.context, LinearLayoutManager.VERTICAL)
        ContextCompat.getDrawable(context!!, R.drawable.divider_border_vertical)?.let {
            verticalDivider.setDrawable(it)
        }

        calendar.addItemDecoration(horizontalDivider)
        calendar.addItemDecoration(verticalDivider)
        calendar.layoutManager = GridLayoutManager(context!!, 7)

//        val adapter = CalendarAdapter(this)
        calendar.adapter = adapter


        activity?.let {activity->
            arguments?.let {arguments->
                Log.d(this.javaClass.simpleName, "Start Observe@${arguments.getInt(POSITION)}")
                val model = ViewModelProviders.of(activity).
                                get(arguments.getInt(POSITION).toString(), CalendarItemViewModel::class.java)
                val observer = Observer<CalendarViewModel> {item ->
                    updateCalendar(item.year,item.month,item.dateList,item.trashList)
                }
                model.cardItem.observe(this, observer)
            }
        }

        if (activity is FragmentListener) {
            arguments?.apply {
                Log.d(this.javaClass.simpleName, "notify to activity@${getInt(POSITION)}")
                val resultIntent = Intent()
                resultIntent.putExtra(
                    POSITION, getInt(
                        POSITION
                    )
                )
                (activity as FragmentListener).onFragmentNotify(
                    ActivityCode.CALENDAR_REQUEST_CREATE_FRAGMENT,
                    resultIntent
                )
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as CalendarActivity).calendarComponent.inject(this)
        adapter.setListener(this)
    }

    override fun onResume() {
        super.onResume()
        Log.d(this.javaClass.simpleName, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(this.javaClass.simpleName,"onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(this.javaClass.simpleName,"onDestroy")
    }

    fun setCalendar(year: Int, month: Int, dateList:ArrayList<Int>, trashList: Array<ArrayList<String>>) {
        Log.i(this.javaClass.simpleName, "Set calendar $year/$month")
        val model = ViewModelProviders.of(activity!!)
                        .get(arguments!!.getInt(POSITION).toString(),CalendarItemViewModel::class.java)
        model.cardItem.value = CalendarViewModel(year,month,dateList,trashList)
    }

    private fun updateCalendar(year: Int, month: Int, dateList:ArrayList<Int>,trashList: Array<ArrayList<String>>) {
        arguments?.let {
            it.putInt(YEAR, year)
            it.putInt(MONTH, month)
        }
        calendar?.apply {
            Log.i(this.javaClass.simpleName, "Update calendar $year/$month")
            (adapter as CalendarAdapter).updateData(year, month, dateList,trashList)

            // アプリ起動時の初期表示用
            // すべてのセルサイズが決まったら可視化する
            visibility = View.VISIBLE
        }
    }

    companion object {
        const val POSITION:String = "POSITION"
        const val YEAR: String = "YEAR"
        const val MONTH: String = "MONTH"
        fun newInstance(position: Int): CalendarFragment {
            val bundle = Bundle()
            bundle.putInt(POSITION,position)
            val fragment = CalendarFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
    interface FragmentListener {
        fun onFragmentNotify(notifyCode:Int, data: Intent)
    }

    override fun showDetailDialog(year: Int,month: Int,date:Int,trashList: ArrayList<String>) {
        fragmentManager?.let { fm ->
            val dialog =
                DetailDialog.newInstance(
                    year,
                    month,
                    date,
                    trashList
                )
            dialog.show(fm, "detailDialog")
        }
    }
}

