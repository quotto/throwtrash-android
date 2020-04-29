package net.mythrowaway.app.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import net.mythrowaway.app.R
import kotlinx.android.synthetic.main.fragment_calendar.*

/**
 * A simple [Fragment] subclass.
 */
class CalendarFragment : Fragment(),
    CalendarAdapter.CalendarAdapterListener {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val horizontalDivider = DividerItemDecoration(calendar.context,
            LinearLayoutManager.HORIZONTAL)
        ContextCompat.getDrawable(context!!,R.drawable.divider_border_horizontal)?.let {
            horizontalDivider.setDrawable(it)
        }
        val verticalDivider = DividerItemDecoration(calendar.context, LinearLayoutManager.VERTICAL)
        ContextCompat.getDrawable(context!!,R.drawable.divider_border_vertical)?.let {
            verticalDivider.setDrawable(it)
        }

        calendar.addItemDecoration(horizontalDivider)
        calendar.addItemDecoration(verticalDivider)
        calendar.setBackgroundResource(R.drawable.divider_frame)
        calendar.layoutManager = GridLayoutManager(context!!, 7)

        val adapter = CalendarAdapter(this)
        calendar.adapter = adapter

        if (activity is FragmentListener) {
            arguments?.apply {
                println("[MyApp] calendarFragment createdView@${getInt(POSITION)}")
                val resultIntent = Intent()
                resultIntent.putExtra(
                        POSITION, getInt(
                        POSITION
                    ))
                (activity as FragmentListener).onFragmentNotify(
                    CalendarActivity.REQUEST_FRAGMENT_CREATED,
                    resultIntent
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        println("[MyApp] calendar resume")
    }

    fun updateCalendar(year: Int, month: Int, dateList:ArrayList<Int>,trashList: Array<ArrayList<String>>) {
        arguments?.let {
            it.putInt(YEAR, year)
            it.putInt(MONTH, month)
        }
        calendar?.apply {
            println("[MyApp] update calendar $year/$month")
            (adapter as CalendarAdapter).updateData(year, month, dateList,trashList)

            // アプリ起動時の初期表示用
            // カレンダー日付部分はゴミ出し予定のマッピングが非同期で行われるため、
            // 画面上に曜日ラベルとデータを同時に可視化することで同時に表示する
            visibility = View.VISIBLE
            calendarLabel.visibility = View.VISIBLE
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

