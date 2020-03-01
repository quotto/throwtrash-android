package com.example.mythrowtrash.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.example.mythrowtrash.R
import com.example.mythrowtrash.adapter.EditController
import com.example.mythrowtrash.adapter.EditPresenter
import com.example.mythrowtrash.adapter.EditViewModel
import com.example.mythrowtrash.adapter.IEditView
import com.example.mythrowtrash.domain.TrashData
import kotlinx.android.synthetic.main.fragment_schedule_main.*


class EditMainFragment : Fragment(), AdapterView.OnItemSelectedListener,
    IEditView {
    private lateinit var controller:EditController
    private fun Button.changeEnabled(enabled: Boolean) {
        isEnabled = enabled
        if(isEnabled) {
            background.colorFilter = null
        } else {
            val colorMatrix = ColorMatrix()
            colorMatrix.setScale(0.299f,0.587f,0.114f,0.1f)
            background.colorFilter = ColorMatrixColorFilter(colorMatrix)
        }
    }

    private fun createAddButton(): ImageButton {
        val addButton: ImageButton =
            layoutInflater.inflate(R.layout.add_button, null) as ImageButton
        addButton.setOnClickListener {
            scheduleContainer.removeView(it)
            controller.addTrashSchedule()
        }
        addButton.tag = "addScheduleButton"
        return addButton
    }

    private fun createRemoveButton(): ImageButton {
        val removeButton: ImageButton =
            layoutInflater.inflate(R.layout.delete_button, null) as ImageButton
        removeButton.setOnClickListener {
            // 削除対象Fragmentのインデックス算出
            // 削除ボタン→Fragment本体の順でカウントすることで1/2した値がフラグメントのインデックス
            // ただし1件目のスケジュールの削除ボタンは存在しないため、削除ボタンのインデックスに1を加算する
            val index = (scheduleContainer.indexOfChild(it) + 1) / 2

            controller.deleteSchedule(index)
            scheduleContainer.removeView(it)
        }
        return removeButton
    }

    private fun getRegisteredData(): EditViewModel {
        val viewModel = EditViewModel()
        viewModel.type = resources.getStringArray(R.array.trashIdList)[trashTypeList.selectedItemPosition]
        if(viewModel.type == "other") viewModel.trashVal = otherTrashText.text.toString()
        childFragmentManager?.fragments?.forEach{
            if(it is InputFragmentListener) {
                viewModel.schedule.add(it.getInputValue())
            }
        }
        arguments?.getInt(ID)?.let {
            viewModel.id = it
        }
        return viewModel
    }

    override fun complete(trashData: TrashData) {
        Toast.makeText(context,"ゴミ出し予定を登録しました",Toast.LENGTH_SHORT).show()
        activity?.setResult(Activity.RESULT_OK,null)
        activity?.finish()
    }

    override fun showTrashDtada(viewModel: EditViewModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(trashTypeList.count == position+1) {
            otherTrashText.visibility = View.VISIBLE
            registButton.changeEnabled(false)
            controller.checkOtherText(otherTrashText.text.toString(), this)
        } else {
            otherTrashText.setText("")
            otherTrashText.visibility = View.INVISIBLE
            registButton.changeEnabled(true)
        }
    }
    override fun onNothingSelected(parent: AdapterView<*>?) {
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("[MyApp] parent onCreate")
        controller = EditController(EditPresenter(this))
        retainInstance = true
    }

    override fun onResume() {
        super.onResume()
        println("[MyApp] parent onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("[MyApp] parent onDestroy")
    }

    override fun onPause() {
        super.onPause()
        println("[MyApp] parent onPause")
    }

    override fun onDetach() {
        super.onDetach()
        println("[MyApp] parent onDetach")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        println("[MyApp] parent onCreateView")
        return inflater.inflate(R.layout.fragment_schedule_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(savedInstanceState == null) {
            controller.addTrashSchedule()
        }
        trashTypeList.onItemSelectedListener = this
        cancelButton.setOnClickListener {
            activity?.finish()
        }
        registButton.setOnClickListener {
            controller.saveTrashData(getRegisteredData())
        }

        otherTrashText.setOnKeyListener { v, keyCode, event ->
            controller.checkOtherText(otherTrashText.text.toString(), this)
            false
        }

        println("[MyApp] parent viewCreated")
    }

    override fun showOtherTextError(resultCode: Int) {
        when(resultCode) {
            1 -> {
                otherTrashErrorText.text = resources.getString(R.string.error_otherText_empty)
                otherTrashErrorText.visibility = View.VISIBLE
                registButton.changeEnabled(false)
            }
            2 -> {
                otherTrashErrorText.text = resources.getString(R.string.error_otherText_illegalCharacter)
                otherTrashErrorText.visibility = View.VISIBLE
                registButton.changeEnabled(false)
            }
            else -> {
                otherTrashErrorText.visibility = View.INVISIBLE
                registButton.changeEnabled(true)
            }
        }
    }

    override fun addTrashSchedule(nextAdd: Boolean, deleteEnabled: Boolean) {
        val mode:Int = if(nextAdd && deleteEnabled) {REQUEST_ADD_DELETE_BUTTON} else if(nextAdd) {REQUEST_ADD_BUTTON} else if(deleteEnabled) {REQUEST_DELETE_BUTTON} else{REQUEST_NONE}
        childFragmentManager?.let {fm ->
            val newInputFragment = InputFragment.newInstance(mode)
            fm.beginTransaction().let {ft ->
                ft.add(R.id.scheduleContainer, newInputFragment)
                ft.commitNow()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("[MyApp] parent onActivityResult ${requestCode}")
        when(requestCode) {
            REQUEST_ADD_BUTTON -> {
                scheduleContainer.addView(createAddButton())
            }
            REQUEST_DELETE_BUTTON -> {
                scheduleContainer.findViewWithTag<ImageButton>("addScheduleButton")?.let {
                    scheduleContainer.removeView(it)
                }
                scheduleContainer.addView(createRemoveButton(),scheduleContainer.childCount - 1)
            }
            REQUEST_ADD_DELETE_BUTTON -> {
                scheduleContainer.findViewWithTag<ImageButton>("addScheduleButton")?.let {
                    scheduleContainer.removeView(it)
                }
                scheduleContainer.addView(createRemoveButton(),scheduleContainer.childCount - 1)
                scheduleContainer.addView(createAddButton())
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        println("[MyApp] parent onAttach")

    }

    override fun deleteTrashSchedule(delete_index: Int, nextAdd: Boolean) {
        childFragmentManager?.let {fm->
            val targetFragment: InputFragment = fm.fragments[delete_index] as InputFragment
            if(targetFragment != fm.fragments.last() && targetFragment != fm.fragments.first()) {
                // 中間のインデックスが削除された場合には最後尾の子Fragmentのmodeを変更する
                (fm.fragments.last() as InputFragmentListener).changeMode(2)
            }
            fm.beginTransaction().apply {
                remove(fm.fragments[delete_index])
                commit()
            }
            if(nextAdd) {
                scheduleContainer.addView(createAddButton())
            }
        }
    }

    companion object {
        const val REQUEST_NONE: Int = 0
        const val REQUEST_ADD_BUTTON: Int = 1
        const val REQUEST_DELETE_BUTTON: Int = 2
        const val REQUEST_ADD_DELETE_BUTTON: Int = 3
        const val ID: String = "ID"

        fun getInstance(id: Int): EditMainFragment {
            val instance = EditMainFragment()
            if(id > 0)  {
                val bundle = Bundle()
                bundle.putInt(ID,id)
                instance.arguments = bundle
            }
            return instance
        }
    }
}
