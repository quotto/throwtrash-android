package net.mythrowaway.app.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
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
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_edit_main.*
import net.mythrowaway.app.R
import net.mythrowaway.app.adapter.IEditView
import net.mythrowaway.app.adapter.controller.EditControllerImpl
import net.mythrowaway.app.usecase.IEditPresenter
import net.mythrowaway.app.viewmodel.EditItemViewModel
import net.mythrowaway.app.viewmodel.EditViewModel
import javax.inject.Inject

class EditMainFragment : Fragment(), AdapterView.OnItemSelectedListener, IEditView {
    @Inject
    lateinit var controllerImpl: EditControllerImpl
    @Inject
    lateinit var presenter: IEditPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as EditActivity).editComponent.inject(this)
        presenter.setView(this)
    }

    private val model by lazy {
        ViewModelProviders.of(this).get(EditViewModel::class.java)
    }

    /*
    Fragmentの実装
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(this.javaClass.simpleName, "onCreate@${this.hashCode()}")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(savedInstanceState == null) {
            // 初期表示は能動的に画面部品を描画する
            controllerImpl.loadTrashData(arguments?.getString(ID))
        } else {
            // 復帰時はViewModelのデータ件数をドメインに伝えるだけ
            controllerImpl.loadTrashData(this, model)
        }

        trashTypeList.onItemSelectedListener = this

        cancelButton.setOnClickListener {
            activity?.finish()
        }
        registerButton.setOnClickListener {
            controllerImpl.saveTrashData(makeEditItem())
        }

        otherTrashText.addTextChangedListener {
            controllerImpl.checkOtherText(it.toString(), this)
        }

        buttonSetExcludeDate.setOnClickListener {
            val intent = Intent(context,EditExcludeDayActivity::class.java)
            intent.putExtra(
                EditExcludeDayActivity.EXTRA_TRASH_NAME,
                trashTypeList.selectedItem as String
            )
            intent.putExtra(
                EditExcludeDayActivity.EXTRA_EXCLUDE_DATE_SET,
                model.excludes
            )
            startActivityForResult(intent, REQUEST_SET_EXCLUDE_DATE)
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(this.javaClass.simpleName, "onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(this.javaClass.simpleName, "onDestroy")
    }

    /**
     * 子Fragment（画面部品）の生成が終わったら部品を追加可能かのメッセージが送られてくる
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(this.javaClass.simpleName, "onActivityResult -> requestCode=$requestCode")
        when(requestCode) {
            REQUEST_ADD_BUTTON -> {
                // 部品追加のみ（デフォルトで描画する1件目のスケジュール）
                scheduleContainer.addView(createAddButton())
            }
            REQUEST_DELETE_BUTTON -> {
                // 削除のみ、部品追加不可能（3件目のスケジュール）
                scheduleContainer.findViewWithTag<ImageButton>("addScheduleButton")?.let {
                    scheduleContainer.removeView(it)
                }
                scheduleContainer.addView(createRemoveButton(),scheduleContainer.childCount - 1)
            }
            REQUEST_ADD_DELETE_BUTTON -> {
                // 追加・削除可能（2件目のスケジュール）
                scheduleContainer.findViewWithTag<ImageButton>("addScheduleButton")?.let {
                    scheduleContainer.removeView(it)
                }
                scheduleContainer.addView(createRemoveButton(),scheduleContainer.childCount - 1)
                scheduleContainer.addView(createAddButton())
            }
            REQUEST_SET_EXCLUDE_DATE -> {
                // 例外日設定をViewModelに反映する
                if(resultCode == Activity.RESULT_OK) {
                    val excludeDate =
                        data?.getSerializableExtra(
                                EditExcludeDayActivity.EXTRA_EXCLUDE_DATE_SET
                        ) as ArrayList<Pair<Int,Int>>
                    model.excludes = excludeDate
                    Log.d(
                        javaClass.simpleName,
                        "Return Exclude Date -> $excludeDate"
                    )
                }
            }
        }
    }

    /*　onItemSelectedListenerの実装　*/

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(trashTypeList.count == position+1) {
            otherTrashText.visibility = View.VISIBLE
            registerButton.isEnabled = false
            controllerImpl.checkOtherText(otherTrashText.text.toString(), this)
        } else {
            otherTrashText.setText("")
            otherTrashText.visibility = View.INVISIBLE
            otherTrashErrorText.visibility = View.INVISIBLE
            registerButton.isEnabled = true
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    /*
    IEditViewの実装
     */

    override fun complete() {
        Toast.makeText(context,getString(R.string.message_complete_edit),Toast.LENGTH_SHORT).show()
        activity?.setResult(Activity.RESULT_OK,null)
        activity?.finish()
    }

    /**
     * 既存データの初期表示の時のみコールされる
     * ControllerからloadTrashDataを呼び出すとUseCaseを実行、
     * Presenterから初期表示用のEditItemを受け取りFragmentを設定する
     */
    override fun setTrashData(item: EditItemViewModel) {
        // ゴミの種類の設定
        val trashIndex = resources.getStringArray(R.array.list_trash_id_select).indexOf(item.type)
        trashTypeList.setSelection(trashIndex)
        if(item.type == "other") {
            otherTrashText.setText(item.trashVal)
        }

        // スケジュールの数だけInputFragmentを追加する
        val requestModes = arrayListOf(REQUEST_ADD_BUTTON, REQUEST_ADD_DELETE_BUTTON,
            REQUEST_DELETE_BUTTON)
        repeat(item.scheduleItem.size) { index ->
            val fragment: InputFragment = InputFragment.newInstance(requestModes[index],item.scheduleItem[index])
            childFragmentManager.let { fm ->
                fm.beginTransaction().let { ft ->
                    ft.add(R.id.scheduleContainer, fragment)
                    ft.commitNow()
                }
            }
        }
        model.excludes = item.excludes
    }

    override fun showErrorMaxSchedule() {
        Toast.makeText(context,getString(R.string.message_max_schedule),Toast.LENGTH_LONG).show()
    }

    override fun showOtherTextError(resultCode: Int) {
        if(resources.getStringArray(R.array.list_trash_id_select)[trashTypeList.selectedItemPosition] == "other") {
            when (resultCode) {
                1 -> {
                    otherTrashErrorText.text = resources.getString(R.string.error_otherText_empty)
                    otherTrashErrorText.visibility = View.VISIBLE
                    registerButton.isEnabled = false
                }
                2 -> {
                    otherTrashErrorText.text =
                        resources.getString(R.string.error_otherText_illegalCharacter)
                    otherTrashErrorText.visibility = View.VISIBLE
                    registerButton.isEnabled = false
                }
                else -> {
                    otherTrashErrorText.visibility = View.INVISIBLE
                    registerButton.isEnabled = true
                }
            }
        }
    }

    /**
     *  新規にスケジュールを追加する
     */
    override fun addTrashSchedule(nextAdd: Boolean, deleteEnabled: Boolean) {
        // このスケジュールを追加した場合に次のスケジュール追加/削除ボタン生成のタイプを引数として渡す
        // 子Fragmentは生成後に親Fragment（当クラス）のonActivityResultにmodeを渡すことで親Fragmentは追加/削除ボタンを設定する
        val mode:Int = if(nextAdd && deleteEnabled) {REQUEST_ADD_DELETE_BUTTON} else if(nextAdd) {REQUEST_ADD_BUTTON} else if(deleteEnabled) {REQUEST_DELETE_BUTTON} else{REQUEST_NONE}
        childFragmentManager.let {fm ->
            val newInputFragment = InputFragment.newInstance(mode,null)
            fm.beginTransaction().let {ft ->
                ft.add(R.id.scheduleContainer, newInputFragment)
                ft.commitNow()
            }
        }
        model.itemCount++
    }

    /**
     * スケジュールを削除する
     */
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
        model.itemCount--
    }


    /*
    固有の実装
     */

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
            // 削除ボタン→Fragment本体の順でカウントすることで1/2した値がフラグメントのインデックスになる
            // ただし1件目のスケジュールの削除ボタンは存在しないため、削除ボタンのインデックスに1を加算する
            val index = (scheduleContainer.indexOfChild(it) + 1) / 2

            controllerImpl.deleteSchedule(index)
            scheduleContainer.removeView(it)
        }
        return removeButton
    }

    private fun makeEditItem(): EditItemViewModel {
        val editItem = EditItemViewModel()
        editItem.type = resources.getStringArray(R.array.list_trash_id_select)[trashTypeList.selectedItemPosition]
        if(editItem.type == "other") editItem.trashVal = otherTrashText.text.toString()
        childFragmentManager.fragments.forEach{
            if(it is InputFragmentListener) {
                editItem.scheduleItem.add(it.getInputValue())
            }
        }
        editItem.id = arguments?.getString(ID)
        editItem.excludes = model.excludes
        return editItem
    }



    companion object {
        const val REQUEST_NONE: Int = 0
        const val REQUEST_ADD_BUTTON: Int = 1
        const val REQUEST_DELETE_BUTTON: Int = 2
        const val REQUEST_ADD_DELETE_BUTTON: Int = 3
        const val REQUEST_SET_EXCLUDE_DATE: Int = 4
        const val RESULT_INIT = 4
        const val RESULT_RESTORE = 5
        const val ID: String = "ID"

        fun getInstance(id: String?): EditMainFragment {
            val instance = EditMainFragment()
            Log.d(this::class.java.simpleName, "New instance -> id=$id")
            id?.let{
                val bundle = Bundle()
                bundle.putString(ID,id)
                instance.arguments = bundle
            }
            return instance
        }
    }
}
