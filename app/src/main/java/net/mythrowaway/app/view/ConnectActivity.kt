package net.mythrowaway.app.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import net.mythrowaway.app.R
import kotlinx.android.synthetic.main.activity_connect.*
import net.mythrowaway.app.adapter.IConnectView
import net.mythrowaway.app.adapter.controller.ConnectControllerImpl
import net.mythrowaway.app.adapter.presenter.ConnectViewModel

class ConnectActivity : AppCompatActivity(), IConnectView {
    private val controller =
        ConnectControllerImpl(this)
    private var viewModel =
        ConnectViewModel()
    override fun setEnabledStatus(viewModel: ConnectViewModel) {
        shareButton.setEnabled(viewModel.enabledShare)
        activationButton.setEnabled(viewModel.enabledActivate)
        alexaButton.setEnabled(viewModel.enabledAlexa)
        this.viewModel = viewModel
    }

    companion object val ACCOUNT_LINK: Int = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)
        shareButton.setOnClickListener {
            val intent = Intent(this,PublishCodeActivity::class.java)
            startActivity(intent)
        }

        activationButton.setOnClickListener {
            val intent = Intent(this, ActivateActivity::class.java)
            startActivityForResult(intent, CalendarActivity.REQUEST_UPDATE
            )
        }

        alexaButton.setOnClickListener {
            val intent = Intent(this, AccountLinkActivity::class.java)
            startActivityForResult(intent, ACCOUNT_LINK)
        }
        
        controller.changeEnabledStatus()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            CalendarActivity.REQUEST_UPDATE -> {
                when(resultCode) {
                    Activity.RESULT_OK -> {
                        setResult(Activity.RESULT_OK)
                    }
                }
            }
        }
    }
}
