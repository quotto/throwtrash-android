package net.mythrowaway.app.view

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import net.mythrowaway.app.R
import kotlinx.android.synthetic.main.activity_activate.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import net.mythrowaway.app.adapter.IActivateView
import net.mythrowaway.app.adapter.MyThrowTrash
import net.mythrowaway.app.adapter.controller.ActivateControllerImpl
import net.mythrowaway.app.adapter.di.ActivateComponent
import net.mythrowaway.app.usecase.IActivatePresenter
import javax.inject.Inject

class ActivateActivity : AppCompatActivity(),
    IActivateView,CoroutineScope by MainScope() {

    @Inject
    lateinit var controller: ActivateControllerImpl
    @Inject
    lateinit var presenter: IActivatePresenter

    private lateinit var activateComponent: ActivateComponent
    override fun success() {
        val context = this
        launch(Dispatchers.Main) {
            Toast.makeText(
                context,
                getString(R.string.message_complete_activation),
                Toast.LENGTH_LONG
            ).show()
            setResult(Activity.RESULT_OK,null)
            finish()
        }
    }

    override fun failed() {
        launch(Dispatchers.Main) {
            activateErrorText.visibility = View.VISIBLE
        }
    }

    override fun invalidCodeError() {
        activateButton.isEnabled = false
    }

    override fun validCode() {
        activateButton.isEnabled = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        activateComponent = (application as MyThrowTrash).appComponent.activateComponent().create()
        activateComponent.inject(this)
        super.onCreate(savedInstanceState)
        presenter.setView(this)
        setContentView(R.layout.activity_activate)
        activateButton.setOnClickListener {
            launch {
                controller.activate(activationCodeInputText.text.toString())
            }
        }
        activationCodeInputText.addTextChangedListener{
            controller.checkCode(activationCodeInputText.text.toString())
        }
    }
}
