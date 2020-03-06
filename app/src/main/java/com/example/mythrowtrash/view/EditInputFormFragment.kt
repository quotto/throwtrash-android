package com.example.mythrowtrash.view

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.example.mythrowtrash.R
import com.example.mythrowtrash.adapter.EditViewModelSchedule
import kotlinx.android.synthetic.main.input_evweek.*
import kotlinx.android.synthetic.main.fragment_edit_input.*
import kotlinx.android.synthetic.main.input_month.*
import kotlinx.android.synthetic.main.input_num_of_week.*
import kotlinx.android.synthetic.main.input_weekday.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_MODE = "mode"
private const val ARG_PRESET = "preset"

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
                schedule.monthValue = monthDateList.selectedItem.toString()
            }
            resources.getString(R.string.schedule_numOfWeek) -> {
                schedule.numOfWeekWeekdayValue = numOfWeekWeekdayList.selectedItemPosition.toString()
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
        arguments?.let {
            mode = it.getInt(ARG_MODE)
            println("[MyApp - InputFragment] set mode: $mode")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 修正モード（TrashDataのIDあり）の場合にデフォルト値に戻るのを避けるため、既存設定上書き後にスケジュールタイプ選択時のリスナーを設定する
        scheduleGroup.setOnCheckedChangeListener { group, checkedId ->
            println("[MyApp - InputFragment] scheduleGroup.onCheckChange: $checkedId")
            val scheduleType:String = (view.findViewById(checkedId) as RadioButton).tag as String
            loadInputScheduleLayout(scheduleType)
        }

        // デフォルトで毎週を選択
        scheduleGroup.check(weekdayRadio.id)

        arguments?.getSerializable(ARG_PRESET)?.let {
            val viewModel: EditViewModelSchedule = it as EditViewModelSchedule
            when(viewModel.type) {
                "weekday" -> {
                    println("[MyApp - InputFragment] preset start: $viewModel")
                    scheduleGroup.check(R.id.weekdayRadio)
                    println("[MyApp - InputFragment] preset change: $viewModel")
                    weekdayWeekdayList.setSelection(viewModel.weekdayValue.toInt())
                }
                "month" -> {
                    println("[MyApp - InputFragment] preset start: $viewModel")
                    scheduleGroup.check(R.id.monthRadio)
                    println("[MyApp - InputFragment] preset change: $viewModel")
                    monthDateList.setSelection(viewModel.monthValue.toInt() - 1)
                }
                "biweek" -> {
                    scheduleGroup.check(R.id.numOfWeekRadio)
                    numOfWeekList.setSelection(viewModel.numOfWeekNumberValue.toInt() - 1)
                    numOfWeekWeekdayList.setSelection(viewModel.numOfWeekWeekdayValue.toInt())
                }
                "evweek" -> {
                    scheduleGroup.check(R.id.evweekRadio)
                    evweekWeekdayList.setSelection(viewModel.evweekWeekdayValue.toInt())
                    when(viewModel.evweekStartValue) {
                        "this" -> evweekWeekButtonGroup.check(evweekThisweekButton.id)
                        "next" -> evweekWeekButtonGroup.check(evweekNextweekButton.id)
                    }
                }
            }
        }

        parentFragment?.let{
            it.onActivityResult(
                mode!!,
                Activity.RESULT_OK,
                null
            )
        }
    }

    /**
     * 選択されたスケジュールのレイアウトを呼び出しFragment上に追加（上書き）する
     */
    fun loadInputScheduleLayout(scheduleType: String) {
        val inputView:View? = when(scheduleType) {
            getString(R.string.schedule_weekday) -> layoutInflater.inflate(
                R.layout.input_weekday,null)
            getString(R.string.schedule_month) -> layoutInflater.inflate(
                R.layout.input_month,null)
            getString(R.string.schedule_numOfWeek) -> layoutInflater.inflate(
                R.layout.input_num_of_week, null)
            getString(R.string.schedule_evweek) -> layoutInflater.inflate(
                R.layout.input_evweek, null)
            else -> null
        }
        inputView?.let {
            scheduleInput.removeAllViews()
            scheduleInput.addView(it)
        }
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
        fun newInstance(mode: Int, preset: EditViewModelSchedule?) =
            InputFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_MODE, mode)
                    preset?.let {
                        putSerializable(ARG_PRESET, it)
                    }
                }
            }
    }
}
