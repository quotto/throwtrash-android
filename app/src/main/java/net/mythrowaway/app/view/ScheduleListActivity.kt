package net.mythrowaway.app.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import net.mythrowaway.app.R

class ScheduleListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_list)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        println("[MyApp - ScheduleListActivity] onActivityResult: requestCode:$requestCode,resultCode:$resultCode")
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
