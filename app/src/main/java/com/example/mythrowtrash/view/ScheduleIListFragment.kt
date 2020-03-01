package com.example.mythrowtrash.view

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.mythrowtrash.R
import com.example.mythrowtrash.adapter.IScheduleListView
import com.example.mythrowtrash.adapter.ScheduleListController
import com.example.mythrowtrash.adapter.ScheduleViewModel

import com.example.mythrowtrash.view.dummy.DummyContent.DummyItem
import kotlinx.android.synthetic.main.fragment_item.view.*
import kotlinx.android.synthetic.main.fragment_item_list.*

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [ScheduleIListFragment.OnListFragmentInteractionListener] interface.
 */
class ScheduleIListFragment : Fragment(),IScheduleListView {

    private val controller = ScheduleListController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("[MyApp] schedule list fragment created")
        trashScheduleList.adapter = ScheduleListAdapter(activity!!,controller)
        controller.showScheduleList()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: DummyItem?)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ScheduleIListFragment()
    }

    override fun update(viewModel: ArrayList<ScheduleViewModel>) {
        println("[MyApp] update list")
        (trashScheduleList.adapter as ScheduleListAdapter).update(viewModel)
    }

    class ScheduleListAdapter(private val activity: Activity, private val controller: ScheduleListController): RecyclerView.Adapter<ScheduleListAdapter.ViewHolder>() {
        class ViewHolder(cell: View): RecyclerView.ViewHolder(cell) {}
        private lateinit var scheduleList: ArrayList<ScheduleViewModel>
        private lateinit var context:Context
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            context = parent.context
            val item:View = LayoutInflater.from(parent.context).inflate(R.layout.fragment_item, parent,false)
            return ViewHolder(item)
        }

        override fun onBindViewHolder(holder:ViewHolder,position: Int) {
            val scheduleViewModel =  scheduleList[position]
            holder.itemView.item_trashType.text = scheduleViewModel?.trashName
            scheduleViewModel?.scheduleList?.forEach {schedule->
                val scheduleTextView = TextView(context)
                scheduleTextView.textSize = 12.0F
                scheduleTextView.text = schedule
                // スケジュールは要素数が不定のため既存のTextViewを全て削除する
                holder.itemView.item_schedule.removeAllViews()
                holder.itemView.item_schedule.addView(scheduleTextView)
                holder.itemView.item_deleteScheduleButton.setOnClickListener {
                    controller.deleteSchedule(scheduleList[position].id)
                    activity.setResult(RESULT_OK)
                    Toast.makeText(context,"1件のゴミ出し予定を削除しました",Toast.LENGTH_SHORT).show()
                }
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
