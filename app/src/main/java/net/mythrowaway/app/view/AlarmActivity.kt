package net.mythrowaway.app.view

import android.app.AlarmManager
import android.app.KeyguardManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.CompoundButton
import net.mythrowaway.app.R
import kotlinx.android.synthetic.main.activity_alarm.*
import net.mythrowaway.app.adapter.IAlarmView
import net.mythrowaway.app.adapter.controller.AlarmControllerImpl
import net.mythrowaway.app.adapter.presenter.AlarmViewModel
import java.util.*

class AlarmActivity : AppCompatActivity(),
    TimePickerFragment.OnTimeSelectedListener,
    IAlarmView {
    private lateinit var viewModel: AlarmViewModel
    private val controller =
        AlarmControllerImpl(this)


    override fun notify(trashList: List<String>) {
        if(!this.viewModel.notifyEveryday && trashList.isEmpty()) {
            finish()
            return
        }

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 -> {
                setShowWhenLocked(true)
                setTurnScreenOn(true)
                val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                keyguardManager.requestDismissKeyguard(this, null)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                window.addFlags(
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                )
                val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                keyguardManager.requestDismissKeyguard(this, null)
            }
            else -> {
                window.addFlags(
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                )
            }
        }
        val dialog =
            AlarmTrashDialog.newInstance(
                trashList
            )
        dialog.show(supportFragmentManager, "alert_dialog")
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.enabled = alarmSwitch.isChecked
        viewModel.notifyEveryday = everydayCheck.isChecked
        controller.saveAlarmConfig(viewModel)

        val am: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmReceiverIntent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmReceiverIntent, 0)

        if (viewModel.enabled) {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, viewModel.hourOfDay)
                set(Calendar.MINUTE, viewModel.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            if(calendar.timeInMillis < System.currentTimeMillis()) {
                calendar.add(Calendar.DATE, 1)
            }

            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                    am.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                }
                else -> {
                    am.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                }
            }
            println("[MyApp - AlarmActivity] set alarm @ ${calendar.get(Calendar.YEAR)}/${calendar.get(Calendar.MONTH)+1}/${calendar.get(Calendar.DATE)} ${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}")
        } else {
            am.cancel(pendingIntent)
            println("[MyApp - AlarmActivity] cancel alarm")
        }
    }

    override fun update(viewModel: AlarmViewModel) {
        alarmSwitch.setChecked(viewModel.enabled)
        timeText.text =  "%1$02d:%2$02d".format(viewModel.hourOfDay, viewModel.minute)
        timeText.setEnabled(viewModel.enabled)
        everydayCheck.setChecked(viewModel.notifyEveryday)
        everydayCheck.setEnabled(viewModel.enabled)
        this.viewModel = viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
        timeText.setOnClickListener {
            val dialog = TimePickerFragment()
            dialog.show(supportFragmentManager, "time_dialog")
        }

        alarmSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            timeText.isEnabled = isChecked
            everydayCheck.isEnabled = isChecked
        })
        controller.loadAlarmConfig()

        intent?.let {intent->
            if(intent.getBooleanExtra(NOTIFY_ALARM, false)) {
                val calendar = Calendar.getInstance()
                controller.alarmToday(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DATE))
            } else if(intent.getBooleanExtra(BOOT_COMPLETED, false)) {
                // 再起動時にはアラーム設定を再設定して終了する
                controller.saveAlarmConfig(viewModel)
                finish()
            }
        }
    }

    override fun onSelected(hourOfDay: Int, minute: Int) {
        timeText.text = "%1$02d:%2$02d".format(hourOfDay, minute)
        this.viewModel.hourOfDay = hourOfDay
        this.viewModel.minute = minute
    }

    companion object {
        const val NOTIFY_ALARM = "NotifyAlarm"
        const val BOOT_COMPLETED = "BootCompleted"
    }
}
