package net.mythrowaway.app.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import net.mythrowaway.app.R
import kotlinx.android.synthetic.main.activity_publish_code.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import net.mythrowaway.app.adapter.IPublishCodeView
import net.mythrowaway.app.adapter.controller.PublishCodeControllerImpl

class PublishCodeActivity : AppCompatActivity(), IPublishCodeView,CoroutineScope by MainScope() {
    private val controller =
        PublishCodeControllerImpl(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publish_code)
        launch {
            controller.publishActivationCode()
        }
    }

    override fun showActivationCode(code: String) {
        errorText.visibility = View.INVISIBLE
        activationCodeText.text = code
    }

    override fun showError() {
        errorText.visibility = View.VISIBLE
    }
}
