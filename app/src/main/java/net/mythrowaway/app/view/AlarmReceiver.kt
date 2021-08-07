package net.mythrowaway.app.view

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import net.mythrowaway.app.R
import net.mythrowaway.app.adapter.IAlarmView
import net.mythrowaway.app.adapter.controller.AlarmControllerImpl
import net.mythrowaway.app.adapter.di.AlarmComponent
import net.mythrowaway.app.adapter.di.DaggerAppComponent
import net.mythrowaway.app.usecase.IAlarmPresenter
import net.mythrowaway.app.viewmodel.AlarmViewModel
import java.util.*
import javax.inject.Inject

class AlarmReceiver : BroadcastReceiver(),IAlarmView,AlarmManagerResponder {
    @Inject
    lateinit var controller: AlarmControllerImpl
    @Inject
    lateinit var presenter: IAlarmPresenter

    private lateinit var alarmComponent: AlarmComponent
    private lateinit var mContext:Context
    private var viewModel = AlarmViewModel()
    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
    }

    /*
    BroadCastReceiverの実装
     */
    override fun onReceive(context: Context, intent: Intent) {
        alarmComponent = DaggerAppComponent.factory().create(context).alarmComponent().create()
        alarmComponent.inject(this)
        presenter.setView(this)

        mContext = context
        controller.loadAlarmConfig()

        if(intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.i(this.javaClass.simpleName, "Received Boot Completed")
        } else {
            Log.i(this.javaClass.simpleName, "Received Alarm Notify")
            createNotificationChannel(context)

            val calendar = Calendar.getInstance()
            controller.alarmToday(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DATE))
        }

        // 次のアラームを予約する
        setAlarm(context, viewModel.hourOfDay, viewModel.minute)
    }


    /*
    AlarmManagerResponderの実装
     */
    override fun notify(trashList: List<String>) {
        val inboxStyle = NotificationCompat.InboxStyle()
        if (trashList.isNotEmpty()) {
            trashList.forEach {value ->
                inboxStyle.addLine(value)
            }
        } else {
            inboxStyle.addLine(mContext.getString(R.string.message_noneTrash))
        }

        // タップ起動時はCalendarを表示
        val calendarIntent = Intent(mContext,CalendarActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingCalendarIntent = PendingIntent.getActivity(mContext, 0, calendarIntent, 0)

        val alarmIntent = Intent(mContext, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingAlarmIntent = PendingIntent.getActivity(mContext, 0, alarmIntent, 0)

        val builder = NotificationCompat.Builder(mContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(mContext.getString(R.string.title_alarm_dialog))
            .setContentIntent(pendingCalendarIntent)
            .setStyle(inboxStyle)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)  // API25以下で有効（API26以上はcreateNotificationChannelで指定）
            .addAction(R.drawable.ic_notification, mContext.getString(R.string.label_button_notification), pendingAlarmIntent)

        with(NotificationManagerCompat.from(mContext)) {
            notify(R.string.id_notify_alarm, builder.build())
        }
    }

    override fun update(viewModel: AlarmViewModel) {
        this.viewModel = viewModel
        Log.d(this.javaClass.simpleName, "CurrentSetting -> ${viewModel.toString()}")
    }

    companion object{
        const val CHANNEL_ID = "net.my.throwtrash.AlarmRecei" +
                "ver"
    }
}
