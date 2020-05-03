package net.mythrowaway.app.view

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import net.mythrowaway.app.R
import kotlinx.android.synthetic.main.activity_activate.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import net.mythrowaway.app.adapter.IActivateView
import net.mythrowaway.app.adapter.controller.ActivateControllerImpl

class ActivateActivity : AppCompatActivity(),
    IActivateView,CoroutineScope by MainScope() {

    private val controller =
        ActivateControllerImpl(this)
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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activate)
        activateButton.setOnClickListener {
            launch {
                controller.activate(activationCodeInputText.text.toString())
            }
        }
        activationCodeInputText.setOnKeyListener { v, keyCode, event ->
            if(keyCode != KeyEvent.KEYCODE_BACK) {
                controller.checkCode(activationCodeInputText.text.toString())
            }
            false
        }
    }
}
