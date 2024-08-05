package net.mythrowaway.app.view.edit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import net.mythrowaway.app.R
import net.mythrowaway.app.adapter.MyThrowTrash
import net.mythrowaway.app.adapter.di.EditComponent
import net.mythrowaway.app.databinding.FragmentEditMainBinding
import net.mythrowaway.app.viewmodel.edit.EditTrashViewModel
import net.mythrowaway.app.viewmodel.edit.ScheduleMessage
import javax.inject.Inject

class NeoEditActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener,
  CoroutineScope by MainScope() {
  private lateinit var  activityEditBinding: FragmentEditMainBinding
  @Inject
  lateinit var editTrashViewModelFactory: EditTrashViewModel.Factory

  private val _editTrashViewModel: EditTrashViewModel by lazy {
    ViewModelProvider(this, editTrashViewModelFactory).get(EditTrashViewModel::class.java)
  }

  lateinit var editComponent: EditComponent
  override fun onCreate(savedInstanceState: Bundle?) {
    editComponent = (application as MyThrowTrash).appComponent.editComponent().create();
    editComponent.inject(this)
    super.onCreate(savedInstanceState)
    Log.d("NeoEditActivity", "onCreate")
    activityEditBinding = FragmentEditMainBinding.inflate(layoutInflater)
    setContentView(activityEditBinding.root)

    activityEditBinding.trashTypeList.onItemSelectedListener = this


    activityEditBinding.cancelButton.setOnClickListener {
      finish()
    }
    activityEditBinding.registerButton.setOnClickListener {
      launch {
        _editTrashViewModel.registerTrash()
      }
    }

    activityEditBinding.otherTrashText.addTextChangedListener {
      launch {
        _editTrashViewModel.changeDisplayTrashName(it.toString())
      }
    }

    activityEditBinding.addButton.setOnClickListener {
      launch {
        _editTrashViewModel.addSchedule()
      }
    }

    activityEditBinding.buttonSetExcludeDate.setOnClickListener {
      supportFragmentManager.beginTransaction().apply {
        addToBackStack("")
        add(
          activityEditBinding.mainScheduleContainer.id,
          ExcludeDayFragment()
        )
        commit()
      }
    }

    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED){
//        _editTrashViewModel.enabledAppendButton.collect {
//          activityEditBinding.addButton.visibility = if(it) View.VISIBLE else View.INVISIBLE
//        }
      }
    }

    lifecycleScope.launch{
      repeatOnLifecycle(Lifecycle.State.STARTED){
        _editTrashViewModel.scheduleMessage.collect {
          when(it){
            is ScheduleMessage.Add -> {
              Log.d("NeoEditActivity", "Add schedule at ${it.position}")

              val newScheduleLayout = FrameLayout(this@NeoEditActivity)
              newScheduleLayout.id = View.generateViewId()
              newScheduleLayout.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
              )

              activityEditBinding.scheduleContainer.addView(newScheduleLayout)
              supportFragmentManager.beginTransaction().apply {
                add(
                  newScheduleLayout.id,
                  ScheduleFragment.newInstance(it.position)
                )
                commit()
              }
            }
            is ScheduleMessage.Remove -> {
              Log.d("NeoEditActivity", "Remove schedule at ${it.position}")
              activityEditBinding.scheduleContainer.removeViewAt(it.position)
              activityEditBinding.scheduleContainer.children.forEachIndexed { index, view ->
                supportFragmentManager.findFragmentById(view.id).apply {
                  (this as ScheduleFragment).updatePosition(index)
                }
              }
            }
            is ScheduleMessage.Update -> {
              Log.d("NeoEditActivity", "Update schedule at ${it.position}")
              supportFragmentManager.findFragmentById(activityEditBinding.scheduleContainer.getChildAt(it.position).id).apply {
                (this as ScheduleFragment).refreshSchedule()
              }

            }
            else -> {
              Log.d(javaClass.simpleName, "Unknown message")
            }
          }
        }
      }
    }

    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED){
        _editTrashViewModel.visibleDisplayNameInput.collect {
          activityEditBinding.otherTrashText.visibility = if(it) View.VISIBLE else View.INVISIBLE
          activityEditBinding.otherTrashErrorText.visibility = if(it) View.VISIBLE else View.INVISIBLE
        }
      }
    }

    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED){
        _editTrashViewModel.inputValidationMessage.collect {
          activityEditBinding.otherTrashErrorText.visibility = if(it.isEmpty()) View.INVISIBLE else View.VISIBLE
          activityEditBinding.otherTrashErrorText.text = it
        }
      }
    }

    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED){
//        _editTrashViewModel.enabledRegisterButton.collect {
//          activityEditBinding.registerButton.isEnabled = it
//        }
      }
    }

    val context = this
//    lifecycleScope.launch {
//      repeatOnLifecycle(Lifecycle.State.STARTED){
//        _editTrashViewModel.registerMessage.collect {
//          when(it) {
//            is RegisterMessage.Success -> {
//              Toast.makeText(context,it.message, Toast.LENGTH_SHORT).show()
//              finish()
//            }
//            is RegisterMessage.Failure -> {
//              Toast.makeText(context,it.message, Toast.LENGTH_LONG).show()
//            }
//            else -> {
//              Log.w(javaClass.simpleName, "Unknown message")
//            }
//          }
//        }
//      }
//    }
  }

  override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    Log.d("NeoEditActivity", "onItemSelected")
    if(parent?.id == activityEditBinding.trashTypeList.id) {
      val trashIndex = resources.getStringArray(R.array.list_trash_id_select).get(position)
      _editTrashViewModel.setTrashType(trashIndex)
    }
  }

  override fun onNothingSelected(p0: AdapterView<*>?) {
    TODO("Not yet implemented")
  }
}
