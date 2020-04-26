package com.example.mythrowtrash.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mythrowtrash.R
import kotlinx.android.synthetic.main.activity_connect.*

class ConnectActivity : AppCompatActivity(){
    companion object val ACCOUNT_LINK: Int = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)
        shareButton.setOnClickListener {
            val intent = Intent(this,PublishCodeActivity::class.java)
            startActivity(intent)
        }

        activationButton.setOnClickListener {
            val intent = Intent(this,ActivateActivity::class.java)
            startActivityForResult(intent,CalendarActivity.REQUEST_UPDATE)
        }

        alexaButton.setOnClickListener {
            val intent = Intent(this, AccountLinkActivity::class.java)
            startActivityForResult(intent, ACCOUNT_LINK)
        }
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
