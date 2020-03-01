package com.example.mythrowtrash.view

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.example.mythrowtrash.R
import com.example.mythrowtrash.adapter.EditViewModel
import com.example.mythrowtrash.adapter.EditViewModelSchedule
import kotlinx.android.synthetic.main.evweek_input.*
import kotlinx.android.synthetic.main.fragment_input.*
import kotlinx.android.synthetic.main.month_input.*
import kotlinx.android.synthetic.main.num_of_week_input.*
import kotlinx.android.synthetic.main.weekday_input.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_MODE = "mode"

interface InputFragmentListener {
    fun getInputValue(): EditViewModelSchedule
    fun changeMode(changeType:Int)
}

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [InputFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [InputFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InputFragment : Fragment(),
    InputFragmentListener {
    override fun getInputValue(): EditViewModelSchedule {
        val schedule = EditViewModelSchedule()
        schedule.type = view?.findViewById<RadioButton>(scheduleGroup.checkedRadioButtonId)?.tag.toString()
        when(schedule.type) {
            resources.getString(R.string.schedule_weekday) -> {
                schedule.weekdayValue = weekdayWeekdayList.selectedItemPosition.toString()
            }
            resources.getString(R.string.schedule_month) -> {
                schedule.monthValue = dateList.selectedItem.toString()
            }
            resources.getString(R.string.schedule_numOfWeek) -> {
                schedule.numOfWeekWeekdayValue = weekdayList.selectedItemPosition.toString()
                schedule.numOfWeekNumberValue = numOfWeekList.selectedItem.toString()
            }
            resources.getString(R.string.schedule_evweek) -> {
                when(evweekWeekButtonGroup.checkedRadioButtonId) {
                    R.id.evweekThisweekButton -> schedule.evweekStartValue = EditViewModelSchedule.EVWEEK_START_THIS_WEEK
                    R.id.evweekNextweekButton -> schedule.evweekStartValue = EditViewModelSchedule.EVWEEK_START_NEXT_WEEK
                }
                schedule.evweekWeekdayValue = evweekWeekdayList.selectedItemPosition.toString()
            }
        }
        return schedule
    }

    /**
     * 他のスケジュールが変更（追加または削除）された場合に復元モードを変更する
     * 現在は削除された場合のみだが拡張性を考慮して条件分けしている
     * @param changeType 1:追加、2：削除
     */
    override fun changeMode(changeType: Int) {
        when(changeType) {
            2-> {
                when(mode) {
                    // 最後のインデックスだった場合は復元時に削除&追加ボタンを設置する
                    EditMainFragment.REQUEST_DELETE_BUTTON ->
                        mode = EditMainFragment.REQUEST_ADD_DELETE_BUTTON
                }
            }
        }
    }

    // 親FragmentのrequestCodeとして機能し,追加ボタン,削除ボタンの要否を伝える
    private var mode: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        println("[MyApp] child onCreate")
        arguments?.let {
            mode = it.getInt(ARG_MODE)
            println("[MyApp] child mode: $mode")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        println("[MyApp] child onAttach")
    }

    override fun onResume() {
        super.onResume()
        println("[MyApp] child onResume")
    }

    override fun onDetach() {
        super.onDetach()
        println("[MyApp] child onDetach")
    }

    override fun onPause() {
        super.onPause()
        println("[MyApp] child onPause")

    }

    override fun onDestroy() {
        super.onDestroy()
        println("[MyApp] child onDestroy")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        println("[MyApp] child onCreateView")
        return inflater.inflate(R.layout.fragment_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scheduleGroup.setOnCheckedChangeListener { group, checkedId ->
            val scheduleType = (view.findViewById(checkedId) as RadioButton).tag
            val inputView:View? = when(scheduleType) {
                getString(R.string.schedule_weekday) -> layoutInflater.inflate(
                    R.layout.weekday_input,null)
                getString(R.string.schedule_month) -> layoutInflater.inflate(
                    R.layout.month_input,null)
                getString(R.string.schedule_numOfWeek) -> layoutInflater.inflate(
                    R.layout.num_of_week_input, null)
                getString(R.string.schedule_evweek) -> layoutInflater.inflate(
                    R.layout.evweek_input, null)
                else -> null
            }
            inputView?.let {
                scheduleInput.removeAllViews()
                scheduleInput.addView(inputView)
            }
        }

        // デフォルトで毎週を選択
        scheduleGroup.check(weekdayRadio.id)

        parentFragment?.let{
            it.onActivityResult(
                mode!!,
                Activity.RESULT_OK,
                null
            )
        }
        println("[MyApp] child onViewCreated")
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InputFragment.
         */

        @JvmStatic
        fun newInstance(mode: Int) =
            InputFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_MODE, mode)
                }
            }
    }
}
