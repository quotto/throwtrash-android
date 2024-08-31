package net.mythrowaway.app.domain.alarm.presentation.view

import android.Manifest
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import net.mythrowaway.app.R
import net.mythrowaway.app.application.di.AlarmComponent
import net.mythrowaway.app.application.di.DaggerAppComponent
import net.mythrowaway.app.domain.alarm.usecase.AlarmManager
import net.mythrowaway.app.domain.alarm.usecase.AlarmUseCase
import net.mythrowaway.app.domain.alarm.usecase.dto.AlarmTrashDTO
import net.mythrowaway.app.domain.trash.presentation.view.calendar.CalendarActivity
import java.util.*
import javax.inject.Inject

class AlarmReceiver : BroadcastReceiver(), AlarmManager {
    @Inject
    lateinit var alarmUseCase: AlarmUseCase

    private lateinit var alarmComponent: AlarmComponent
    private lateinit var mContext:Context

    fun init(context: Context) {
        alarmComponent = DaggerAppComponent.factory().create(context).alarmComponent().create()
        alarmComponent.inject(this)
        mContext = context
    }

    private fun createNotificationChannel(context: Context) {
        val name = context.getString(R.string.channel_name)
        val descriptionText = context.getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    /*
    BroadCastReceiverの実装
     */
    override fun onReceive(context: Context, intent: Intent) {
        alarmComponent = DaggerAppComponent.factory().create(context).alarmComponent().create()
        alarmComponent.inject(this)

        mContext = context

        if(intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.i(this.javaClass.simpleName, "Received Boot Completed")
        } else {
            Log.i(this.javaClass.simpleName, "Received Alarm Notify")
            createNotificationChannel(context)

            val calendar = Calendar.getInstance()
            try {
                alarmUseCase.alarm(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DATE),
                    this
                )
            } catch (e: Exception) {
                Log.e(this.javaClass.simpleName, e.message.toString())
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }


    /*
    AlarmManagerResponderの実装
     */
    override fun showAlarmMessage(notifyTrashList: List<AlarmTrashDTO>) {
        val inboxStyle = NotificationCompat.InboxStyle()
        if (notifyTrashList.isEmpty()) {
            inboxStyle.addLine("今日出せるゴミはありません")
        } else {
            notifyTrashList.forEach { value ->
                inboxStyle.addLine(value.displayName)
            }
        }

        // タップ起動時はCalendarを表示
        val calendarIntent = Intent(mContext, CalendarActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingCalendarIntent = PendingIntent.getActivity(mContext, 0, calendarIntent, PendingIntent.FLAG_IMMUTABLE)

        val alarmIntent = Intent(mContext, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingAlarmIntent = PendingIntent.getActivity(mContext, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(mContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(mContext.getString(R.string.title_alarm_dialog))
            .setContentIntent(pendingCalendarIntent)
            .setStyle(inboxStyle)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)  // API25以下で有効（API26以上はcreateNotificationChannelで指定）
            .addAction(R.drawable.ic_notification, mContext.getString(R.string.label_button_notification), pendingAlarmIntent)

        with(NotificationManagerCompat.from(mContext)) {
            if (ActivityCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w(this.javaClass.simpleName, "Notification permission is not granted")
                FirebaseCrashlytics.getInstance().log("Notification permission is not granted")
                return
            }
            notify(R.string.id_notify_alarm, builder.build())
        }
    }

    override fun setAlarm(hourOfDay: Int, minute: Int) {
        val am: android.app.AlarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val alarmReceiverIntent = Intent(mContext, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(mContext, 0, alarmReceiverIntent, PendingIntent.FLAG_IMMUTABLE)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DATE, 1)
        }
        am.setExactAndAllowWhileIdle(
            android.app.AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
        am.setExact(android.app.AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        Log.i(
            this.javaClass.simpleName,
            "Set alarm @ ${calendar.get(Calendar.YEAR)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(
                Calendar.DATE
            )} ${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"
        )
    }

    override fun cancelAlarm() {
        val am: android.app.AlarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val alarmReceiverIntent = Intent(mContext, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(mContext, 0, alarmReceiverIntent, PendingIntent.FLAG_IMMUTABLE)
        am.cancel(pendingIntent)
    }

    companion object{
        const val CHANNEL_ID = "net.my.throwtrash.AlarmRecei" +
                "ver"
    }
}