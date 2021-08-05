package net.mythrowaway.app.view

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import net.mythrowaway.app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import net.mythrowaway.app.adapter.IActivateView
import net.mythrowaway.app.adapter.controller.ActivateControllerImpl
import net.mythrowaway.app.databinding.ActivityActivateBinding

class ActivateActivity : AppCompatActivity(),
    IActivateView,CoroutineScope by MainScope() {

    private val controller =
        ActivateControllerImpl(this)
    private lateinit var activityActivateBinding: ActivityActivateBinding
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
            activityActivateBinding.activateErrorText.visibility = View.VISIBLE
        }
    }

    override fun invalidCodeError() {
        activityActivateBinding.activateButton.isEnabled = false
    }

    override fun validCode() {
        activityActivateBinding.activateButton.isEnabled = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityActivateBinding = ActivityActivateBinding.inflate(layoutInflater)
        setContentView(activityActivateBinding.root)

        activityActivateBinding.activateButton.setOnClickListener {
            launch {
                controller.activate(activityActivateBinding.activationCodeInputText.text.toString())
            }
        }
        activityActivateBinding.activationCodeInputText.addTextChangedListener{
            controller.checkCode(activityActivateBinding.activationCodeInputText.text.toString())
        }
    }
}