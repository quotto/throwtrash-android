package com.example.mythrowtrash.view

import android.annotation.SuppressLint
import android.app.Activity
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
import com.example.mythrowtrash.adapter.*
import com.example.mythrowtrash.domain.TrashData
import com.example.mythrowtrash.usecase.ICalendarManager
import com.example.mythrowtrash.usecase.TrashManager
import kotlinx.android.synthetic.main.fragment_edit_main.*


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

    @SuppressLint("InflateParams")
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

    @SuppressLint("InflateParams")
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
        viewModel.type = resources.getStringArray(R.array.list_trash_id_select)[trashTypeList.selectedItemPosition]
        if(viewModel.type == "other") viewModel.trashVal = otherTrashText.text.toString()
        childFragmentManager.fragments.forEach{
            if(it is InputFragmentListener) {
                viewModel.schedule.add(it.getInputValue())
            }
        }
        viewModel.id = arguments?.getString(ID)
        return viewModel
    }

    override fun complete(trashData: TrashData) {
        Toast.makeText(context,getString(R.string.message_complete_edit),Toast.LENGTH_SHORT).show()
        activity?.setResult(Activity.RESULT_OK,null)
        activity?.finish()
    }

    override fun showTrashDtada(viewModel: EditViewModel) {
        println("[MyApp - EditMainFragment] showTrashData: $viewModel")
        val requestModes = arrayListOf(REQUEST_ADD_BUTTON, REQUEST_ADD_DELETE_BUTTON,
            REQUEST_DELETE_BUTTON)
        val trashIndex = resources.getStringArray(R.array.list_trash_id_select).indexOf(viewModel.type)
        trashTypeList.setSelection(trashIndex)
        if(viewModel.type == "other") {
            otherTrashText.setText(viewModel.trashVal)
        }

        repeat(viewModel.schedule.size) {index ->
            val fragment:InputFragment = InputFragment.newInstance(requestModes[index],viewModel.schedule[index])
            childFragmentManager.let {fm ->
                fm.beginTransaction().let {ft ->
                    ft.add(R.id.scheduleContainer, fragment)
                    ft.commitNow()
                }
            }
        }
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
        controller = EditController(EditPresenter(DIContainer.resolve(ICalendarManager::class.java)!!,DIContainer.resolve(TrashManager::class.java)!!,this))
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(savedInstanceState == null) {
            controller.loadTrashData(arguments?.getString(ID))
        }
        trashTypeList.onItemSelectedListener = this
        cancelButton.setOnClickListener {
            activity?.finish()
        }
        registButton.setOnClickListener {
            controller.saveTrashData(getRegisteredData())
        }

        otherTrashText.setOnKeyListener { _, _, _ ->
            controller.checkOtherText(otherTrashText.text.toString(), this)
            false
        }
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
        childFragmentManager.let {fm ->
            val newInputFragment = InputFragment.newInstance(mode,null)
            fm.beginTransaction().let {ft ->
                ft.add(R.id.scheduleContainer, newInputFragment)
                ft.commitNow()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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

    override fun deleteTrashSchedule(delete_index: Int, nextAdd: Boolean) {
        childFragmentManager.let {fm->
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

        fun getInstance(id: String?): EditMainFragment {
            val instance = EditMainFragment()
            println("[MyApp - EditMainFragment] new instance @ id:$id")
            id?.let{
                val bundle = Bundle()
                bundle.putString(ID,id)
                instance.arguments = bundle
            }
            return instance
        }
    }
}
