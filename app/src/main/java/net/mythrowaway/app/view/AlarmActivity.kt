package net.mythrowaway.app.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import net.mythrowaway.app.R
import kotlinx.android.synthetic.main.activity_alarm.*
import net.mythrowaway.app.adapter.IAlarmView
import net.mythrowaway.app.adapter.MyThrowTrash
import net.mythrowaway.app.adapter.controller.AlarmControllerImpl
import net.mythrowaway.app.adapter.di.AlarmComponent
import net.mythrowaway.app.usecase.IAlarmPresenter
import net.mythrowaway.app.viewmodel.AlarmViewModel
import javax.inject.Inject

class AlarmActivity : AppCompatActivity(),
    TimePickerFragment.OnTimeSelectedListener,
    AlarmManagerResponder,
    IAlarmView {

    private lateinit var viewModel: AlarmViewModel

    lateinit var alarmComponent: AlarmComponent

    @Inject
    lateinit var controller: AlarmControllerImpl
    @Inject
    lateinit var presenter: IAlarmPresenter

    private fun changeAlarm() {
        viewModel.enabled = alarmSwitch.isChecked
        viewModel.notifyEveryday = everydayCheck.isChecked
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
        alarmSwitch.isChecked = viewModel.enabled
        timeText.text =  getString(R.string.format_alarm_time).format(viewModel.hourOfDay, viewModel.minute)
        timeText.isEnabled = viewModel.enabled
        everydayCheck.isChecked = viewModel.notifyEveryday
        everydayCheck.isEnabled = viewModel.enabled
        this.viewModel = viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        alarmComponent = (application as MyThrowTrash).appComponent.alarmComponent().create()
        alarmComponent.inject(this)
        presenter.setView(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
        timeText.setOnClickListener {
            val dialog = TimePickerFragment()
            dialog.show(supportFragmentManager, "time_dialog")
        }

        alarmSwitch.setOnCheckedChangeListener { _, isChecked ->
            timeText.isEnabled = isChecked
            everydayCheck.isEnabled = isChecked
        }
        controller.loadAlarmConfig()
    }

    override fun onSelected(hourOfDay: Int, minute: Int) {
        timeText.text = getString(R.string.format_alarm_time).format(hourOfDay, minute)
        this.viewModel.hourOfDay = hourOfDay
        this.viewModel.minute = minute
    }
}
