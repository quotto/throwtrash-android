package net.mythrowaway.app.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import net.mythrowaway.app.R

class ScheduleListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_list)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(this.javaClass.simpleName, "onActivityResult -> requestCode=$requestCode,resultCode=$resultCode")
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            ActivityCode.CALENDAR_REQUEST_UPDATE -> {
                when(resultCode) {
                   Activity.RESULT_OK -> {
                       setResult(Activity.RESULT_OK)
                   }
                }
            }
        }
    }
}
