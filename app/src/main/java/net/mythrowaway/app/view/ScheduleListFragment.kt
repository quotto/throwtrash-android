package net.mythrowaway.app.view

import android.app.Activity
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import net.mythrowaway.app.R

import net.mythrowaway.app.adapter.ScheduleListViewInterface
import net.mythrowaway.app.adapter.controller.ScheduleListController
import net.mythrowaway.app.usecase.ScheduleListPresenterInterface
import net.mythrowaway.app.databinding.FragmentScheduleListBinding
import net.mythrowaway.app.databinding.FragmentScheduleListItemBinding
import net.mythrowaway.app.viewmodel.ScheduleViewModel
import javax.inject.Inject

class ScheduleListFragment : Fragment(), ScheduleListViewInterface {
    @Inject lateinit var presenter: ScheduleListPresenterInterface
    @Inject lateinit var controller: ScheduleListController

    private lateinit var fragmentScheduleListBinding: FragmentScheduleListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentScheduleListBinding = FragmentScheduleListBinding.inflate(inflater)
        return fragmentScheduleListBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as ScheduleListActivity).scheduleListComponent.inject(this)
        presenter.setView(this)
    }

    override fun onResume() {
        super.onResume()
        controller.showScheduleList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val verticalDivider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        ContextCompat.getDrawable(requireContext(),R.drawable.divider_border_vertical)?.let {
            verticalDivider.setDrawable(it)
        }
        fragmentScheduleListBinding.trashScheduleList.addItemDecoration(verticalDivider)

        fragmentScheduleListBinding.trashScheduleList.adapter =
            ScheduleListAdapter(
                requireActivity(),controller,
                requireActivity().activityResultRegistry)
        lifecycle.addObserver(fragmentScheduleListBinding.trashScheduleList.adapter as ScheduleListAdapter)
        controller.showScheduleList()
    }

    override fun update(viewModel: ArrayList<ScheduleViewModel>) {
        (fragmentScheduleListBinding.trashScheduleList.adapter as ScheduleListAdapter).update(viewModel)
    }

    class ScheduleListAdapter(
        private val activity: FragmentActivity,
        private val controller: ScheduleListController,
        private val registry: ActivityResultRegistry
    ): RecyclerView.Adapter<ScheduleListAdapter.ViewHolder>(),DefaultLifecycleObserver {
        class ViewHolder(cell: View): RecyclerView.ViewHolder(cell)

        private lateinit var scheduleList: ArrayList<ScheduleViewModel>
        private lateinit var context:Context
        private lateinit var launcher: ActivityResultLauncher<Intent>
        override fun onCreate(owner: LifecycleOwner) {
            super.onCreate(owner)
            launcher = registry.register("key",owner,ActivityResultContracts.StartActivityForResult()) {result ->
                activity.setResult(result.resultCode)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            context = parent.context

            val item:View = LayoutInflater.from(parent.context).inflate(R.layout.fragment_schedule_list_item, parent,false)
            return ViewHolder(item)
        }

        override fun onBindViewHolder(holder:ViewHolder,position: Int) {
            val scheduleViewModel =  scheduleList[position]
            val fragmentScheduleListItemBinding: FragmentScheduleListItemBinding =
                FragmentScheduleListItemBinding.bind(holder.itemView)
            fragmentScheduleListItemBinding.itemTrashType.text = scheduleViewModel.trashName
            // スケジュールは要素数が不定のため既存のTextViewを全て削除する
            fragmentScheduleListItemBinding.itemSchedule.removeAllViews()

            scheduleViewModel.scheduleList.forEach { schedule ->
                val scheduleTextView = TextView(context)
                scheduleTextView.textSize = 12.0F
                scheduleTextView.text = schedule
                fragmentScheduleListItemBinding.itemSchedule.addView(scheduleTextView)
            }
            fragmentScheduleListItemBinding.itemDeleteScheduleButton.setOnClickListener {
                controller.deleteSchedule(scheduleList[position].id)
                activity.setResult(Activity.RESULT_OK)
                Toast.makeText(context,"1件のゴミ出し予定を削除しました",Toast.LENGTH_SHORT).show()
            }
            holder.itemView.setOnClickListener {
                Log.d(this.javaClass.simpleName, "Start EditActivity -> id=${scheduleViewModel.id}")
                val intent = Intent(context, EditActivity::class.java)
                intent.putExtra(EditMainFragment.ID, scheduleViewModel.id)
                launcher.launch(intent)
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
