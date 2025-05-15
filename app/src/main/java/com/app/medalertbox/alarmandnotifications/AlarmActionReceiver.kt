package com.app.medalertbox.alarmandnotifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat

class AlarmActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val notificationId = intent.getIntExtra("notificationId", -1)
        val alarmId = intent.getStringExtra("alarm_id") ?: "default_id"

        Log.d("AlarmActionReceiver", "Received action: $action")

        when (action) {
            "STOP" -> {
                Log.d("AlarmActionReceiver", "Stopping alarm")
                context.stopService(Intent(context, AlarmRingtoneService::class.java)) // ← Important!
                AlarmRingtoneService.stopSound(context)
                NotificationManagerCompat.from(context).cancel(notificationId)
            }

            "SNOOZE" -> {
                Log.d("AlarmActionReceiver", "Snoozing alarm")
                context.stopService(Intent(context, AlarmRingtoneService::class.java))
                AlarmRingtoneService.stopSound(context)
                NotificationManagerCompat.from(context).cancel(notificationId)

                val snoozeMillis = System.currentTimeMillis() + 5 * 60 * 1000
                AlarmHelper.setSnoozeAlarm(context, snoozeMillis, alarmId) // Ensure this exists!
            }
        }
    }

    private fun stopAlarm(context: Context, notificationId: Int) {
        Log.d("AlarmActionReceiver", "Stopping alarm")
        AlarmRingtoneService.stopSound(context)
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    private fun snoozeAlarm(context: Context, alarmId: String, notificationId: Int) {
        Log.d("AlarmActionReceiver", "Snoozing alarm for 5 minutes")
        AlarmRingtoneService.stopSound(context)
        NotificationManagerCompat.from(context).cancel(notificationId)

        val snoozeMillis = System.currentTimeMillis() + 5 * 60 * 1000
        AlarmHelper.setSnoozeAlarm(context, snoozeMillis, alarmId)
    }
}