package net.mythrowaway.app.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.coroutines.*
import net.mythrowaway.app.adapter.IPublishCodeView
import net.mythrowaway.app.adapter.controller.PublishCodeControllerImpl
import net.mythrowaway.app.databinding.ActivityPublishCodeBinding

class PublishCodeActivity : AppCompatActivity(), IPublishCodeView,CoroutineScope by MainScope() {
    private lateinit var activityPublishCodeBinding: ActivityPublishCodeBinding

    private val controller =
        PublishCodeControllerImpl(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityPublishCodeBinding = ActivityPublishCodeBinding.inflate(layoutInflater)
        setContentView(activityPublishCodeBinding.root)
        if(savedInstanceState == null) {
            launch {
                controller.publishActivationCode()
            }
        } else {
            activityPublishCodeBinding.activationCodeText.text = savedInstanceState.getString(CODE)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CODE,activityPublishCodeBinding.activationCodeText.text.toString())
        Log.d(this.javaClass.simpleName,
            "onSaveInstanceState,put code -> ${activityPublishCodeBinding.activationCodeText.text.toString()}"
        )
    }

    override fun showActivationCode(code: String) {
        launch {
            withContext(Dispatchers.Main) {
                activityPublishCodeBinding.errorText.visibility = View.INVISIBLE
                activityPublishCodeBinding.activationCodeText.text = code
            }
        }
    }

    override fun showError() {
        launch {
            withContext(Dispatchers.Main) {
                activityPublishCodeBinding.errorText.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        private const val CODE = "CODE"
    }
}
