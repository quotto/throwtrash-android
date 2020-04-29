package net.mythrowaway.app.view

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import net.mythrowaway.app.R

import kotlinx.android.synthetic.main.fragment_schedule_list_item.view.*
import kotlinx.android.synthetic.main.fragment_schedule_list.*
import net.mythrowaway.app.adapter.IScheduleListView
import net.mythrowaway.app.adapter.controller.ScheduleListController
import net.mythrowaway.app.adapter.presenter.ScheduleViewModel

class ScheduleIListFragment : Fragment(), IScheduleListView {

    private val controller =
        ScheduleListController(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_schedule_list, container, false)
    }

    override fun onResume() {
        super.onResume()
        controller.showScheduleList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val verticalDivider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        ContextCompat.getDrawable(context!!,R.drawable.divider_border_vertical)?.let {
            verticalDivider.setDrawable(it)
        }
        trashScheduleList.addItemDecoration(verticalDivider)

        trashScheduleList.adapter = ScheduleListAdapter(activity!!,controller)
        controller.showScheduleList()
    }

    companion object {
        @JvmStatic
        fun newInstance() = ScheduleIListFragment()
    }

    override fun update(viewModel: ArrayList<ScheduleViewModel>) {
        (trashScheduleList.adapter as ScheduleListAdapter).update(viewModel)
    }

    class ScheduleListAdapter(private val activity: Activity, private val controller: ScheduleListController): RecyclerView.Adapter<ScheduleListAdapter.ViewHolder>() {
        class ViewHolder(cell: View): RecyclerView.ViewHolder(cell) {}
        private lateinit var scheduleList: ArrayList<ScheduleViewModel>
        private lateinit var context:Context
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            context = parent.context
            val item:View = LayoutInflater.from(parent.context).inflate(R.layout.fragment_schedule_list_item, parent,false)
            return ViewHolder(item)
        }

        override fun onBindViewHolder(holder:ViewHolder,position: Int) {
            val scheduleViewModel =  scheduleList[position]
            holder.itemView.item_trashType.text = scheduleViewModel.trashName
            // スケジュールは要素数が不定のため既存のTextViewを全て削除する
            holder.itemView.item_schedule.removeAllViews()

            scheduleViewModel.scheduleList.forEach { schedule ->
                val scheduleTextView = TextView(context)
                scheduleTextView.textSize = 12.0F
                scheduleTextView.text = schedule
                holder.itemView.item_schedule.addView(scheduleTextView)
            }
            holder.itemView.item_deleteScheduleButton.setOnClickListener {
                controller.deleteSchedule(scheduleList[position].id)
                activity.setResult(RESULT_OK)
                Toast.makeText(context,"1件のゴミ出し予定を削除しました",Toast.LENGTH_SHORT).show()
            }
            holder.itemView.setOnClickListener {
                Log.d(this.javaClass.simpleName, "Start EditActivity -> id=${scheduleViewModel.id}")
                val intent = Intent(context, EditActivity::class.java)
                intent.putExtra(EditMainFragment.ID, scheduleViewModel.id)
                activity.startActivityForResult(intent, CalendarActivity.REQUEST_UPDATE)
            }
        }

        override fun getItemCount(): Int {
            return scheduleList.size
        }

        fun update(newDataSet: ArrayList<ScheduleViewModel>) {
            scheduleList = newDataSet
            notifyDataSetChanged()
        }
    }
}
