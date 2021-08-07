package net.mythrowaway.app.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import net.mythrowaway.app.R
import net.mythrowaway.app.adapter.MyThrowTrash
import net.mythrowaway.app.adapter.di.ScheduleListComponent

class ScheduleListActivity : AppCompatActivity() {
    lateinit var scheduleListComponent: ScheduleListComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        scheduleListComponent = (application as MyThrowTrash).appComponent.scheduleListComponent().create()
        scheduleListComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_list)
    }
}
