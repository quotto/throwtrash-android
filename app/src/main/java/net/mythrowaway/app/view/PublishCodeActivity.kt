package net.mythrowaway.app.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import net.mythrowaway.app.R
import kotlinx.android.synthetic.main.activity_publish_code.*
import kotlinx.coroutines.*
import net.mythrowaway.app.adapter.IPublishCodeView
import net.mythrowaway.app.adapter.MyThrowTrash
import net.mythrowaway.app.adapter.controller.PublishCodeControllerImpl
import net.mythrowaway.app.adapter.di.PublishCodeComponent
import net.mythrowaway.app.usecase.IPublishCodePresenter
import javax.inject.Inject

class PublishCodeActivity : AppCompatActivity(), IPublishCodeView,CoroutineScope by MainScope() {
    @Inject
    lateinit var controller: PublishCodeControllerImpl
    @Inject
    lateinit var presenter: IPublishCodePresenter

    private lateinit var publishCodeComponent: PublishCodeComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        publishCodeComponent = (application as MyThrowTrash).appComponent.publishCodeComponent().create()
        publishCodeComponent.inject(this)

        super.onCreate(savedInstanceState)

        presenter.setView(this)
        setContentView(R.layout.activity_publish_code)
        if(savedInstanceState == null) {
            launch {
                controller.publishActivationCode()
            }
        } else {
            activationCodeText.text = savedInstanceState.getString(CODE)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CODE,activationCodeText.text.toString())
        Log.d(this.javaClass.simpleName, "onSaveInstanceState,put code -> ${activationCodeText.text.toString()}")
    }

    override fun showActivationCode(code: String) {
        launch {
            withContext(Dispatchers.Main) {
                errorText.visibility = View.INVISIBLE
                activationCodeText.text = code
            }
        }
    }

    override fun showError() {
        launch {
            withContext(Dispatchers.Main) {
                errorText.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        private const val CODE = "CODE"
    }
}
