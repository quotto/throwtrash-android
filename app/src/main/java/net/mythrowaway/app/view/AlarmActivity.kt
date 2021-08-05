package net.mythrowaway.app.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import net.mythrowaway.app.R
import net.mythrowaway.app.adapter.IAlarmView
import net.mythrowaway.app.adapter.controller.AlarmControllerImpl
import net.mythrowaway.app.databinding.ActivityAlarmBinding
import net.mythrowaway.app.viewmodel.AlarmViewModel

class AlarmActivity : AppCompatActivity(),
    TimePickerFragment.OnTimeSelectedListener,
    AlarmManagerResponder,
    IAlarmView {
    private lateinit var viewModel: AlarmViewModel
    private val controller =
        AlarmControllerImpl(this)
    private lateinit var activityAlarmBinding: ActivityAlarmBinding

    private fun changeAlarm() {
        viewModel.enabled = activityAlarmBinding.alarmSwitch.isChecked
        viewModel.notifyEveryday = activityAlarmBinding.everydayCheck.isChecked
        controller.saveAlarmConfig(viewModel)

        if (viewModel.enabled) {
            setAlarm(this,viewModel.hourOfDay,viewModel.minute)
        } else {
            cancelAlarm(this)
            Log.i(this.javaClass.simpleName,"Cancel alarm")
        }
    }

    override fun notify(trashList: List<String>) {
        Log.w(this.javaClass.simpleName, "notify is never used")
    }

    override fun onPause() {
        super.onPause()
        changeAlarm()
    }

    override fun onDestroy() {
        super.onDestroy()
        changeAlarm()
    }

    override fun update(viewModel: AlarmViewModel) {
        activityAlarmBinding.alarmSwitch.isChecked = viewModel.enabled
        activityAlarmBinding.timeText.text =  getString(R.string.format_alarm_time).format(viewModel.hourOfDay, viewModel.minute)
        activityAlarmBinding.timeText.isEnabled = viewModel.enabled
        activityAlarmBinding.everydayCheck.isChecked = viewModel.notifyEveryday
        activityAlarmBinding.everydayCheck.isEnabled = viewModel.enabled
        this.viewModel = viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAlarmBinding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(activityAlarmBinding.root)
        activityAlarmBinding.timeText.setOnClickListener {
            val dialog = TimePickerFragment()
            dialog.show(supportFragmentManager, "time_dialog")
        }

        activityAlarmBinding.alarmSwitch.setOnCheckedChangeListener { _, isChecked ->
            activityAlarmBinding.timeText.isEnabled = isChecked
            activityAlarmBinding.everydayCheck.isEnabled = isChecked
        }
        controller.loadAlarmConfig()
    }

    override fun onSelected(hourOfDay: Int, minute: Int) {
        activityAlarmBinding.timeText.text = getString(R.string.format_alarm_time).format(hourOfDay, minute)
        this.viewModel.hourOfDay = hourOfDay
        this.viewModel.minute = minute
    }
}