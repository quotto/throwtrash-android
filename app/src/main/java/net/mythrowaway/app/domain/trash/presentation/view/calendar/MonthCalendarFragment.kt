package net.mythrowaway.app.domain.trash.presentation.view.calendar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import net.mythrowaway.app.R
import net.mythrowaway.app.databinding.FragmentCalendarBinding
import net.mythrowaway.app.domain.trash.usecase.dto.MonthCalendarDTO
import net.mythrowaway.app.domain.trash.presentation.view_model.viewModelFactory
import net.mythrowaway.app.domain.trash.presentation.view_model.CalendarViewModelMessage
import net.mythrowaway.app.domain.trash.presentation.view_model.CalendarViewModel
import net.mythrowaway.app.domain.trash.presentation.view_model.MonthCalendarViewModel
import javax.inject.Inject

class MonthCalendarFragment :
    Fragment(),
  MonthCalendarAdapter.CalendarAdapterListener,
    CoroutineScope by MainScope() {
    interface FragmentListener {
        fun onFragmentNotify(notifyCode:Int, data: Intent)
    }

    @Inject
    lateinit var calendarViewModelFactory: MonthCalendarViewModel.Factory

    @Inject
    lateinit var calendarTrashScheduleViewModelFactory: CalendarViewModel.Factory

    private lateinit var fragmentCalendarBinding: FragmentCalendarBinding
    private val calendarTrashScheduleViewModel: CalendarViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            viewModelFactory { calendarTrashScheduleViewModelFactory.create() }
        ).get(CalendarViewModel::class.java)
    }
    private lateinit var monthCalendarViewModel: MonthCalendarViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentCalendarBinding = FragmentCalendarBinding.inflate(inflater, container, false)
        return fragmentCalendarBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val horizontalDivider = DividerItemDecoration(
            fragmentCalendarBinding.calendar.context,
            LinearLayoutManager.HORIZONTAL
        )
        ContextCompat.getDrawable(requireContext(), R.drawable.divider_border_horizontal)?.let {
            horizontalDivider.setDrawable(it)
        }
        val verticalDivider = DividerItemDecoration(fragmentCalendarBinding.calendar.context, LinearLayoutManager.VERTICAL)
        ContextCompat.getDrawable(requireContext(), R.drawable.divider_border_vertical)?.let {
            verticalDivider.setDrawable(it)
        }

        fragmentCalendarBinding.calendar.addItemDecoration(horizontalDivider)
        fragmentCalendarBinding.calendar.addItemDecoration(verticalDivider)
        fragmentCalendarBinding.calendar.layoutManager = GridLayoutManager(requireContext(), 7)


        arguments?.let {arguments->
            Log.d(this.javaClass.simpleName, "set schedule observer@${arguments.getInt(POSITION)}")
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    calendarTrashScheduleViewModel.message.collect { message ->
                        Log.d(this.javaClass.simpleName, "Observe schedule message")
                        when(message) {
                            is CalendarViewModelMessage.Update, CalendarViewModelMessage.PullUpdate -> {
                                Log.d(this.javaClass.simpleName, "Update message")
                                launch {
                                    monthCalendarViewModel.updateCalendar()
                                }
                            }
                            is CalendarViewModelMessage.Failed -> {
                                Log.d(this.javaClass.simpleName, "Failed message")
                            }
                        }
                    }
                }
            }

            monthCalendarViewModel = ViewModelProvider(this,
                viewModelFactory { calendarViewModelFactory.create(arguments.getInt(POSITION)) }
            ).get(MonthCalendarViewModel::class.java)
            Log.d(this.javaClass.simpleName, "set calendar observer@${arguments.getInt(POSITION)}")
            val observer = Observer<MonthCalendarDTO> { item ->
                Log.d(this.javaClass.simpleName, "Observe calendar@${arguments.getInt(POSITION)}")
                fragmentCalendarBinding.calendar.adapter = MonthCalendarAdapter(item.baseMonth, item.calendarDayDTOS)
                (fragmentCalendarBinding.calendar.adapter as MonthCalendarAdapter).setListener(this)
                this.updateCalendar(item)
            }
            monthCalendarViewModel.trashCalendar.observe(viewLifecycleOwner, observer)
            launch {
                // 初期表示のため更新処理を呼び出す
                monthCalendarViewModel.updateCalendar()
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
        Log.d(this.javaClass.simpleName, "onAttach ${arguments?.getInt(POSITION)}")
        (activity as CalendarActivity).calendarComponent.inject(this)
    }

    override fun onResume() {
        super.onResume()
        Log.d(this.javaClass.simpleName, "onResume ${arguments?.getInt(POSITION)}")
    }

    override fun onPause() {
        super.onPause()
        Log.d(this.javaClass.simpleName,"onPause ${arguments?.getInt(POSITION)}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(this.javaClass.simpleName,"onDestroy ${arguments?.getInt(POSITION)}")
    }

    override fun showDetailDialog(year: Int,month: Int,date:Int,trashList: ArrayList<String>) {
        parentFragmentManager.let { fm ->
            val dialog =
              TrashOfDayDialog.newInstance(
                year,
                month,
                date,
                trashList
              )
            dialog.show(fm, "detailDialog")
        }
    }


    private fun updateCalendar(calendar: MonthCalendarDTO) {
        arguments?.let {
            it.putInt(YEAR, calendar.baseYear)
            it.putInt(MONTH, calendar.baseMonth)
        }
        fragmentCalendarBinding.calendar.apply {
            Log.i(this.javaClass.simpleName, "Update calendar ${calendar.baseYear}/${calendar.baseMonth}")
            (adapter as MonthCalendarAdapter).updateData(calendar.calendarDayDTOS)

            // アプリ起動時の初期表示用
            // すべてのセルサイズが決まったら可視化する
            visibility = View.VISIBLE
        }
    }

    companion object {
        const val POSITION:String = "POSITION"
        const val YEAR: String = "YEAR"
        const val MONTH: String = "MONTH"
        fun newInstance(position: Int): MonthCalendarFragment {
            val bundle = Bundle()
            bundle.putInt(POSITION,position)
            val fragment = MonthCalendarFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}

