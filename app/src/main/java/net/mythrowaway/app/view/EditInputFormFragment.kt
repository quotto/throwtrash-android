package net.mythrowaway.app.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.lifecycle.ViewModelProvider
import net.mythrowaway.app.R
import net.mythrowaway.app.databinding.*
import net.mythrowaway.app.viewmodel.EditScheduleItem
import net.mythrowaway.app.viewmodel.EditScheduleItemViewModel
import net.mythrowaway.app.viewmodel.EvWeekViewModel

interface InputFragmentListener {
    fun getInputValue(): EditScheduleItem
    fun changeMode(changeType:Int)
}

class InputFragment : Fragment(),
    InputFragmentListener {
    // 親FragmentのrequestCodeとして機能し,追加ボタン,削除ボタンの要否を伝える
    private var mMode: Int? = null

    private lateinit var viewModel: EditScheduleItemViewModel

    private lateinit var fragmentEditInputBinding: FragmentEditInputBinding
    private lateinit var inputWeekdayBinding: InputWeekdayBinding
    private lateinit var inputMonthBinding: InputMonthBinding
    private lateinit var inputNumOfWeekBinding: InputNumOfWeekBinding

    /*
    Fragmentの実装
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mMode = it.getInt(ARG_MODE)
            Log.d(this.javaClass.simpleName, "Set mode: $mMode")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentEditInputBinding = FragmentEditInputBinding.inflate(inflater, container, false)
        return fragmentEditInputBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(EditScheduleItemViewModel::class.java)

        if (savedInstanceState == null) {
            // 初期表示かつ保存済みデータの編集の場合はViewModelに反映
            arguments?.getSerializable(ARG_PRESET)?.also {
                viewModel.editScheduleItem = it as EditScheduleItem
            } ?: run {
                // 初期表示かつ新規データの場合はデフォルトで毎週を設定する
                fragmentEditInputBinding.toggleEveryweek.isChecked = true
                fragmentEditInputBinding.toggleEveryweek.isClickable = true
                loadInputScheduleLayout("weekday")
            }
        } else {
            // 復帰時は親Fragmentに伝えるためのスケジュール追加モードも復元
            mMode = savedInstanceState.getInt(ARG_MODE)
        }

        // ViewModelの内容に従って部品を設定する
        if (viewModel.editScheduleItem != null) {
            val item = viewModel.editScheduleItem!!
            when (item.type) {
                "weekday" -> {
                    loadInputScheduleLayout(item.type)
                    Log.d(this.javaClass.simpleName, "Preset weekday start: $item")
                    fragmentEditInputBinding.toggleEveryweek.toggle()
                    inputWeekdayBinding.weekdayWeekdayList.setSelection(item.weekdayValue.toInt())
                }
                "month" -> {
                    loadInputScheduleLayout(item.type)
                    Log.d(this.javaClass.simpleName, "Preset month start: $item")
                    fragmentEditInputBinding.toggleEveryMonth.toggle()
                    inputMonthBinding.monthDateList.setSelection(item.monthValue.toInt() - 1)
                }
                "biweek" -> {
                    loadInputScheduleLayout(item.type)
                    Log.d(this.javaClass.simpleName, "Preset num of week start: $item")
                    fragmentEditInputBinding.toggleNumOfWeek.toggle()
                    inputNumOfWeekBinding.numOfWeekList.setSelection(item.numOfWeekNumberValue.toInt() - 1)
                    inputNumOfWeekBinding.numOfWeekWeekdayList.setSelection(item.numOfWeekWeekdayValue.toInt())
                }
                "evweek" -> {
                    // 隔週はフラグメントを使うためプリセットかつ初期表示時のみ
                    // loadInputScheduleLayoutを使わずにパラメータをFragmentに渡して生成する
                    // 初期表示以外は自動で復帰する
                    if (savedInstanceState == null) {
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
                    fragmentEditInputBinding.toggleEvWeek.toggle()
                }
            }
        }

        // プリセット or onResumeからの復帰時に2重でレイアウトの描画を防ぐために
        // ここでスケジュールオプションボタンのアクションを定義する

        listOf(
            fragmentEditInputBinding.toggleEveryweek,
            fragmentEditInputBinding.toggleEveryMonth,
            fragmentEditInputBinding.toggleNumOfWeek,
            fragmentEditInputBinding.toggleEvWeek
        ).forEach { clickedButton ->
            clickedButton.setOnClickListener {
                Log.d(this.javaClass.simpleName, "ToggleButtons.onClick: ${it.tag}")
                val scheduleType: String = clickedButton.tag as String
                getToggleButtons().forEach { toggleButton ->
                    Log.d(
                        this.javaClass.simpleName,
                        "target:toggleButton ${clickedButton.id}:${toggleButton.id}"
                    )
                    toggleButton.isClickable = clickedButton.id != toggleButton.id
                    toggleButton.isChecked = clickedButton.id == toggleButton.id
                }
                loadInputScheduleLayout(scheduleType)
            }
        }

        if(parentFragment is MainEditListener) {
            (parentFragment as MainEditListener).notifyAppendInputFragment(mMode!!)
        }
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
        val schedule = EditScheduleItem()
        getToggleButtons().forEach {
            if (it.isChecked) {
                schedule.type = it.tag.toString()
                return@forEach
            }
        }
        when (schedule.type) {
            resources.getString(R.string.schedule_weekday) -> {
                schedule.weekdayValue = inputWeekdayBinding.weekdayWeekdayList.selectedItemPosition.toString()
            }
            resources.getString(R.string.schedule_month) -> {
                schedule.monthValue = (inputMonthBinding.monthDateList.selectedItemPosition + 1).toString()
            }
            resources.getString(R.string.schedule_numOfWeek) -> {
                schedule.numOfWeekWeekdayValue =
                    inputNumOfWeekBinding.numOfWeekWeekdayList.selectedItemPosition.toString()
                schedule.numOfWeekNumberValue = (inputNumOfWeekBinding.numOfWeekList.selectedItemPosition + 1).toString()
            }
            resources.getString(R.string.schedule_evweek) -> {
                val fragment = childFragmentManager.fragments[0]
                if (fragment is EditInputEvweekFragment) {
                    val evWeekViewModel: EvWeekViewModel = fragment.getEvWeekViewModel()
                    schedule.evweekIntervalValue = evWeekViewModel.interval
                    schedule.evweekStartValue = evWeekViewModel.start
                    schedule.evweekWeekdayValue = evWeekViewModel.weekday
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
        when (changeType) {
            2 -> {
                when (mMode) {
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
        fragmentEditInputBinding.scheduleInput.removeAllViews()

        val inputView: View? = when (scheduleType) {
            getString(R.string.schedule_weekday) -> {
                inputWeekdayBinding = InputWeekdayBinding.bind(layoutInflater.inflate(
                    R.layout.input_weekday, null
                ))
                inputWeekdayBinding.root
            }
            getString(R.string.schedule_month) -> {
                inputMonthBinding = InputMonthBinding.bind(layoutInflater.inflate(
                    R.layout.input_month, null
                ))
                inputMonthBinding.root
            }
            getString(R.string.schedule_numOfWeek) -> {
                inputNumOfWeekBinding = InputNumOfWeekBinding.bind(layoutInflater.inflate(
                    R.layout.input_num_of_week, null
                ))
                inputNumOfWeekBinding.root
            }
            "evweek" -> {
                childFragmentManager.let { fm ->
                    fm.beginTransaction().let { ft ->
                        val evweekFragment = EditInputEvweekFragment.newInstance()
                        ft.add(fragmentEditInputBinding.scheduleInput.id, evweekFragment)
                        ft.commit()
                    }
                }
                null
            }
            else -> null
        }
        inputView?.let {
            fragmentEditInputBinding.scheduleInput.addView(it)
        }
    }

    private fun  getToggleButtons():List<ToggleButton> {
        return listOf<ToggleButton>(
            fragmentEditInputBinding.toggleEveryweek,
            fragmentEditInputBinding.toggleEveryMonth,
            fragmentEditInputBinding.toggleNumOfWeek,
            fragmentEditInputBinding.toggleEvWeek
        )
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

