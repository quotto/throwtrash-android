package net.mythrowaway.app.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmIntent = Intent(context, AlarmActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if(intent.action == Intent.ACTION_BOOT_COMPLETED) {
            alarmIntent.putExtra(AlarmActivity.BOOT_COMPLETED,true)
        } else {
            alarmIntent.putExtra(AlarmActivity.NOTIFY_ALARM, true)
        }
        context.startActivity(alarmIntent)
    }
}
