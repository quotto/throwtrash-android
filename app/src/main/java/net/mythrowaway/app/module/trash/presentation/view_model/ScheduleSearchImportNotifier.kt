package net.mythrowaway.app.module.trash.presentation.view_model

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import net.mythrowaway.app.R
import net.mythrowaway.app.module.trash.presentation.view.calendar.CalendarActivity
import net.mythrowaway.app.module.trash.usecase.ScheduleSearchImportStatus
import javax.inject.Inject

class ScheduleSearchImportNotifier @Inject constructor(
  private val context: Context
) {
  fun notifyImportResult(status: ScheduleSearchImportStatus, message: String) {
    createNotificationChannel()
    if (ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      Log.w(this.javaClass.simpleName, "Notification permission is not granted")
      return
    }

    val intent = Intent(context, CalendarActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(
      context,
      0,
      intent,
      PendingIntent.FLAG_IMMUTABLE
    )
    val title = context.getString(
      if (status == ScheduleSearchImportStatus.FAILURE) {
        R.string.title_schedule_search_import_notification_failure
      } else {
        R.string.title_schedule_search_import_notification_success
      }
    )
    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_notification)
      .setContentTitle(title)
      .setContentText(message.lineSequence().firstOrNull().orEmpty())
      .setStyle(NotificationCompat.BigTextStyle().bigText(message))
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .build()

    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
  }

  private fun createNotificationChannel() {
    val channel = NotificationChannel(
      CHANNEL_ID,
      context.getString(R.string.channel_name_schedule_search_import),
      NotificationManager.IMPORTANCE_HIGH
    ).apply {
      description = context.getString(R.string.channel_description_schedule_search_import)
    }
    val notificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
  }

  companion object {
    private const val CHANNEL_ID = "net.my.throwtrash.ScheduleSearchImport"
    private const val NOTIFICATION_ID = 20260507
  }
}
