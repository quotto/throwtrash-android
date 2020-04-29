package net.mythrowaway.app.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_edit_main.*
import net.mythrowaway.app.R
import net.mythrowaway.app.adapter.DIContainer
import net.mythrowaway.app.adapter.IEditView
import net.mythrowaway.app.adapter.presenter.EditPresenterImpl
import net.mythrowaway.app.adapter.controller.EditControllerImpl
import net.mythrowaway.app.adapter.presenter.EditViewModel
import net.mythrowaway.app.usecase.ICalendarManager
import net.mythrowaway.app.usecase.TrashManager


class EditMainFragment : Fragment(), AdapterView.OnItemSelectedListener, IEditView {
    private lateinit var controllerImpl: EditControllerImpl

    @SuppressLint("InflateParams")
    private fun createAddButton(): ImageButton {
        val addButton: ImageButton =
            layoutInflater.inflate(R.layout.add_button, null) as ImageButton
        addButton.setOnClickListener {
            scheduleContainer.removeView(it)
            controllerImpl.addTrashSchedule()
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

            controllerImpl.deleteSchedule(index)
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

    override fun complete() {
        Toast.makeText(context,getString(R.string.message_complete_edit),Toast.LENGTH_SHORT).show()
        activity?.setResult(Activity.RESULT_OK,null)
        activity?.finish()
    }

    override fun showTrashData(viewModel: EditViewModel) {
        Log.d(this.javaClass.simpleName, "Show TrashData -> $viewModel")
        val requestModes = arrayListOf(REQUEST_ADD_BUTTON, REQUEST_ADD_DELETE_BUTTON,
            REQUEST_DELETE_BUTTON)
        val trashIndex = resources.getStringArray(R.array.list_trash_id_select).indexOf(viewModel.type)
        trashTypeList.setSelection(trashIndex)
        if(viewModel.type == "other") {
            otherTrashText.setText(viewModel.trashVal)
        }

        repeat(viewModel.schedule.size) { index ->
            val fragment:InputFragment = InputFragment.newInstance(requestModes[index],viewModel.schedule[index])
            childFragmentManager.let {fm ->
                fm.beginTransaction().let {ft ->
                    ft.add(R.id.scheduleContainer, fragment)
                    ft.commitNow()
                }
            }
        }
    }

    override fun showErrorMaxSchedule() {
        Toast.makeText(context,getString(R.string.message_max_schedule),Toast.LENGTH_LONG).show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(trashTypeList.count == position+1) {
            otherTrashText.visibility = View.VISIBLE
            registerButton.isEnabled = false
            controllerImpl.checkOtherText(otherTrashText.text.toString(), this)
        } else {
            otherTrashText.setText("")
            otherTrashText.visibility = View.INVISIBLE
            registerButton.isEnabled = true
        }
    }
    override fun onNothingSelected(parent: AdapterView<*>?) {
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controllerImpl =
            EditControllerImpl(
                EditPresenterImpl(
                    DIContainer.resolve(
                        ICalendarManager::class.java
                    )!!,
                    DIContainer.resolve(
                        TrashManager::class.java
                    )!!,
                    this
                )
            )
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
            controllerImpl.loadTrashData(arguments?.getString(ID))
        }
        trashTypeList.onItemSelectedListener = this
        cancelButton.setOnClickListener {
            activity?.finish()
        }
        registerButton.setOnClickListener {
            controllerImpl.saveTrashData(getRegisteredData())
        }

        otherTrashText.setOnKeyListener { _, _, _ ->
            controllerImpl.checkOtherText(otherTrashText.text.toString(), this)
            false
        }
    }

    override fun showOtherTextError(resultCode: Int) {
        when(resultCode) {
            1 -> {
                otherTrashErrorText.text = resources.getString(R.string.error_otherText_empty)
                otherTrashErrorText.visibility = View.VISIBLE
                registerButton.setEnabled(false)
            }
            2 -> {
                otherTrashErrorText.text = resources.getString(R.string.error_otherText_illegalCharacter)
                otherTrashErrorText.visibility = View.VISIBLE
                registerButton.setEnabled(false)
            }
            else -> {
                otherTrashErrorText.visibility = View.INVISIBLE
                registerButton.setEnabled(true)
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
            Log.d(this.javaClass.simpleName, "New instance -> id=$id")
            id?.let{
                val bundle = Bundle()
                bundle.putString(ID,id)
                instance.arguments = bundle
            }
            return instance
        }
    }
}
