package net.mythrowaway.app.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import net.mythrowaway.app.R
import kotlinx.android.synthetic.main.fragment_edit_input.*
import kotlinx.android.synthetic.main.fragment_edit_input_evweek.*
import kotlinx.android.synthetic.main.input_month.*
import kotlinx.android.synthetic.main.input_num_of_week.*
import kotlinx.android.synthetic.main.input_weekday.*
import net.mythrowaway.app.adapter.presenter.EditScheduleItem

interface InputFragmentListener {
    fun getInputValue(): EditScheduleItem
    fun changeMode(changeType:Int)
}

class EditScheduleItemViewModel: ViewModel() {
    var editScheduleItem: EditScheduleItem? = null
}

class InputFragment : Fragment(),
    InputFragmentListener {
    // 親FragmentのrequestCodeとして機能し,追加ボタン,削除ボタンの要否を伝える
    private var mMode: Int? = null

    private lateinit var viewModel: EditScheduleItemViewModel

    /*
    Fragmentの実装
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mMode = it.getInt(ARG_MODE)
            Log.d(this.javaClass.simpleName,"Set mode: $mMode")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(EditScheduleItemViewModel::class.java)

        var resultCode = EditMainFragment.RESULT_INIT
        if(savedInstanceState == null) {
            // 初期表示かつ保存済みデータの編集の場合はViewModelに反映
            arguments?.getSerializable(ARG_PRESET)?.also {
                viewModel.editScheduleItem = it as EditScheduleItem
            } ?: run {
                // 初期表示かつ新規データの場合はデフォルトで毎週を設定する
                scheduleGroup.check(weekdayRadio.id)
                loadInputScheduleLayout("weekday")
            }
        } else {
            // 復帰時は親Fragmentに伝えるためのスケジュール追加モードも復元
            resultCode = EditMainFragment.RESULT_RESTORE
            mMode = savedInstanceState.getInt(ARG_MODE)
        }

        // ViewModelの内容に従って部品を設定する
        if(viewModel.editScheduleItem != null){
            val item = viewModel.editScheduleItem!!
            when(item.type) {
                "weekday" -> {
                    loadInputScheduleLayout(item.type)
                    Log.d(this.javaClass.simpleName,"Preset weekday start: $item")
                    scheduleGroup.check(R.id.weekdayRadio)
                    weekdayWeekdayList.setSelection(item.weekdayValue.toInt())
                }
                "month" -> {
                    loadInputScheduleLayout(item.type)
                    Log.d(this.javaClass.simpleName,"Preset month start: $item")
                    scheduleGroup.check(R.id.monthRadio)
                    monthDateList.setSelection(item.monthValue.toInt() - 1)
                }
                "biweek" -> {
                    loadInputScheduleLayout(item.type)
                    Log.d(this.javaClass.simpleName,"Preset num of week start: $item")
                    scheduleGroup.check(R.id.numOfWeekRadio)
                    numOfWeekList.setSelection(item.numOfWeekNumberValue.toInt() - 1)
                    numOfWeekWeekdayList.setSelection(item.numOfWeekWeekdayValue.toInt())
                }
                "evweek" -> {
                    // 隔週はフラグメントを使うためプリセットかつ初期表示時のみ
                    // loadInputScheduleLayoutを使わずにパラメータをFragmentに渡して生成する
                    // 初期表示以外は自動で復帰する
                    if(savedInstanceState == null) {
                        childFragmentManager.let { fm ->
                            fm.beginTransaction().let { ft ->
                                val evweekFragment = EditInputEvweekFragment.newInstance(
                                    item.evweekWeekdayValue,
                                    item.evweekIntervalValue,
                                    item.evweekStartValue
                                )
                                ft.add(R.id.scheduleInput, evweekFragment)
                                ft.commit()
                            }
                        }
                    }
                    scheduleGroup.check(R.id.evweekRadio)
                }
            }
        }

        // プリセット or onResumeからの復帰時に2重でレイアウトの描画を防ぐために
        // ここでスケジュールラジオボタンのアクションを定義する
        scheduleGroup.setOnCheckedChangeListener { _, checkedId ->
            Log.d(this.javaClass.simpleName,"ScheduleGroup.onCheckChange: $checkedId")
            val scheduleType:String = (view.findViewById(checkedId) as RadioButton).tag as String
            loadInputScheduleLayout(scheduleType)
        }

        // 親Fragmentに追加モードを伝える
        parentFragment?.onActivityResult(
            mMode!!,
            resultCode,
            null
        )
    }


    /*
    InputFragmentListenerの実装
     */

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ARG_MODE, mMode ?: 0)
    }

    /**
     * 画面部品の設定値をEditScheduleItemにして返す
     */
    override fun getInputValue(): EditScheduleItem {
        val schedule =
            EditScheduleItem()
        schedule.type = view?.findViewById<RadioButton>(scheduleGroup.checkedRadioButtonId)?.tag.toString()
        when(schedule.type) {
            resources.getString(R.string.schedule_weekday) -> {
                schedule.weekdayValue = weekdayWeekdayList.selectedItemPosition.toString()
            }
            resources.getString(R.string.schedule_month) -> {
                schedule.monthValue = (monthDateList.selectedItemPosition+1).toString()
            }
            resources.getString(R.string.schedule_numOfWeek) -> {
                schedule.numOfWeekWeekdayValue = numOfWeekWeekdayList.selectedItemPosition.toString()
                schedule.numOfWeekNumberValue = (numOfWeekList.selectedItemPosition+1).toString()
            }
            resources.getString(R.string.schedule_evweek) -> {
                val fragment = childFragmentManager.fragments[0]
                if(fragment is EditInputEvweekFragment) {
                    schedule.evweekWeekdayValue = fragment.evweekWeekdayList.selectedItemPosition.toString()
                    schedule.evweekIntervalValue = fragment.evweekIntervalList.selectedItemPosition + 2
                    schedule.evweekStartValue = fragment.evweekDateText.text.toString()
                }
            }
        }
        return schedule
    }

    /**
     * 現在の設定値をViewModelに反映する
     */
    override fun onPause() {
        super.onPause()
        viewModel.editScheduleItem = getInputValue()
    }

    /**
     * 他のスケジュールが変更（追加または削除）された場合に復元モードを変更する
     * 現在は削除された場合のみだが拡張性を考慮して条件分けしている
     * @param changeType 1:追加、2：削除
     */
    override fun changeMode(changeType: Int) {
        when(changeType) {
            2-> {
                when(mMode) {
                    // 最後のインデックスだった場合は復元時に削除&追加ボタンを設置する
                    EditMainFragment.REQUEST_DELETE_BUTTON -> {
                        Log.d(this.javaClass.simpleName, "ChangeMode 2->3")
                        mMode = EditMainFragment.REQUEST_ADD_DELETE_BUTTON
                    }
                }
            }
        }
    }

    /**
     * 選択されたスケジュールのレイアウトを呼び出しFragment上に追加（上書き）する
     */
    @SuppressLint("InflateParams")
    private fun loadInputScheduleLayout(scheduleType: String) {
        scheduleInput.removeAllViews()

        val inputView:View? = when(scheduleType) {
            getString(R.string.schedule_weekday) -> layoutInflater.inflate(
                R.layout.input_weekday,null)
            getString(R.string.schedule_month) -> layoutInflater.inflate(
                R.layout.input_month,null)
            getString(R.string.schedule_numOfWeek) -> layoutInflater.inflate(
                R.layout.input_num_of_week, null)
            "evweek" -> {
                childFragmentManager.let {fm ->
                    fm.beginTransaction().let {ft->
                        val evweekFragment = EditInputEvweekFragment.newInstance()
                        ft.add(R.id.scheduleInput,evweekFragment)
                        ft.commit()
                    }
                }
                null
            }
            else -> null
        }
        inputView?.let {
            scheduleInput.addView(it)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(mode: Int, preset: EditScheduleItem?) =
            InputFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_MODE, mode)
                    preset?.let {
                        putSerializable(ARG_PRESET, it)
                    }
                }
            }

        private const val ARG_MODE = "mode"
        private const val ARG_PRESET = "preset"
    }
}